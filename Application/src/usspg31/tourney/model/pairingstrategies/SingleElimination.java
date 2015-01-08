package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Tournament;

public class SingleElimination implements PairingStrategy {

	@Override
	public ArrayList<Pairing> generatePairing(Tournament tournament) {
		// TODO Auto-generated method stub
		ArrayList<Pairing> result = new ArrayList<>();
		if (PairingHelper.isFirstInPhase(tournament.getRounds().size(),
				tournament, PairingHelper.findPhase(tournament.getRounds()
						.size(), tournament))) {

		}
		return null;
	}
}
