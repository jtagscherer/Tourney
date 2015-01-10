package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Random;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;

public class SingleElimination implements PairingStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * usspg31.tourney.model.pairingstrategies.PairingStrategy#generatePairing
	 * (usspg31.tourney.model.Tournament)
	 */
	@Override
	public ArrayList<Pairing> generatePairing(Tournament tournament) {

		ArrayList<Pairing> result = new ArrayList<>();
		Pairing partResult = new Pairing();
		// checks if this round is the first round in the gamphase and modify
		// the pairing strategy for this round
		if (PairingHelper.isFirstInPhase(tournament.getRounds().size(),
				tournament, PairingHelper.findPhase(tournament.getRounds()
						.size(), tournament))) {
			Random randomGenerator = new Random();
			ArrayList<Player> randomList = new ArrayList<>();
			randomList.addAll(tournament.getRegisteredPlayers());
			int randomNumber;

			while (randomList.size() >= PairingHelper.findPhase(
					tournament.getRounds().size(), tournament)
					.getNumberOfOpponents()) {
				partResult = new Pairing();

				for (int i = 0; i < PairingHelper.findPhase(
						tournament.getRounds().size(), tournament)
						.getNumberOfOpponents(); i++) {
					randomNumber = randomGenerator.nextInt(randomList.size());
					partResult.getOpponents().add(randomList.get(randomNumber));
					randomList.remove(randomNumber);
				}
				result.add(partResult);
			}
		} else {
			ArrayList<Pairing> tmp = new ArrayList<>();
			tmp.addAll(tournament.getRounds()
					.get(tournament.getRounds().size() - 1).getPairings());
			while (tmp.size() > PairingHelper.findPhase(
					tournament.getRounds().size(), tournament)
					.getNumberOfOpponents()) {
				partResult = new Pairing();

				for (int i = 0; i < PairingHelper.findPhase(
						tournament.getRounds().size(), tournament)
						.getNumberOfOpponents(); i++) {
					partResult.getOpponents().add(
							PairingHelper.identifyWinner(tmp.get(0)));
					tmp.remove(0);
				}
			}

		}
		return result;
	}
}
