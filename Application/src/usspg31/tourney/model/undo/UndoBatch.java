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
		for (int i = this.undoActions.size() - 1; i >= 0; i--) {
			this.undoActions.get(i).undo();
		}
	}

	@Override
	public void redo() {
		for (int i = 0; i < this.undoActions.size(); i++) {
			this.undoActions.get(i).redo();
		}
	}

}
