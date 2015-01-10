package usspg31.tourney.controller.controls.eventphases;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import usspg31.tourney.controller.MainWindow;
import usspg31.tourney.controller.controls.EventUser;
import usspg31.tourney.controller.controls.UndoTextArea;
import usspg31.tourney.controller.controls.UndoTextField;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.undo.UndoManager;

public class EventSetupPhaseController implements EventUser {

	private final static Logger log = Logger.getLogger(EventSetupPhaseController.class.getName());

	@FXML private UndoTextField textFieldEventTitle;
	@FXML private DatePicker datePickerStartDate;
	@FXML private DatePicker datePickerEndDate;
	@FXML private UndoTextArea textAreaEventLocation;

	@FXML private TableView<String> tableTournaments;
	@FXML private Button buttonAddTournament;
	@FXML private Button buttonRemoveTournament;
	@FXML private Button buttonEditTournament;

	private Event loadedEvent;

	private final UndoManager undoManager;

	public EventSetupPhaseController() {
		this.undoManager = new UndoManager();
	}

	@Override
	public void loadEvent(Event event) {
		log.info("Loading Event");
		if (this.loadedEvent != null) {
			this.unloadEvent();
		}
		MainWindow.getInstance().getEventPhaseViewController().setActiveUndoManager(this.undoManager);

		this.loadedEvent = event;

		this.textFieldEventTitle.undoTextProperty().bindBidirectional(event.nameProperty());
		this.datePickerStartDate.valueProperty().bindBidirectional(event.startDateProperty());
		this.datePickerEndDate.valueProperty().bindBidirectional(event.endDateProperty());
		this.textAreaEventLocation.undoTextProperty().bindBidirectional(event.locationProperty());

		this.undoManager.registerUndoProperty(this.textFieldEventTitle.undoTextProperty());
		this.undoManager.registerUndoProperty(this.datePickerStartDate.valueProperty());
		this.undoManager.registerUndoProperty(this.datePickerEndDate.valueProperty());
		this.undoManager.registerUndoProperty(this.textAreaEventLocation.undoTextProperty());
	}

	@Override
	public void unloadEvent() {
		log.info("Unloading Event");
		if (this.loadedEvent == null) {
			log.warning("Trying to unload an event, even though no event was loaded");
			return;
		}
		MainWindow.getInstance().getEventPhaseViewController().unsetUndoManager();

		// TODO: unregister all listeners we registered to anything in the event
		Event event = this.loadedEvent;

		event.nameProperty().unbindBidirectional(this.textFieldEventTitle.undoTextProperty());
		event.locationProperty().unbindBidirectional(this.textAreaEventLocation.textProperty());
		event.startDateProperty().unbindBidirectional(this.datePickerStartDate.valueProperty());
		event.endDateProperty().unbindBidirectional(this.datePickerEndDate.valueProperty());

		// TODO: unregister all undo properties we registered with the UndoManager
		this.undoManager.unregisterUndoProperty(this.textFieldEventTitle.undoTextProperty());
		this.undoManager.unregisterUndoProperty(this.datePickerStartDate.valueProperty());
		this.undoManager.unregisterUndoProperty(this.datePickerEndDate.valueProperty());
		this.undoManager.unregisterUndoProperty(this.textAreaEventLocation.undoTextProperty());

		this.undoManager.clearHistory();

		this.loadedEvent = null;
	}

}
