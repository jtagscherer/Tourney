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

	/** Used for collecting multiple actions and put them into a single undo action */
	private UndoBatch undoBatch;

	/**
	 * Initializes a new UndoManager.
	 */
	public UndoManager() {
		this.headNode = new UndoNode(null, null);
		this.currentNode = this.headNode;

		this.undoAvailableProperty = new SimpleBooleanProperty(false);
		this.redoAvailableProperty = new SimpleBooleanProperty(false);

		this.isPerformingAction = false;

		this.undoBatch = null;
	}

	/**
	 * Clears the whole undo history.
	 * It will be no longer possible to undo or redo previously added actions.
	 */
	public void clearHistory() {
		this.headNode.setNext(null);
		this.currentNode = this.headNode;

		this.setUndoAvailable(false);
		this.setRedoAvailable(false);

		this.isPerformingAction = false;
	}

	/**
	 * Tells the UndoManager to put all following actions into one single undo
	 * action, until {@link#endUndoBatch()} is called. The batch only is actually
	 * added to the undo history, if it contains at least one action.
	 */
	public void beginUndoBatch() {
		this.undoBatch = new UndoBatch();
	}

	/**
	 * Ends a previously started collection of undo actions.
	 */
	public void endUndoBatch() {
		this.undoBatch = null;
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
	 * Ends the batching of undo actions, if it was activated before.
	 * Otherwise does nothing.
	 */
	public void undo() {
		if (this.undoAvailable()) {
			this.endUndoBatch();

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
	 * Unregisters a property to stop watching for changes.
	 * Entries in the undo history will not be removed.
	 * @param property
	 */
	public <T> void unregisterUndoProperty(Property<T> property) {
		property.removeListener(this::propertyChangeListener);
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
	 * Unregisters a list to stop watching for changes.
	 * Entries in the undo history will not be removed.
	 * @param list
	 */
	public <T> void unregisterUndoProperty(ObservableList<T> list) {
		list.removeListener(this::listChangeListener);
	}

	/**
	 * Adds an undoAction to the undo history.
	 * @param undoAction
	 */
	private void addUndoAction(UndoAction undoAction) {
		if (this.undoBatch != null) {
			this.undoBatch.addUndoAction(undoAction);
			// did we just add the first element to the undoBatch?
			if (this.undoBatch.getUndoActionCount() == 1) {
				this.currentNode.setNext(new UndoNode(this.currentNode, this.undoBatch));
			}
		} else {
			this.currentNode.setNext(new UndoNode(this.currentNode, undoAction));
		}

		this.currentNode = this.currentNode.getNext();
		this.setUndoAvailable(true);
		this.setRedoAvailable(false);
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
			this.addUndoAction(new PropertyUndoAction<T>(
					(Property<T>) observable, oldValue, newValue));
		}
	}

	/**
	 * Listener that is being attached to registered lists.
	 * @param change the change that occurred on the list
	 */
	@SuppressWarnings("unchecked")
	private <T> void listChangeListener(Change<? extends T> change) {
		if (!this.isPerformingAction) {
			this.addUndoAction(new ListUndoAction<>((Change<T>) change));
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
