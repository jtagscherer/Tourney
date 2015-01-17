package usspg31.tourney.tests.model.filemanagement;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import javax.xml.parsers.FactoryConfigurationError;

import org.junit.Test;

import usspg31.tourney.model.Event;
import usspg31.tourney.model.Event.UserFlag;
import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.model.filemanagement.FileSaver;

public class TestFileSaver {
    @Test
    public void testSaveTournamentModule() {
	File file = new File("module.ttm");
	TournamentModule module = TestFileManagement.getDummyTournamentModule();
	FileSaver.saveTournamentModuleToFile(module, "module");
	assertTrue(file.exists());

	/* Repeat it initialized */
	FileSaver.saveTournamentModuleToFile(module, "module");
	assertTrue(file.exists());

	/* Repeat it with file ending */
	FileSaver.saveTournamentModuleToFile(module, "module.ttm");
	assertTrue(file.exists());

	assertTrue(file.exists());
	file.delete();
    }

    @Test
    public void testUninitialized() {
	Event event = TestFileManagement.getDummyEvent();
	try {
	    FileSaver.saveEventToFile(event, "test.tef");
	} catch (Exception e) {
	    fail(e.getMessage());
	}

	File file = new File("test.tef");
	assertTrue(file.exists());
	file.delete();
    }

    @Test
    public void testFolderCreation() {
	Event event = TestFileManagement.getDummyEvent();
	FileSaver.saveEventToFile(event, "parent/test.tef");

	File file = new File("parent/test.tef");
	assertTrue(file.exists());
	file.delete();
	new File("parent").delete();
    }

    @Test
    public void testTournamentUserFlag() {
	Event event = TestFileManagement.getDummyEvent();
	event.setUserFlag(UserFlag.TOURNAMENT_EXECUTION);
	event.setExecutedTournament(event.getTournaments().get(0));
	try {
	    FileSaver.saveEventToFile(event, "test.tef");
	} catch (Exception e) {
	    fail(e.getMessage());
	}

	File file = new File("test.tef");
	assertTrue(file.exists());
	file.delete();
    }

    @Test
    public void testNonexistentRuleSet() {
	Event event = TestFileManagement.getDummyEvent();
	event.getTournaments().get(0).setRuleSet(null);
	try {
	    FileSaver.saveEventToFile(event, "test.tef");
	} catch (Exception e) {
	    fail(e.getMessage());
	}

	File file = new File("test.tef");
	assertTrue(file.exists());
	file.delete();
    }

    @Test(expected = FactoryConfigurationError.class)
    public void testDamagedParserConfiguration() {
	System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
		"invalid");
	FileSaver.initialize();
    }
}
