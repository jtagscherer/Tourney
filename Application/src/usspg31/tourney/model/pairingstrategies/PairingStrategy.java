package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Tournament;

public interface PairingStrategy {

	public ArrayList<Pairing> generatePairing(Tournament tournament);

}
