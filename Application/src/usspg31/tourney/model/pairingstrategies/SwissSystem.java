package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Collections;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.Tournament;

public class SwissSystem implements PairingStrategy {

	@Override
	public ArrayList<Pairing> generatePairing(Tournament tournament) {
		// TODO Auto-generated method stub
		ArrayList<Pairing> result = new ArrayList<>();
		Pairing partResult;
		ArrayList<PlayerScore> mergedScoreTable = new ArrayList<>();

		mergedScoreTable = PairingHelper.mergeScoreRemainingPlayer(tournament);
		Collections.sort(mergedScoreTable);
		while (mergedScoreTable.size() >= PairingHelper.findPhase(
				tournament.getRounds().size(), tournament)
				.getNumberOfOpponents()) {
			partResult = new Pairing();

			for (int i = 0; i < PairingHelper.findPhase(
					tournament.getRounds().size(), tournament)
					.getNumberOfOpponents(); i++) {
				if (i == 0) {
					partResult.getOpponents().add(
							mergedScoreTable.get(mergedScoreTable.size() - 1)
									.getPlayer());
					mergedScoreTable.remove(mergedScoreTable.size() - 1);
				} else {
					partResult.getOpponents().add(
							mergedScoreTable.get(mergedScoreTable.size() - i)
									.getPlayer());
					if (i == PairingHelper.findPhase(
							tournament.getRounds().size(), tournament)
							.getNumberOfOpponents() - 1) {
						if (PairingHelper.checkForSimiliarPairings(partResult,
								tournament)) {

							// TODO finish the procedure for similar pairings
							result.add(partResult);
						} else {
							result.add(partResult);
						}
					}
				}

			}
		}
		return result;
	}
}
