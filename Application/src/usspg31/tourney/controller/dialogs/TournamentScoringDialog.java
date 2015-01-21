package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.model.PossibleScoring;

public class TournamentScoringDialog extends VBox implements
IModalDialogProvider<PossibleScoring, PossibleScoring> {

    public static class ScoringEntry {
        private final StringProperty name;
        private final IntegerProperty score;

        public ScoringEntry(String name, int score) {
            this.name = new SimpleStringProperty(name);
            this.score = new SimpleIntegerProperty(score);
        }

        public StringProperty nameProperty() {
            return this.name;
        }

        public String getName() {
            return this.nameProperty().get();
        }

        public void setName(String value) {
            this.nameProperty().set(value);
        }

        public IntegerProperty scoreProperty() {
            return this.score;
        }

        public int getScore() {
            return this.scoreProperty().get();
        }

        public void setScore(int value) {
            this.scoreProperty().set(value);
        }
    }

    private static final Logger log = Logger.getLogger(TournamentScoringDialog.class.getName());

    @FXML private TableView<ScoringEntry> tablePossibleScores;
    @FXML private Button buttonAddPredefinedScore;
    @FXML private Button buttonRemovePredefinedScore;
    @FXML private Button buttonEditPredefinedScore;

    private ObservableList<ScoringEntry> predefinedScores;

    private final ModalDialog<Object, ScoringEntry> predefinedScoreDialog;

    private PossibleScoring loadedScoring;

    public TournamentScoringDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/tournament-scoring-dialog.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                    .getLanguageBundle());
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        this.predefinedScoreDialog = new PredefinedScoreDialog().modalDialog();
    }

    @FXML
    private void initialize() {
        PreferencesManager preferences = PreferencesManager.getInstance();

        TableColumn<ScoringEntry, String> possibleScoreName = new TableColumn<>(
                preferences.localizeString("dialogs.tournamentscoring.title"));
        possibleScoreName.setCellValueFactory(value -> {
            return value.getValue().nameProperty();
        });
        this.tablePossibleScores.getColumns().add(possibleScoreName);

        TableColumn<ScoringEntry, Integer> possibleScoreScore = new TableColumn<>(
                preferences.localizeString("dialogs.tournamentscoring.score"));
        possibleScoreScore.setCellValueFactory(value -> {
            return value.getValue().scoreProperty().asObject();
        });
        this.tablePossibleScores.getColumns().add(possibleScoreScore);

        // only enable the edit and remove buttons if a score is selected
        this.buttonEditPredefinedScore.disableProperty().bind(
                this.tablePossibleScores.getSelectionModel()
                .selectedItemProperty().isNull());

        this.buttonRemovePredefinedScore.disableProperty().bind(
                this.tablePossibleScores.getSelectionModel()
                .selectedItemProperty().isNull());

        this.predefinedScores = FXCollections.observableArrayList();
        this.tablePossibleScores.setItems(this.predefinedScores);
    }

    @Override
    public void initModalDialog(ModalDialog<PossibleScoring, PossibleScoring> modalDialog) {
        modalDialog.title("dialogs.tournamentscoring").dialogButtons(DialogButtons.OK_CANCEL);
    }

    @Override
    public void setProperties(PossibleScoring properties) {
        this.loadedScoring = properties;

        this.predefinedScores.clear();

        for (Entry<String, Integer> entry : properties.getScores().entrySet()) {
            this.predefinedScores.add(new ScoringEntry(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public PossibleScoring getReturnValue() {
        return this.loadedScoring;
    }

    @FXML
    private void onButtonAddPredefinedScoreClicked(ActionEvent event) {
        log.fine("Button Add Predefined Score was clicked");
        this.predefinedScoreDialog
        .properties(this.predefinedScores)
        .properties(new ScoringEntry(
                PreferencesManager.getInstance().localizeString(
                        "dialogs.tournamentscoring.untitled"), 0))
        .onResult((result, value) -> {
            if (result == DialogResult.OK) {
                this.addScore(value);
            }
        })
        .show();
    }

    @FXML
    private void onButtonRemovePredefinedScoreClicked(ActionEvent event) {
        log.fine("Button Remove Predefined Score was clicked");
        this.removeScore(this.getSelectedScore());
    }

    @FXML
    private void onButtonEditPredefinedScoreClicked(ActionEvent event) {
        final String originalName = this.getSelectedScore().getName();
        final int originalScore = this.getSelectedScore().getScore();

        log.fine("Button Edit Predefined Score was clicked");
        this.predefinedScoreDialog
        .properties(this.predefinedScores)
        .properties(this.getSelectedScore())
        .onResult((result, value) -> {
            this.removeScore(value);
            if (result == DialogResult.OK) {
                this.addScore(value);
            } else {
                this.addScore(new ScoringEntry(originalName, originalScore));
            }
        })
        .show();
    }

    private ScoringEntry getSelectedScore() {
        return this.tablePossibleScores.getSelectionModel().getSelectedItem();
    }

    private void addScore(ScoringEntry entry) {
        this.predefinedScores.add(entry);
        this.loadedScoring.getScores().put(entry.getName(), entry.getScore());
    }

    private void removeScore(ScoringEntry entry) {
        this.predefinedScores.remove(entry);
        this.loadedScoring.getScores().remove(entry.getName());
    }

}
