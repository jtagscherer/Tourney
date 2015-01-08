package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Random;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;

public class FreeForAll implements PairingStrategy {

	@Override
	public ArrayList<Pairing> generatePairing(Tournament value) {
		ArrayList<Pairing> result = new ArrayList<>();
		Random randomGenerator = new Random();
		ArrayList<Player> randomList = new ArrayList<>();
		Pairing partResult;
		randomList.addAll(value.getRegisteredPlayers());
		int randomNumber;
		if (value.getRuleSet().getPhaseList().get(value.getRounds().size() - 1)
				.getPairingMethod().getClass() != FreeForAll.class) {
			while (randomList.size() > 0) {
				partResult = new Pairing();

				for (int i = 0; i < value.getRuleSet().getNumberOfOpponents(); i++) {
					randomNumber = randomGenerator.nextInt(randomList.size());
					partResult.getOpponents().add(randomList.get(randomNumber));
					randomList.remove(randomNumber);
				}
				result.add(partResult);
			}
		}
		return result;
	}
}
