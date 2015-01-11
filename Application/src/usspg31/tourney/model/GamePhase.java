package usspg31.tourney.model;

import java.time.Duration;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import usspg31.tourney.model.pairingstrategies.PairingStrategy;
import usspg31.tourney.model.pairingstrategies.SingleElimination;

public class GamePhase {

	private final IntegerProperty cutoff;
	private final ObjectProperty<PairingStrategy> pairingMethod;
	private final IntegerProperty roundCount;
	private final IntegerProperty phaseNumber;
	private final ObjectProperty<Duration> roundDuration;
	private final IntegerProperty numberOfOpponents;

	public GamePhase() {
		this.cutoff = new SimpleIntegerProperty();
		this.roundCount = new SimpleIntegerProperty();
		this.phaseNumber = new SimpleIntegerProperty();
		this.pairingMethod = new SimpleObjectProperty<PairingStrategy>(new SingleElimination());
		this.roundDuration = new SimpleObjectProperty<Duration>(Duration.ZERO);
		this.numberOfOpponents = new SimpleIntegerProperty();
	}

	public int getCutoff() {
		return this.cutoff.get();
	}

	public void setCutoff(int value) {
		this.cutoff.set(value);
	}

	public IntegerProperty cutoffProperty() {
		return this.cutoff;
	}

	public PairingStrategy getPairingMethod() {
		return this.pairingMethod.get();
	}

	public void setPairingMethod(PairingStrategy value) {
		this.pairingMethod.set(value);
	}

	public ObjectProperty<PairingStrategy> pairingMethodProperty() {
		return this.pairingMethod;
	}

	public int getRoundCount() {
		return this.roundCount.get();
	}

	public void setRoundCount(int value) {
		this.roundCount.set(value);
	}

	public IntegerProperty roundCountProperty() {
		return this.roundCount;
	}

	public int getPhaseNumber() {
		return this.phaseNumber.get();
	}

	public void setPhaseNumber(int value) {
		this.phaseNumber.set(value);
	}

	public IntegerProperty phaseNumberProperty() {
		return this.phaseNumber;
	}

	public Duration getRoundDuration() {
		return this.roundDuration.get();
	}

	public void setRoundDuration(Duration value) {
		this.roundDuration.set(value);
	}

	public ObjectProperty<Duration> roundDurationProperty() {
		return this.roundDuration;
	}

	public int getNumberOfOpponents() {
		return this.numberOfOpponents.get();
	}

	public void setNumberOfOpponents(int value) {
		this.numberOfOpponents.set(value);
	}

	public IntegerProperty numberOfOpponentsProperty() {
		return this.numberOfOpponents;
	}
}
