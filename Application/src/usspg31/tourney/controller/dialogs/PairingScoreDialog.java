package usspg31.tourney.controller.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.NumberTextField;
import usspg31.tourney.controller.dialogs.PairingScoreDialog.PairingEntry;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.layout.IconPane;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.Tournament;

public class PairingScoreDialog extends TableView<PairingScoreDialog.PlayerScoreEntry>
        implements IModalDialogProvider<PairingEntry, Pairing> {

    public static class PairingEntry {
        private final Tournament tournament;
        private final Pairing pairing;

        public PairingEntry(Tournament tournament, Pairing pairing) {
            this.tournament = tournament;
            this.pairing = pairing;
        }
    }

    public static class PlayerScoreEntry {
        private final Player player;
        private final List<ObjectProperty<Integer>> scores;

        public PlayerScoreEntry(Player player) {
            this.player = player;
            this.scores = new ArrayList<>();
        }
    }

    private static class ScoreCell extends TableCell<PlayerScoreEntry, ObjectProperty<Integer>> {
        private ObjectProperty<Integer> score;

        final HBox container;
        final NumberTextField numberFieldScore;
        final Button dropDownButton;

        public ScoreCell(PossibleScoring possibleScores) {
            this.container = new HBox();

            this.numberFieldScore = new NumberTextField();
            this.numberFieldScore.setPrefWidth(50);

            this.dropDownButton = new Button();
            IconPane icon = new IconPane();
            icon.getStyleClass().addAll("icon-arrow-down", "half");
            this.dropDownButton.setGraphic(icon);

            if (possibleScores.getScores().size() > 0) {
                ContextMenu possibleScoreDropDown = new ContextMenu();
                for (Entry<String, Integer> possibleScore : possibleScores.getScores().entrySet()) {
                    MenuItem scoreItem = new MenuItem(possibleScore.getKey() + ": " + possibleScore.getValue());
                    scoreItem.setOnAction(event -> {
                        this.numberFieldScore.setText(possibleScore.getValue().toString());
                    });
                    possibleScoreDropDown.getItems().add(scoreItem);
                }
                this.dropDownButton.setOnAction(event -> {
                    possibleScoreDropDown.show(this.dropDownButton, Side.BOTTOM, 0, 0);
                });
            } else {
                this.dropDownButton.setDisable(true);
            }

            this.numberFieldScore.numberValueProperty().addListener((ov, o, n) -> {
                this.score.set(n.intValue());
            });

            this.container.getChildren().addAll(this.numberFieldScore, this.dropDownButton);
        }

        @Override
        protected void updateItem(ObjectProperty<Integer> item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty && item != null) {
                this.score = item;
                if (this.score.get() != null) {
                    this.numberFieldScore.setText(this.score.getValue().toString());
                }
                this.setGraphic(this.container);
            }
        }

    }

    private PairingEntry pairingEntry;

    private List<PlayerScoreEntry> scoreList; // <Player<Score>>

    public PairingScoreDialog() {
        this.scoreList = new ArrayList<>();

        this.getStyleClass().add("pairing-score-dialog");
    }

    @Override
    public void setProperties(PairingEntry properties) {
        this.pairingEntry = properties;
        this.updateTable();
    }

    private void updateTable() {
        ObservableList<Player> opponents = this.pairingEntry.pairing.getOpponents();
        ObservableList<PossibleScoring> possibleScores = this.pairingEntry.tournament.getRuleSet().getPossibleScores();
        int scoreCount = possibleScores.size();

        // initialize score list with empty scores
        this.scoreList.clear();
        for (int i = 0; i < opponents.size(); i++) {
            PlayerScoreEntry playerScore = new PlayerScoreEntry(opponents.get(i));
            for (int j = 0; j < scoreCount; j++) {
                playerScore.scores.add(new SimpleObjectProperty<Integer>(null));
            }
            this.scoreList.add(playerScore);
        }

        // create columns for every score
        this.getColumns().clear();
        TableColumn<PlayerScoreEntry, String> nameColumn = new TableColumn<>(
                PreferencesManager.getInstance().localizeString("dialog.pairingscore.name"));
        nameColumn.setCellValueFactory(value -> {
            Player p = value.getValue().player;
            return new SimpleStringProperty(
                    p.getLastName() + " (" + p.getStartingNumber() + ")");
        });
        this.getColumns().add(nameColumn);

        for (int scoreNumber = 0; scoreNumber < scoreCount; scoreNumber++) {
            TableColumn<PlayerScoreEntry, ObjectProperty<Integer>> scoreColumn = new TableColumn<>(
                    Integer.toString(scoreNumber + 1));
            final int scoreID = scoreNumber;
            scoreColumn.setCellFactory(cell -> new ScoreCell(possibleScores.get(scoreID)));
            scoreColumn.setCellValueFactory(value ->
            new SimpleObjectProperty<ObjectProperty<Integer>>(value.getValue().scores.get(scoreID)));
            this.getColumns().add(scoreColumn);
        }

        this.getItems().clear();
        for (int i = 0; i < opponents.size(); i++) {
            PlayerScoreEntry playerScore = this.scoreList.get(i);
            for (int j = 0; j < scoreCount; j++) {
                if (j < this.pairingEntry.pairing.getScoreTable().get(i).getScore().size()) {
                    playerScore.scores.get(j).set(
                            this.pairingEntry.pairing.getScoreTable().get(i)
                            .getScore().get(j));
                }
            }
            this.getItems().add(playerScore);
        }
    }

    @Override
    public Pairing getReturnValue() {
        Pairing ret = new Pairing();
        for (PlayerScoreEntry score : this.scoreList) {
            ret.getOpponents().add(score.player);
            PlayerScore playerScore = new PlayerScore();
            playerScore.setPlayer(score.player);

            ret.getScoreTable().add(playerScore);
            for (ObjectProperty<Integer> scoreValue : score.scores) {
                playerScore.getScore().add(scoreValue.get());
            }
        }
        return ret;
    }

    @Override
    public void initModalDialog(ModalDialog<PairingEntry, Pairing> modalDialog) {
        modalDialog
        .title("dialogs.pairingscore")
        .dialogButtons(DialogButtons.OK_CANCEL);
    }

}
