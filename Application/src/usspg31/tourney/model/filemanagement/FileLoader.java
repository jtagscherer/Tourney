package usspg31.tourney.model.filemanagement;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Contains static methods that load rule modules and events including
 * tournaments and players using XML files
 * 
 * @author Jan Tagscherer
 */
public class FileLoader {
	private static final Logger log = Logger.getLogger(FileSaver.class
			.getName());

	private static DocumentBuilder documentBuilder;
	private static boolean initialized = false;

	public static void initialize() {
		try {
			FileLoader.documentBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			FileLoader.initialized = true;
		} catch (ParserConfigurationException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Get all direct child nodes by tag
	 * 
	 * @param parent
	 *            Parent of the child nodes
	 * @param tag
	 *            Tag to be searched for
	 * @return A list of nodes with the specified tag
	 */
	public static ArrayList<Node> getChildNodesByTag(Node parent, String tag) {
		ArrayList<Node> childNodes = new ArrayList<Node>();

		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getParentNode() == parent
					&& childNode.getNodeName().equals(tag)) {
				childNodes.add(childNode);
			}
		}

		return childNodes;
	}

	/**
	 * Get the first direct child node by tag
	 * 
	 * @param parent
	 *            Parent of the child node
	 * @param tag
	 *            Tag to be searched for
	 * @return A list of nodes with the specified tag
	 */
	public static Node getFirstChildNodeByTag(Node parent, String tag) {
		ArrayList<Node> childNodes = FileLoader.getChildNodesByTag(parent, tag);

		if (childNodes.size() == 0) {
			return null;
		} else {
			return FileLoader.getChildNodesByTag(parent, tag).get(0);
		}
	}
}
