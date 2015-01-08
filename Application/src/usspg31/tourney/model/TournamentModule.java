package usspg31.tourney.model;

import java.util.HashMap;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
	private final IntegerProperty numberOfOpponents;

	public TournamentModule() {
		this.name = new SimpleStringProperty();
		this.description = new SimpleStringProperty();
		this.possibleScores = FXCollections
				.observableMap(new HashMap<String, Integer>());
		this.phaseList = FXCollections.observableArrayList();
		this.numberOfOpponents = new SimpleIntegerProperty();

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

	public ObservableMap<String, Integer> getPossibleScores() {
		return this.possibleScores;
	}

	public ObservableList<GamePhase> getPhaseList() {
		return this.phaseList;
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
