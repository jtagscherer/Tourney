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
import javafx.scene.layout.HBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.model.TournamentModule;

public class TournamentModuleListDialog extends HBox implements IModalDialogProvider<ObservableList<TournamentModule>, Object> {

	private static final Logger log = Logger.getLogger(TournamentModuleListDialog.class.getName());

	@FXML private TableView<TournamentModule> tableTournamentModules;
	private TableColumn<TournamentModule, String> tableColumnModuleName;
	private TableColumn<TournamentModule, String> tableColumnModuleDescription;

	@FXML private Button buttonAddTournamentModule;
	@FXML private Button buttonRemoveTournamentModule;
	@FXML private Button buttonEditTournamentModule;

	private ModalDialog<TournamentModule, TournamentModule> tournamentmoduleEditorDialog;

	public TournamentModuleListDialog() {
	    try {
	        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
	                "/ui/fxml/dialogs/tournament-module-list-dialog.fxml"),
	                PreferencesManager.getInstance().getSelectedLanguage().getLanguageBundle());
	        loader.setController(this);
	        loader.setRoot(this);
	        loader.load();
	    } catch (IOException e) {
	        log.log(Level.SEVERE, e.getMessage(), e);
	    }
	}

	@FXML private void initialize() {
	    PreferencesManager preferences = PreferencesManager.getInstance();

		this.tournamentmoduleEditorDialog = new TournamentModuleEditorDialog().modalDialog();

		this.tableColumnModuleName = new TableColumn<TournamentModule, String>(
		        preferences.localizeString("dialogs.tournamentmodulelist.name"));
		this.tableColumnModuleName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		this.tableTournamentModules.getColumns().add(this.tableColumnModuleName);

		this.tableColumnModuleDescription = new TableColumn<TournamentModule, String>(
		        preferences.localizeString("dialogs.tournamentmodulelist.description"));
		this.tableColumnModuleDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		this.tableTournamentModules.getColumns().add(this.tableColumnModuleDescription);

		this.buttonEditTournamentModule.disableProperty().bind(
				this.tableTournamentModules.getSelectionModel()
				.selectedItemProperty().isNull());

		this.buttonRemoveTournamentModule.disableProperty().bind(
				this.tableTournamentModules.getSelectionModel()
				.selectedItemProperty().isNull());
	}

	@Override
	public void setProperties(ObservableList<TournamentModule> properties) {
		this.tableTournamentModules.setItems(properties);
	}

	@Override
	public void initModalDialog(ModalDialog<ObservableList<TournamentModule>, Object> modalDialog) {
		modalDialog.title("dialogs.tournamentmodulelist").dialogButtons(DialogButtons.OK);
	}

	@FXML private void onButtonAddTournamentModuleClicked(ActionEvent event) {
		log.fine("Add Tournament Module Button clicked");
		this.tournamentmoduleEditorDialog
		.properties(new TournamentModule())
		.onResult((result, returnValue) -> {
			if (result == DialogResult.OK && returnValue != null) {
				if (this.tableTournamentModules.getItems() == null) {
					this.tableTournamentModules.setItems(FXCollections.observableArrayList());
				}
				this.tableTournamentModules.getItems().add(returnValue);
			}
		}).show();
	}

	@FXML private void onButtonRemoveTournamentModuleClicked(ActionEvent event) {
		log.fine("Remove Tournament Module Button clicked");
		this.tableTournamentModules.getItems().remove(
				this.tableTournamentModules.getSelectionModel().getSelectedIndex());
	}

	@FXML private void onButtonEditTournamentModuleClicked(ActionEvent event) {
		log.fine("Edit Tournament Module Button clicked");
		this.tournamentmoduleEditorDialog
		.properties(this.tableTournamentModules.getSelectionModel().getSelectedItem())
		.onResult((result, returnValue) -> {
			if (result == DialogResult.OK && returnValue != null) {

			}
		}).show();
	}
}
