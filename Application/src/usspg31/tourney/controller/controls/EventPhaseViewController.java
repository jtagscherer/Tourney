package usspg31.tourney.controller.controls;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

import org.controlsfx.dialog.Dialogs;

import usspg31.tourney.controller.EntryPoint;
import usspg31.tourney.controller.MainMenuController;
import usspg31.tourney.controller.MainWindow;
import usspg31.tourney.controller.controls.eventphases.EventSetupPhaseController;
import usspg31.tourney.controller.controls.eventphases.PreRegistrationPhaseController;
import usspg31.tourney.controller.controls.eventphases.RegistrationPhaseController;
import usspg31.tourney.controller.controls.eventphases.TournamentExecutionPhaseController;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.filemanagement.FileSaver;
import usspg31.tourney.model.undo.UndoManager;

@SuppressWarnings("deprecation")
public class EventPhaseViewController implements EventUser {

	private static final Logger log = Logger.getLogger(MainMenuController.class
			.getName());

	// Loaded event file
	private File loadedEventFile;

	// Menu bar
	@FXML private Button buttonClose;
	@FXML private Button buttonSave;

	@FXML private Button buttonUndo;
	@FXML private Button buttonRedo;

	@FXML private Button buttonLock;
	@FXML private Button buttonOptions;

	// Breadcrumb controls
	@FXML private HBox breadcrumbContainer;
	@FXML private Button breadcrumbEventSetup;
	@FXML private Button breadcrumbPreRegistration;
	@FXML private Button breadcrumbRegistration;
	@FXML private Button breadcrumbTournamentExecution;

	// Event phases
	@FXML private StackPane eventPhaseContainer;

	private Node eventSetupPhase;
	private EventSetupPhaseController eventSetupPhaseController;

	private Node preRegistrationPhase;
	private PreRegistrationPhaseController preRegistrationPhaseController;

	private Node registrationPhase;
	private RegistrationPhaseController registrationPhaseController;

	private Node tournamentExecutionPhase;
	private TournamentExecutionPhaseController tournamentExecutionPhaseController;

	// Animations
	private DoubleProperty phasePosition;
	private Timeline currentAnimation;

	private static final Duration transitionDuration = Duration.millis(300);
	private static Interpolator transitionInterpolator = Interpolator.SPLINE(
			.4, 0, 0, 1);

	// Event
	private Event loadedEvent;

	// UndoManager
	private UndoManager activeUndoManager;

	@FXML
	private void initialize() throws IOException {
		this.phasePosition = new SimpleDoubleProperty(0);

		this.loadSubViews();
		this.initBreadcrumbs();

		// add all event phase views to the event phase container
		this.eventPhaseContainer.getChildren().addAll(this.eventSetupPhase,
				this.preRegistrationPhase, this.registrationPhase);
	}

	private void initBreadcrumbs() {
	}

	private void loadSubViews() throws IOException {
		FXMLLoader eventSetupPhaseLoader = new FXMLLoader(this.getClass()
				.getResource(
						"/ui/fxml/controls/eventphases/event-setup-phase.fxml"));
		this.eventSetupPhase = eventSetupPhaseLoader.load();
		this.eventSetupPhaseController = eventSetupPhaseLoader.getController();
		this.eventSetupPhase.setVisible(true);

		FXMLLoader preRegistrationPhaseLoader = new FXMLLoader(
				this.getClass()
				.getResource(
						"/ui/fxml/controls/eventphases/pre-registration-phase.fxml"));
		this.preRegistrationPhase = preRegistrationPhaseLoader.load();
		this.preRegistrationPhase.setVisible(true);

		FXMLLoader registrationPhaseLoader = new FXMLLoader(
				this.getClass()
				.getResource(
						"/ui/fxml/controls/eventphases/registration-phase.fxml"));
		this.registrationPhase = registrationPhaseLoader.load();
		this.registrationPhase.setVisible(true);

		// FXMLLoader tournamentExecutionPhaseLoader = new
		// FXMLLoader(this.getClass()
		// .getResource("/ui/fxml/controls/eventphases/tournament-execution-phase.fxml"));
		// this.tournamentExecutionPhase =
		// tournamentExecutionPhaseLoader.load();
		// this.tournamentExecutionPhase.setVisible(true);

		// bind the phase view's translateX property to the phasePosition
		// property, so the pages scroll all together, when the phasePosition
		// gets changed. (e.g. phasePosition == 1 -> show phase 2)

		this.eventSetupPhase.translateXProperty().bind(
				this.eventPhaseContainer
				.widthProperty()
				.multiply(0)
				.subtract(
						this.eventPhaseContainer.widthProperty()
						.multiply(this.phasePosition)));

		this.preRegistrationPhase.translateXProperty().bind(
				this.eventPhaseContainer
				.widthProperty()
				.multiply(1)
				.subtract(
						this.eventPhaseContainer.widthProperty()
						.multiply(this.phasePosition)));

		this.registrationPhase.translateXProperty().bind(
				this.eventPhaseContainer
				.widthProperty()
				.multiply(2)
				.subtract(
						this.eventPhaseContainer.widthProperty()
						.multiply(this.phasePosition)));

	}

