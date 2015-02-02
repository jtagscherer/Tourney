package usspg31.tourney.tests.pairingstrategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.RoundGeneratorFactory;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.model.TournamentRound;
import usspg31.tourney.model.pairingstrategies.FreeForAll;
import usspg31.tourney.model.pairingstrategies.ModifiedSwissSystem;
import usspg31.tourney.model.pairingstrategies.SingleElimination;
import usspg31.tourney.model.pairingstrategies.SpecialModifiedSwissSystem;
import usspg31.tourney.model.pairingstrategies.SwissSystem;

public class TestPairingStrategies {

    public void testSingleElimination() {
        Tournament testTournament = new Tournament();
        TournamentModule testTournamentModule = new TournamentModule();
        GamePhase testGamePhase = new GamePhase();
        PossibleScoring testPossibleScoring = new PossibleScoring();

        for (int i = 0; i < 2; i++) {
            testPossibleScoring.getScores().put(i + " Test", 10);
        }

        testGamePhase.setNumberOfOpponents(2);
        testGamePhase.setPhaseNumber(0);
        testGamePhase.setRoundCount(4);
        testGamePhase.setPairingMethod(new SingleElimination());

        testTournamentModule.getPossibleScores().add(testPossibleScoring);
        testTournamentModule.getPhaseList().add(testGamePhase);
        testTournament.setRuleSet(testTournamentModule);

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

        for (Pairing testPairing : testTournament.getRounds().get(0)
                .getPairings()) {
            for (PlayerScore testPlayerScore : testPairing.getScoreTable()) {
                if (testPairing.getScoreTable().indexOf(testPlayerScore) == 0) {

                    for (int i = 0; i < 2; i++) {
                        testPlayerScore.getScore().add(i, 0);
                    }
                } else {
                    for (int i = 0; i < 2; i++) {
                        testPlayerScore.getScore().add(i, 10);
                    }
                }
            }
        }

        testTournament.getRounds().add(
                testRoundGenerator.generateRound(testTournament));

        assertEquals(testTournament.getRounds().get(0).getPairings().size(), 4);

    }

    @Test
    public void testSwissSystem() {
        Tournament testTournament = new Tournament();
        TournamentModule testTournamentModule = new TournamentModule();
        GamePhase testGamePhase = new GamePhase();

        PossibleScoring testPossibleScoring = new PossibleScoring();

        for (int i = 0; i < 2; i++) {
            testPossibleScoring.getScores().put(i + " Test", 10);

        }

        testGamePhase.setNumberOfOpponents(2);
        testGamePhase.setPhaseNumber(0);
        testGamePhase.setRoundCount(10);
        testGamePhase.setPairingMethod(new SwissSystem());

        testTournamentModule.getPossibleScores().add(testPossibleScoring);
        testTournamentModule.getPhaseList().add(testGamePhase);
        testTournament.setRuleSet(testTournamentModule);

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
        PlayerScore stuffDogePlayerScore;
        for (Pairing stuffDogePairing : testTournament.getRounds().get(0)
                .getPairings()) {
            for (Player stuffDogePlayer : stuffDogePairing.getOpponents()) {
                stuffDogePlayerScore = new PlayerScore();
                stuffDogePlayerScore.setPlayer(stuffDogePlayer);
                stuffDogePlayerScore.getScore().add(0,
                        Integer.valueOf((stuffDogePlayer.getId())) * 10);
                stuffDogePlayerScore.getScore().add(1, 10);

                stuffDogePairing.getScoreTable().add(stuffDogePlayerScore);
            }
        }

        testTournament.getRounds().add(
                testRoundGenerator.generateRound(testTournament));

        for (TournamentRound testOutputRound : testTournament.getRounds()) {
            System.out.println(testOutputRound.getRoundNumber() + ": "
                    + testOutputRound.getPairings().size());
        }
        // Player with the highest id is first player added to the second round
        assertEquals(testTournament.getRounds().get(1).getPairings().size(),
                testTournament.getRounds().get(0).getPairings().size());

    }

