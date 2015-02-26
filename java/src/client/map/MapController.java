package client.map;

import java.util.*;

import shared.definitions.*;
import shared.locations.*;
import shared.models.*;
import client.base.*;
import client.data.*;
import client.model.ModelFacade;
import client.model.Populator;

/**
 * Implementation for the map controller
 */
public class MapController extends Controller 
    implements IMapController, Observer {

    private IRobView robView;
    private State state;
    private String currState;
    private ModelFacade facade = new ModelFacade();

    public MapController(IMapView view, IRobView robView) {

        super(view);
        Populator.getInstance().addObserver(this);
        setRobView(robView);

        initFromModel(null);
    }


	public IMapView getView() {

        return (IMapView) super.getView();
    }

    private IRobView getRobView() {
        return robView;
    }

    private void setRobView(IRobView robView) {
        this.robView = robView;
    }

    protected void initFromModel(ModelFacade facade) {
        if (facade == null || !facade.hasModel()) return;
                
        for (int i = 0; i < facade.NumberOfHexes(); i++) {
            Hex hex = facade.GetHexAt(i);
            if (hex != null) {
                getView().addHex(hex.getLocation(), hex.getResource()); 
                getView().addNumber(hex.getLocation(), hex.getChit());
            }

        }
        
        for (int i = 0; i < facade.NumberOfRoads(); i++) {
        	Road road = facade.GetRoadAt(i);
        	getView().placeRoad(road.getLocation(), facade.GetPlayerColor(road.getOwner()));
        }
        
        for (int i = 0; i < facade.NumberOfCities(); i++) {
        	VertexObject city = facade.GetCityAt(i);        	
            getView().placeCity(city.getLocation(), facade.GetPlayerColor(city.getOwner()));
        }
        
        for (int i = 0; i < facade.NumberOfSettlements(); i++) {
        	VertexObject settlement = facade.GetSettlementAt(i);
            getView().placeSettlement(settlement.getLocation(), facade.GetPlayerColor(settlement.getOwner()));
        }
        
        
        for (int i = 0; i < facade.NumberOfHarbors(); i++) {
        	Harbor port = facade.GetHarborAt(i); 
            getView().addPort(port.getLocation(), port.getResource()); 
        }
        
        getView().placeRobber(facade.GetRobber().getLocation());
        
        /*
         * //<temp>
        Random rand = new Random();

        for (int x = 0; x <= 3; ++x) {

            int maxY = 3 - x;
            for (int y = -3; y <= maxY; ++y) {
                int r = rand.nextInt(HexType.values().length);
                HexType hexType = HexType.values()[r];
                HexLocation hexLoc = new HexLocation(x, y);
                getView().addHex(hexLoc, hexType);
                getView().placeRoad(new EdgeLocation(hexLoc, EdgeDirection.NorthWest),
                        CatanColor.RED);
                getView().placeRoad(new EdgeLocation(hexLoc, EdgeDirection.SouthWest),
                        CatanColor.BLUE);
                getView().placeRoad(new EdgeLocation(hexLoc, EdgeDirection.South),
                        CatanColor.ORANGE);
                getView().placeSettlement(new VertexLocation(hexLoc, VertexDirection.NorthWest), CatanColor.GREEN);
                getView().placeCity(new VertexLocation(hexLoc, VertexDirection.NorthEast), CatanColor.PURPLE);
            }

            if (x != 0) {
                int minY = x - 3;
                for (int y = minY; y <= 3; ++y) {
                    int r = rand.nextInt(HexType.values().length);
                    HexType hexType = HexType.values()[r];
                    HexLocation hexLoc = new HexLocation(-x, y);
                    getView().addHex(hexLoc, hexType);
                    getView().placeRoad(new EdgeLocation(hexLoc, EdgeDirection.NorthWest),
                            CatanColor.RED);
                    getView().placeRoad(new EdgeLocation(hexLoc, EdgeDirection.SouthWest),
                            CatanColor.BLUE);
                    getView().placeRoad(new EdgeLocation(hexLoc, EdgeDirection.South),
                            CatanColor.ORANGE);
                    getView().placeSettlement(new VertexLocation(hexLoc, VertexDirection.NorthWest), CatanColor.GREEN);
                    getView().placeCity(new VertexLocation(hexLoc, VertexDirection.NorthEast), CatanColor.PURPLE);
                }
            }
        }

        PortType portType = PortType.BRICK;
        getView().addPort(new EdgeLocation(new HexLocation(0, 3), EdgeDirection.North), portType);
        getView().addPort(new EdgeLocation(new HexLocation(0, -3), EdgeDirection.South), portType);
        getView().addPort(new EdgeLocation(new HexLocation(-3, 3), EdgeDirection.NorthEast), portType);
        getView().addPort(new EdgeLocation(new HexLocation(-3, 0), EdgeDirection.SouthEast), portType);
        getView().addPort(new EdgeLocation(new HexLocation(3, -3), EdgeDirection.SouthWest), portType);
        getView().addPort(new EdgeLocation(new HexLocation(3, 0), EdgeDirection.NorthWest), portType);

        getView().placeRobber(new HexLocation(0, 0));

        getView().addNumber(new HexLocation(-2, 0), 2);
        getView().addNumber(new HexLocation(-2, 1), 3);
        getView().addNumber(new HexLocation(-2, 2), 4);
        getView().addNumber(new HexLocation(-1, 0), 5);
        getView().addNumber(new HexLocation(-1, 1), 6);
        getView().addNumber(new HexLocation(1, -1), 8);
        getView().addNumber(new HexLocation(1, 0), 9);
        getView().addNumber(new HexLocation(2, -2), 10);
        getView().addNumber(new HexLocation(2, -1), 11);
        getView().addNumber(new HexLocation(2, 0), 12);

        //</temp>
         */

    }

    public boolean canPlaceRoad(EdgeLocation edgeLoc) {

        return state.canPlaceRoad(edgeLoc);
    }

    public boolean canPlaceSettlement(VertexLocation vertLoc) {

        return true;
    }

    public boolean canPlaceCity(VertexLocation vertLoc) {

        return true;
    }

    public boolean canPlaceRobber(HexLocation hexLoc) {

        return true;
    }

    public void placeRoad(EdgeLocation edgeLoc) {

        getView().placeRoad(edgeLoc, CatanColor.ORANGE);
    }

    public void placeSettlement(VertexLocation vertLoc) {

        getView().placeSettlement(vertLoc, CatanColor.ORANGE);
    }

    public void placeCity(VertexLocation vertLoc) {

        getView().placeCity(vertLoc, CatanColor.ORANGE);
    }

    public void placeRobber(HexLocation hexLoc) {

        getView().placeRobber(hexLoc);

        getRobView().showModal();
    }

    public void startMove(PieceType pieceType, boolean isFree, boolean allowDisconnected) {

        getView().startDrop(pieceType, CatanColor.ORANGE, true);
    }

    public void cancelMove() {

    }

    public void playSoldierCard() {

    }

    public void playRoadBuildingCard() {

    }

    public void robPlayer(RobPlayerInfo victim) {

    }
    
    public void updateState(String currState) {
    	switch (currState) {
    		case "FirstRound":
    			state = new State.Setup();
    			break;
    		case "SecondRound":
    			state = new State.Setup();
    			break;
    		case "Rolling":
    			state = new State.Rolling();
    			break;
    		case "Robbing":
    			state = new State.MoveRobber();
    			break;
    		case "Playing":
    			state = new State.Playing();
    			break;
    		case "Discarding":
    			state = new State.Discarding();
    			break;
			default:
				break;
    		
    	}
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Populator && arg instanceof ModelFacade) {
//            ModelFacade facade = (ModelFacade) arg;
//            initFromModel(facade);
//    		currState = facade.getState();
//    		updateState(currState);
        }
        
    }

}
