package usspg31.tourney.controller.controls.eventphases;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import usspg31.tourney.controller.EntryPoint;
import usspg31.tourney.controller.controls.EventUser;
import usspg31.tourney.controller.dialogs.PlayerPreRegistrationDialog;
import usspg31.tourney.controller.dialogs.RegistrationDistributionDialog;
import usspg31.tourney.controller.dialogs.RegistrationDistributionNumberSelectionDialog;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.controller.util.SearchUtilities;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.Event.UserFlag;
import usspg31.tourney.model.IdentificationManager;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.filemanagement.FileLoader;
import usspg31.tourney.model.filemanagement.FileSaver;

public class RegistrationPhaseController implements EventUser {

    private static final Logger log = Logger
            .getLogger(RegistrationPhaseController.class.getName());

    @FXML private TextField textFieldPlayerSearch;
    @FXML private TableView<Player> tableRegisteredPlayers;
    @FXML private Button buttonAddPlayer;
    @FXML private Button buttonRemovePlayer;
    @FXML private Button buttonEditPlayer;
    @FXML private Button buttonRegisterPlayer;
    @FXML private Button buttonUnregisterPlayer;

    @FXML private Button buttonDistributeRegistration;
    @FXML private Button buttonImportRegistration;

    private TableColumn<Player, String> tableColumnPlayerFirstName;
    private TableColumn<Player, String> tableColumnPlayerLastName;
    private TableColumn<Player, String> tableColumnPlayerNickName;
    private TableColumn<Player, String> tableColumnPlayerMailAddress;
    private TableColumn<Player, Boolean> tableColumnPlayerPayed;
    private TableColumn<Player, String> tableColumnPlayerStartNumber;

    private ModalDialog<Object, Player> registrationDialog;
    private ModalDialog<String, Integer> distributionDialog;
    private ModalDialog<Integer, Integer> distributionNumberSelectionDialog;

    private Event loadedEvent;
    private int registratorNumber = 1;
    private File loadedEventFile;

    @FXML
    private void initialize() {
        this.registrationDialog = new PlayerPreRegistrationDialog()
                .modalDialog().title(
                        "dialogs.playerpreregistration.registration");
        this.distributionDialog = new RegistrationDistributionDialog()
                .modalDialog();
        this.distributionNumberSelectionDialog = new RegistrationDistributionNumberSelectionDialog()
                .modalDialog();
    }

