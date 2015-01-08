package usspg31.tourney.model;

import java.util.ArrayList;

public class StaticMethods {
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

}
