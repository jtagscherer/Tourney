package usspg31.tourney.model.filemanagement;

import javafx.collections.ObservableList;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

		this.rootElement = this.document.createElement("players");
		this.document.appendChild(this.rootElement);
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
