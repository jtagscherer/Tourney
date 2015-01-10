package usspg31.tourney.tests.model.filemanagement;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;

import usspg31.tourney.model.Player;
import usspg31.tourney.model.filemanagement.PlayerDocument;

public class TestPlayerDocument {
	private PlayerDocument document;

	@Before
	public void init() {
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		this.document = new PlayerDocument(documentBuilder.newDocument());
	}

	@Test
	public void testPlayerList() {
		ObservableList<Player> playerList = FXCollections.observableArrayList();

		// Add a player to the list
		Player player = new Player();
		player.setFirstName("Peter");
		player.setLastName("Player");
		player.setId("2");
		player.setMailAdress("p.player@mail.com");
		player.setNickName("pplayer");
		player.setStartingNumber("3");
		player.setPayed(true);
		player.setDisqualified(false);

		playerList.add(player);

		// Write the data to the document
		this.document.appendPlayerList(playerList);

		// Read the data back from the document
		ArrayList<Player> readPlayers = this.document.getPlayerList();

		// Test the data
		assertEquals(1, readPlayers.size());

		Player readPlayer = readPlayers.get(0);
		assertEquals("Peter", readPlayer.getFirstName());
		assertEquals("Player", readPlayer.getLastName());
		assertEquals("p.player@mail.com", readPlayer.getMailAddress());
		assertEquals("pplayer", readPlayer.getNickName());
		assertEquals("3", readPlayer.getStartingNumber());
		assertEquals(true, readPlayer.getPayed());
		assertEquals(false, readPlayer.getDisqualified());
	}

}
