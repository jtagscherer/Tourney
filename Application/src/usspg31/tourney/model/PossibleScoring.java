package usspg31.tourney.model;

import java.util.HashMap;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Represents a possible scoring priority
 */
public class PossibleScoring {
    private ObservableMap<String, Integer> scores;
    private IntegerProperty priority;

    /**
     * Create a new possible scoring and initialize all its properties
     */
    public PossibleScoring() {
        this.priority = new SimpleIntegerProperty();
        this.scores = FXCollections
                .observableMap(new HashMap<String, Integer>());
    }

    /**
     * Get the map of all scores in this scoring
     * 
     * @return Map of all scores in this scoring
     */
    public ObservableMap<String, Integer> getScores() {
        return this.scores;
    }

    /**
     * Set the map of scores in this scoring
     * 
     * @param scores
     *            New map of scores in this scoring
     */
    public void setScores(ObservableMap<String, Integer> scores) {
        this.scores = scores;
    }

    /**
     * Get the priority of this scoring
     * 
     * @return Current priority of this scoring
     */
    public int getPriority() {
        return this.priority.get();
    }

    /**
     * Set the priority of this scoring
     * 
     * @param priority
     *            New priority of this scoring
     */
    public void setPriority(int priority) {
        this.priority.set(priority);
    }

    /**
     * Get the priority property of this scoring
     * 
     * @return Priority property of this scoring
     */
    public IntegerProperty priorityProperty() {
        return this.priority;
    }
}