    @Override
    public void loadEvent(Event event) {
        log.info("Loading Event");
        if (this.loadedEvent != null) {
            this.unloadEvent();
        }

        this.loadedEvent = event;

        if (this.loadedEvent.getUserFlag() == UserFlag.REGISTRATION) {
            this.buttonDistributeRegistration.setDisable(true);
            this.buttonImportRegistration.setDisable(true);

            this.distributionNumberSelectionDialog
                    .properties(this.loadedEvent.getNumberOfRegistrators())
                    .onResult((result, returnValue) -> {
                        if (result != DialogResult.OK) {
                            return;
                        }
                        this.registratorNumber = returnValue;
                    }).show();
        }

        this.tableRegisteredPlayers.getSelectionModel().clearSelection();

        this.initPlayerTable();

        // Add all registered players to the table view and enable searching
        FilteredList<Player> filteredPlayerList = new FilteredList<>(
                this.loadedEvent.getRegisteredPlayers(), p -> true);

        this.textFieldPlayerSearch.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    filteredPlayerList.setPredicate(player -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        return SearchUtilities.fuzzyMatches(
                                player.getFirstName(), newValue)
                                || SearchUtilities.fuzzyMatches(
                                        player.getLastName(), newValue)
                                || SearchUtilities.fuzzyMatches(
                                        player.getNickName(), newValue)
                                || SearchUtilities.fuzzyMatches(
                                        player.getMailAddress(), newValue);
                    });
                });

        SortedList<Player> sortedPlayerList = new SortedList<>(
                filteredPlayerList);
        sortedPlayerList.comparatorProperty().bind(
                this.tableRegisteredPlayers.comparatorProperty());

        this.tableRegisteredPlayers.setItems(sortedPlayerList);

        // Bind the button's availability to the list selection
        this.buttonRemovePlayer.disableProperty().bind(
                this.tableRegisteredPlayers.getSelectionModel()
                        .selectedItemProperty().isNull());
        this.buttonEditPlayer.disableProperty().bind(
                this.tableRegisteredPlayers.getSelectionModel()
                        .selectedItemProperty().isNull());

        /*
         * Bind the availability of the register and unregister buttons to
         * whether a player from the list is selected and is registered
         */
        this.tableRegisteredPlayers
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (ChangeListener<Player>) (arg0, oldVal, newVal) -> {
                            if (newVal != null) {
                                if (newVal.getStartingNumber().equals("")) {
                                    RegistrationPhaseController.this.buttonRegisterPlayer
                                            .setDisable(false);
                                    RegistrationPhaseController.this.buttonUnregisterPlayer
                                            .setDisable(true);
                                } else {
                                    RegistrationPhaseController.this.buttonRegisterPlayer
                                            .setDisable(true);
                                    RegistrationPhaseController.this.buttonUnregisterPlayer
                                            .setDisable(false);
                                }
                            }
                        });

        this.buttonRegisterPlayer.setDisable(true);
        this.buttonUnregisterPlayer.setDisable(true);
    }

    @Override
    public void unloadEvent() {
        log.info("Unloading Event");
        if (this.loadedEvent == null) {
            log.warning("Trying to unload an event, even though no event was loaded");
            return;
        }

        // TODO: unregister all listeners we registered to anything in the event
        this.tableRegisteredPlayers.getColumns().clear();

        this.loadedEvent = null;
    }

    private void initPlayerTable() {
        this.tableColumnPlayerFirstName = new TableColumn<>("Vorname");
        this.tableColumnPlayerFirstName
                .setCellValueFactory(cellData -> cellData.getValue()
                        .firstNameProperty());
        this.tableColumnPlayerFirstName.setEditable(false);
        this.tableRegisteredPlayers.getColumns().add(
                this.tableColumnPlayerFirstName);

        this.tableColumnPlayerLastName = new TableColumn<>("Nachname");
        this.tableColumnPlayerLastName.setCellValueFactory(cellData -> cellData
                .getValue().lastNameProperty());
        this.tableColumnPlayerLastName.setEditable(false);
        this.tableRegisteredPlayers.getColumns().add(
                this.tableColumnPlayerLastName);

        this.tableColumnPlayerNickName = new TableColumn<>("Nickname");
        this.tableColumnPlayerNickName.setCellValueFactory(cellData -> cellData
                .getValue().nickNameProperty());
        this.tableColumnPlayerNickName.setEditable(false);
        this.tableRegisteredPlayers.getColumns().add(
                this.tableColumnPlayerNickName);

        this.tableColumnPlayerMailAddress = new TableColumn<>("E-Mail");
        this.tableColumnPlayerMailAddress
                .setCellValueFactory(cellData -> cellData.getValue()
                        .mailAdressProperty());
        this.tableColumnPlayerMailAddress.setEditable(false);
        this.tableRegisteredPlayers.getColumns().add(
                this.tableColumnPlayerMailAddress);

        this.tableColumnPlayerPayed = new TableColumn<>("Bezahlt");
        this.tableColumnPlayerPayed
                .setCellValueFactory(new PropertyValueFactory<Player, Boolean>(
                        "payed"));
        this.tableColumnPlayerPayed.setCellFactory(CheckBoxTableCell
                .forTableColumn(this.tableColumnPlayerPayed));
        this.tableColumnPlayerPayed.setEditable(true);
        this.tableRegisteredPlayers.getColumns().add(
                this.tableColumnPlayerPayed);

        this.tableColumnPlayerStartNumber = new TableColumn<Player, String>(
                "Startnummer");
        this.tableColumnPlayerStartNumber
                .setCellValueFactory(cellData -> cellData.getValue()
                        .startingNumberProperty());
        this.tableColumnPlayerStartNumber.setCellFactory(column -> {
            return new TableCell<Player, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        if (item.equals("")) {
                            this.setText("Nicht anwesend");
                        } else {
                            this.setText(item);
                        }
                    }
                }
            };
        });
        this.tableColumnPlayerStartNumber.setEditable(false);
        this.tableRegisteredPlayers.getColumns().add(
                this.tableColumnPlayerStartNumber);

        this.tableRegisteredPlayers.setEditable(true);
    }

    @FXML
    private void onButtonAddPlayerClicked(ActionEvent event) {
        log.fine("Add Player Button clicked");
        this.checkEventLoaded();
        this.registrationDialog
                .properties(new Player())
                .properties(this.loadedEvent)
                .onResult(
                        (result, returnValue) -> {
                            if (result == DialogResult.OK
                                    && returnValue != null) {
                                returnValue.setId(IdentificationManager
                                        .generateId(returnValue));
                                this.loadedEvent.getRegisteredPlayers().add(
                                        returnValue);
                            }
                        }).show();
    }

    @FXML
    private void onButtonRemovePlayerClicked(ActionEvent event) {
        log.fine("Remove Player Button clicked");
        this.checkEventLoaded();

        Player selectedPlayer = this.tableRegisteredPlayers.getSelectionModel()
                .getSelectedItem();
        if (selectedPlayer == null) {
            new SimpleDialog<>(
                    "Bitte wählen Sie einen Spieler aus der Liste aus.")
                    .modalDialog().dialogButtons(DialogButtons.OK)
                    .title("Fehler").show();
        } else {
            new SimpleDialog<>("Wollen Sie den Spieler \""
                    + selectedPlayer.getFirstName() + " "
                    + selectedPlayer.getLastName() + "\" wirklich löschen?")
                    .modalDialog()
                    .dialogButtons(DialogButtons.YES_NO)
                    .title("Löschen bestätigen")
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

        final Player selectedPlayer = this.tableRegisteredPlayers
                .getSelectionModel().getSelectedItem();
        if (selectedPlayer == null) {
            new SimpleDialog<>(
                    "Bitte wählen Sie einen Spieler aus der Liste aus.")
                    .modalDialog().dialogButtons(DialogButtons.OK)
                    .title("Fehler").show();
        } else {
            this.registrationDialog
                    .properties(selectedPlayer)
                    .properties(this.loadedEvent)
                    .onResult(
                            (result, returnValue) -> {
                                if (result == DialogResult.OK
                                        && returnValue != null) {
                                    this.loadedEvent.getRegisteredPlayers()
                                            .remove(selectedPlayer);
                                    this.loadedEvent.getRegisteredPlayers()
                                            .add(returnValue);
                                }
                            }).show();
        }
    }

    // TODO: fix the dialogs
    @FXML
    private void onButtonRegisterPlayerClicked(ActionEvent event) {
        log.fine("Register Player Button clicked");
        this.checkEventLoaded();

        Player selectedPlayer = this.tableRegisteredPlayers.getSelectionModel()
                .getSelectedItem();
        if (selectedPlayer == null) {
            new SimpleDialog<>(
                    "Bitte wählen Sie einen Spieler aus der Liste aus.")
                    .modalDialog().dialogButtons(DialogButtons.OK)
                    .title("Fehler").show();
        } else {
            if (!selectedPlayer.getStartingNumber().equals("")) {
                new SimpleDialog<>(
                        "Dieser Spieler wurde bereits registriert und hat eine Startnummer erhalten.")
                        .modalDialog().dialogButtons(DialogButtons.OK)
                        .title("Fehler").show();
                return;
            }

            /*
             * Get the currently highest starting number to generate the next
             * one
             */
            int highestStartingNumber = 0;
            for (Player player : this.loadedEvent.getRegisteredPlayers()) {
                if (!player.getStartingNumber().equals("")) {
                    if (Integer.parseInt(player.getStartingNumber()) > highestStartingNumber) {
                        highestStartingNumber = Integer.parseInt(player
                                .getStartingNumber());
                    }
                }
            }

            /*
             * Ask the user for confirmation and register the player if
             * necessary
             */
            if (highestStartingNumber == 0) {
                final int currentStartingNumber = this.registratorNumber;
                new SimpleDialog<>("Wollen Sie den Spieler \""
                        + selectedPlayer.getFirstName() + " "
                        + selectedPlayer.getLastName()
                        + "\" wirklich als anwesend markieren?")
                        .modalDialog()
                        .dialogButtons(DialogButtons.YES_NO)
                        .title("Registrieren mit der Startnummer "
                                + currentStartingNumber + " bestätigen", false)
                        .onResult(
                                (result, returnValue) -> {
                                    if (result == DialogResult.YES) {
                                        selectedPlayer.setStartingNumber(String
                                                .valueOf(currentStartingNumber));
                                        this.buttonRegisterPlayer
                                                .setDisable(true);
                                        this.buttonUnregisterPlayer
                                                .setDisable(false);
                                    }
                                }).show();
            } else {
                final int currentStartingNumber = highestStartingNumber
                        + Math.max(this.loadedEvent.getNumberOfRegistrators(),
                                1);
                new SimpleDialog<>("Wollen Sie den Spieler \""
                        + selectedPlayer.getFirstName() + " "
                        + selectedPlayer.getLastName()
                        + "\" wirklich als anwesend markieren?")
                        .modalDialog()
                        .dialogButtons(DialogButtons.YES_NO)
                        .title("Registrieren mit der Startnummer "
                                + currentStartingNumber + " bestätigen", false)
                        .onResult(
                                (result, returnValue) -> {
                                    if (result == DialogResult.YES) {
                                        selectedPlayer.setStartingNumber(String
                                                .valueOf(currentStartingNumber));
                                        this.buttonRegisterPlayer
                                                .setDisable(true);
                                        this.buttonUnregisterPlayer
                                                .setDisable(false);
                                    }
                                }).show();
            }
        }
    }

    @FXML
    private void onButtonUnregisterPlayerClicked(ActionEvent event) {
        log.fine("Unregister Player Button clicked");
        this.checkEventLoaded();

        Player selectedPlayer = this.tableRegisteredPlayers.getSelectionModel()
                .getSelectedItem();
        if (selectedPlayer == null) {
            new SimpleDialog<>(
                    "Bitte wählen Sie einen Spieler aus der Liste aus.")
                    .modalDialog().dialogButtons(DialogButtons.OK)
                    .title("dialogs.titles.error").show();
        } else {
            if (selectedPlayer.getStartingNumber().equals("")) {
                new SimpleDialog<>(
                        "Dieser Spieler ist noch nicht registriert und kann daher nicht abgemeldet werden.")
                        .modalDialog().dialogButtons(DialogButtons.OK)
                        .title("dialogs.titles.error").show();
                return;
            }

            /*
             * Ask the user for confirmation and unregister the player if
             * necessary
             */
            new SimpleDialog<>("Wollen Sie den Spieler \""
                    + selectedPlayer.getFirstName() + " "
                    + selectedPlayer.getLastName()
                    + "\" wirklich als abwesend markieren?").modalDialog()
                    .dialogButtons(DialogButtons.YES_NO)
                    .title("registrationphase.dialogs.unregister.title")
                    .onResult((result, returnValue) -> {
                        if (result == DialogResult.YES) {
                            selectedPlayer.setStartingNumber("");
                            this.buttonRegisterPlayer.setDisable(false);
                            this.buttonUnregisterPlayer.setDisable(true);
                        }
                    }).show();
        }
    }

    @FXML
    private void onButtonDistributeRegistrationClicked(ActionEvent event) {
        log.fine("Distribute Registration Button clicked");

        for (Player player : this.loadedEvent.getRegisteredPlayers()) {
            if (!player.getStartingNumber().equals("")) {
                new SimpleDialog<>(
                        "Damit sichergestellt werden kann, dass alle Arbeitsplätze unterschiedliche Startnummern verteilen,\n"
                                + "kann die Anmeldung nur verteilt werden, wenn noch keine anwesenden Spieler registriert wurden.")
                        .modalDialog().dialogButtons(DialogButtons.OK)
                        .title("Fehler").show();
                return;
            }
        }

        this.distributionDialog
                .onResult(
                        (result, returnValue) -> {
                            if (result != DialogResult.OK) {
                                return;
                            }

                            FileChooser fileChooser = new FileChooser();
                            fileChooser
                                    .setTitle("Eventdatei für andere Anmeldungsarbeitsplätze speichern");
                            fileChooser.getExtensionFilters().add(
                                    new ExtensionFilter(
                                            "Tourney Eventdatei (*.tef)",
                                            "*.tef"));
                            File selectedFile = fileChooser
                                    .showSaveDialog(EntryPoint
                                            .getPrimaryStage());
                            if (selectedFile == null) {
                                return;
                            }
                            if (!selectedFile.getName().endsWith(".tef")) {
                                selectedFile = new File(selectedFile
                                        .getAbsolutePath() + ".tef");
                            }

                            this.loadedEvent.setUserFlag(UserFlag.REGISTRATION);
                            this.loadedEvent
                                    .setNumberOfRegistrators(returnValue);

                            try {
                                FileSaver.saveEventToFile(this.loadedEvent,
                                        selectedFile.getAbsolutePath());
                            } catch (Exception e) {
                                log.log(Level.SEVERE,
                                        "Could not save the event.", e);

                                new SimpleDialog<>(
                                        "Das Event konnte nicht gespeichert werden.\n"
                                                + "Bitte stellen Sie sicher, dass Sie für die Zieldatei "
                                                + "alle Berechtigungen besitzen.")
                                        .modalDialog().title("Fehler").show();
                            }

                            this.loadedEvent
                                    .setUserFlag(UserFlag.ADMINISTRATION);
                            FileSaver.saveEventToFile(this.loadedEvent, this
                                    .getLoadedEventFile().getAbsolutePath());
                        }).show();
    }

    @FXML
    private void onButtonImportRegistrationClicked(ActionEvent event) {
        log.fine("Merge Registration Button clicked");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Anmeldedaten aus Eventdatei importieren");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("Tourney Eventdateien (*.tef)", "*.tef"));
        File selectedFile = fileChooser.showOpenDialog(EntryPoint
                .getPrimaryStage());
        if (selectedFile != null) {
            Event importedEvent = null;

            try {
                importedEvent = FileLoader.loadEventFromFile(selectedFile
                        .getAbsolutePath());
            } catch (Exception e) {
                log.log(Level.SEVERE, "Could not load the specified event.", e);
                new SimpleDialog<>(
                        "Die Eventdatei \""
                                + selectedFile.getName()
                                + "\" konnte nicht geladen werden.\nBitte stellen Sie sicher, "
                                + "dass es sich dabei um eine gültige Eventdatei handelt.")
                        .modalDialog().dialogButtons(DialogButtons.OK)
                        .title("Fehler").show();
            }

            if (importedEvent != null) {
                ArrayList<Player> newPlayers = new ArrayList<Player>();
                /* Add all players that were already existent */
                loaded_players: for (Player loadedPlayer : importedEvent
                        .getRegisteredPlayers()) {
                    for (Player player : this.loadedEvent
                            .getRegisteredPlayers()) {
                        if (player.getId().equals(loadedPlayer.getId())) {
                            if (player.getStartingNumber().equals("")
                                    && !loadedPlayer.getStartingNumber()
                                            .equals("")) {
                                /*
                                 * The loaded player has a starting number and
                                 * should be preferred compared to the existent
                                 * player that has none
                                 */
                                this.loadedEvent.getRegisteredPlayers().remove(
                                        player);
                                this.loadedEvent.getRegisteredPlayers().add(
                                        loadedPlayer);
                            }

                            continue loaded_players;
                        }
                    }
                    newPlayers.add(loadedPlayer);
                }

                /* Add all new players from the imported event */
                for (Player loadedPlayer : newPlayers) {
                    this.loadedEvent.getRegisteredPlayers().add(loadedPlayer);
                }

                new SimpleDialog<>(
                        "Alle Spielerdaten wurden erfolgreich importiert.")
                        .modalDialog().dialogButtons(DialogButtons.OK)
                        .title("Import der Anmeldungen").show();
            }
        }
    }

    private void checkEventLoaded() {
        if (this.loadedEvent == null) {
            throw new IllegalStateException("An event must be loaded in order "
                    + "to perform actions on this controller");
        }
    }

    public File getLoadedEventFile() {
        return this.loadedEventFile;
    }

    public void setLoadedEventFile(File loadedEventFile) {
        this.loadedEventFile = loadedEventFile;
    }
}
