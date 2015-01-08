package usspg31.tourney.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Tournament {

	private final ObservableList<Player> registeredPlayers;
	private final ObservableList<Player> attendingPlayers;
	private final ObservableList<TournamentRound> rounds;
	private final StringProperty name;
	private final ObservableList<PlayerScore> scoreTable;
	private final ObservableList<TournamentAdministrator> administrators;

	public Tournament() {
		this.registeredPlayers = FXCollections.observableArrayList();
		this.attendingPlayers = FXCollections.observableArrayList();
		this.rounds = FXCollections.observableArrayList();
		this.name = new SimpleStringProperty();
		this.scoreTable = FXCollections.observableArrayList();
		this.administrators = FXCollections.observableArrayList();
	}

	public ObservableList<Player> getRegisteredPlayers() {
		return this.registeredPlayers;
	}

	public ObservableList<Player> getAttendingPlayers() {
		return this.attendingPlayers;
	}

	public ObservableList<TournamentRound> getRound() {
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
}
