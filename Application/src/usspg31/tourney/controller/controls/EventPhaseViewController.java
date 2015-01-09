package usspg31.tourney.controller.controls;

import java.io.IOException;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import usspg31.tourney.controller.MainWindow;
import usspg31.tourney.controller.controls.eventphases.EventSetupPhaseController;
import usspg31.tourney.controller.controls.eventphases.PreRegistrationPhaseController;
import usspg31.tourney.controller.controls.eventphases.RegistrationPhaseController;
import usspg31.tourney.controller.controls.eventphases.TournamentExecutionPhaseController;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.undo.UndoManager;

public class EventPhaseViewController implements EventUser {

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
	private static Interpolator transitionInterpolator = Interpolator.SPLINE(.4, 0, 0, 1);

	// Event
	private Event loadedEvent;

	// UndoManager
	private UndoManager activeUndoManager;

	@FXML private void initialize() throws IOException {
		this.phasePosition = new SimpleDoubleProperty(0);

		this.loadSubViews();
		this.initBreadcrumbs();


		this.buttonClose.setOnAction(event -> {
			// TODO: ask the user to save the currently active event
			this.unloadEvent();
			MainWindow.getInstance().slideDown(MainWindow.getInstance().getMainMenu());
		});

		this.buttonUndo.setOnAction(event -> {
			if (this.activeUndoManager != null) {
				this.activeUndoManager.undo();
			}
		});
		this.buttonRedo.setOnAction(event -> {
			if (this.activeUndoManager != null) {
				this.activeUndoManager.redo();
			}
		});

		this.buttonOptions.setOnAction(event -> {
			MainWindow mainWindow = MainWindow.getInstance();
			mainWindow.getOptionsViewController().setExitProperties("Event",
					"Zurückkehren",
					"Kehren Sie zu Ihrem momentan geöffneten Event zurück.", () -> {
						mainWindow.slideDown(mainWindow.getEventPhaseView());
					});
			mainWindow.slideUp(mainWindow.getOptionsView());
		});

		// register listeners on the breadcrumb bar
		this.breadcrumbEventSetup.setOnAction(event -> {
			this.slideToPhase(0);
		});
		this.breadcrumbPreRegistration.setOnAction(event -> {
			this.slideToPhase(1);
		});
		this.breadcrumbRegistration.setOnAction(event -> {
			this.slideToPhase(2);
		});
		this.breadcrumbTournamentExecution.setOnAction(event -> {
			this.slideToPhase(3);
		});

		// add all event phase views to the event phase container
		this.eventPhaseContainer.getChildren().addAll(this.eventSetupPhase,
				this.preRegistrationPhase, this.registrationPhase);
	}

	private void initBreadcrumbs() {
	}

	private void loadSubViews() throws IOException {
		FXMLLoader eventSetupPhaseLoader = new FXMLLoader(this.getClass()
				.getResource("/ui/fxml/controls/eventphases/event-setup-phase.fxml"));
		this.eventSetupPhase = eventSetupPhaseLoader.load();
		this.eventSetupPhaseController = eventSetupPhaseLoader.getController();
		this.eventSetupPhase.setVisible(true);

		FXMLLoader preRegistrationPhaseLoader = new FXMLLoader(this.getClass()
				.getResource("/ui/fxml/controls/eventphases/pre-registration-phase.fxml"));
		this.preRegistrationPhase = preRegistrationPhaseLoader.load();
		this.preRegistrationPhase.setVisible(true);

		FXMLLoader registrationPhaseLoader = new FXMLLoader(this.getClass()
				.getResource("/ui/fxml/controls/eventphases/registration-phase.fxml"));
		this.registrationPhase = registrationPhaseLoader.load();
		this.registrationPhase.setVisible(true);

		//		FXMLLoader tournamentExecutionPhaseLoader = new FXMLLoader(this.getClass()
		//				.getResource("/ui/fxml/controls/eventphases/tournament-execution-phase.fxml"));
		//		this.tournamentExecutionPhase = tournamentExecutionPhaseLoader.load();
		//		this.tournamentExecutionPhase.setVisible(true);

		// bind the phase view's translateX property to the phasePosition
		// property, so the pages scroll all together, when the phasePosition
		// gets changed. (e.g. phasePosition == 1 -> show phase 2)

		this.eventSetupPhase.translateXProperty().bind(
				this.eventPhaseContainer.widthProperty()
				.multiply(0)
				.subtract(this.eventPhaseContainer.widthProperty()
						.multiply(this.phasePosition)));

		this.preRegistrationPhase.translateXProperty().bind(
				this.eventPhaseContainer.widthProperty()
				.multiply(1)
				.subtract(this.eventPhaseContainer.widthProperty()
						.multiply(this.phasePosition)));

		this.registrationPhase.translateXProperty().bind(
				this.eventPhaseContainer.widthProperty()
				.multiply(2)
				.subtract(this.eventPhaseContainer.widthProperty()
						.multiply(this.phasePosition)));

	}

	private void slideToPhase(int phaseNumber) {
		if (this.currentAnimation != null) {
			this.currentAnimation.stop();
		}
		this.currentAnimation = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(
						this.phasePosition, this.phasePosition.get())),
						new KeyFrame(transitionDuration,
								new KeyValue(this.phasePosition, phaseNumber,
										transitionInterpolator)));
		this.currentAnimation.play();
	}

	public void setActiveUndoManager(UndoManager undoManager) {
		this.activeUndoManager = undoManager;
		this.buttonUndo.disableProperty().bind(undoManager.undoAvailableProperty().not());
		this.buttonRedo.disableProperty().bind(undoManager.redoAvailableProperty().not());
	}

	public void unsetUndoManager() {
		this.activeUndoManager = null;
		this.buttonUndo.disableProperty().unbind();
		this.buttonUndo.disableProperty().set(true);
		this.buttonRedo.disableProperty().unbind();
		this.buttonRedo.disableProperty().set(true);
	}

	@Override
	public void loadEvent(Event event) {
		if (this.loadedEvent != null) {
			this.unloadEvent();
		}

		this.eventSetupPhaseController.loadEvent(event);
		this.loadedEvent = event;
	}

	@Override
	public void unloadEvent() {
		// TODO unload any registered listeners on the event
		this.eventSetupPhaseController.unloadEvent();

		this.loadedEvent = null;
	}
}
