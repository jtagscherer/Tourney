package usspg31.tourney.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a bye that players can get in certain scenarios during the
 * tournament execution
 */
public class Bye {
    public enum ByeType {
	NORMALBYE, SUPERBYE;
    }

    private ObjectProperty<PlayerScore> byePoints;
    private ObjectProperty<ByeType> byeType;

    public Bye() {
	this.byePoints = new SimpleObjectProperty<>();
	this.byeType = new SimpleObjectProperty<>();
    }

    public PlayerScore getByePoints() {
	return this.byePoints.get();
    }

    public void setByePoints(PlayerScore value) {
	this.byePoints.set(value);
    }

    public ObjectProperty<PlayerScore> byePointsProperty() {
	return this.byePoints;
    }

    public ByeType getByeType() {
	return this.byeType.get();
    }

    public void setByeType(ByeType value) {
	this.byeType.set(value);
    }

    public ObjectProperty<ByeType> byeTypeProperty() {
	return this.byeType;
    }

}
