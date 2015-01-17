package usspg31.tourney.tests.model.filemanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;

import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.model.filemanagement.TournamentModuleDocument;
import usspg31.tourney.model.pairingstrategies.SwissSystem;

public class TestTournamentModuleDocument {
    private TournamentModuleDocument document;

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
	this.document = new TournamentModuleDocument(
		documentBuilder.newDocument());
    }

    @Test
    public void testMetaData() {
	TournamentModule module = new TournamentModule();
	module.setName("TestTournamentModule");
	module.setDescription("This is a test module.");

	this.document.appendMetaData(module);

	assertEquals("TestTournamentModule", this.document.getName());
	assertEquals("This is a test module.", this.document.getDescription());
    }

    @Test
    public void testPossibleScores() {
	ObservableList<PossibleScoring> possibleScorings = FXCollections
		.observableArrayList();

	PossibleScoring primaryScores = new PossibleScoring();
	primaryScores.setPriority(1);
	primaryScores.getScores().put("Victory", 3);
	primaryScores.getScores().put("Tie", 2);
	primaryScores.getScores().put("Defeat", 1);
	possibleScorings.add(primaryScores);

	// Write the list of possible scorings
	this.document.appendPossibleScores(possibleScorings);

	// Read the list of possible scorings
	ArrayList<PossibleScoring> readScorings = this.document
		.getPossibleScores();

	// Test the scorings
	assertEquals(1, readScorings.size());
	PossibleScoring readScoring = readScorings.get(0);
	assertEquals(1, readScoring.getPriority());
	assertEquals(3, readScoring.getScores().get("Victory").intValue());
	assertEquals(2, readScoring.getScores().get("Tie").intValue());
	assertEquals(1, readScoring.getScores().get("Defeat").intValue());
    }

    @Test
    public void testTournamentPhases() {
	ObservableList<GamePhase> phases = FXCollections.observableArrayList();

	GamePhase phase = new GamePhase();
	phase.setPhaseNumber(1);
	phase.setCutoff(16);
	phase.setPairingMethod(new SwissSystem());
	phase.setRoundCount(4);
	phase.setRoundDuration(Duration.ofMinutes(10));
	phases.add(phase);

	// Write the list of game phases
	this.document.appendTournamentPhases(phases);

	// Read the list of game phases
	ArrayList<GamePhase> readPhases = this.document.getTournamentPhases();

	// Test the game phases
	assertEquals(1, readPhases.size());
	GamePhase readPhase = readPhases.get(0);
	assertEquals(1, readPhase.getPhaseNumber());
	assertEquals(16, readPhase.getCutoff());
	assertTrue(readPhase.getPairingMethod() instanceof SwissSystem);
	assertEquals(4, readPhase.getRoundCount());
	assertEquals(Duration.ofMinutes(10), readPhase.getRoundDuration());
    }
}
