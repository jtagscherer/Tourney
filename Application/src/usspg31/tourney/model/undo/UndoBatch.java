package usspg31.tourney.model.undo;

import java.util.ArrayList;
import java.util.List;

public class UndoBatch implements UndoAction {

	private List<UndoAction> undoActions;

	public UndoBatch() {
		this.undoActions = new ArrayList<>();
	}

	public int getUndoActionCount() {
		return this.undoActions.size();
	}

	public void addUndoAction(UndoAction undoAction) {
		this.undoActions.add(undoAction);
	}

	@Override
	public void undo() {
		for (UndoAction action : this.undoActions) {
			action.undo();
		}
	}

	@Override
	public void redo() {
		for (UndoAction action : this.undoActions) {
			action.redo();
		}
	}

}
