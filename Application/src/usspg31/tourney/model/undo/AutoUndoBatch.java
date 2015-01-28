package usspg31.tourney.model.undo;

import javafx.beans.Observable;


public class AutoUndoBatch extends UndoBatch {

    private final Observable undoProperty;

    public AutoUndoBatch(Observable undoProperty) {
        super();

        this.undoProperty = undoProperty;
    }

    @Override
    public Observable getObservable() {
        return this.undoProperty;
    }

}
