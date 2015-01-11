package usspg31.tourney.controller.controls.eventphases;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import usspg31.tourney.controller.MainWindow;
import usspg31.tourney.controller.controls.EventUser;
import usspg31.tourney.controller.controls.UndoTextArea;
import usspg31.tourney.controller.controls.UndoTextField;
import usspg31.tourney.controller.dialogs.DialogResult;
import usspg31.tourney.controller.dialogs.TournamentDialog;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.undo.UndoManager;

public class EventSetupPhaseController implements EventUser {

	private final static Logger log = Logger
			.getLogger(EventSetupPhaseController.class.getName());

	@FXML
	private UndoTextField textFieldEventTitle;
	@FXML
	private DatePicker datePickerStartDate;
	@FXML
	private DatePicker datePickerEndDate;
	@FXML
	private UndoTextArea textAreaEventLocation;

	@FXML
	private TableView<Tournament> tableTournaments;
	@FXML
	private Button buttonAddTournament;
	@FXML
	private Button buttonRemoveTournament;
	@FXML
	private Button buttonEditTournament;

	private TableColumn<Tournament, String> tableColumnTournamentTitle;

	private Event loadedEvent;

	private final UndoManager undoManager;

	public EventSetupPhaseController() {
		this.undoManager = new UndoManager();
	}

	@FXML
	private void initialize() {
		this.initTournamentTable();
		this.buttonEditTournament.disableProperty().bind(
				this.tableTournaments.getSelectionModel()
						.selectedItemProperty().isNull());
		this.buttonRemoveTournament.disableProperty().bind(
				this.tableTournaments.getSelectionModel()
						.selectedItemProperty().isNull());

	}

	private void initTournamentTable() {
		// create title column
		this.tableColumnTournamentTitle = new TableColumn<>("Titel");
		this.tableColumnTournamentTitle
				.setCellValueFactory(cellData -> cellData.getValue()
						.nameProperty());
		this.tableTournaments.getColumns().add(this.tableColumnTournamentTitle);
	}

	@Override
	public void loadEvent(Event event) {
		log.info("Loading Event");
		if (this.loadedEvent != null) {
			this.unloadEvent();
		}
		MainWindow.getInstance().getEventPhaseViewController()
				.setActiveUndoManager(this.undoManager);

		this.loadedEvent = event;

		// bind all basic control's values
		this.textFieldEventTitle.undoTextProperty().bindBidirectional(
				event.nameProperty());
		this.datePickerStartDate.valueProperty().bindBidirectional(
				event.startDateProperty());
		this.datePickerEndDate.valueProperty().bindBidirectional(
				event.endDateProperty());
		this.textAreaEventLocation.undoTextProperty().bindBidirectional(
				event.locationProperty());

		// create table bindings
		this.tableTournaments.setItems(this.loadedEvent.getTournaments());

		// register all undoable properties
		this.undoManager.registerUndoProperty(this.textFieldEventTitle
				.undoTextProperty());
		this.undoManager.registerUndoProperty(this.datePickerStartDate
				.valueProperty());
		this.undoManager.registerUndoProperty(this.datePickerEndDate
				.valueProperty());
		this.undoManager.registerUndoProperty(this.textAreaEventLocation
				.undoTextProperty());
		this.undoManager.registerUndoProperty(event.getTournaments());
	}

	@Override
	public void unloadEvent() {
		log.info("Unloading Event");
		if (this.loadedEvent == null) {
			log.warning("Trying to unload an event, even though no event was loaded");
			return;
		}
		MainWindow.getInstance().getEventPhaseViewController()
				.unsetUndoManager();

		// TODO: unregister all listeners we registered to anything in the event
		Event event = this.loadedEvent;

		// unbind all basic control's values
		event.nameProperty().unbindBidirectional(
				this.textFieldEventTitle.undoTextProperty());
		event.locationProperty().unbindBidirectional(
				this.textAreaEventLocation.textProperty());
		event.startDateProperty().unbindBidirectional(
				this.datePickerStartDate.valueProperty());
		event.endDateProperty().unbindBidirectional(
				this.datePickerEndDate.valueProperty());

		// unregister all undo properties we registered with the UndoManager
		this.undoManager.unregisterUndoProperty(this.textFieldEventTitle
				.undoTextProperty());
		this.undoManager.unregisterUndoProperty(this.datePickerStartDate
				.valueProperty());
		this.undoManager.unregisterUndoProperty(this.datePickerEndDate
				.valueProperty());
		this.undoManager.unregisterUndoProperty(this.textAreaEventLocation
				.undoTextProperty());
		this.undoManager.unregisterUndoProperty(event.getTournaments());

		this.undoManager.clearHistory();

		this.loadedEvent = null;
	}

	@FXML
	private void onButtonAddTournamentClicked(ActionEvent event) {
		log.fine("Add Tournament Button clicked");
		this.checkEventLoaded();
		new TournamentDialog()
				.modalDialog()
				.properties(new Tournament())
				.onResult(
						(result, returnValue) -> {
							if (result == DialogResult.OK
									&& returnValue != null) {
								this.loadedEvent.getTournaments().add(
										returnValue);
							}
							returnValue.setId(String.valueOf(this.loadedEvent
									.getTournaments().size()));
						}).show();
	}

	@FXML
	private void onButtonRemoveTournamentClicked(ActionEvent event) {
		log.fine("Remove Tournament Button clicked");
		this.checkEventLoaded();
		this.loadedEvent.getTournaments().remove(this.getSelectedTournament());
	}

	@FXML
	private void onButtonEditTournamentClicked(ActionEvent event) {
		log.fine("Edit Tournament Button clicked");
		this.checkEventLoaded();
		final Tournament selectedTournament = this.getSelectedTournament();
		new TournamentDialog()
				.modalDialog()
				.properties(selectedTournament)
				.onResult((result, returnValue) -> {
					// TODO: well, this obviously won't work like that.
						if (result == DialogResult.OK && returnValue != null) {
							this.undoManager.beginUndoBatch();
							this.loadedEvent.getTournaments().remove(
									selectedTournament);
							this.loadedEvent.getTournaments().add(returnValue);
							this.undoManager.endUndoBatch();
						}
					}).show();
	}

	private Tournament getSelectedTournament() {
		return this.tableTournaments.getSelectionModel().getSelectedItem();
	}

	private void checkEventLoaded() {
		if (this.loadedEvent == null) {
			throw new IllegalStateException("An Event must be loaded in order "
					+ "to perform actions on this controller");
		}
	}
}
