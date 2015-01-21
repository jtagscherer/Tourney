package usspg31.tourney.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a single round in a game phase of a tournament
 */
public class TournamentRound {

    private final ObservableList<Pairing> pairings;
    private int roundNumber;

    /**
     * Create a new tournament round and initialize all its properties
     * 
     * @param roundNumber
     *            Number of this tournament round
     */
    public TournamentRound(int roundNumber) {
        this.pairings = FXCollections.observableArrayList();
        this.roundNumber = roundNumber;
    }

    /**
     * Get all pairings in this tournament round
     * 
     * @return A list of all pairings in this tournament round
     */
    public ObservableList<Pairing> getPairings() {
        return this.pairings;
    }

    /**
     * Get the number of this tournament round
     * 
     * @return Current number of this tournament round
     */
    public int getRoundNumber() {
        return this.roundNumber;
    }

    /**
     * Set the number of this tournament round
     * 
     * @param value
     *            New number of this tournament round
     */
    public void setRoundNumber(int value) {
        this.roundNumber = value;
    }
}
