package usspg31.tourney.tests.pairingstrategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentRound;

public class TestPairingHelper {

    @Test
    public void testSimilarPairingCheck() {
        Tournament testTournament = new Tournament();
        TournamentRound testRound = new TournamentRound(0);
        Pairing testPairing1 = new Pairing();
        Player testPlayer;
        Pairing testPairing2 = new Pairing();

        for (int i = 0; i < 8; i++) {
            testPlayer = new Player();
            testPlayer.setId(Integer.toString(i));
            testPairing1.getOpponents().add(testPlayer);
            testPairing2.getOpponents().add(testPlayer);
        }

        testRound.getPairings().add(testPairing1);
        testTournament.getRounds().add(testRound);
        assertTrue(PairingHelper.isThereASimilarPairings(testPairing2,
                testTournament, null));
    }

    @Test
    public void testIdentifyLoser() {
        Pairing testPairing = new Pairing();

        PlayerScore tPlayerScore1 = new PlayerScore();
        PlayerScore tPlayerScore2 = new PlayerScore();

        Player tPlayer1 = new Player();
        tPlayer1.setId("1");
        tPlayerScore1.setPlayer(tPlayer1);
        tPlayerScore1.getScore().add(3);
        Player tPlayer2 = new Player();
        tPlayer2.setId("2");
        tPlayerScore2.setPlayer(tPlayer2);
        tPlayerScore2.getScore().add(0);

        testPairing.getOpponents().add(tPlayer1);
        testPairing.getScoreTable().add(tPlayerScore1);
        testPairing.getOpponents().add(tPlayer2);
        testPairing.getScoreTable().add(tPlayerScore2);

        assertTrue(PairingHelper.identifyLoser(testPairing).size() == 1);
    }

    @Test
    public void testComparePlayerScore() {
        PlayerScore score1 = new PlayerScore();
        PlayerScore score2 = new PlayerScore();
        PlayerScore score3 = new PlayerScore();
        PlayerScore score4 = new PlayerScore();

        score1.getScore().add(3);
        score2.getScore().add(3);
        score3.getScore().add(0);
        score4.getScore().add(10);

        assertEquals(score1.compareTo(score2), 0);
        assertEquals(score1.compareTo(score3), 1);
        assertEquals(score1.compareTo(score4), -1);

    }

    @Test
    public void testAddScoreToTournament() {
        PlayerScore score1 = new PlayerScore();
        PlayerScore score2 = new PlayerScore();
        PlayerScore score3 = new PlayerScore();
        PlayerScore score4 = new PlayerScore();
        Player testPlayer1 = new Player();
        Tournament testTournment = new Tournament();

        testPlayer1.setId("1");
        score1.getScore().add(3);
        score1.setPlayer(testPlayer1);
        score2.getScore().add(3);
        score3.getScore().add(0);
        score4.getScore().add(10);
        System.out.println(testTournment.getRounds().size());
        testTournment.addAScore(score1);

        assertEquals(testTournment.getScoreTable().get(0).getPlayer().getId(),
                score1.getPlayer().getId());
    }

}
