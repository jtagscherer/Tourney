package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
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
        TableColumn<Player, String> attendingPlayerNameColumn = new TableColumn<>(
                PreferencesManager.getInstance().localizeString(
                        "dialogs.attendancedialog.playername"));
        attendingPlayerNameColumn.setCellValueFactory(cellValue -> {
            return cellValue.getValue().firstNameProperty().concat(" ")
                    .concat(cellValue.getValue().lastNameProperty());
        });
        TableColumn<Player, String> registeredPlayerNameColumn = new TableColumn<>(
                PreferencesManager.getInstance().localizeString(
                        "dialogs.attendancedialog.playername"));
        registeredPlayerNameColumn.setCellValueFactory(cellValue -> {
            return cellValue.getValue().firstNameProperty().concat(" ")
                    .concat(cellValue.getValue().lastNameProperty());
        });

        this.tableRegisteredPlayers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.tableAttendingPlayers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        this.tableAttendingPlayers.getColumns().add(attendingPlayerNameColumn);
        this.tableRegisteredPlayers.getColumns()
                .add(registeredPlayerNameColumn);

        this.tableAttendingPlayers.setPlaceholder(new Text(PreferencesManager
                .getInstance().localizeString("tableplaceholder.noplayers")));
        this.tableRegisteredPlayers.setPlaceholder(new Text(PreferencesManager
                .getInstance().localizeString("tableplaceholder.noplayers")));
    }

    @Override
    public void setProperties(ObservableList<Player> properties) {
        this.loadPlayerList(properties);
    }

    public void loadPlayerList(ObservableList<Player> players) {
        this.unloadPlayerList();

        /* Set the items of the tables */
        this.tableRegisteredPlayers.setItems(FXCollections
                .observableArrayList());
        this.tableAttendingPlayers
                .setItems(FXCollections.observableArrayList());

        this.tableRegisteredPlayers.getItems().addAll(players);

        /* Bind the button's availability */
        this.buttonAddAttendee.disableProperty().bind(
                this.tableRegisteredPlayers.getSelectionModel()
                        .selectedItemProperty().isNull());
        this.buttonRemoveAttendee.disableProperty().bind(
                this.tableAttendingPlayers.getSelectionModel()
                        .selectedItemProperty().isNull());
    }

    public void unloadPlayerList() {
        /* Unbind the table of attending players */
        this.tableAttendingPlayers.getSelectionModel().clearSelection();

        /* Unbind the table of registered players */
        this.tableRegisteredPlayers.getSelectionModel().clearSelection();

        /* Unbind the button's availability */
        this.buttonAddAttendee.disableProperty().unbind();
        this.buttonRemoveAttendee.disableProperty().unbind();
    }

    @Override
    public ObservableList<Player> getReturnValue() {
        return this.tableAttendingPlayers.getItems();
    }

    @Override
    public void initModalDialog(
            ModalDialog<ObservableList<Player>, ObservableList<Player>> modalDialog) {
        modalDialog.title("dialogs.attendancedialog.attendingplayers")
                .dialogButtons(DialogButtons.OK_CANCEL);
    }

    @Override
    public String getInputErrorString() {
        if (this.tableAttendingPlayers.getItems().size() == 0) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.attendancedialog.errors.noattendingplayers");
        }
        return null;
    }

    @FXML
    private void onButtonAddAttendeeClicked(ActionEvent event) {
        List<Player> selectedPlayers = new ArrayList<>(
                this.tableRegisteredPlayers.getSelectionModel().getSelectedItems());

        while (selectedPlayers.size() > 0) {
            Player p = selectedPlayers.remove(0);
            this.tableAttendingPlayers.getItems().add(p);
            this.tableRegisteredPlayers.getItems().remove(p);
        }
    }

    @FXML
    private void onButtonRemoveAttendeeClicked(ActionEvent event) {
        List<Player> selectedPlayers = new ArrayList<>(
                this.tableAttendingPlayers.getSelectionModel().getSelectedItems());

        while (selectedPlayers.size() > 0) {
            Player p = selectedPlayers.remove(0);
            this.tableAttendingPlayers.getItems().remove(p);
            this.tableRegisteredPlayers.getItems().add(p);
        }
    }
}
