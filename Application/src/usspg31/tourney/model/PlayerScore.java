package usspg31.tourney.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlayerScore {

	private Player player;
	private final ObservableList<Integer> score;

	public PlayerScore() {
		this.player = new Player();
		this.score = FXCollections.observableArrayList();
	}

	public Player getPlayer() {
		return this.player;
	}

	public void setPlayer(Player value) {
		this.player = value;
	}

	public ObservableList<Integer> getScore() {
		return this.score;
	}
}
