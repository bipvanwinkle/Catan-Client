/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import client.model.Serializer;
import java.net.URLDecoder;

import java.util.List;

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

    private String serverHost = "localhost";
    private String serverPort = "8081";
    private String userCookie = "";
    private String gameCookie = "";
    private final String COOKIE_HEADER = "Set-cookie";

    /**
     * Class constructor
     */
    public ServerProxy() {

    }

    /**
     * Class constructor
     */
    public ServerProxy(String serverHost, String serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    Serializer serializer = new Serializer();

    public String doGet(String urlPath) throws IOException {
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

                return out.toString();
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                return null;
            } else {
                throw new IOException(String.format("doGet failed: %s (http code %d)",
                        urlPath, connection.getResponseCode()));
            }
        } catch (IOException e) {
            throw new IOException(String.format("doGet failed: %s", e.getMessage()), e);
        }
    }

    public String doPost(String urlPath, String jsonString,
                            boolean extractCookie) throws IOException {
        try {
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
//            System.out.println(jsonString);
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
                    } else if (cookieField != null && cookieField.contains("catan.user")) {
                        userCookie = extractUserCookie(cookieField);
                    }
                }
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }

                return out.toString();
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                return null;
            } else {
                throw new IOException(String.format("doPost failed: %s (http code %d)",
                        urlPath, connection.getResponseCode()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public void login(UserCredentials user) throws IOException {
        try {
            String params = serializer.serialize(user);
            doPost("/user/login", params, true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Did not succeed");
        }
    }

    @Override
    public void registerNewUser(UserCredentials user) throws IOException {
        try {
            String params = serializer.serialize(user);
            doPost("/user/register", params, false);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public GameContainerDTO listGames() throws IOException {
        try {
            GameContainerDTO list = new GameContainerDTO();
            List<GameDTO> list2 = (List<GameDTO>) serializer.deserialize(doGet("/games/list"));
            list.setGames(list2);
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public GameDTO createGames(CreateGameRequest game) throws IOException {
        try {
            String params = serializer.serialize(game);
            return serializer.deserializeGame(doPost("/games/create", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO retrieveCurrentState(Integer version) throws IOException {
        try {
            return serializer.deserializeModel(doGet("/game/model"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO sendChat(SendChat message) throws IOException {
        try {
            String params = serializer.serialize(message);
            return serializer.deserializeModel(doPost("/moves/sendChat", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO acceptTrade(AcceptTrade accept) throws IOException {
        try {
            String params = serializer.serialize(accept);
            return serializer.deserializeModel(doPost("/moves/acceptTrade", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO discardCards(DiscardCards discardedCards) throws IOException {
        try {
            String params = serializer.serialize(discardedCards);
            return serializer.deserializeModel(doPost("/moves/discardCards", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO rollNumber(RollNumber rollMove) throws IOException {
        try {
            String params = serializer.serialize(rollMove);
            return serializer.deserializeModel(doPost("/moves/rollNumber", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO buildRoad(BuildRoad roadMove) throws IOException {
        try {
            String params = serializer.serialize(roadMove);
            return serializer.deserializeModel(doPost("/moves/buildRoad", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO buildSettlement(BuildSettlement settlementMove) throws IOException {
        try {
            String params = serializer.serialize(settlementMove);
            return serializer.deserializeModel(doPost("/moves/buildSettlement", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO buildCity(BuildCity cityMove) throws IOException {
        try {
            String params = serializer.serialize(cityMove);
            return serializer.deserializeModel(doPost("/moves/buildCity", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO offerTrade(OfferTrade tradeOffer) throws IOException {
        try {
            String params = serializer.serialize(tradeOffer);
            return serializer.deserializeModel(doPost("/moves/offerTrade", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO maritimeTrade(MaritimeTrade maritimeMove) throws IOException {
        try {
            String params = serializer.serialize(maritimeMove);
            return serializer.deserializeModel(doPost("/moves/maritimeTrade", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO robPlayer(RobPlayer robMove) throws IOException {
        try {
            String params = serializer.serialize(robMove);
            return serializer.deserializeModel(doPost("/moves/robPlayer", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO finishTurn(FinishTurn turn) throws IOException {
        try {
            String params = serializer.serialize(turn);
            return serializer.deserializeModel(doPost("/moves/finishTurn", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO buyDevCard(BuyDevCard card) throws IOException {
        try {
            String params = serializer.serialize(card);
            return serializer.deserializeModel(doPost("/moves/buyDevCard", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO playSoldier(Soldier soldier) throws IOException {
        try {
            String params = serializer.serialize(soldier);
            return serializer.deserializeModel(doPost("/moves/Soldier", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO playYearOfPlenty(YearOfPlenty yearOfPlentyMove) throws IOException {
        try {
            String params = serializer.serialize(yearOfPlentyMove);
            return serializer.deserializeModel(doPost("/moves/Year_of_Plenty", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO playRoadBuilding(RoadBuilding roadBuildingMove) throws IOException {
        try {
            String params = serializer.serialize(roadBuildingMove);
            return serializer.deserializeModel(doPost("/moves/Road_Building", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO playMonopoly(Monopoly monopoly) throws IOException {
        try {
            String params = serializer.serialize(monopoly);
            return serializer.deserializeModel(doPost("/moves/Monopoly", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public ClientModelDTO playMonument(Monument monument) throws IOException {
        try {
            String params = serializer.serialize(monument);
            return serializer.deserializeModel(doPost("/moves/Monument", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public void joinGame(JoinGameRequest game) throws IOException {
        try {
            String params = serializer.serialize(game);
            serializer.deserialize(doPost("/games/join", params, true));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
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
    @Override
    public List<AddAIRequest> listAITypes() throws IOException {
        try {
            return (List<AddAIRequest>) serializer.deserialize(doGet("/game/listAI"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Override
    public void addAIPlayer(AddAIRequest player) throws IOException {
        try {
            String params = serializer.serialize(player);
            serializer.deserialize(doPost("/game/addAI", params, false));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
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

    private String extractGameCookie(String cookieField) {
        String trimPath = cookieField.replace(";Path=/;", "");
        String trimCatan = trimPath.replace("catan.game=", "");
        return trimCatan;
    }

    private String extractUserCookie(String cookieField) {
        return cookieField.replace(";Path=/;", "").replace("catan.user=", "");
    }
    
}
