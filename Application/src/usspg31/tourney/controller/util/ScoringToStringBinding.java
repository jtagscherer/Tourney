package usspg31.tourney.controller.util;

import java.util.Map.Entry;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.PossibleScoring.ScoringType;

public class ScoringToStringBinding extends StringBinding {
    private final ObjectProperty<PossibleScoring> possibleScoring;

    public ScoringToStringBinding(PossibleScoring possibleScoring) {
        this.possibleScoring = new SimpleObjectProperty<PossibleScoring>();
        this.possibleScoring.set(possibleScoring);
        this.bind(this.possibleScoring);
    }

    public StringProperty getStringProperty() {
        SimpleStringProperty sp = new SimpleStringProperty();
        sp.bind(this);
        return sp;
    }

    @Override
    protected String computeValue() {
        StringBuilder ret = new StringBuilder();

        if (this.possibleScoring.getValue().getScoreType() == ScoringType.NORMAL) {
            for (Entry<String, Integer> entry : this.possibleScoring.getValue()
                    .getScores().entrySet()) {
                ret.append(entry.getKey() + ": " + entry.getValue());
                ret.append(", ");
            }
            ret.append(PreferencesManager.getInstance().localizeString(
                    "pairingnode.bye")
                    + ": " + this.possibleScoring.getValue().getByeValue());
        } else {
            ret.append(PreferencesManager.getInstance().localizeString(
                    "dialogs.tournamentscoring.mode.tablestrength"));
        }

        return ret.toString();
    }
}
