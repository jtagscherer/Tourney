package usspg31.tourney.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a bye that players can get in certain scenarios during the
 * tournament execution
 */
public class Bye {
    public enum ByeType {
	NORMAL_BYE, SUPER_BYE;
    }

    private ObjectProperty<Integer> byePoints;
    private ObjectProperty<ByeType> byeType;

    public Bye() {
	this.byePoints = new SimpleObjectProperty<>();
	this.byeType = new SimpleObjectProperty<>();
    }

    public Integer getByePoints() {
	return this.byePoints.get();
    }

    public void setByePoints(Integer value) {
	this.byePoints.set(value);
    }

    public ObjectProperty<Integer> byePointsProperty() {
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
