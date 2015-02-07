package client.network;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import shared.definitions.ResourceType;
import shared.locations.*;
import shared.models.DTO.ResourceListDTO;
import shared.models.DTO.params.*;

public class ServerProxyTest {
	
	private static ServerProxy proxy;

	@Test
	public void testLogin() {
		proxy = new ServerProxy();
		UserCredentials user = new UserCredentials("Sam", "sam");
		try {
			proxy.login(user);
		} catch (IOException e) {
			fail("Did not succeed");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testJoinGame() {
		JoinGameRequest game = new JoinGameRequest(0, "orange");
		try {
			proxy.joinGame(game);
		} catch (IOException e) {
			fail("Did no succeed");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRegisterNewUser() {
//		UserCredentials tiger = new UserCredentials("tiger", "shark");
//		try {
//			proxy.registerNewUser(tiger);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	@Test
	public void testListGames() {
		try {
			proxy.listGames();
		} catch (IOException e) {
			fail("Did no succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateGames() {
		CreateGameRequest game = new CreateGameRequest(true, true, true, "FirstGame");
		try {
			proxy.createGames(game);
		} catch (IOException e) {
			fail("Did no succeed");	
			e.printStackTrace();
		}
	}

	@Test
	public void testRetrieveCurrentState() {
		try {
			proxy.retrieveCurrentState(0);
		} catch (IOException e) {
			fail("Did no succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testSendChat() {
		SendChat message = new SendChat(0, "chat");
		try {
			proxy.sendChat(message);
		} catch (IOException e) {
			fail("Did no succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testOfferTrade() {
		ResourceListDTO list = new ResourceListDTO(1, 0, 0, 0, 2);
		OfferTrade trade = new OfferTrade(3, list, 2);
		try {
			proxy.offerTrade(trade);
		} catch (IOException e) {
			fail("Did no succeed");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAcceptTrade() {
		AcceptTrade accept = new AcceptTrade(2, true);
		try {
			proxy.acceptTrade(accept);
		} catch (IOException e) {
			fail("Did not succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testDiscardCards() {
		ResourceListDTO list = new ResourceListDTO(0, 1, 1, 3, 0);
		DiscardCards cards = new DiscardCards(0, list);
		try {
			proxy.discardCards(cards);
		} catch (IOException e) {
			fail("Did not succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testRollNumber() {
		RollNumber roll = new RollNumber(1,8);
		try {
			proxy.rollNumber(roll);
		} catch (IOException e) {
			fail("Did not succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testBuildRoad() {
		HexLocation loc = new HexLocation(-1, -1);
		EdgeLocation edge = new EdgeLocation(loc, EdgeDirection.NorthEast);
		BuildRoad road = new BuildRoad(1, edge, false);
		try {
			proxy.buildRoad(road);
		} catch (IOException e) {
			fail("Did not succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testBuildSettlement() {
		HexLocation hex = new HexLocation(-1, -1);
		VertexLocation vert = new VertexLocation(hex, VertexDirection.NorthEast);
		BuildSettlement sett = new BuildSettlement(2, vert, false);
		try {
			proxy.buildSettlement(sett);
		} catch (IOException e) {
			fail("Did not succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testBuildCity() {
		HexLocation hex = new HexLocation(-1, -1);
		VertexLocation vert = new VertexLocation(hex, VertexDirection.NorthEast);
		BuildCity city = new BuildCity(2, vert, false);
		try {
			proxy.buildCity(city);
		} catch (IOException e) {
			fail("Did not succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testMaritimeTrade() {
		fail("Not yet implemented");
	}

	@Test
	public void testRobPlayer() {
		fail("Not yet implemented");
	}

	@Test
	public void testFinishTurn() {
		FinishTurn turn = new FinishTurn(2);
		try {
			proxy.finishTurn(turn);
		} catch (IOException e) {
			fail("Did not succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testBuyDevCard() {
		BuyDevCard card = new BuyDevCard(2);
		try {
			proxy.buyDevCard(card);
		} catch (IOException e) {
			fail("Did not succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testPlaySoldier() {
		fail("Not yet implemented");
	}

	@Test
	public void testPlayYearOfPlenty() {
		YearOfPlenty card = new YearOfPlenty(3, ResourceType.ORE, ResourceType.SHEEP);
		try {
			proxy.playYearOfPlenty(card);
		} catch (IOException e) {
			fail("Did not succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testPlayRoadBuilding() {
		HexLocation hex = new HexLocation(0, 0);
		EdgeLocation edge = new EdgeLocation(hex, "NE");
		RoadBuilding card = new RoadBuilding(3, edge, false);
		try {
			proxy.playRoadBuilding(card);
		} catch (IOException e) {
			fail("Did not succeed");
			e.printStackTrace();
		}	}

	@Test
	public void testPlayMonopoly() {
		Monopoly monopoly = new Monopoly(0, ResourceType.BRICK);
		try {
			proxy.playMonopoly(monopoly);
		} catch (IOException e) {
			fail("Did not succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testPlayMonument() {
		Monument monument = new Monument(1);
		try {
			proxy.playMonument(monument);
		} catch (IOException e) {
			fail("Did not succeed");
			e.printStackTrace();
		}
	}

	@Test
	public void testListAITypes() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddAIPlayer() {
		AddAIRequest ai = new AddAIRequest()
	}

}