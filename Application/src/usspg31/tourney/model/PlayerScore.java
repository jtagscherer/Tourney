package usspg31.tourney.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Accumulates the scores of a player
 */
public class PlayerScore implements Comparable<PlayerScore>, Cloneable {

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

        for (int i = 0; i < o.getScore().size(); i++) {
            if (this.getScore().get(i) > o.getScore().get(i)) {
                return 1;
            } else if (this.getScore().get(i) < o.getScore().get(i)) {
                return -1;
            }
        }
        return 0;
    }

    @Override
    public Object clone() {
        PlayerScore clone = new PlayerScore();
        clone.setPlayer((Player) player.clone());
        for (Integer score : this.score) {
            clone.getScore().add((int) score);
        }

        return clone;
    }

}
