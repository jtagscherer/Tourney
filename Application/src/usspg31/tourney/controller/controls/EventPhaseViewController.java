package usspg31.tourney.controller.controls;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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


	@FXML private void initialize() throws IOException {
		this.buttonClose.setOnAction(event -> {
			MainWindow.getInstance().displayMainMenu();
		});

		this.loadSubViews();
		this.initBreadcrumbs();

		this.eventPhaseContainer.getChildren().addAll(this.eventSetupPhase);
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
		this.eventSetupPhase.setVisible(true);
	}
}
