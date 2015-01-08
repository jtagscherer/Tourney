package usspg31.tourney.model;

import java.util.HashMap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class TournamentModule {

	private final StringProperty name;
	private final StringProperty description;
	private final ObservableMap<String, Integer> possibleScores;
	private final ObservableList<GamePhase> phaseList;

	public TournamentModule() {
		this.name = new SimpleStringProperty();
		this.description = new SimpleStringProperty();
		this.possibleScores = FXCollections
				.observableMap(new HashMap<String, Integer>());
		this.phaseList = FXCollections.observableArrayList();
	}
}
