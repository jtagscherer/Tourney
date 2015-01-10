package usspg31.tourney.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TournamentModule {

	private final StringProperty name;
	private final StringProperty description;
	private final ObservableList<PossibleScoring> possibleScorings;
	private final ObservableList<GamePhase> phaseList;

	public TournamentModule() {
		this.name = new SimpleStringProperty("");
		this.description = new SimpleStringProperty("");
		this.possibleScorings = FXCollections.observableArrayList();
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

	public ObservableList<PossibleScoring> getPossibleScores() {
		return this.possibleScorings;
	}

	public ObservableList<GamePhase> getPhaseList() {
		return this.phaseList;
	}

}
