package client.join;

import client.base.*;
import client.data.GameInfo;
import client.data.PlayerInfo;
import client.network.ServerProxy;
import client.network.iServerProxy;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implementation for the player waiting controller
 */
public class PlayerWaitingController extends Controller implements IPlayerWaitingController {

    public PlayerWaitingController(IPlayerWaitingView view) {

        super(view);
    }

    @Override
    public IPlayerWaitingView getView() {

        return (IPlayerWaitingView) super.getView();
    }

    private int setViewPlayers(IPlayerWaitingView view, ServerProxy proxy) {
        int numPlayers = 0;
    	try {
            int gameNumber = proxy.getGameNumber();
            System.out.println("Game ID: " + gameNumber);
            List<GameInfo> games = proxy.listGames();
            for (GameInfo game : games) {
                if (game.getId() == gameNumber) {
                    List<PlayerInfo> players = game.getPlayers();
                    numPlayers = players.size();
                    view.setPlayers(players.toArray(new PlayerInfo[players.size()]));
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Exception while starting view controller");
            ex.printStackTrace();
        }
    	return numPlayers;
    }

    private void setViewAIs(IPlayerWaitingView view, ServerProxy proxy) {
        try {
            List<String> AIs = proxy.listAITypes();
            String[] AITypes = new String[AIs.size()];
            for (int i = 0; i < AIs.size(); i++) {
                AITypes[i] = AIs.get(i).toString();
            }
            view.setAIChoices(AITypes);
        } catch (IOException ex) {
            System.err.println("Exception occurred while setting AIs in view");
            ex.printStackTrace();
        }
    }

    @Override
    public void start() {
        ServerProxy proxy = ServerProxy.getInstance();
        IPlayerWaitingView view = getView();
        setViewPlayers(view, proxy);
        setViewAIs(view, proxy);
        view.showModal();
        
        Timer timer = new Timer();
        timer.schedule(new WaitForPlayersTask(this, proxy), 2000, 2000);
    }
    
    class WaitForPlayersTask extends TimerTask {
    	private PlayerWaitingController parent;
    	private ServerProxy proxy;
    	private int players = 0;
    	
    	public WaitForPlayersTask(PlayerWaitingController parent, ServerProxy proxy) {
    		this.parent = parent;
    		this.proxy = proxy;    		
    	}

		@Override
		public void run() {
			if(players < 4) {
				players = parent.setViewPlayers(parent.getView(), proxy);
			}
			else {
				parent.getView().closeModal();
				System.exit(0);
			}
		}
    	
    }

    @Override
    public void addAI() {
    }

}
