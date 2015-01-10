package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Tournament;

public interface PairingStrategy {

	/**
	 * produce a complete tournament round
	 * 
	 * @param tournament
	 *            for which the pairings are generated
	 * @return the finished pairings for one round
	 */
	public ArrayList<Pairing> generatePairing(Tournament tournament);

}
