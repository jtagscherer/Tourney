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

public class EventPhaseViewController {

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

	@FXML private void initialize() throws IOException {
		this.buttonClose.setOnAction(event -> {
			MainWindow.getInstance().displayMainMenu();
		});

		this.phasePosition = new SimpleDoubleProperty(0);

		this.loadSubViews();
		this.initBreadcrumbs();

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

		this.eventPhaseContainer.getChildren().addAll(this.eventSetupPhase,
				this.preRegistrationPhase, this.registrationPhase);
	}

	private void initBreadcrumbs() {
		// make all breadcrumb buttons equal size
		this.breadcrumbEventSetup.prefWidthProperty().bind(
				this.breadcrumbContainer.widthProperty().divide(4));

		this.breadcrumbPreRegistration.prefWidthProperty().bind(
				this.breadcrumbContainer.widthProperty().divide(4));

		this.breadcrumbRegistration.prefWidthProperty().bind(
				this.breadcrumbContainer.widthProperty().divide(4));

		this.breadcrumbTournamentExecution.prefWidthProperty().bind(
				this.breadcrumbContainer.widthProperty().divide(4));
	}

	private void loadSubViews() throws IOException {
		FXMLLoader eventSetupPhaseLoader = new FXMLLoader(this.getClass()
				.getResource("/ui/fxml/controls/eventphases/event-setup-phase.fxml"));
		this.eventSetupPhase = eventSetupPhaseLoader.load();
		//this.eventSetupPhaseController = eventSetupPhaseLoader.getController();
		this.eventSetupPhase.translateXProperty().bind(
				this.eventPhaseContainer.widthProperty().multiply(0)
				.subtract(this.eventPhaseContainer.widthProperty()
						.multiply(this.phasePosition)));
		this.eventSetupPhase.setVisible(true);

		FXMLLoader preRegistrationPhaseLoader = new FXMLLoader(this.getClass()
				.getResource("/ui/fxml/controls/eventphases/pre-registration-phase.fxml"));
		this.preRegistrationPhase = preRegistrationPhaseLoader.load();
		this.preRegistrationPhase.translateXProperty().bind(
				this.eventPhaseContainer.widthProperty().multiply(1)
				.subtract(this.eventPhaseContainer.widthProperty()
						.multiply(this.phasePosition)));
		this.preRegistrationPhase.setVisible(true);

		FXMLLoader registrationPhaseLoader = new FXMLLoader(this.getClass()
				.getResource("/ui/fxml/controls/eventphases/registration-phase.fxml"));
		this.registrationPhase = registrationPhaseLoader.load();
		this.registrationPhase.translateXProperty().bind(
				this.eventPhaseContainer.widthProperty().multiply(2)
				.subtract(this.eventPhaseContainer.widthProperty()
						.multiply(this.phasePosition)));
		this.registrationPhase.setVisible(true);

		//		FXMLLoader tournamentExecutionPhaseLoader = new FXMLLoader(this.getClass()
		//				.getResource("/ui/fxml/controls/eventphases/tournament-execution-phase.fxml"));
		//		this.tournamentExecutionPhase = tournamentExecutionPhaseLoader.load();
		//		this.tournamentExecutionPhase.setVisible(true);
	}

	private void slideToPhase(int phaseNumber) {
		if (this.currentAnimation != null) {
			this.currentAnimation.stop();
		}
		this.currentAnimation = new Timeline(
				new KeyFrame(Duration.ZERO,
						new KeyValue(this.phasePosition, this.phasePosition.get())),
						new KeyFrame(transitionDuration,
								new KeyValue(this.phasePosition, phaseNumber, transitionInterpolator)));
		this.currentAnimation.play();
	}
}
