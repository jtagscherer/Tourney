package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
        private final BooleanProperty editable;

        public ScoringEntry(String name, int score) {
            this.name = new SimpleStringProperty(name);
            this.score = new SimpleIntegerProperty(score);
            this.editable = new SimpleBooleanProperty();
            this.setEditable(true);
        }

        public void setEditable(boolean editable) {
            this.editable.set(editable);
        }

        public boolean isEditable() {
            return this.editable.get();
        }

        public BooleanProperty editableProperty() {
            return this.editable;
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

    private static final Logger log = Logger
            .getLogger(TournamentScoringDialog.class.getName());

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

        this.predefinedScores = FXCollections.observableArrayList();
        this.tablePossibleScores.setItems(this.predefinedScores);

        /* Edit the predefined score on double click */
        this.tablePossibleScores.setRowFactory(tableView -> {
            TableRow<ScoringEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    this.editPredefinedScore(row.getItem());
                }
            });
            return row;
        });

        this.tablePossibleScores.setPlaceholder(new Text(PreferencesManager
                .getInstance().localizeString("tableplaceholder.noscorings")));
    }

    @Override
    public void initModalDialog(
            ModalDialog<PossibleScoring, PossibleScoring> modalDialog) {
        modalDialog.title("dialogs.tournamentscoring").dialogButtons(
                DialogButtons.OK_CANCEL);
    }

    @Override
    public void setProperties(PossibleScoring properties) {
        this.loadPossibleScoring(properties);
    }

    public void loadPossibleScoring(PossibleScoring scoring) {
        if (this.loadedScoring != null) {
            this.unloadPossibleScoring();
        }

        /* Set the table's content */
        this.loadedScoring = (PossibleScoring) scoring.clone();
        this.refreshTable();

        /*
         * Bind the button's availability to true if an item is selected and if
         * it is editable
         */
        this.tablePossibleScores
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (ChangeListener<ScoringEntry>) (arg0, oldVal, newVal) -> {
                            if (newVal != null) {
                                if (newVal.isEditable()) {
                                    this.buttonEditPredefinedScore
                                            .setDisable(false);
                                    this.buttonRemovePredefinedScore
                                            .setDisable(false);
                                } else {
                                    this.buttonEditPredefinedScore
                                            .setDisable(true);
                                    this.buttonRemovePredefinedScore
                                            .setDisable(true);
                                }
                            }
                        });
    }

    public void unloadPossibleScoring() {
        /* Clear the table */
        this.tablePossibleScores.getSelectionModel().clearSelection();

        this.loadedScoring = null;
    }

    public void refreshTable() {
        this.predefinedScores.clear();

        for (Entry<String, Integer> entry : this.loadedScoring.getScores()
                .entrySet()) {
            this.predefinedScores.add(new ScoringEntry(entry.getKey(), entry
                    .getValue()));
        }

        /* Add the bye to the table */
        ScoringEntry byeEntry = new ScoringEntry(PreferencesManager
                .getInstance().localizeString("pairingnode.bye"),
                this.loadedScoring.getByeValue());
        byeEntry.setEditable(false);
        this.tablePossibleScores.getItems().add(byeEntry);
    }

    @Override
    public PossibleScoring getReturnValue() {
        return this.loadedScoring;
    }

    @Override
    public String getInputErrorString() {
        if (this.loadedScoring.getScores().size() == 0) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournamentscoring.error.emptydata");
        }
        return null;
    }

    private void updateNormalBye() {
        int maximumValue = 0;
        for (ScoringEntry entry : this.predefinedScores) {
            if (entry.getScore() > maximumValue) {
                maximumValue = entry.getScore();
            }
        }
        this.loadedScoring.setByeValue(maximumValue);

        this.refreshTable();
    }

    @FXML
    private void onButtonAddPredefinedScoreClicked(ActionEvent event) {
        log.fine("Button Add Predefined Score was clicked");
        this.predefinedScoreDialog
                .properties(this.predefinedScores)
                .properties(
                        new ScoringEntry(PreferencesManager.getInstance()
                                .localizeString(
                                        "dialogs.tournamentscoring.untitled"),
                                0)).onResult((result, value) -> {
                    if (result == DialogResult.OK) {
                        this.addScore(value);
                        this.updateNormalBye();
                    }
                }).show();
    }

    @FXML
    private void onButtonRemovePredefinedScoreClicked(ActionEvent event) {
        log.fine("Button Remove Predefined Score was clicked");

        this.removeScore(this.getSelectedScore());
        this.updateNormalBye();
    }

    @FXML
    private void onButtonEditPredefinedScoreClicked(ActionEvent event) {
        log.fine("Button Edit Predefined Score was clicked");

        this.editPredefinedScore(this.getSelectedScore());
        this.updateNormalBye();
    }

    private void editPredefinedScore(ScoringEntry selectedScore) {
        final String originalName = selectedScore.getName();
        final int originalScore = selectedScore.getScore();

        this.predefinedScoreDialog
                .properties(this.predefinedScores)
                .properties(this.getSelectedScore())
                .onResult(
                        (result, value) -> {
                            if (result == DialogResult.OK) {
                                this.removeScore(originalName);
                                this.addScore(value);
                            } else {
                                this.removeScore(originalName);
                                this.addScore(new ScoringEntry(originalName,
                                        originalScore));
                            }

                            this.refreshTable();
                        }).show();
    }

    private ScoringEntry getSelectedScore() {
        return this.tablePossibleScores.getSelectionModel().getSelectedItem();
    }

    private void addScore(ScoringEntry entry) {
        this.predefinedScores.add(entry);
        this.loadedScoring.getScores().put(entry.getName(), entry.getScore());
    }

    private void removeScore(String scoreKey) {
        this.loadedScoring.getScores().remove(scoreKey);
    }

    private void removeScore(ScoringEntry entry) {
        this.predefinedScores.remove(entry);
        this.loadedScoring.getScores().remove(entry.getName());
    }

}
