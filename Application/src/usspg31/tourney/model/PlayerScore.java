package usspg31.tourney.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlayerScore {

	private final Player player;
	private final ObservableList<Integer> score;

	public PlayerScore() {
		this.player = new Player();
		this.score = FXCollections.observableArrayList();
	}
}
