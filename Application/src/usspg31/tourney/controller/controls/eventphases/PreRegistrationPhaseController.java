package usspg31.tourney.controller.controls.eventphases;

import java.util.Comparator;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import usspg31.tourney.controller.MainWindow;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.EventUser;
import usspg31.tourney.controller.controls.MaterialTextField;
import usspg31.tourney.controller.dialogs.PlayerPreRegistrationDialog;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.controller.util.SearchUtilities;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.IdentificationManager;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.undo.UndoManager;

public class PreRegistrationPhaseController implements EventUser {

    private static final Logger log = Logger
            .getLogger(PreRegistrationPhaseController.class.getName());

    @FXML private MaterialTextField textFieldPlayerSearch;
    @FXML private TableView<Player> tablePreRegisteredPlayers;
    @FXML private Button buttonAddPlayer;
    @FXML private Button buttonRemovePlayer;
    @FXML private Button buttonEditPlayer;

    private TableColumn<Player, String> tableColumnPlayerFirstName;
    private TableColumn<Player, String> tableColumnPlayerLastName;
    private TableColumn<Player, String> tableColumnPlayerNickName;
    private TableColumn<Player, String> tableColumnPlayerMailAddress;

    private Event loadedEvent;

    private ModalDialog<Object, Player> preRegistrationDialog;

    @FXML
    private void initialize() {
        this.preRegistrationDialog = new PlayerPreRegistrationDialog()
                .modalDialog();

        this.initPlayerTable();
    }

