package usspg31.tourney.model.filemanagement;

import java.util.ArrayList;

import javafx.collections.ObservableList;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import usspg31.tourney.model.Player;

/**
 * An XML document that represents a list of players
 * 
 * @author Jan Tagscherer
 */
public class PlayerDocument {
	private Document document;
	private Element rootElement;

	/**
	 * Create a new players document
	 * 
	 * @param document
	 *            XML document source to be used
	 */
	public PlayerDocument(Document document) {
		this.document = document;

		if (this.document.getFirstChild() == null) {
			this.rootElement = this.document.createElement("players");
			this.document.appendChild(this.rootElement);
		}
	}

	/**
	 * Append all players to the document
	 * 
	 * @param players
	 *            List of players to be appended
	 */
	public void appendPlayerList(ObservableList<Player> players) {
		for (Player player : players) {
			// Add a new player
			Element playerElement = this.document.createElement("player");
			Attr playerId = this.document.createAttribute("id");
			playerId.setValue(player.getId());
			playerElement.setAttributeNode(playerId);
			this.rootElement.appendChild(playerElement);

			// Add the name of the player
			Element name = this.document.createElement("name");
			playerElement.appendChild(name);

			Element firstName = this.document.createElement("first-name");
			name.appendChild(firstName);
			firstName.appendChild(this.document.createTextNode(player
					.getFirstName()));

			Element lastName = this.document.createElement("last-name");
			name.appendChild(lastName);
			lastName.appendChild(this.document.createTextNode(player
					.getLastName()));

			// Add the mail address of the player
			Element mailAddress = this.document.createElement("mail-address");
			playerElement.appendChild(mailAddress);
			mailAddress.appendChild(this.document.createTextNode(player
					.getMailAddress()));

			// Add the nick name of the player
			Element nickname = this.document.createElement("nickname");
			playerElement.appendChild(nickname);
			nickname.appendChild(this.document.createTextNode(player
					.getNickName()));

			// Add the starting number of the player
			Element startingNumber = this.document
					.createElement("starting-number");
			playerElement.appendChild(startingNumber);
			startingNumber.appendChild(this.document.createTextNode(player
					.getStartingNumber()));

			// Add the payment status of the player
			Element payed = this.document.createElement("payed");
			playerElement.appendChild(payed);
			payed.appendChild(this.document.createTextNode(String
					.valueOf(player.getPayed())));

			// Add the disqualification status of the player
			Element disqualified = this.document.createElement("disqualified");
			playerElement.appendChild(disqualified);
			disqualified.appendChild(this.document.createTextNode(String
					.valueOf(player.getDisqualified())));
		}
	}

	/**
	 * Extract the list of players from this document
	 * 
	 * @return List of players to be extracted
	 */
	public ArrayList<Player> getPlayerList() {
		ArrayList<Player> players = new ArrayList<Player>();

		Node playersNode = this.document.getElementsByTagName("players")
				.item(0);

		for (Node player : FileLoader.getChildNodesByTag(playersNode, "player")) {
			Player newPlayer = new Player();

			newPlayer.setId(player.getAttributes().getNamedItem("id")
					.getTextContent());

			Node playerName = FileLoader.getFirstChildNodeByTag(player, "name");
			newPlayer.setFirstName(FileLoader.getFirstChildNodeByTag(
					playerName, "first-name").getTextContent());
			newPlayer.setLastName(FileLoader.getFirstChildNodeByTag(playerName,
					"last-name").getTextContent());

			newPlayer.setMailAdress(FileLoader.getFirstChildNodeByTag(player,
					"mail-address").getTextContent());
			newPlayer.setNickName(FileLoader.getFirstChildNodeByTag(player,
					"nickname").getTextContent());
			newPlayer.setStartingNumber(FileLoader.getFirstChildNodeByTag(
					player, "starting-number").getTextContent());

			newPlayer.setPayed(Boolean.parseBoolean(FileLoader
					.getFirstChildNodeByTag(player, "payed").getTextContent()));
			newPlayer.setDisqualified(Boolean.parseBoolean(FileLoader
					.getFirstChildNodeByTag(player, "disqualified")
					.getTextContent()));

			players.add(newPlayer);
		}

		return players;
	}

	/**
	 * Get the source document of this event document
	 * 
	 * @return Source document of this event document
	 */
	public Document getDocument() {
		return this.document;
	}

	/**
	 * Set the source document of this event document
	 * 
	 * @param document
	 *            Source document of this event document
	 */
	public void setDocument(Document document) {
		this.document = document;
	}
}
