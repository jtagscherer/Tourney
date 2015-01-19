package usspg31.tourney.model.filemanagement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import usspg31.tourney.model.Event.UserFlag;

/**
 * Wrapper class for a XML document that saves the meta data attached to an
 * event which can be extracted using its methods
 * 
 * @author Jan Tagscherer
 */
public class MetaDocument {
    private Document document;
    private Element rootElement;

    /**
     * Create a new meta document
     * 
     * @param document
     *            XML document source to be used
     */
    public MetaDocument(Document document) {
	this.document = document;

	if (this.document.getFirstChild() == null) {
	    this.rootElement = this.document.createElement("meta");
	    this.document.appendChild(this.rootElement);
	}
    }

    /**
     * Set the user flag of the represented event
     * 
     * @param userFlag
     *            New user flag of the represented event
     */
    public void setUserFlag(UserFlag userFlag) {
	Element userFlagElement = this.document.createElement("user-flag");
	this.rootElement.appendChild(userFlagElement);
	userFlagElement.appendChild(this.document.createTextNode(userFlag
		.toString()));
    }

    /**
     * Get the current user flag of the represented event
     * 
     * @return Current user flag of the represented event
     */
    public UserFlag getUserFlag() {
	Node metaNode = this.document.getElementsByTagName("user-flag").item(0);

	if (metaNode == null) {
	    return null;
	}

	if (!metaNode.getTextContent().equals("")) {
	    return UserFlag.valueOf(metaNode.getTextContent());
	} else {
	    return null;
	}
    }

    /**
     * Set the ID of the currently executed tournament in the represented event
     * 
     * @param id
     *            New executed tournament ID
     */
    public void setExecutedTournamentId(String id) {
	Element executedTournamentElement = this.document
		.createElement("executed-tournament-id");
	this.rootElement.appendChild(executedTournamentElement);
	executedTournamentElement.appendChild(this.document.createTextNode(id));
    }

    /**
     * Get the ID of the currently executed tournament in the represented event
     * 
     * @return New executed tournament ID
     */
    public String getExecutedTournamentId() {
	Node metaNode = this.document.getElementsByTagName(
		"executed-tournament-id").item(0);
	if (metaNode != null) {
	    return metaNode.getTextContent();
	} else {
	    return null;
	}
    }

    /**
     * Set the number of registrators in the represented event
     * 
     * @param number
     *            New number of registrators
     */
    public void setNumberOfRegistrators(int number) {
	Element numberOfRegistratorsElement = this.document
		.createElement("number-of-registrators");
	this.rootElement.appendChild(numberOfRegistratorsElement);
	numberOfRegistratorsElement.appendChild(this.document
		.createTextNode(String.valueOf(number)));
    }

    /**
     * Get the current number of registrators in the represented event
     * 
     * @return Current number of registrators
     */
    public int getNumberOfRegistrators() {
	Node metaNode = this.document.getElementsByTagName(
		"number-of-registrators").item(0);

	if (metaNode == null) {
	    return 0;
	}

	if (!metaNode.getTextContent().equals("")) {
	    return Integer.valueOf(metaNode.getTextContent());
	} else {
	    return 0;
	}
    }

    /**
     * Get the underlying document of this meta document
     * 
     * @return The actual XML document
     */
    public Document getDocument() {
	return this.document;
    }
}
