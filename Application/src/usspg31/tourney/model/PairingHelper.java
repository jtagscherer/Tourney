package usspg31.tourney.model;

import java.util.ArrayList;

import javafx.collections.FXCollections;

public class PairingHelper {

    /**
     * identify the phase in which the round takes place
     * 
     * if this method return a null object then their are no more rounds left in
     * the tournament
     * 
     * @param roundcount
     *            identify the round
     * @param value
     *            the tournament in which the round take place
     * @return GamePhase in which the round is located
     */
    public static GamePhase findPhase(int roundcount, Tournament value) {
        for (GamePhase actPhase : value.getRuleSet().getPhaseList()) {
            roundcount -= actPhase.getRoundCount();

            if (roundcount < 0) {
                return actPhase;
            }

        }
        return null;
    }

    /**
     * checks if the round is the first in the phase
     * 
     * @param roundcount
     *            which round is getting checked
     * @param value
     *            in which tournament the round takes place
     * @param chkPhase
     *            the gamephase of the round
     * @return if the round is the first in his gamephase
     */
    public static boolean isFirstInPhase(int roundcount, Tournament value,
            GamePhase chkPhase) {
        if (findPhase(roundcount - 1, value) != chkPhase) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * produce the scores for the remaining players in the tournament
     * 
     * @param tournament
     *            source of the players and scores
     * @return the unsorted scores for the remaining players in the tournament
     */
    public static ArrayList<PlayerScore> mergeScoreRemainingPlayer(
            Tournament tournament) {
        ArrayList<PlayerScore> remainingPlayerScore = new ArrayList<>();
        for (Player player : tournament.getRemainingPlayers()) {
            for (PlayerScore chkScore : tournament.getScoreTable()) {
                if (player == chkScore.getPlayer()) {
                    remainingPlayerScore.add(chkScore);
                }
            }
        }
        return remainingPlayerScore;
    }

    /**
     * identifies the winner of a pairing
     * 
     * @param pairing
     *            the pairing for whom the winnier will be identified
     * @return the winner of the pairing
     */
    public static ArrayList<Player> identifyWinner(Pairing pairing) {
        Pairing sortClone = new Pairing();
        ArrayList<Player> winningPlayers = new ArrayList<>();
        boolean isAlsoWinner = true;
        int count = 0;

        sortClone.getScoreTable().addAll(pairing.getScoreTable());
        FXCollections.sort(sortClone.getScoreTable());

        winningPlayers.add(sortClone.getScoreTable()
                .get(sortClone.getScoreTable().size() - 1).getPlayer());
        while (isAlsoWinner) {
            if (sortClone
                    .getScoreTable()
                    .get(sortClone.getScoreTable().size() - 1 - count)
                    .compareTo(
                            sortClone.getScoreTable().get(
                                    sortClone.getScoreTable().size() - 1)) == 0) {
                winningPlayers.add(sortClone.getScoreTable()
                        .get(sortClone.getScoreTable().size() - 1 - count)
                        .getPlayer());
                System.out.println("winner");
            } else {
                isAlsoWinner = false;
            }
            if (count == sortClone.getScoreTable().size() - 1) {
                isAlsoWinner = false;
            } else {

                count++;
            }
        }

        return winningPlayers;

    }

    /**
     * checks the pairing if there were a similar one in a previous round
     * 
     * @param value
     * @param tournament
     * @return if there this pairing already take place in the tournament
     */
    public static boolean isThereASimilarPairings(Pairing value,
            Tournament tournament) {
        for (TournamentRound tRound : tournament.getRounds()) {
            for (Pairing tPairing : tRound.getPairings()) {
                if (tPairing.getOpponents().containsAll(value.getOpponents())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * generate an empty player score
     * 
     * @param opponent
     *            for which player the score is generated
     * @param numberOfScores
     * @return the player score
     */
    public static PlayerScore generateEmptyScore(Player opponent,
            Integer numberOfScores) {
        PlayerScore result = new PlayerScore();
        result.setPlayer(opponent);

        return result;
    }

    /**
     * 
     * 
     * @param pairing
     * @return
     */
    public static ArrayList<Player> identifyLoser(Pairing pairing) {
        ArrayList<Player> result = new ArrayList<>();

        result.addAll(pairing.getOpponents());

        result.remove(PairingHelper.identifyWinner(pairing));

        return result;
    }

    public static boolean cutOffAfterRound(int roundcount, Tournament value) {
        if (PairingHelper.isFirstInPhase(roundcount + 1, value,
                PairingHelper.findPhase(roundcount + 1, value))) {
            return true;
        } else {
            return false;
        }
    }
}
