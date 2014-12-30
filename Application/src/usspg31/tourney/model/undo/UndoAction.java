package usspg31.tourney.model.undo;

interface UndoAction {
	public void undo();
	public void redo();
}