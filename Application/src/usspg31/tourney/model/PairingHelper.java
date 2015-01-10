package usspg31.tourney.model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import usspg31.tourney.model.PlayerScore.PlayerScoreComparator;

public class PairingHelper {
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
		PlayerScoreComparator comparator = null;
		FXCollections.sort(pairing.getScoreTable(), comparator);
		return pairing.getScoreTable().get(pairing.getScoreTable().size() - 1)
				.getPlayer();
	}
}