    private void initPlayerTable() {
        this.tableColumnPlayerFirstName = new TableColumn<>(PreferencesManager
                .getInstance().localizeString(
                        "preregistrationphase.player.firstname"));
        this.tableColumnPlayerFirstName
                .setCellValueFactory(cellData -> cellData.getValue()
                        .firstNameProperty());
        this.tablePreRegisteredPlayers.getColumns().add(
                this.tableColumnPlayerFirstName);

        this.tableColumnPlayerLastName = new TableColumn<>(PreferencesManager
                .getInstance().localizeString(
                        "preregistrationphase.player.lastname"));
        this.tableColumnPlayerLastName.setCellValueFactory(cellData -> cellData
                .getValue().lastNameProperty());
        this.tablePreRegisteredPlayers.getColumns().add(
                this.tableColumnPlayerLastName);

        this.tableColumnPlayerNickName = new TableColumn<>(PreferencesManager
                .getInstance().localizeString(
                        "preregistrationphase.player.nickname"));
        this.tableColumnPlayerNickName.setCellValueFactory(cellData -> cellData
                .getValue().nickNameProperty());
        this.tablePreRegisteredPlayers.getColumns().add(
                this.tableColumnPlayerNickName);

        this.tableColumnPlayerMailAddress = new TableColumn<>(
                PreferencesManager.getInstance().localizeString(
                        "preregistrationphase.player.mail"));
        this.tableColumnPlayerMailAddress
                .setCellValueFactory(cellData -> cellData.getValue()
                        .mailAdressProperty());
        this.tablePreRegisteredPlayers.getColumns().add(
                this.tableColumnPlayerMailAddress);

        this.tablePreRegisteredPlayers.setPlaceholder(new Text(
                PreferencesManager.getInstance().localizeString(
                        "tableplaceholder.noplayers")));

        /* Edit the player on double click */
        this.tablePreRegisteredPlayers.setRowFactory(tableView -> {
            TableRow<Player> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    this.editPlayer(row.getItem());
                }
            });
            return row;
        });
    }

    @Override
    public void loadEvent(Event event) {
        log.info("Loading Event");
        if (this.loadedEvent != null) {
            this.unloadEvent();
        }

        this.tablePreRegisteredPlayers.getSelectionModel().clearSelection();

        this.loadedEvent = event;

        /* Add all registered players to the table view and enable searching */
        Comparator<Player> comparator = (firstPlayer, lastPlayer) -> (int) (SearchUtilities.getSearchRelevance(firstPlayer,
                PreRegistrationPhaseController.this.textFieldPlayerSearch.getText()) - SearchUtilities
                .getSearchRelevance(lastPlayer,
                        PreRegistrationPhaseController.this.textFieldPlayerSearch.getText()));

        this.textFieldPlayerSearch.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    FXCollections.sort(
                            this.tablePreRegisteredPlayers.getItems(),
                            comparator);
                });

        this.tablePreRegisteredPlayers.setItems(this.loadedEvent
                .getRegisteredPlayers());

        /* Set the inital column widths */
        this.tableColumnPlayerFirstName.prefWidthProperty().set(
                this.tablePreRegisteredPlayers.widthProperty().get() * 0.25);
        this.tableColumnPlayerLastName.prefWidthProperty().set(
                this.tablePreRegisteredPlayers.widthProperty().get() * 0.25);
        this.tableColumnPlayerMailAddress.prefWidthProperty().set(
                this.tablePreRegisteredPlayers.widthProperty().get() * 0.25);
        this.tableColumnPlayerNickName.prefWidthProperty().set(
                this.tablePreRegisteredPlayers.widthProperty().get() * 0.25);

        /* Bind the button's availability to the list selection */
        this.buttonRemovePlayer.disableProperty().bind(
                this.tablePreRegisteredPlayers.getSelectionModel()
                        .selectedItemProperty().isNull());
        this.buttonEditPlayer.disableProperty().bind(
                this.tablePreRegisteredPlayers.getSelectionModel()
                        .selectedItemProperty().isNull());
    }

    @Override
    public void unloadEvent() {
        log.info("Unloading Event");
        if (this.loadedEvent == null) {
            log.warning("Trying to unload an event, even though no event was loaded");
            return;
        }

        /* Unbind the player table */
        this.tablePreRegisteredPlayers.getSelectionModel().clearSelection();

        /* Unbind the buttons */
        this.buttonRemovePlayer.disableProperty().unbind();
        this.buttonEditPlayer.disableProperty().unbind();

        this.loadedEvent = null;
    }

    @FXML
    private void onButtonAddPlayerClicked(ActionEvent event) {
        log.fine("Add Player Button clicked");
        this.checkEventLoaded();
        this.preRegistrationDialog
        .properties(new Player())
        .properties(this.loadedEvent)
        .onResult((result, returnValue) -> {
            if (result == DialogResult.OK && returnValue != null) {
                returnValue.setId(IdentificationManager.generateId(returnValue));
                this.loadedEvent.getRegisteredPlayers().add(returnValue);
            }
        }).show();
    }

    @FXML
    private void onButtonRemovePlayerClicked(ActionEvent event) {
        // TODO: no-one should be able to remove a player that has already
        // played in a tournament

        log.fine("Remove Player Button clicked");
        this.checkEventLoaded();

        Player selectedPlayer = this.tablePreRegisteredPlayers
                .getSelectionModel().getSelectedItem();
        if (selectedPlayer == null) {
            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "dialogs.messages.noplayerchosen")).modalDialog()
                    .dialogButtons(DialogButtons.OK)
                    .title("dialogs.titles.error").show();
        } else {
            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "preregistrationphase.dialogs.delete.before")
                    + " \""
                    + selectedPlayer.getFirstName()
                    + " "
                    + selectedPlayer.getLastName()
                    + "\" "
                    + PreferencesManager.getInstance().localizeString(
                            "preregistrationphase.dialogs.delete.after"))
                    .modalDialog()
                    .dialogButtons(DialogButtons.YES_NO)
                    .title("preregistrationphase.dialogs.delete.title")
                    .onResult(
                            (result, returnValue) -> {
                                if (result == DialogResult.YES) {
                                    this.loadedEvent.getRegisteredPlayers()
                                            .remove(selectedPlayer);
                                }
                            }).show();
        }
    }

    @FXML
    private void onButtonEditPlayerClicked(ActionEvent event) {
        log.fine("Edit Player Button clicked");
        this.checkEventLoaded();

        final Player selectedPlayer = this.tablePreRegisteredPlayers
                .getSelectionModel().getSelectedItem();
        if (selectedPlayer == null) {
            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "dialogs.messages.noplayerchosen")).modalDialog()
                    .dialogButtons(DialogButtons.OK)
                    .title("dialogs.titles.error").show();
        } else {
            this.editPlayer(selectedPlayer);
        }
    }

    /**
     * Open a dialog to edit the chosen player
     *
     * @param player
     *            Player to be edited
     */
    public void editPlayer(Player player) {
        this.preRegistrationDialog
        .properties(player)
        .properties(this.loadedEvent)
        .onResult((result, returnValue) -> {
            if (result == DialogResult.OK  && returnValue != null) {
                UndoManager undo = MainWindow.getInstance()
                        .getEventPhaseViewController().getUndoManager();
                undo.beginUndoBatch();
                this.loadedEvent.getRegisteredPlayers().remove(player);
                this.loadedEvent.getRegisteredPlayers().add(returnValue);
                undo.endUndoBatch();
            }
        }).show();
    }

    private void checkEventLoaded() {
        if (this.loadedEvent == null) {
            throw new IllegalStateException("An Event must be loaded in order "
                    + "to perform actions on this controller");
        }
    }
}
