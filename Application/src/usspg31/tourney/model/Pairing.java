package usspg31.tourney.model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Pairing {

	private final ObservableList<Player> opponents;
	private final ObservableList<PlayerScore> scoreTable;

	public Pairing() {
		this.opponents = FXCollections.observableArrayList();
		this.scoreTable = FXCollections.observableArrayList();
	}

	public Pairing(ArrayList<Player> opponents) {
		this.opponents = FXCollections.observableArrayList();
		this.opponents.setAll(opponents);
		this.scoreTable = FXCollections.observableArrayList();
	}

	public ObservableList<Player> getOpponents() {
		return this.opponents;
	}

	public ObservableList<PlayerScore> getScoreTable() {
		return this.scoreTable;
	}
}
