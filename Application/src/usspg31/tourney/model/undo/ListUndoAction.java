package usspg31.tourney.model.undo;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

class ListUndoAction<T> implements UndoAction {
	private final ObservableList<T> list;
	private final List<ListChange<T>> changes;

	ListUndoAction(Change<T> change) {
		this.list = change.getList();
		this.changes = new ArrayList<ListChange<T>>();

		while (change.next()) {
			if (change.wasPermutated()) {
				continue; // not supported
			} else if (change.wasUpdated()) {
				continue; // not supported
			} else {
				for (T t : change.getRemoved()) {
					this.changes.add(new ListRemoval<T>(this.list, t));
				}
				for (T t : change.getAddedSubList()) {
					this.changes.add(new ListAddition<T>(this.list, t));
				}
			}
		}
	}

	@Override
	public void undo() {
		for (ListChange<T> change : this.changes) {
			change.undo();
		}
	}

	@Override
	public void redo() {
		for (ListChange<T> change : this.changes) {
			change.redo();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ListUndoActions: [");
		for (ListChange<?> c : this.changes) {
			sb.append(c);
			sb.append(" ");
		}
		sb.append("]");
		return sb.toString();
	}
}