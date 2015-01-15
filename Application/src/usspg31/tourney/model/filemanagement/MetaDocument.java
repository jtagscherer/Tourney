package usspg31.tourney.model.filemanagement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implements a parser for meta data for a saved event
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
	 * Set the meta data string
	 * 
	 * @param metaData
	 *            New meta data string to be set
	 */
	public void setMetaData(String metaData) {
		this.rootElement.appendChild(this.document.createTextNode(metaData));
	}

	/**
	 * Get the meta data string of this document
	 * 
	 * @return Current meta data string
	 */
	public String getMetaData() {
		return this.document.getElementsByTagName("meta").item(0)
				.getTextContent();
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
}
