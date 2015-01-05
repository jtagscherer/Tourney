package usspg31.tourney.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Pairing {

	private final ObservableList<Player> opponents;
	private final ObservableList<PlayerScore> scoreTable;

	public Pairing() {
		this.opponents = FXCollections.observableArrayList();
		this.scoreTable = FXCollections.observableArrayList();
	}
}
