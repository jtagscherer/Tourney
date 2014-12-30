package usspg31.tourney.model.pairingstrategies;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Tournament;

public interface PairingStrategy {

	public Pairing generatePairing(Tournament tournament);

}
