package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;

public class PairingSwapDialog extends VBox implements
        IModalDialogProvider<ObservableList<Pairing>, ObservableList<Pairing>> {

    private class PairingEntry {
        private Pairing pairing;
        private int index;

        public PairingEntry(Pairing pairing, int index) {
            this.pairing = pairing;
            this.index = index;
        }

        public Pairing getPairing() {
            return pairing;
        }

        public int getIndex() {
            return index;
        }
    }

    private static final Logger log = Logger.getLogger(PairingSwapDialog.class
            .getName());

    @FXML private TableView<Player> tableFirstPairing;
    @FXML private TableView<Player> tableSecondPairing;
    @FXML private Button buttonSwapPlayer;

    @FXML private ComboBox<PairingEntry> comboBoxFirstPairing;
    @FXML private ComboBox<PairingEntry> comboBoxSecondPairing;

    private ChangeListener<PairingEntry> firstComboBoxListener;
    private ChangeListener<PairingEntry> secondComboBoxListener;

    private ObservableList<PairingEntry> pairings;

    public PairingSwapDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/pairing-swap-dialog.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                            .getLanguageBundle());
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    public void initialize() {
        this.pairings = FXCollections.observableArrayList();

        TableColumn<Player, String> firstPairingPlayerColumn = new TableColumn<>(
                PreferencesManager.getInstance().localizeString(
                        "pairingswapdialog.player"));
        firstPairingPlayerColumn.setCellValueFactory(cellValue -> {
            return cellValue
                    .getValue()
                    .firstNameProperty()
                    .concat(" ")
                    .concat(cellValue.getValue().lastNameProperty()
                            .concat(" (")
                            .concat(cellValue.getValue().getStartingNumber())
                            .concat(")"));
        });
        TableColumn<Player, String> secondPairingPlayerColumn = new TableColumn<>(
                PreferencesManager.getInstance().localizeString(
                        "pairingswapdialog.player"));
        secondPairingPlayerColumn.setCellValueFactory(cellValue -> {
            return cellValue
                    .getValue()
                    .firstNameProperty()
                    .concat(" ")
                    .concat(cellValue.getValue().lastNameProperty()
                            .concat(" (")
                            .concat(cellValue.getValue().getStartingNumber())
                            .concat(")"));
        });

        firstPairingPlayerColumn.prefWidthProperty().bind(
                this.tableFirstPairing.widthProperty().subtract(1));
        secondPairingPlayerColumn.prefWidthProperty().bind(
                this.tableSecondPairing.widthProperty().subtract(1));

        this.tableFirstPairing.getColumns().add(firstPairingPlayerColumn);
        this.tableSecondPairing.getColumns().add(secondPairingPlayerColumn);

        this.tableFirstPairing.setPlaceholder(new Text(PreferencesManager
                .getInstance().localizeString("tableplaceholder.noplayers")));
        this.tableSecondPairing.setPlaceholder(new Text(PreferencesManager
                .getInstance().localizeString("tableplaceholder.noplayers")));

        this.comboBoxFirstPairing.setCellFactory(listView -> {
            return new ListCell<PairingEntry>() {
                @Override
                protected void updateItem(PairingEntry item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        this.setGraphic(null);
                        this.setText("");
                    } else {
                        this.setText(PreferencesManager.getInstance()
                                .localizeString("pairingswapdialog.pairing")
                                + " " + String.valueOf(item.getIndex() + 1));
                    }
                }
            };
        });

        this.comboBoxFirstPairing.setButtonCell(new ListCell<PairingEntry>() {
            @Override
            protected void updateItem(PairingEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    this.setGraphic(null);
                    this.setText("");
                } else {
                    this.setText(PreferencesManager.getInstance()
                            .localizeString("pairingswapdialog.pairing")
                            + " "
                            + String.valueOf(item.getIndex() + 1));
                }
            }
        });

        this.comboBoxSecondPairing.setCellFactory(listView -> {
            return new ListCell<PairingEntry>() {
                @Override
                protected void updateItem(PairingEntry item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        this.setGraphic(null);
                        this.setText("");
                    } else {
                        this.setText(PreferencesManager.getInstance()
                                .localizeString("pairingswapdialog.pairing")
                                + " " + String.valueOf(item.getIndex() + 1));
                    }
                }
            };
        });

        this.comboBoxSecondPairing.setButtonCell(new ListCell<PairingEntry>() {
            @Override
            protected void updateItem(PairingEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    this.setGraphic(null);
                    this.setText("");
                } else {
                    this.setText(PreferencesManager.getInstance()
                            .localizeString("pairingswapdialog.pairing")
                            + " "
                            + String.valueOf(item.getIndex() + 1));
                }
            }
        });
    }

    @Override
    public void setProperties(ObservableList<Pairing> properties) {
        this.loadPairingList(properties);
    }

    public void loadPairingList(ObservableList<Pairing> pairings) {
        if (this.pairings.size() > 0) {
            this.unloadPairingList();
        }

        int pairingCount = 0;
        for (Pairing pairing : pairings) {
            this.pairings.add(new PairingEntry((Pairing) pairing.clone(),
                    pairingCount));
            pairingCount++;
        }

        this.firstComboBoxListener = new ChangeListener<PairingEntry>() {
            @Override
            public void changed(ObservableValue<? extends PairingEntry> ov,
                    PairingEntry o, PairingEntry n) {
                tableFirstPairing.setItems(n.getPairing().getOpponents());
            }
        };

        this.secondComboBoxListener = new ChangeListener<PairingEntry>() {
            @Override
            public void changed(ObservableValue<? extends PairingEntry> ov,
                    PairingEntry o, PairingEntry n) {
                tableSecondPairing.setItems(n.getPairing().getOpponents());
            }
        };

        this.comboBoxFirstPairing.setItems(this.pairings);
        this.comboBoxSecondPairing.setItems(this.pairings);

        this.comboBoxFirstPairing.getSelectionModel().selectedItemProperty()
                .addListener(this.firstComboBoxListener);
        this.comboBoxSecondPairing.getSelectionModel().selectedItemProperty()
                .addListener(this.secondComboBoxListener);

        this.buttonSwapPlayer.disableProperty().bind(
                this.tableFirstPairing
                        .getSelectionModel()
                        .selectedItemProperty()
                        .isNull()
                        .or(this.tableSecondPairing.getSelectionModel()
                                .selectedItemProperty().isNull())
                        .or(Bindings.equal(this.comboBoxFirstPairing
                                .getSelectionModel().selectedItemProperty(),
                                this.comboBoxSecondPairing.getSelectionModel()
                                        .selectedItemProperty())));
    }

    public void unloadPairingList() {
        this.comboBoxFirstPairing.getSelectionModel().selectedItemProperty()
                .removeListener(this.firstComboBoxListener);
        this.comboBoxSecondPairing.getSelectionModel().selectedItemProperty()
                .removeListener(this.secondComboBoxListener);

        this.comboBoxFirstPairing.getItems().clear();
        this.comboBoxFirstPairing.getSelectionModel().clearSelection();
        this.comboBoxSecondPairing.getItems().clear();
        this.comboBoxSecondPairing.getSelectionModel().clearSelection();

        this.tableFirstPairing.setItems(null);
        this.tableFirstPairing.getSelectionModel().clearSelection();
        this.tableSecondPairing.setItems(null);
        this.tableSecondPairing.getSelectionModel().clearSelection();

        this.buttonSwapPlayer.disableProperty().unbind();

        this.pairings.clear();
    }

    @Override
    public ObservableList<Pairing> getReturnValue() {
        ObservableList<Pairing> newPairings = FXCollections
                .observableArrayList();
        for (PairingEntry pairingEntry : this.pairings) {
            newPairings.add(pairingEntry.getPairing());
        }
        return newPairings;
    }

    @Override
    public void initModalDialog(
            ModalDialog<ObservableList<Pairing>, ObservableList<Pairing>> modalDialog) {
        modalDialog.title("pairingswapdialog.title").dialogButtons(
                DialogButtons.OK_CANCEL);
    }

    @FXML
    private void onButtonSwapPlayerClicked(ActionEvent event) {
        PairingEntry firstPairing = this.comboBoxFirstPairing
                .getSelectionModel().getSelectedItem();
        PairingEntry secondPairing = this.comboBoxSecondPairing
                .getSelectionModel().getSelectedItem();
        if (firstPairing.getIndex() != secondPairing.getIndex()) {
            Player firstPlayer = this.tableFirstPairing.getSelectionModel()
                    .getSelectedItem();
            Player secondPlayer = this.tableSecondPairing.getSelectionModel()
                    .getSelectedItem();
            PlayerScore firstScoreTable = null;
            for (PlayerScore score : firstPairing.getPairing().getScoreTable()) {
                if (score.getPlayer().getId().equals(firstPlayer.getId())) {
                    firstScoreTable = score;
                    break;
                }
            }
            PlayerScore secondScoreTable = null;
            for (PlayerScore score : secondPairing.getPairing().getScoreTable()) {
                if (score.getPlayer().getId().equals(secondPlayer.getId())) {
                    secondScoreTable = score;
                    break;
                }
            }

            firstPairing.getPairing().getOpponents().remove(firstPlayer);
            firstPairing.getPairing().getOpponents().add(secondPlayer);
            firstPairing.getPairing().getScoreTable().remove(firstScoreTable);
            firstPairing.getPairing().getScoreTable().add(secondScoreTable);

            secondPairing.getPairing().getOpponents().remove(secondPlayer);
            secondPairing.getPairing().getOpponents().add(firstPlayer);
            secondPairing.getPairing().getScoreTable().remove(secondScoreTable);
            secondPairing.getPairing().getScoreTable().add(firstScoreTable);

            this.tableFirstPairing.getSelectionModel().clearSelection();
            this.tableSecondPairing.getSelectionModel().clearSelection();
        }
    }
}
