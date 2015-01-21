package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.model.Player;

public class AttendanceDialog extends VBox implements
        IModalDialogProvider<ObservableList<Player>, ObservableList<Player>> {

    private static final Logger log = Logger.getLogger(AttendanceDialog.class
            .getName());

    @FXML private TableView<Player> tableRegisteredPlayers;
    @FXML private TableView<Player> tableAttendingPlayers;
    @FXML private Button buttonAddAttendee;
    @FXML private Button buttonRemoveAttendee;
    @FXML private Button buttonStartTournament;

    public AttendanceDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/attendance-dialog.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    public void initialize() {
        TableColumn<Player, String> playerName = new TableColumn<>("Name");
        playerName.setCellValueFactory(cellValue -> {
            return cellValue.getValue().firstNameProperty().concat(" ")
                    .concat(cellValue.getValue().lastNameProperty());
        });
        this.tableAttendingPlayers.getColumns().add(playerName);
        this.tableRegisteredPlayers.getColumns().add(playerName);

        this.buttonAddAttendee.disableProperty().bind(
                this.tableRegisteredPlayers.getSelectionModel()
                        .selectedItemProperty().isNull());
        this.buttonRemoveAttendee.disableProperty().bind(
                this.tableAttendingPlayers.getSelectionModel()
                        .selectedItemProperty().isNull());
    }

    @Override
    public void setDialogRoot(
            ModalDialog<ObservableList<Player>, ObservableList<Player>> dialogRoot) {
        this.buttonStartTournament.setOnAction(event -> {
            // TODO: ask the user if he really intends to start the tournament
                dialogRoot.exitWith(DialogResult.OK);
            });
    }

    @Override
    public void setProperties(ObservableList<Player> properties) {
        this.tableRegisteredPlayers.setItems(FXCollections
                .observableArrayList());
        this.tableAttendingPlayers
                .setItems(FXCollections.observableArrayList());

        this.tableRegisteredPlayers.getItems().addAll(properties);
    }

    @Override
    public ObservableList<Player> getReturnValue() {
        return this.tableAttendingPlayers.getItems();
    }

    @Override
    public void initModalDialog(
            ModalDialog<ObservableList<Player>, ObservableList<Player>> modalDialog) {
        modalDialog.title("Teilnehmende Spieler").dialogButtons(
                DialogButtons.NONE);
    }

    @FXML
    private void onButtonAddAttendeeClicked(ActionEvent event) {
        ObservableList<Player> selectedPlayers = this.tableRegisteredPlayers
                .getSelectionModel().getSelectedItems();

        for (Player p : selectedPlayers) {
            this.tableRegisteredPlayers.getItems().remove(p);
            this.tableAttendingPlayers.getItems().add(p);
        }
    }

    @FXML
    private void onButtonRemoveAttendeeClicked(ActionEvent event) {
        ObservableList<Player> selectedPlayers = this.tableAttendingPlayers
                .getSelectionModel().getSelectedItems();

        for (Player p : selectedPlayers) {
            this.tableAttendingPlayers.getItems().remove(p);
            this.tableRegisteredPlayers.getItems().add(p);
        }
    }
}
