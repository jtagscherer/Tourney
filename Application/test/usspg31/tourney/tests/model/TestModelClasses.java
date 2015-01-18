package usspg31.tourney.tests.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import org.junit.Test;

import usspg31.tourney.model.Administrator;
import usspg31.tourney.model.Bye;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Person;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.tests.model.filemanagement.TestFileManagement;

public class TestModelClasses {

    @Test
    public void testTournament() {
	Event event = TestFileManagement.getDummyEvent();
	Tournament tournament = event.getTournaments().get(0);

	assertTrue(tournament.nameProperty() != null);
	assertTrue(tournament.ruleSetProperty() != null);

	Tournament clonedTournament = (Tournament) tournament.clone();
	assertEquals(clonedTournament.getName(), tournament.getName());
	assertFalse(clonedTournament == tournament);
    }

    @Test
    public void testEvent() {
	Event event = TestFileManagement.getDummyEvent();

	assertTrue(event.nameProperty() != null);
	assertTrue(event.startDateProperty() != null);
	assertTrue(event.endDateProperty() != null);
	assertTrue(event.locationProperty() != null);
	assertTrue(event.eventPhaseProperty() != null);
	assertTrue(event.userFlagProperty() != null);
	assertTrue(event.executedTournamentProperty() != null);
    }

    @Test
    public void testPlayer() {
	Event event = TestFileManagement.getDummyEvent();
	Player player = event.getRegisteredPlayers().get(0);

	assertTrue(player.nickNameProperty() != null);
	assertTrue(player.startingNumberProperty() != null);
	assertTrue(player.payedProperty() != null);
	assertTrue(player.disqualifiedProperty() != null);
	assertTrue(player.idProperty() != null);

	Player clonedPlayer = (Player) player.clone();
	assertEquals(player.getFirstName(), player.getFirstName());
	assertFalse(clonedPlayer == player);
    }

    @Test
    public void testGamePhase() {
	TournamentModule module = TestFileManagement.getDummyTournamentModule();
	GamePhase phase = module.getPhaseList().get(0);

	assertTrue(phase.cutoffProperty() != null);
	assertTrue(phase.pairingMethodProperty() != null);
	assertTrue(phase.roundCountProperty() != null);
	assertTrue(phase.phaseNumberProperty() != null);
	assertTrue(phase.roundDurationProperty() != null);
	assertTrue(phase.numberOfOpponentsProperty() != null);
    }

    @Test
    public void testPairing() {
	Player testPlayer = new Player();
	testPlayer.setFirstName("John");
	ArrayList<Player> opponents = new ArrayList<Player>();
	opponents.add(testPlayer);

	Pairing pairing = new Pairing(opponents);
	assertEquals("John", pairing.getOpponents().get(0).getFirstName());
    }

    @Test
    public void testPerson() {
	Person player = new Player();

	assertTrue(player.firstNameProperty() != null);
	assertTrue(player.lastNameProperty() != null);
	assertTrue(player.mailAdressProperty() != null);
    }

    @Test
    public void testPossibleScoring() {
	PossibleScoring scoring = new PossibleScoring();

	ObservableMap<String, Integer> scores = FXCollections
		.observableHashMap();
	scoring.setScores(scores);
	assertTrue(scoring.getScores() == scores);

	assertTrue(scoring.priorityProperty() != null);
    }

    @Test
    public void testTournamentModule() {
	TournamentModule module = TestFileManagement.getDummyTournamentModule();

	assertTrue(module.nameProperty() != null);
	assertTrue(module.descriptionProperty() != null);
    }

    @Test
    public void testAdministrator() {
	Administrator admin = new Administrator();

	assertTrue(admin.phoneNumberProperty() != null);
    }

    @Test
    public void testBye() {
	Bye bye = new Bye();

	assertTrue(bye != null);
    }
}