	private void slideToPhase(int phaseNumber) {
		if (this.currentAnimation != null) {
			this.currentAnimation.stop();
		}
		this.currentAnimation = new Timeline(new KeyFrame(Duration.ZERO,
				new KeyValue(this.phasePosition, this.phasePosition.get())),
				new KeyFrame(transitionDuration,
						new KeyValue(this.phasePosition, phaseNumber,
								transitionInterpolator)));
		this.currentAnimation.play();
	}

	public void setActiveUndoManager(UndoManager undoManager) {
		this.activeUndoManager = undoManager;
		this.buttonUndo.disableProperty().bind(
				undoManager.undoAvailableProperty().not());
		this.buttonRedo.disableProperty().bind(
				undoManager.redoAvailableProperty().not());
	}

	public void unsetUndoManager() {
		this.activeUndoManager = null;
		this.buttonUndo.disableProperty().unbind();
		this.buttonUndo.disableProperty().set(true);
		this.buttonRedo.disableProperty().unbind();
		this.buttonRedo.disableProperty().set(true);
	}

	public File getLoadedEventFile() {
		return this.loadedEventFile;
	}

	public void setLoadedEventFile(File loadedEventFile) {
		this.loadedEventFile = loadedEventFile;
	}

	@Override
	public void loadEvent(Event event) {
		if (this.loadedEvent != null) {
			this.unloadEvent();
		}

		this.eventSetupPhaseController.loadEvent(event);
		this.loadedEvent = event;
		switch (this.loadedEvent.getEventPhase()) {
		case EVENT_SETUP:
			this.phasePosition.set(0);
			break;
		case PRE_REGISTRATION:
			this.phasePosition.set(1);
			break;
		case REGISTRATION:
			this.phasePosition.set(2);
			break;
		case TOURNAMENT_EXECUTION:
			this.phasePosition.set(3);
			break;
		}
	}

	@Override
	public void unloadEvent() {
		// TODO unload any registered listeners on the event
		this.eventSetupPhaseController.unloadEvent();

		this.loadedEvent = null;
	}

	@FXML private void onButtonCloseClicked(ActionEvent event) {
		this.onButtonSaveClicked(null);

		this.unloadEvent();
		MainWindow.getInstance().slideDown(
				MainWindow.getInstance().getMainMenu());
	}

	@FXML private void onButtonUndoClicked(ActionEvent event) {
		if (this.activeUndoManager != null) {
			this.activeUndoManager.undo();
		}
	}

	@FXML private void onButtonRedoClicked(ActionEvent event) {
		if (this.activeUndoManager != null) {
			this.activeUndoManager.redo();
		}
	}

	@FXML private void onButtonSaveClicked(ActionEvent event) {
		if (this.getLoadedEventFile() == null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Eventdatei speichern");
			fileChooser.getExtensionFilters().add(
					new ExtensionFilter(
							"Tourney Eventdatei (*.tef)", "*.tef"));
			File selectedFile = fileChooser
					.showSaveDialog(EntryPoint.getPrimaryStage());
			if (selectedFile == null) {
				return;
			}
			if (!selectedFile.getName().endsWith(".tef")) {
				selectedFile = new File(selectedFile
						.getAbsolutePath() + ".tef");
			}
			this.setLoadedEventFile(selectedFile);
		}

		try {
			FileSaver.saveEventToFile(this.loadedEvent, this
					.getLoadedEventFile().getAbsolutePath());
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not save the event.", e);
			Dialogs.create()
			.owner(EntryPoint.getPrimaryStage())
			.title("Fehler")
			.message(
					"Das Event konnte nicht gespeichert werden.\nBitte stellen Sie sicher, dass Sie für die Zieldatei alle Berechtigungen besitzen.")
					.showError();
		}
	}

	@FXML private void onButtonLockClicked(ActionEvent event) {

	}

	@FXML private void onButtonOptionsClicked(ActionEvent event) {
		MainWindow mainWindow = MainWindow.getInstance();
		mainWindow.getOptionsViewController().setExitProperties("Event",
				"Zurückkehren",
				"Kehren Sie zu Ihrem momentan geöffneten Event zurück.",
				() -> {
					mainWindow.slideDown(mainWindow.getEventPhaseView());
				});
		mainWindow.slideUp(mainWindow.getOptionsView());
	}

	@FXML private void onBreadcrumbEventSetupClicked(ActionEvent event) {
		this.slideToPhase(0);
		this.loadedEvent.setEventPhase(Event.EventPhase.EVENT_SETUP);
	}

	@FXML private void onBreadcrumbPreRegistrationClicked(ActionEvent event) {
		this.slideToPhase(1);
		this.loadedEvent.setEventPhase(Event.EventPhase.PRE_REGISTRATION);
	}

	@FXML private void onBreadcrumbRegistrationClicked(ActionEvent event) {
		this.slideToPhase(2);
		this.loadedEvent.setEventPhase(Event.EventPhase.REGISTRATION);
	}

	@FXML private void onBreadcrumbTournamentExecutionClicked(ActionEvent event) {
		this.slideToPhase(3);
		this.loadedEvent.setEventPhase(Event.EventPhase.TOURNAMENT_EXECUTION);
	}
}
