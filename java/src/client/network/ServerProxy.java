package client.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import client.data.GameInfo;
import client.model.Serializer;
import com.google.gson.JsonSyntaxException;

import java.util.List;

import org.javatuples.Pair;

import shared.exceptions.NoCookieException;
import shared.models.DTO.*;
import shared.models.DTO.params.*;

/**
 * ServerProxy is our implementation of the iServerProxy interface. This class
 * will be used to handle all communication with the server.
 *
 * @author Peter Anderson <anderson.peter@byu.edu>
 * @author Jillian Koontz <jpkoontz@gmail.com>
 */
public class ServerProxy implements iServerProxy {

    private static String serverHost = "localhost";
    private static String serverPort = "8088";
    private String userCookie = "";
    private String gameCookie = "";
    private final String COOKIE_HEADER = "Set-cookie";
    private UserCookie uCookie;
    private int gameNum = -1;

    private static ServerProxy instance;
    
    private ServerProxy(){}

    public UserCookie getUserCookie() {
    	return uCookie;
    }

    public int getGameNumber() {
        return gameNum;
    }
    
    public static void init(String host, String port) 
            throws ProxyAlreadyInstantiated {
        if (instance == null) {
            if (host != null && !host.equals("${host}")) {
                serverHost = host;
            }
            if (port != null && !port.equals("${port}")) {
                serverPort = port;
            }
        } else {
            throw new ProxyAlreadyInstantiated();
        }
    }

    public static ServerProxy getInstance() {
        if (instance == null) {
            instance = new ServerProxy();
        }
        return instance;
    }

    Serializer serializer = new Serializer();

    public Pair<String, Integer> doGet(String urlPath) throws IOException {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            String cookieValue = "catan.user=" + userCookie + "; catan.game=" + gameCookie;
            connection.setRequestProperty("Cookie", cookieValue);
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }

