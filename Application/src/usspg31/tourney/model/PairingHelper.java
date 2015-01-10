package usspg31.tourney.model;

import java.util.ArrayList;

import javafx.collections.FXCollections;

public class PairingHelper {

	/**
	 * @param roundcount
	 * @param value
	 * @return the GamePhase
	 */
	public static GamePhase findPhase(int roundcount, Tournament value) {
		for (GamePhase actPhase : value.getRuleSet().getPhaseList()) {
			if (roundcount - actPhase.getRoundCount() < 1) {
				return actPhase;
			}
		}
		return null;
	}

	public static boolean isFirstInPhase(int roundcount, Tournament value,
			GamePhase chkPhase) {
		if (findPhase(roundcount - 1, value) != chkPhase) {
			return true;
		} else {
			return false;
		}
	}

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

	public static Player identifyWinner(Pairing pairing) {
		FXCollections.sort(pairing.getScoreTable());
		return pairing.getScoreTable().get(pairing.getScoreTable().size() - 1)
				.getPlayer();
	}

	/**
	 * checks the pairing if there were a similar one in a previous round
	 * 
	 * @param value
	 * @return
	 */
	public static boolean checkForSimiliarPairings(Pairing value,
			Tournament tournament) {
		// TODO implement checking for similar pairings in the same GamePhase
		return false;
	}
}
