package usspg31.tourney.model;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class TournamentModule {

	private final StringProperty name;
	private final StringProperty description;
	private final ObservableMap<String, Integer> possibleScores;
	private final ObservableList<GamePhase> phaseList;

}
