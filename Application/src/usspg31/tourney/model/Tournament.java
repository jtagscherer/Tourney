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
	private final ObservableList<TournamentAdministrator> administrator;

	public Tournament() {
		this.registeredPlayers = FXCollections.observableArrayList();
		this.attendingPlayers = FXCollections.observableArrayList();
		this.rounds = FXCollections.observableArrayList();
		this.name = new SimpleStringProperty();
		this.scoreTable = FXCollections.observableArrayList();
		this.administrator = FXCollections.observableArrayList();
	}
}
