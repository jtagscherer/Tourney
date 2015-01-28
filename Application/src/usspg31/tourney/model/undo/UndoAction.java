package usspg31.tourney.model.undo;

import javafx.beans.Observable;

interface UndoAction {
    public void undo();
    public void redo();
    public Observable getObservable();
}
