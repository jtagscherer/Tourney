package usspg31.tourney.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Accumulates the scores of a player
 */
public class PlayerScore implements Comparable<PlayerScore> {

    private Player player;
    private final ObservableList<Integer> score;

    /**
     * Create a new player score and initialize all its properties
     */
    public PlayerScore() {
        this.player = new Player();
        this.score = FXCollections.observableArrayList();
    }

    /**
     * Get the player this score belongs to
     * 
     * @return Current player this score belongs to
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Set the player this score belongs to
     * 
     * @param value
     *            New player this score belongs to
     */
    public void setPlayer(Player value) {
        this.player = value;
    }

    /**
     * Get a list of all scores in this player score
     * 
     * @return List of all scores in this player score
     */
    public ObservableList<Integer> getScore() {
        return this.score;
    }

    @Override
    public int compareTo(PlayerScore o) {
        // TODO Auto-generated method stub

        for (int i = 0; i < o.getScore().size(); i++) {
            if (this.getScore().get(i) > o.getScore().get(i)) {
                return 1;
            } else if (this.getScore().get(i) < o.getScore().get(i)) {
                return -1;
            }
        }
        return 0;
    }

    // public class PlayerScoreComparator implements Comparator<PlayerScore> {
    //
    // @Override
    // public int compare(PlayerScore o1, PlayerScore o2) {
    // // TODO Auto-generated method stub
    // if (o1.getScore().get(0) > o2.getScore().get(0)) {
    // return 1;
    // } else if (o1.getScore().get(0) == o2.getScore().get(0)) {
    // return 0;
    // } else {
    // return -1;
    // }
    // }
    // }

}
