package usspg31.tourney.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Represents a possible scoring priority
 */
public class PossibleScoring implements Cloneable {
    public enum ScoringType {
        TABLE_STRENGTH,
        NORMAL;
    }

    private ObservableMap<String, Integer> scores;
    private IntegerProperty priority;
    private ScoringType scoreType;
    private IntegerProperty byeValue;

    /**
     * Create a new possible scoring and initialize all its properties
     */
    public PossibleScoring() {
        this.priority = new SimpleIntegerProperty();
        this.byeValue = new SimpleIntegerProperty();
        this.scores = FXCollections
                .observableMap(new HashMap<String, Integer>());
        this.scoreType = ScoringType.NORMAL;
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

    /**
     * Get the bye value of this scoring
     * 
     * @return Current bye value of this scoring
     */
    public int getByeValue() {
        return this.byeValue.get();
    }

    /**
     * Set the bye value of this scoring
     * 
     * @param priority
     *            New priority of this scoring
     */
    public void setByeValue(int byeValue) {
        this.byeValue.set(byeValue);
    }

    /**
     * Get the bye value property of this scoring
     * 
     * @return Bye value property of this scoring
     */
    public IntegerProperty byeValueProperty() {
        return this.byeValue;
    }

    public ScoringType getScoreType() {
        return this.scoreType;
    }

    public void setScoreType(ScoringType scoreType) {
        this.scoreType = scoreType;
    }

    @Override
    public Object clone() {
        PossibleScoring clone = new PossibleScoring();

        clone.setPriority(this.getPriority());
        clone.setByeValue(this.getByeValue());
        clone.setScoreType(this.getScoreType());

        Iterator<Entry<String, Integer>> iterator = this.getScores().entrySet()
                .iterator();
        while (iterator.hasNext()) {
            Map.Entry<?, ?> pairs = (Map.Entry<?, ?>) iterator.next();
            clone.getScores().put((String) pairs.getKey(),
                    (Integer) pairs.getValue());
        }

        return clone;
    }
}
