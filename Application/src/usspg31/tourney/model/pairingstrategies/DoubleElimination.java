package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Tournament;

public class DoubleElimination implements PairingStrategy {

	@Override
	public ArrayList<Pairing> generatePairing(Tournament tournament) {
		ArrayList<Pairing> result = new ArrayList<>();

		return null;
	}

	@Override
	public String getName() {
		return "Doppel-K.O.-System (not implemented)";
	}

}
