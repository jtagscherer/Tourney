package usspg31.tourney.model.undo;

import javafx.collections.ObservableList;

class ListAddition<T> extends ListChange<T> {
	ListAddition(ObservableList<T> list, T value) {
		super(list, value);
	}

	@Override
	public void undo() {
		this.list.remove(this.value);
	}

	@Override
	public void redo() {
		this.list.add(this.value);
	}

	@Override
	public String toString() {
		return "ListAddition: " + this.value;
	}
}