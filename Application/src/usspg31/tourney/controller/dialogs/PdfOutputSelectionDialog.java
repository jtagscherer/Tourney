package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.dialogs.PdfOutputConfiguration.TournamentEntry;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;

public class PdfOutputSelectionDialog extends VBox implements
        IModalDialogProvider<PdfOutputConfiguration, PdfOutputConfiguration> {

    private static final Logger log = Logger
            .getLogger(PdfOutputSelectionDialog.class.getName());

    @FXML private CheckBox checkBoxExportPlayers;
    @FXML private CheckBox checkBoxExportTournaments;
    @FXML private TableView<TournamentEntry> tableTournaments;

    private PdfOutputConfiguration loadedConfiguration;

    public PdfOutputSelectionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/pdf-output-selection-dialog.fxml"),
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
    private void initialize() {
        TableColumn<TournamentEntry, String> tournamentNameColumn = new TableColumn<>(
                PreferencesManager.getInstance().localizeString(
                        "dialogs.pdfoutputselection.table.tournamentname"));
        tournamentNameColumn.setCellValueFactory(cellValue -> {
            return cellValue.getValue().getTournamentProperty().getValue()
                    .nameProperty();
        });
        tournamentNameColumn.setEditable(false);

        TableColumn<TournamentEntry, Boolean> exportColumn = new TableColumn<>(
                PreferencesManager.getInstance().localizeString(
                        "dialogs.pdfoutputselection.table.export"));
        exportColumn.setCellValueFactory(cellValue -> {
            return cellValue.getValue().getExportedProperty();
        });
        exportColumn.setCellFactory(CheckBoxTableCell
                .forTableColumn(exportColumn));
        exportColumn.setEditable(true);

        this.tableTournaments.getColumns().add(tournamentNameColumn);
        this.tableTournaments.getColumns().add(exportColumn);

        this.tableTournaments.setEditable(true);

        this.tableTournaments
                .setPlaceholder(new Text(PreferencesManager.getInstance()
                        .localizeString("tableplaceholder.notournaments")));
    }

    @Override
    public void setProperties(PdfOutputConfiguration properties) {
        this.loadConfiguration(properties);
    }

    private void loadConfiguration(PdfOutputConfiguration configuration) {
        if (this.loadedConfiguration != null) {
            this.unloadConfiguration();
        }

        this.loadedConfiguration = configuration;

        /* Bind the general settings */
        this.checkBoxExportPlayers.selectedProperty().bindBidirectional(
                this.loadedConfiguration.playerListProperty());
        this.checkBoxExportTournaments.selectedProperty().bindBidirectional(
                this.loadedConfiguration.tournamentProperty());

        /* Bind the table availability */
        this.tableTournaments.disableProperty().bind(
                this.loadedConfiguration.tournamentProperty().not());

        /* Fill the table */
        this.tableTournaments.setItems(this.loadedConfiguration
                .getTournamentList());
    }

    public void unloadConfiguration() {
        /* Unbind the general settings */
        this.checkBoxExportPlayers.selectedProperty().unbindBidirectional(
                this.loadedConfiguration.playerListProperty());
        this.checkBoxExportTournaments.selectedProperty().unbindBidirectional(
                this.loadedConfiguration.tournamentProperty());

        /* Unbind the table availability */
        this.tableTournaments.disableProperty().unbind();

        /* Clear the table */
        this.tableTournaments.getItems().clear();
        this.tableTournaments.getSelectionModel().clearSelection();

        this.loadedConfiguration = null;
    }

    @Override
    public PdfOutputConfiguration getReturnValue() {
        return this.loadedConfiguration;
    }

    @Override
    public String getInputErrorString() {
        if (!this.loadedConfiguration.exportPlayerList()
                && !this.loadedConfiguration.exportTournaments()) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.pdfoutputselection.error");
        } else {
            return null;
        }
    }

    @Override
    public void initModalDialog(
            ModalDialog<PdfOutputConfiguration, PdfOutputConfiguration> modalDialog) {
        modalDialog.title("dialogs.pdfoutputselection.title").dialogButtons(
                DialogButtons.OK_CANCEL);
    }
}
