package usspg31.tourney.model;

import java.util.ArrayList;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a pairing in a tournament game phase
 */
public class Pairing implements Cloneable {

    public enum PairingFlag {
        WINNER_BRACKET,
        LOSER_BRACKET,
        IGNORE;
    }

    private final ObservableList<Player> opponents;
    private final ObservableList<PlayerScore> scoreTable;
    private final ObjectProperty<PairingFlag> flag;

    /**
     * Create a new pairing and initialize all its properties
     */
    public Pairing() {
        this.opponents = FXCollections.observableArrayList();
        this.scoreTable = FXCollections.observableArrayList();
        this.flag = new SimpleObjectProperty<Pairing.PairingFlag>();
    }

    /**
     * Create a new pairing and initialize all its properties
     * 
     * @param opponents
     *            A list of opponents in this pairing
     */
    public Pairing(ArrayList<Player> opponents) {
        this.opponents = FXCollections.observableArrayList();
        this.opponents.setAll(opponents);
        this.scoreTable = FXCollections.observableArrayList();
        this.flag = new SimpleObjectProperty<Pairing.PairingFlag>();
    }

    /**
     * Get a list of all opponents in this pairing
     * 
     * @return List of all opponents in this pairing
     */
    public ObservableList<Player> getOpponents() {
        return this.opponents;
    }

    /**
     * Get a list of all scores in this pairing
     * 
     * @return List of all scores in this pairing
     */
    public ObservableList<PlayerScore> getScoreTable() {
        return this.scoreTable;
    }

    public PairingFlag getFlag() {
        return this.flag.get();
    }

    public void setFlag(PairingFlag value) {
        this.flag.set(value);
    }

    public ObjectProperty<PairingFlag> flagProperty() {
        return this.flag;
    }

    @Override
    public Object clone() {
        Pairing clone = new Pairing();

        clone.setFlag(this.getFlag());
        for (PlayerScore score : this.getScoreTable()) {
            clone.getScoreTable().add((PlayerScore) score.clone());
        }
        for (Player opponent : this.getOpponents()) {
            clone.getOpponents().add((Player) opponent.clone());
        }

        return clone;
    }
}
