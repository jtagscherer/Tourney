package usspg31.tourney.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a bye that players can get in certain scenarios during the
 * tournament execution
 */
public class Bye implements Cloneable {
    public enum ByeType {
        NORMAL_BYE,
        SUPER_BYE;
    }

    private ObservableList<Integer> byePoints;
    private ObjectProperty<ByeType> byeType;

    public Bye() {
        this.byePoints = FXCollections.observableArrayList();
        this.byeType = new SimpleObjectProperty<>();
    }

    public ObservableList<Integer> byePointsProperty() {
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

    public ObservableList<Integer> getByePoints() {
        return this.byePoints;
    }

    @Override
    public Object clone() {
        Bye clone = new Bye();

        clone.setByeType(this.getByeType());
        for (Integer point : this.getByePoints()) {
            clone.getByePoints().add(point);
        }

        return clone;
    }
}
