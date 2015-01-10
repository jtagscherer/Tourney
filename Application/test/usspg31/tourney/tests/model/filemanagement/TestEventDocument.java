package usspg31.tourney.tests.model.filemanagement;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;

import usspg31.tourney.model.Event;
import usspg31.tourney.model.EventAdministrator;
import usspg31.tourney.model.filemanagement.EventDocument;

public class TestEventDocument {
	private EventDocument document;

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
		this.document = new EventDocument(documentBuilder.newDocument());
	}

	@Test
	public void testMetaData() {
		Event event = new Event();
		// Set some meta data for the new event
		event.setName("TestEvent");
		event.setLocation("TestLocation");
		event.setStartDate(LocalDate.of(2015, 2, 1));
		event.setEndDate(LocalDate.of(2015, 2, 3));
		event.setEventPhase(Event.EventPhase.PRE_REGISTRATION);

		// Add a list of event administrators
		ArrayList<EventAdministrator> eventAdministrators = new ArrayList<EventAdministrator>();
		EventAdministrator administrator = new EventAdministrator();
		administrator.setFirstName("Aaron");
		administrator.setLastName("Admin");
		administrator.setMailAdress("a.admin@mail.com");
		administrator.setPhoneNumber("123456");
		eventAdministrators.add(administrator);

		event.getAdministrators().setAll(eventAdministrators);

		// Write all data to the document
		this.document.appendMetaData(event);

		assertEquals("asdf", "asdf");
	}

}
