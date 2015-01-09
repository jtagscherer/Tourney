package usspg31.tourney.model.filemanagement;

import javafx.collections.ObservableList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import usspg31.tourney.model.Event;
import usspg31.tourney.model.EventAdministrator;
import usspg31.tourney.model.Tournament;

/**
 * An XML document that represents an event
 * 
 * @author Jan Tagscherer
 */
public class EventDocument {
	private Document document;
	private Element rootElement;

	/**
	 * Create a new event document
	 * 
	 * @param document
	 *            XML document source to be used
	 */
	public EventDocument(Document document) {
		this.document = document;

		this.rootElement = this.document.createElement("event");
		this.document.appendChild(this.rootElement);
	}

	/**
	 * Append all meta data to this document
	 * 
	 * @param event
	 *            Event to be used for reading its meta data
	 */
	public void appendMetaData(Event event) {
		Element meta = this.document.createElement("meta");
		this.rootElement.appendChild(meta);

		// Add the name
		Element name = this.document.createElement("name");
		meta.appendChild(name);
		name.appendChild(this.document.createTextNode(event.getName()));

		// Add the location
		Element location = this.document.createElement("location");
		meta.appendChild(location);
		location.appendChild(this.document.createTextNode(event.getLocation()));

		// Add the date
		Element date = this.document.createElement("date");
		meta.appendChild(date);

		Element startDate = this.document.createElement("start-date");
		date.appendChild(startDate);
		startDate.appendChild(this.document.createTextNode(event.getStartDate()
				.toString()));

		Element endDate = this.document.createElement("end-date");
		date.appendChild(endDate);
		endDate.appendChild(this.document.createTextNode(event.getEndDate()
				.toString()));

		// Add the event administrators
		Element eventAdministrators = this.document
				.createElement("event-administrators");
		meta.appendChild(eventAdministrators);

		for (EventAdministrator eventAdministrator : event.getAdministrators()) {
			Element administrator = this.document
					.createElement("administrator");
			eventAdministrators.appendChild(administrator);

			// Add the name of the current administrator
			Element administratorName = this.document.createElement("name");
			administrator.appendChild(administratorName);

			Element firstName = this.document.createElement("first-name");
			administratorName.appendChild(firstName);
			firstName.appendChild(this.document
					.createTextNode(eventAdministrator.getFirstName()));

			Element lastName = this.document.createElement("last-name");
			administratorName.appendChild(lastName);
			lastName.appendChild(this.document
					.createTextNode(eventAdministrator.getLastName()));

			// Add the mail address of the current administrator
			Element mailAddress = this.document.createElement("mail-address");
			administrator.appendChild(mailAddress);
			mailAddress.appendChild(this.document
					.createTextNode(eventAdministrator.getMailAddress()));

			// Add the phone number of the current administrator
			Element phoneNumber = this.document.createElement("phone-number");
			administrator.appendChild(phoneNumber);
			phoneNumber.appendChild(this.document
					.createTextNode(eventAdministrator.getPhoneNumber()));
		}
	}

	/**
	 * Append references to all tournaments to the document
	 * 
	 * @param tournaments
	 *            List of tournaments to be appended
	 */
	public void appendTournamentList(ObservableList<Tournament> tournaments) {
		Element tournamentsElement = this.document.createElement("tournaments");
		this.rootElement.appendChild(tournamentsElement);

		for (Tournament tournament : tournaments) {
			Element tournamentElement = this.document
					.createElement("tournament");
			tournamentsElement.appendChild(tournamentElement);

			// Add a reference to the current tournament using its ID
			Element tournamentId = this.document.createElement("tournament-id");
			tournamentElement.appendChild(tournamentId);
			tournamentId.appendChild(this.document.createTextNode(tournament
					.getId()));
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
