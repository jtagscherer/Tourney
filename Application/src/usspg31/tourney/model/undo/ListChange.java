package usspg31.tourney.model.undo;

import javafx.beans.Observable;
import javafx.collections.ObservableList;

abstract class ListChange<T> implements UndoAction {
    final ObservableList<T> list;
    final T value;

    ListChange(ObservableList<T> list, T value) {
        this.list = list;
        this.value = value;
    }

    @Override
    public Observable getObservable() {
        return this.list;
    }
}
