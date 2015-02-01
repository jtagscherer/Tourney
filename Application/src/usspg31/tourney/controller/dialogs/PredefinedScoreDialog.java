package usspg31.tourney.controller.dialogs;

import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.MaterialTextField;
import usspg31.tourney.controller.controls.NumberTextField;
import usspg31.tourney.controller.dialogs.TournamentScoringDialog.ScoringEntry;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;

public class PredefinedScoreDialog extends VBox implements
        IModalDialogProvider<Object, ScoringEntry> {

    private final MaterialTextField textFieldScoreTitle;
    private final NumberTextField numberTextFieldScore;

    private ObservableList<ScoringEntry> entryList;
    private ScoringEntry entry;

    public PredefinedScoreDialog() {
        super(5);

        PreferencesManager preferences = PreferencesManager.getInstance();

        this.textFieldScoreTitle = new MaterialTextField();
        this.textFieldScoreTitle.setPromptText(preferences
                .localizeString("dialogs.predefinedscore.scoretitle"));
        this.textFieldScoreTitle.setFloatingPrompt(true);
        this.textFieldScoreTitle.textProperty().addListener((ov, o, n) -> {
            if (this.entry != null) {
                this.entry.setName(n);
            }
        });
        this.numberTextFieldScore = new NumberTextField();
        this.numberTextFieldScore.setPromptText(preferences
                .localizeString("dialogs.predefinedscore.score"));
        this.numberTextFieldScore.setFloatingPrompt(true);
        this.numberTextFieldScore.setShowPrompt(true);
        this.numberTextFieldScore.numberValueProperty().addListener(
                (ov, o, n) -> {
                    if (this.entry != null) {
                        this.entry.setScore(n.intValue());
                    }
                });

        this.getChildren().addAll(this.textFieldScoreTitle,
                this.numberTextFieldScore);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setProperties(Object properties) {
        if (properties instanceof ObservableList) {
            this.entryList = (ObservableList<ScoringEntry>) properties;
        } else if (properties instanceof ScoringEntry) {
            this.entry = (ScoringEntry) properties;
            this.textFieldScoreTitle.setText(this.entry.getName());
            this.numberTextFieldScore.setNumberValue(this.entry.getScore());
        }
    }

    @Override
    public ScoringEntry getReturnValue() {
        return this.entry;
    }

    @Override
    public String getInputErrorString() {
        for (ScoringEntry entry : this.entryList) {
            if (entry != this.entry
                    && entry.getName().equals(this.entry.getName())) {
                return PreferencesManager.getInstance().localizeString(
                        "dialogs.predefinedscore.errorduplicatekey");
            }
        }

        if (this.entry.getName().equals("")) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.predefinedscoredialog.error.emptyname");
        }

        if (this.numberTextFieldScore.getText().equals("")) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.predefinedscoredialog.error.emptypoints");
        }

        return null;
    }

    @Override
    public void initModalDialog(ModalDialog<Object, ScoringEntry> modalDialog) {
        modalDialog.title("dialogs.predefinedscore").dialogButtons(
                DialogButtons.OK_CANCEL);
    }

}
