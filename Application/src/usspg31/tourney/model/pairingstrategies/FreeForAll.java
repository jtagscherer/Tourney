package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Random;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;

public class FreeForAll implements PairingStrategy {

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
		Random randomGenerator = new Random();
		ArrayList<Player> randomList = new ArrayList<>();
		Pairing partResult;
		randomList.addAll(tournament.getRegisteredPlayers());
		int randomNumber;

		// checks if this is the first round in the tournament
		if (tournament.getRounds().size() == 0) {
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
			// checks if the round is the first in his gamephase
		} else if (PairingHelper
				.findPhase(tournament.getRounds().size() - 1, tournament)
				.getPairingMethod().getClass() != FreeForAll.class) {
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

				if (!PairingHelper.checkForSimiliarPairings(partResult,
						tournament)) {
					// TODO finish checking
					result.add(partResult);
				}
			}
		}
		for (int i = 0; i < randomList.size(); i++) {
			// TODO implement bye for players
		}
		return result;
	}

	@Override
	public String getName() {
		return "Jeder gegen Jeden";
	}

}
