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
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentAdministrator;
import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.model.TournamentRound;
import usspg31.tourney.model.filemanagement.TournamentDocument;
import usspg31.tourney.model.pairingstrategies.SwissSystem;

public class TestTournamentDocument {
    private TournamentDocument document;

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
	this.document = new TournamentDocument(documentBuilder.newDocument());
    }

    @Test
    public void testMetaData() {
	Tournament tournament = new Tournament();
	tournament.setName("TestTournament");

	this.document.appendMetaData(tournament);

	assertEquals("TestTournament", this.document.getTournamentName());
    }

    @Test
    public void testAdministratorList() {
	// Add a list of event administrators
	ObservableList<TournamentAdministrator> tournamentAdministrators = FXCollections
		.observableArrayList();
	TournamentAdministrator administrator = new TournamentAdministrator();
	administrator.setFirstName("Aaron");
	administrator.setLastName("Admin");
	administrator.setMailAdress("a.admin@mail.com");
	administrator.setPhoneNumber("123456");
	tournamentAdministrators.add(administrator);

	this.document.appendAdministratorList(tournamentAdministrators);

	ArrayList<TournamentAdministrator> readAdministrators = this.document
		.getTournamentAdministrators();

	// Test the administrator list
	assertEquals(1, readAdministrators.size());
	assertEquals("Aaron", readAdministrators.get(0).getFirstName());
	assertEquals("Admin", readAdministrators.get(0).getLastName());
	assertEquals("a.admin@mail.com", readAdministrators.get(0)
		.getMailAddress());
	assertEquals("123456", readAdministrators.get(0).getPhoneNumber());
    }

    @Test
    public void testPlayerList() {
	ObservableList<Player> playerList = FXCollections.observableArrayList();
	Player player = new Player();
	player.setFirstName("Peter");
	player.setLastName("Player");
	player.setId("2");
	player.setMailAdress("p.player@mail.com");
	player.setNickName("pplayer");
	player.setStartingNumber("3");
	player.setPayed(true);
	player.setDisqualified(false);
	playerList.add(player);

	// Write the data to the document
	this.document.appendPlayerList(playerList,
		TournamentDocument.PlayerListType.ATTENDANT_PLAYERS);
	this.document.appendPlayerList(playerList,
		TournamentDocument.PlayerListType.REGISTERED_PLAYERS);
	this.document.appendPlayerList(playerList,
		TournamentDocument.PlayerListType.REMAINING_PLAYERS);

	// Read the data from the document
	ArrayList<Player> playerArrayList = new ArrayList<Player>(playerList);

	assertEquals(
		1,
		this.document.getPlayerList(
			TournamentDocument.PlayerListType.ATTENDANT_PLAYERS,
			playerArrayList).size());
	assertEquals(
		1,
		this.document.getPlayerList(
			TournamentDocument.PlayerListType.REMAINING_PLAYERS,
			playerArrayList).size());
	assertEquals(
		1,
		this.document.getPlayerList(
			TournamentDocument.PlayerListType.REGISTERED_PLAYERS,
			playerArrayList).size());

	assertEquals(
		"2",
		this.document
			.getPlayerList(
				TournamentDocument.PlayerListType.ATTENDANT_PLAYERS,
				playerArrayList).get(0).getId());
	assertEquals(
		"2",
		this.document
			.getPlayerList(
				TournamentDocument.PlayerListType.REMAINING_PLAYERS,
				playerArrayList).get(0).getId());
	assertEquals(
		"2",
		this.document
			.getPlayerList(
				TournamentDocument.PlayerListType.REGISTERED_PLAYERS,
				playerArrayList).get(0).getId());

	Player readPlayer = this.document.getPlayerList(
		TournamentDocument.PlayerListType.ATTENDANT_PLAYERS,
		playerArrayList).get(0);
	assertEquals("Peter", readPlayer.getFirstName());
	assertEquals("Player", readPlayer.getLastName());
	assertEquals("p.player@mail.com", readPlayer.getMailAddress());
	assertEquals("pplayer", readPlayer.getNickName());
	assertEquals("3", readPlayer.getStartingNumber());
	assertEquals(true, readPlayer.hasPayed());
	assertEquals(false, readPlayer.isDisqualified());
    }

    @Test
    public void testTournamentRounds() {
	ObservableList<TournamentRound> tournamentRounds = FXCollections
		.observableArrayList();

	// Create a player list
	ArrayList<Player> playerList = new ArrayList<Player>();
	Player player = new Player();
	player.setFirstName("Peter");
	player.setLastName("Player");
	player.setId("2");
	player.setMailAdress("p.player@mail.com");
	player.setNickName("pplayer");
	player.setStartingNumber("3");
	player.setPayed(true);
	player.setDisqualified(false);
	playerList.add(player);

	// Add a new tournament round
	TournamentRound round = new TournamentRound(1);
	Pairing pairing = new Pairing();
	pairing.getOpponents().add(player);
	PlayerScore score = new PlayerScore();
	score.setPlayer(player);
	score.getScore().add(10);
	pairing.getScoreTable().add(score);
	round.getPairings().add(pairing);
	tournamentRounds.add(round);

	this.document.appendTournamentRounds(tournamentRounds);

	ArrayList<TournamentRound> readTournamentRounds = this.document
		.getTournamentRounds(playerList);

	assertEquals(1, readTournamentRounds.size());

	TournamentRound readRound = readTournamentRounds.get(0);

	assertEquals(1, readRound.getRoundNumber());

	assertEquals(1, readRound.getPairings().size());

	Pairing readPairing = readRound.getPairings().get(0);
	assertEquals(1, readPairing.getOpponents().size());

	Player readOpponent = readPairing.getOpponents().get(0);
	assertEquals("Peter", readOpponent.getFirstName());
	assertEquals("Player", readOpponent.getLastName());
	assertEquals("p.player@mail.com", readOpponent.getMailAddress());
	assertEquals("pplayer", readOpponent.getNickName());
	assertEquals("3", readOpponent.getStartingNumber());
	assertEquals("2", readOpponent.getId());
	assertEquals(true, readOpponent.hasPayed());
	assertEquals(false, readOpponent.isDisqualified());

	assertEquals(1, readPairing.getScoreTable().size());
	PlayerScore readScore = readPairing.getScoreTable().get(0);

	assertEquals("2", readScore.getPlayer().getId());

	assertEquals(1, readScore.getScore().size());
	assertEquals(10, readScore.getScore().get(0).intValue());
    }

    @Test
    public void testTournamentRules() {
	// Create some new tournament rules
	TournamentModule module = new TournamentModule();
	PossibleScoring primaryScores = new PossibleScoring();
	primaryScores.setPriority(1);
	primaryScores.getScores().put("Victory", 3);
	primaryScores.getScores().put("Tie", 2);
	primaryScores.getScores().put("Defeat", 1);
	module.getPossibleScores().add(primaryScores);
	GamePhase phase = new GamePhase();
	phase.setPhaseNumber(1);
	phase.setCutoff(16);
	phase.setPairingMethod(new SwissSystem());
	phase.setRoundCount(4);
	phase.setRoundDuration(Duration.ofMinutes(10));
	module.getPhaseList().add(phase);

	// Write the rules to the document
	this.document.appendTournamentRules(module);

	// Read the rules from the document
	TournamentModule readTournamentModule = this.document
		.getTournamentRules();

	// Test the possible scorings in the rule set
	assertEquals(1, readTournamentModule.getPossibleScores().size());
	PossibleScoring readScoring = readTournamentModule.getPossibleScores()
		.get(0);
	assertEquals(1, readScoring.getPriority());
	assertEquals(3, readScoring.getScores().get("Victory").intValue());
	assertEquals(2, readScoring.getScores().get("Tie").intValue());
	assertEquals(1, readScoring.getScores().get("Defeat").intValue());

	// Test the game phases in the rule set
	assertEquals(1, readTournamentModule.getPhaseList().size());
	GamePhase readPhase = readTournamentModule.getPhaseList().get(0);
	assertEquals(1, readPhase.getPhaseNumber());
	assertEquals(16, readPhase.getCutoff());
	assertTrue(readPhase.getPairingMethod() instanceof SwissSystem);
	assertEquals(4, readPhase.getRoundCount());
	assertEquals(Duration.ofMinutes(10), readPhase.getRoundDuration());
    }

    @Test
    public void testSetDocument() {
	this.document.setDocument(null);
	assertTrue(this.document.getDocument() == null);
    }
}