    public void testFreeForAllComplete() {
        Tournament testTournament = new Tournament();
        TournamentModule testTournamentModule = new TournamentModule();
        GamePhase testGamePhase = new GamePhase();

        PossibleScoring testPossibleScoring = new PossibleScoring();

        for (int i = 0; i < 2; i++) {
            testPossibleScoring.getScores().put(i + " Test", 10);

        }
        testTournamentModule.getPossibleScores().add(testPossibleScoring);

        testGamePhase.setNumberOfOpponents(2);
        testGamePhase.setPhaseNumber(0);
        testGamePhase.setRoundCount(1);
        testGamePhase.setPairingMethod(new FreeForAll());

        testTournamentModule.getPhaseList().add(testGamePhase);

        testGamePhase = new GamePhase();

        testGamePhase.setNumberOfOpponents(2);
        testGamePhase.setPhaseNumber(1);
        testGamePhase.setRoundCount(1);
        testGamePhase.setPairingMethod(new FreeForAll());

        testTournamentModule.getPhaseList().add(testGamePhase);

        testGamePhase = new GamePhase();

        testGamePhase.setNumberOfOpponents(2);
        testGamePhase.setPhaseNumber(2);
        testGamePhase.setRoundCount(1);
        testGamePhase.setPairingMethod(new FreeForAll());

        testTournamentModule.getPhaseList().add(testGamePhase);

        testTournament.setRuleSet(testTournamentModule);

        ArrayList<Player> testRemainingPlayer = new ArrayList<>();
        Player testPlayer;
        for (int i = 0; i < 8; i++) {
            testPlayer = new Player();
            testPlayer.setId(Integer.toString(i));

            testRemainingPlayer.add(testPlayer);
        }
        testTournament.getRemainingPlayers().addAll(testRemainingPlayer);

        RoundGeneratorFactory roundGenerator = new RoundGeneratorFactory();

        testTournament.getRounds().add(
                roundGenerator.generateRound(testTournament));
        testTournament.getRounds().add(
                roundGenerator.generateRound(testTournament));
        testTournament.getRounds().add(
                roundGenerator.generateRound(testTournament));

        assertTrue(testTournament.getRounds().get(0).getPairings().size() * 2 == 8);
        assertTrue(testTournament.getRounds().get(0).getPairings().size() == testTournament
                .getRounds().get(2).getPairings().size());

    }

    @Test
    public void testModifiedSwissSystem() {
        Tournament testTournament = new Tournament();
        TournamentModule testTournamentModule = new TournamentModule();
        GamePhase testGamePhase = new GamePhase();

        PossibleScoring testPossibleScoring = new PossibleScoring();

        for (int i = 0; i < 2; i++) {
            testPossibleScoring.getScores().put(i + " Test", 10);

        }

        testGamePhase.setNumberOfOpponents(2);
        testGamePhase.setPhaseNumber(0);
        testGamePhase.setRoundCount(0);
        testGamePhase.setPairingMethod(new ModifiedSwissSystem());
        testTournamentModule.getPhaseList().add(testGamePhase);

        testGamePhase.setNumberOfOpponents(2);
        testGamePhase.setPhaseNumber(1);
        testGamePhase.setRoundCount(2);
        testGamePhase.setPairingMethod(new ModifiedSwissSystem());
        testTournamentModule.getPhaseList().add(testGamePhase);

        testTournamentModule.getPossibleScores().add(testPossibleScoring);
        testTournament.setRuleSet(testTournamentModule);

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
        assertEquals(testTournament.getRounds().get(0).getPairings().size(),
                testTournament.getRounds().get(1).getPairings().size());
    }

    @Test
    public void testSpecialSwissSystem() {
        Tournament testTournament = new Tournament();
        TournamentModule testTournamentModule = new TournamentModule();
        GamePhase testGamePhase = new GamePhase();

        PossibleScoring testPossibleScoring = new PossibleScoring();

        for (int i = 0; i < 2; i++) {
            testPossibleScoring.getScores().put(i + " Test", 10);

        }

        testGamePhase.setNumberOfOpponents(2);
        testGamePhase.setPhaseNumber(0);
        testGamePhase.setRoundCount(2);
        testGamePhase.setPairingMethod(new SpecialModifiedSwissSystem());
        testTournamentModule.getPhaseList().add(testGamePhase);

        testTournament.setRuleSet(testTournamentModule);

        ArrayList<Player> testRemainingPlayer = new ArrayList<>();
        Player testPlayer;
        for (int i = 0; i < 4; i++) {
            testPlayer = new Player();
            testPlayer.setId(Integer.toString(i));
            testPlayer.setStartingNumber(Integer.toString(i));

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

        assertEquals(testTournament.getRounds().get(0).getPairings().size(),
                testTournament.getRounds().get(1).getPairings().size());
    }
}
