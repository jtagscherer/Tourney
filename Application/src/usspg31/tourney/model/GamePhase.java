package usspg31.tourney.model;

import java.time.Duration;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import usspg31.tourney.model.pairingstrategies.PairingStrategy;
import usspg31.tourney.model.pairingstrategies.SingleElimination;

/**
 * Represents one of the phases a tournament is made up of
 */
public class GamePhase {

    private final IntegerProperty cutoff;
    private final ObjectProperty<PairingStrategy> pairingMethod;
    private final IntegerProperty roundCount;
    private final IntegerProperty phaseNumber;
    private final ObjectProperty<Duration> roundDuration;
    private final IntegerProperty numberOfOpponents;

    /**
     * Create a new game phase and initialize all its properties
     */
    public GamePhase() {
	this.cutoff = new SimpleIntegerProperty();
	this.roundCount = new SimpleIntegerProperty();
	this.phaseNumber = new SimpleIntegerProperty();
	this.pairingMethod = new SimpleObjectProperty<PairingStrategy>(
		new SingleElimination());
	this.roundDuration = new SimpleObjectProperty<Duration>(Duration.ZERO);
	this.numberOfOpponents = new SimpleIntegerProperty();
    }

    /**
     * Get the number of players left after ending this phase
     * 
     * @return Number of players left after ending this phase
     */
    public int getCutoff() {
	return this.cutoff.get();
    }

    /**
     * Set the number of players left after ending this phase
     * 
     * @param value
     *            New number of players left after ending this phase
     */
    public void setCutoff(int value) {
	this.cutoff.set(value);
    }

    /**
     * Get the cut off property of this game phase
     * 
     * @return Cut off property of this game phase
     */
    public IntegerProperty cutoffProperty() {
	return this.cutoff;
    }

    /**
     * Get the pairing method that is used in this game phase
     * 
     * @return Pairing method that is used in this game phase
     */
    public PairingStrategy getPairingMethod() {
	return this.pairingMethod.get();
    }

    /**
     * Set the pairing method to be used in this game phase
     * 
     * @param value
     *            New pairing method to be used in this game phase
     */
    public void setPairingMethod(PairingStrategy value) {
	this.pairingMethod.set(value);
    }

    /**
     * Get the pairing method used in this game phase
     * 
     * @return Current pairing method used in this game phase
     */
    public ObjectProperty<PairingStrategy> pairingMethodProperty() {
	return this.pairingMethod;
    }

    /**
     * Get the number of rounds in this game phase
     * 
     * @return Number of rounds in this game phase
     */
    public int getRoundCount() {
	return this.roundCount.get();
    }

    /**
     * Set the number of rounds in this game phase
     * 
     * @param value
     *            New number of rounds in this game phase
     */
    public void setRoundCount(int value) {
	this.roundCount.set(value);
    }

    /**
     * Get the round count property of this game phase
     * 
     * @return Round count property of this game phase
     */
    public IntegerProperty roundCountProperty() {
	return this.roundCount;
    }

    /**
     * Get the number of this phase
     * 
     * @return Current number of this phase
     */
    public int getPhaseNumber() {
	return this.phaseNumber.get();
    }

    /**
     * Set the number of this phase
     * 
     * @param value
     *            New number of this phase
     */
    public void setPhaseNumber(int value) {
	this.phaseNumber.set(value);
    }

    /**
     * Get the phase number property of this game phase
     * 
     * @return Phase number property of this game phase
     */
    public IntegerProperty phaseNumberProperty() {
	return this.phaseNumber;
    }

    /**
     * Get the duration of a round in this game phase
     * 
     * @return Current round duration in this game phase
     */
    public Duration getRoundDuration() {
	return this.roundDuration.get();
    }

    /**
     * Set the duration of a round in this game phase
     * 
     * @param value
     *            New round duration in this game phase
     */
    public void setRoundDuration(Duration value) {
	this.roundDuration.set(value);
    }

    /**
     * Get the round duration property of this game phase
     * 
     * @return Round duration property of this game phase
     */
    public ObjectProperty<Duration> roundDurationProperty() {
	return this.roundDuration;
    }

    /**
     * Get the number of opponents in a pairing in this game phase
     * 
     * @return Current number of opponents in a pairing in this game phase
     */
    public int getNumberOfOpponents() {
	return this.numberOfOpponents.get();
    }

    /**
     * Set the number of opponents in a pairing in this game phase
     * 
     * @param value
     *            New number of opponents in a pairing in this game phase
     */
    public void setNumberOfOpponents(int value) {
	this.numberOfOpponents.set(value);
    }

    /**
     * Get the number of opponents property in this game phase
     * 
     * @return Number of opponents property in this game phase
     */
    public IntegerProperty numberOfOpponentsProperty() {
	return this.numberOfOpponents;
    }
}
