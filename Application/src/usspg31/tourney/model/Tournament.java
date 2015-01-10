package usspg31.tourney.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Tournament {

	private final ObservableList<Player> registeredPlayers;
	private final ObservableList<Player> attendingPlayers;
	private final ObservableList<Player> remainingPlayers;
	private final ObservableList<TournamentRound> rounds;
	private final StringProperty name;
	private final ObservableList<PlayerScore> scoreTable;
	private final ObservableList<TournamentAdministrator> administrators;
	private final StringProperty id;
	private final ObjectProperty<TournamentModule> ruleSet;

	public Tournament() {
		this.registeredPlayers = FXCollections.observableArrayList();
		this.attendingPlayers = FXCollections.observableArrayList();
		this.remainingPlayers = FXCollections.observableArrayList();
		this.rounds = FXCollections.observableArrayList();
		this.name = new SimpleStringProperty("");
		this.scoreTable = FXCollections.observableArrayList();
		this.administrators = FXCollections.observableArrayList();
		this.id = new SimpleStringProperty("");
		this.ruleSet = new SimpleObjectProperty<TournamentModule>();
	}

	public ObservableList<Player> getRegisteredPlayers() {
		return this.registeredPlayers;
	}

	public ObservableList<Player> getAttendingPlayers() {
		return this.attendingPlayers;
	}

	public ObservableList<Player> getRemainingPlayers() {
		return this.remainingPlayers;
	}

	public ObservableList<TournamentRound> getRounds() {
		return this.rounds;
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

	public ObservableList<PlayerScore> getScoreTable() {
		return this.scoreTable;
	}

	public ObservableList<TournamentAdministrator> getAdministrators() {
		return this.administrators;
	}

	public String getId() {
		return this.id.get();
	}

	public void setId(String id) {
		this.id.set(id);
	}

	public TournamentModule getRuleSet() {
		return this.ruleSet.get();
	}

	public void setRuleSet(TournamentModule value) {
		this.ruleSet.set(value);
	}

	public ObjectProperty<TournamentModule> ruleSetProperty() {
		return this.ruleSet;
	}
}
