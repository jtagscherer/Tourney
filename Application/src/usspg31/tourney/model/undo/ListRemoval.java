package usspg31.tourney.model.undo;

import javafx.collections.ObservableList;

class ListRemoval<T> extends ListChange<T> {
    public ListRemoval(ObservableList<T> list, T value) {
        super(list, value);
    }

    @Override
    public void undo() {
        this.list.add(this.value);
    }

    @Override
    public void redo() {
        this.list.remove(this.value);
    }

    @Override
    public String toString() {
        return "ListRemoval: " + this.value;
    }
}
