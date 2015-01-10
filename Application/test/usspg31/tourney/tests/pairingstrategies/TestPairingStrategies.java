package usspg31.tourney.tests.pairingstrategies;

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

public class TestPairingStrategies {

	@Test
	public void testSingleEliminationFirstRoundInTournament() {
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

		// assertEquals();
		Integer[] prePlayerScore = { 0, 4 };
		for (Pairing testPairing : testTournament.getRounds().get(0)
				.getPairings()) {
			for (PlayerScore testPlayerScore : testPairing.getScoreTable()) {
				testPlayerScore.getScore().addAll(prePlayerScore);
			}
		}
		testTournament.getRounds().add(
				testRoundGenerator.generateRound(testTournament));

	}
}