                return new Pair<>(out.toString(), HttpURLConnection.HTTP_OK);
//            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
//                return null;
            } else {
                return new Pair<>("Error", HttpURLConnection.HTTP_OK);
            }
        } catch (IOException e) {
            throw new IOException(String.format("doGet failed: %s", e.getMessage()), e);
        }
    }

    public Pair<String, Integer> doPost(String urlPath, String jsonString,
            boolean extractCookie) throws IOException {
        try {
            //System.out.println(jsonString);
            URL url = new URL("http://" + serverHost + ":" + serverPort + urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            String cookieValue = "catan.user=" + userCookie + "; catan.game=" + gameCookie;
            connection.setRequestProperty("Cookie", cookieValue);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.connect();
            byte[] outputBytes = jsonString.getBytes("UTF-8");
            OutputStream os = connection.getOutputStream();
            os.write(outputBytes);

            os.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                if (extractCookie) {
                    //Set cookies
                    String cookieField = connection.getHeaderField(COOKIE_HEADER);
                    if (cookieField != null && cookieField.contains("catan.game")) {
                        gameCookie = extractGameCookie(cookieField);
                        gameNum = Integer.parseInt(gameCookie);
                    } else if (cookieField != null && cookieField.contains("catan.user")) {
                        System.out.println(cookieField);
                        userCookie = extractUserCookie(cookieField);
                        System.out.println(userCookie);
                        String result = URLDecoder.decode(userCookie, "UTF-8");
                        System.out.println(result);
                        storeCookies(result);
                    }
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }

                return new Pair<>(out.toString(), HttpURLConnection.HTTP_OK);
//            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
//                return null;
            } else {
                System.out.println("Error: " + connection.getResponseCode());
                return new Pair<>("Error", connection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public Pair<Boolean, Integer> login(UserCredentials user) throws IOException {
        String params = serializer.serialize(user);
        Pair<String, Integer> result = doPost("/user/login", params, true);
        String message = result.getValue0();
        int code = result.getValue1();
        if (message.compareTo("Success") == 0) {
            return new Pair<>(true, code);
        } else {
            return new Pair<>(false, code);
        }
    }

    @Override
    public Pair<Boolean, Integer> registerNewUser(UserCredentials user) throws IOException {
        String params = serializer.serialize(user);
        Pair<String, Integer> result = doPost("/user/register", params, true);
        String message = result.getValue0();
        int code = result.getValue1();
        if (message.compareTo("Success") == 0) {
            return new Pair<>(true, code);
        } else {
            return new Pair<>(false, code);
        }
    }

    @Override
    public List<GameInfo> listGames() throws IOException {
        Pair<String, Integer> result = doGet("/games/list");
        return serializer.deserializeGameInfoList(result.getValue0());
    }

    @Override
    public GameInfo createGames(CreateGameRequest game) throws IOException {
        String params = serializer.serialize(game);
        System.out.print(params);
        Pair<String, Integer> result = doPost("/games/create", params, false);
        return serializer.deserializeGameInfo(result.getValue0());
    }

    @Override
    public ClientModelDTO retrieveCurrentState(Integer version) throws IOException {
        Pair<String, Integer> result;
        if (version != null) {
            result = doGet("/game/model?version=" + version);
        } else {
            result = doGet("/game/model");
        }
        try {
            return serializer.deserializeModel(result.getValue0());
        } catch (JsonSyntaxException ex) {
            return null;
        }
    }

    @Override
    public ClientModelDTO sendChat(SendChat message) throws IOException {
        String params = serializer.serialize(message);
        Pair<String, Integer> result = doPost("/moves/sendChat", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO acceptTrade(AcceptTrade accept) throws IOException {
        String params = serializer.serialize(accept);
        Pair<String, Integer> result = doPost("/moves/acceptTrade", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO discardCards(DiscardCards discardedCards) throws IOException {
        String params = serializer.serialize(discardedCards);
        Pair<String, Integer> result = doPost("/moves/discardCards", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO rollNumber(RollNumber rollMove) throws IOException {
        System.out.println("Pre Roll Number");
        String params = serializer.serialize(rollMove);
        Pair<String, Integer> result = doPost("/moves/rollNumber", params, false);
        System.out.println("Post Roll Number");
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO buildRoad(BuildRoad roadMove) throws IOException {
        String params = serializer.serialize(roadMove);
        Pair<String, Integer> result = doPost("/moves/buildRoad", params, roadMove.isFree());
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO buildSettlement(BuildSettlement settlementMove) throws IOException {
        String params = serializer.serialize(settlementMove);
        Pair<String, Integer> result = doPost("/moves/buildSettlement", params, settlementMove.isFree());
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO buildCity(BuildCity cityMove) throws IOException {
        String params = serializer.serialize(cityMove);
        Pair<String, Integer> result = doPost("/moves/buildCity", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO offerTrade(OfferTrade tradeOffer) throws IOException {
        String params = serializer.serialize(tradeOffer);
        Pair<String, Integer> result = doPost("/moves/offerTrade", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO maritimeTrade(MaritimeTrade maritimeMove) throws IOException {
        String params = serializer.serialize(maritimeMove);
        Pair<String, Integer> result = doPost("/moves/maritimeTrade", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO robPlayer(RobPlayer robMove) throws IOException {
        String params = serializer.serialize(robMove);
        Pair<String, Integer> result = doPost("/moves/robPlayer", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO finishTurn(FinishTurn turn) throws IOException {
        String params = serializer.serialize(turn);
        Pair<String, Integer> result = doPost("/moves/finishTurn", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO buyDevCard(BuyDevCard card) throws IOException {
        String params = serializer.serialize(card);
        Pair<String, Integer> result = doPost("/moves/buyDevCard", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO playSoldier(Soldier soldier) throws IOException {
        String params = serializer.serialize(soldier);
        Pair<String, Integer> result = doPost("/moves/Soldier", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO playYearOfPlenty(YearOfPlenty yearOfPlentyMove) throws IOException {
        String params = serializer.serialize(yearOfPlentyMove);
        Pair<String, Integer> result = doPost("/moves/Year_of_Plenty", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO playRoadBuilding(RoadBuilding roadBuildingMove) throws IOException {
        String params = serializer.serialize(roadBuildingMove);
        Pair<String, Integer> result = doPost("/moves/Road_Building", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO playMonopoly(Monopoly monopoly) throws IOException {
        String params = serializer.serialize(monopoly);
        Pair<String, Integer> result = doPost("/moves/Monopoly", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public ClientModelDTO playMonument(Monument monument) throws IOException {
        String params = serializer.serialize(monument);
        Pair<String, Integer> result = doPost("/moves/Monument", params, false);
        return serializer.deserializeModel(result.getValue0());
    }

    @Override
    public Integer joinGame(JoinGameRequest game) throws IOException {
        String params = serializer.serialize(game);
        System.out.println(params);
        Pair<String, Integer> result = doPost("/games/join", params, true);
        return result.getValue1();
    }

//    @Override
//    public void saveGames(int gameId, String fileName) throws IOException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    @Override
//    public void loadGame(String fileName) throws IOException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    @Override
//    public ClientModelDTO resetGame() throws IOException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    @Override
//    public CommandContainerDTO getCommands() throws IOException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    @Override
//    public ClientModelDTO postGameCommands(CommandContainerDTO commands) throws IOException {
//    	try {
//	    	String params = serializer.serialize(commands);
//	        return (ClientModelDTO) serializer.deserialize(doPost("/games/commands", params));
//    	} catch (IOException e) {
//    		e.printStackTrace();
//    		throw new IOException();
//    	}
//    }
    @SuppressWarnings("unchecked")
    @Override
    public List<String> listAITypes() throws IOException {
        Pair<String, Integer> result = doGet("/game/listAI");
         return (List<String>) serializer.deserialize(result.getValue0());
    }

    @Override
    public void addAIPlayer(AddAIRequest player) throws IOException {
        String params = serializer.serialize(player);
        Pair<String, Integer> result = doPost("/game/addAI", params, false);
        serializer.deserialize(result.getValue0());
    }

//    @Override
//    public void changeLogLevel(String logLevel) throws IOException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//  public static void main(final String[] args) {
//      ServerProxy test = new ServerProxy();
//  	try {
//          String user = test.doPost("/user/login", "{'username':'Pete','password':'pete'}");
//          user = test.doGet("/games/list");
//      } catch (IOException e) {
//          e.printStackTrace();
//      }
//  }
    private String extractGameCookie(String cookie) {
        cookie = cookie.substring(cookie.indexOf("catan.game"));
        String cookieName = cookie.substring(0, cookie.indexOf("="));
        String cookieValue = cookie.substring(cookie.indexOf("=")+1,
            cookie.indexOf(";"));
//        System.out.println(cookieValue);
        return cookieValue;
    }

    private String extractUserCookie(String cookie) {
        cookie = cookie.substring(0, cookie.indexOf(";"));
        String cookieName = cookie.substring(0, cookie.indexOf("="));
        String cookieValue = cookie.substring(cookie.indexOf("%"),
                cookie.length());
//        System.out.println(cookieValue);
        return cookieValue;
    }

    private void storeCookies(String result) {
    	uCookie = serializer.deserializeUserCookie(result);
//        System.out.println("User id: " + uCookie.getPlayerID());
    }

	@Override
	public String getLocalPlayerName() throws NoCookieException {
		if(uCookie == null)
			throw new NoCookieException();
		return uCookie.getName();
	}

}
