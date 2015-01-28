package usspg31.tourney.model.undo;

import javafx.beans.Observable;
import javafx.beans.property.Property;

class PropertyUndoAction<T> implements UndoAction {
    private final Property<T> property;
    private final T oldValue;
    private final T newValue;

    PropertyUndoAction(Property<T> property, T oldValue, T newValue) {
        this.property = property;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public void undo() {
        this.property.setValue(this.oldValue);
    }

    @Override
    public void redo() {
        this.property.setValue(this.newValue);
    }

    @Override
    public Observable getObservable() {
        return this.property;
    }
}
