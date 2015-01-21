package usspg31.tourney.model.undo;

import javafx.collections.ObservableList;

abstract class ListChange<T> implements UndoAction {
    final ObservableList<T> list;
    final T value;

    ListChange(ObservableList<T> list, T value) {
        this.list = list;
        this.value = value;
    }
}
