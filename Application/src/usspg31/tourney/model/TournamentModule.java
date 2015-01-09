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
	private final ObservableList<ObservableMap<String, Integer>> possibleScores;
	private final ObservableList<GamePhase> phaseList;

	@SuppressWarnings("unchecked")
	public TournamentModule() {
		this.name = new SimpleStringProperty();
		this.description = new SimpleStringProperty();
		this.possibleScores = FXCollections.observableArrayList(FXCollections
				.observableMap(new HashMap<String, Integer>()));
		this.phaseList = FXCollections.observableArrayList();

	}

	public String getName() {
		return this.name.get();
	}

	public void setName(String value) {
		this.name.set(value);
	}

	public StringProperty nameProperty() {
		return this.name;
	}

	public String getDescription() {
		return this.description.get();
	}

	public void setDescription(String value) {
		this.description.set(value);
	}

	public StringProperty descriptionProperty() {
		return this.description;
	}

	public ObservableList<ObservableMap<String, Integer>> getPossibleScores() {
		return this.possibleScores;
	}

	public ObservableList<GamePhase> getPhaseList() {
		return this.phaseList;
	}

}
