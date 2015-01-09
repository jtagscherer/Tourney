package usspg31.tourney.model;

import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlayerScore implements Comparable<PlayerScore> {

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

	@Override
	public int compareTo(PlayerScore o) {
		// TODO Auto-generated method stub
		if (this.getScore().get(0) > o.getScore().get(0)) {
			return 1;
		} else if (this.getScore().get(0) == (o.getScore().get(0))) {
			return 0;
		} else {
			return -1;
		}
	}

	public class PlayerScoreComparator implements Comparator<PlayerScore> {

		@Override
		public int compare(PlayerScore o1, PlayerScore o2) {
			// TODO Auto-generated method stub
			if (o1.getScore().get(0) > o2.getScore().get(0)) {
				return 1;
			} else if (o1.getScore().get(0) == o2.getScore().get(0)) {
				return 0;
			} else {
				return -1;
			}
		}
	}

}
