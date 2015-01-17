package usspg31.tourney.tests.pairingstrategies;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.RoundGeneratorFactory;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.model.pairingstrategies.SingleElimination;
import usspg31.tourney.model.pairingstrategies.SwissSystem;

public class TestPairingStrategies {

    @Test
    public void testSingleElimination() {
	Tournament testTournament = new Tournament();
	TournamentModule testTournamentMoudle = new TournamentModule();
	GamePhase testGamePhase = new GamePhase();

	testGamePhase.setNumberOfOpponents(2);
	testGamePhase.setPhaseNumber(0);
	testGamePhase.setRoundCount(10);
	testGamePhase.setPairingMethod(new SingleElimination());

	testTournamentMoudle.getPhaseList().add(testGamePhase);
	testTournament.setRuleSet(testTournamentMoudle);

	ArrayList<Player> testRemainingPlayer = new ArrayList<>();
	Player testPlayer;
	for (int i = 0; i < 8; i++) {
	    testPlayer = new Player();
	    testPlayer.setId(Integer.toString(i));

	    testRemainingPlayer.add(testPlayer);
	}
	testTournament.getRemainingPlayers().addAll(testRemainingPlayer);

	RoundGeneratorFactory testRoundGenerator = new RoundGeneratorFactory();
	testTournament.getRounds().add(
		testRoundGenerator.generateRound(testTournament));

	// the second player in each pairing wins
	Integer[] prePlayerScore = { 0, 4 };
	for (Pairing testPairing : testTournament.getRounds().get(0)
		.getPairings()) {
	    for (PlayerScore testPlayerScore : testPairing.getScoreTable()) {
		testPlayerScore.getScore().addAll(prePlayerScore);
	    }
	}
	testTournament.getRounds().add(
		testRoundGenerator.generateRound(testTournament));
	/*
	 * assertEquals(testTournament.getRounds().get(0).getPairings().get(0)
	 * .getOpponents().get(1), testTournament.getRounds().get(1)
	 * .getPairings().get(0).getOpponents().get(0));
	 */

    }

    @Test
    public void testSwissSystem() {
	Tournament testTournament = new Tournament();
	TournamentModule testTournamentMoudle = new TournamentModule();
	GamePhase testGamePhase = new GamePhase();

	testGamePhase.setNumberOfOpponents(2);
	testGamePhase.setPhaseNumber(0);
	testGamePhase.setRoundCount(10);
	testGamePhase.setPairingMethod(new SwissSystem());

	testTournamentMoudle.getPhaseList().add(testGamePhase);
	testTournament.setRuleSet(testTournamentMoudle);

	ArrayList<Player> testRemainingPlayer = new ArrayList<>();
	Player testPlayer;
	for (int i = 0; i < 8; i++) {
	    testPlayer = new Player();
	    testPlayer.setId(Integer.toString(i));

	    testRemainingPlayer.add(testPlayer);
	}
	testTournament.getRemainingPlayers().addAll(testRemainingPlayer);

	RoundGeneratorFactory testRoundGenerator = new RoundGeneratorFactory();
	testTournament.getRounds().add(
		testRoundGenerator.generateRound(testTournament));
	PlayerScore playerScore;

	for (Player playlerForScore : testTournament.getRemainingPlayers()) {
	    playerScore = new PlayerScore();
	    playerScore.setPlayer(playlerForScore);
	    playerScore.getScore().add(
		    Integer.parseInt(playlerForScore.getId()) * 10);

	    testTournament.getScoreTable().add(playerScore);
	}

	testTournament.getRounds().add(
		testRoundGenerator.generateRound(testTournament));
	// Player with the highest id is first player added to the second round
	assertEquals(testTournament.getRounds().get(1).getPairings().get(0)
		.getOpponents().get(0), testTournament.getRemainingPlayers()
		.get(testTournament.getRemainingPlayers().size() - 1));

	// Player at second position have to play against him
	assertEquals(testTournament.getRounds().get(1).getPairings().get(0)
		.getOpponents().get(1), testTournament.getRemainingPlayers()
		.get(testTournament.getRemainingPlayers().size() - 2));

    }
}
