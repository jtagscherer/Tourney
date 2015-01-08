package usspg31.tourney.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TournamentRound {

	private final ObservableList<Pairing> pairings;
	private int roundNumber;

	/**
	 * @param roundNumber
	 */
	public TournamentRound(int roundNumber) {
		this.pairings = FXCollections.observableArrayList();
		this.roundNumber = roundNumber;
	}

	public ObservableList<Pairing> getPairings() {
		return this.pairings;
	}

	public int getRoundNumber() {
		return this.roundNumber;
	}

	public void setRoundNumber(int value) {
		this.roundNumber = value;
	}
}
