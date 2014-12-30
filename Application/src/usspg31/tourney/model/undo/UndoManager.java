package usspg31.tourney.model.undo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

/**
 * Provides functionality for undoing and redoing actions performed on javafx
 * properties.
 * To use, simple register the property you want to be able to perform undo and
 * redo actions on using {@link#registerUndoProperty}.
 * To undo or redo the last performed action on any of the registered values,
 * call the {@link#undo} or {@link#redo} methods accordingly.
 * @author Jonas Auer
 */
public class UndoManager {

	/** The first node in the undo chain. Does not contain an action */
	private final UndoNode headNode;
	/** The currently active node in the undo chain */
	private UndoNode currentNode;

	/** Contains a value indicating if an undo can be performed */
	private final BooleanProperty undoAvailableProperty;
	/** Contains a value indicating if a redo can be performed */
	private final BooleanProperty redoAvailableProperty;

	/** Flag set if a registered property is currently changed by an undo or redo action */
	private boolean isPerformingAction;

	/**
	 * Initializes a new UndoManager.
	 */
	public UndoManager() {
		this.headNode = new UndoNode(null, null);
		this.currentNode = this.headNode;

		this.undoAvailableProperty = new SimpleBooleanProperty(false);
		this.redoAvailableProperty = new SimpleBooleanProperty(false);

		this.isPerformingAction = false;
	}

	/**
	 * Property indicating if an undo action is available.
	 * @return a read-only property
	 */
	public ReadOnlyBooleanProperty undoAvailableProperty() {
		return BooleanProperty.readOnlyBooleanProperty(this.undoAvailableProperty);
	}

	/**
	 * @return true if an undo action is available, otherwise false
	 */
	public boolean undoAvailable() {
		return this.undoAvailableProperty.get();
	}

	/**
	 * @param value true if an undo action is available
	 */
	private void setUndoAvailable(boolean value) {
		this.undoAvailableProperty.set(value);
	}

	/**
	 * Property indicating if an undo action is available.
	 * @return a read-only property
	 */
	public ReadOnlyBooleanProperty redoAvailableProperty() {
		return BooleanProperty.readOnlyBooleanProperty(this.redoAvailableProperty);
	}

	/**
	 * @return true if a redo action is available, otherwise false
	 */
	public boolean redoAvailable() {
		return this.redoAvailableProperty.get();
	}

	/**
	 * @param value true if an undo action is available
	 */
	private void setRedoAvailable(boolean value) {
		this.redoAvailableProperty.set(value);
	}

	/**
	 * Undoes the current active action if one is available.
	 * Otherwise does nothing.
	 */
	public void undo() {
		if (this.undoAvailable()) {
			this.isPerformingAction = true;

			this.currentNode.getAction().undo();
			this.currentNode = this.currentNode.getPrev();

			this.isPerformingAction = false;

			this.setUndoAvailable(this.currentNode != this.headNode);
			this.setRedoAvailable(true);
		}
	}

	/**
	 * Re-does the following action of the currently active one if one is available.
	 * Otherwise does nothing.
	 */
	public void redo() {
		if (this.redoAvailable()) {
			this.isPerformingAction = true;

			this.currentNode = this.currentNode.getNext();
			this.currentNode.getAction().redo();

			this.isPerformingAction = false;

			this.setRedoAvailable(this.currentNode.hasNext());
			this.setUndoAvailable(true);
		}
	}

	/**
	 * Registers a property to watch for changes.
	 * Adds a listener to the property that automatically stores changes to the
	 * property in in the undo history.
	 * @param property
	 */
	public <T> void registerUndoProperty(Property<T> property) {
		property.addListener(this::propertyChangeListener);
	}

	/**
	 * Registers a list to watch for changes.
	 * Adds a listener to the list that automatically stores changes to the
	 * list in in the undo history.
	 * @param property
	 */
	public <T> void registerUndoProperty(ObservableList<T> list) {
		list.addListener(this::listChangeListener);
	}

	/**
	 * Listener that is being attached to registered properties.
	 * @param observable the property that is being changed
	 * @param oldValue the old value of the property
	 * @param newValue the new value of the property
	 */
	@SuppressWarnings("unchecked")
	private <T> void propertyChangeListener(ObservableValue<? extends T> observable,
			T oldValue, T newValue) {
		if (!this.isPerformingAction) {
			this.currentNode.setNext(new UndoNode(this.currentNode,
					new PropertyUndoAction<T>((Property<T>) observable,
							oldValue, newValue)));
			this.currentNode = this.currentNode.getNext();
			this.setUndoAvailable(true);
			this.setRedoAvailable(false);
		}
	}

	/**
	 * Listener that is being attached to registered lists.
	 * @param change the change that occurred on the list
	 */
	@SuppressWarnings("unchecked")
	private <T> void listChangeListener(Change<? extends T> change) {
		if (!this.isPerformingAction) {
			this.currentNode.setNext(new UndoNode(this.currentNode,
					new ListUndoAction<T>((Change<T>) change)));
			this.currentNode = this.currentNode.getNext();
			this.setUndoAvailable(true);
			this.setRedoAvailable(false);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Current state: ");
		sb.append(this.currentNode);
		sb.append("\n\tUndoes available: [");
		UndoNode a = this.currentNode;
		while ((a = a.getPrev()) != this.headNode) {
			sb.append(a);
			sb.append(" ");
		}
		sb.append("]\n\tRedoes available: [");
		a = this.currentNode;
		while ((a = a.getNext()) != null) {
			sb.append(a);
			sb.append(" ");
		}
		return sb.toString();
	}

}
