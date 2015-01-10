package usspg31.tourney.model;

import java.util.HashMap;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class PossibleScoring {
	private ObservableMap<String, Integer> scores;
	private IntegerProperty priority;

	public PossibleScoring() {
		this.priority = new SimpleIntegerProperty();
		this.scores = FXCollections
				.observableMap(new HashMap<String, Integer>());
	}

	public ObservableMap<String, Integer> getScores() {
		return this.scores;
	}

	public void setScores(ObservableMap<String, Integer> scores) {
		this.scores = scores;
	}

	public IntegerProperty getPriority() {
		return this.priority;
	}

	public void setPriority(IntegerProperty priority) {
		this.priority = priority;
	}

	public void setPriorityValue(int value) {
		this.priority.set(value);
	}
}
