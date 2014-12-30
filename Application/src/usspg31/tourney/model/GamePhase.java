package usspg31.tourney.model;

import java.time.Duration;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import usspg31.tourney.model.pairingstrategies.PairingStrategy;

public class GamePhase {

	private final IntegerProperty cutoff;
	private final ObjectProperty<PairingStrategy> pairingMethod;
	private final IntegerProperty roundCount;
	private final IntegerProperty phaseNumber;
	private final ObjectProperty<Duration> roundDuration;

}
