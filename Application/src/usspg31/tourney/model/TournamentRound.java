package usspg31.tourney.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TournamentRound {

	private final ObservableList<Pairing> pairings;
	private final int roundNumber;

	/**
	 * @param roundNumber
	 */
	public TournamentRound(int roundNumber) {
		this.pairings = FXCollections.observableArrayList();
		this.roundNumber = roundNumber;
	}
}
