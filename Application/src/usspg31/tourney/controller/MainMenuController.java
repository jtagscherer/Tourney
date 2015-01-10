package usspg31.tourney.controller;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import org.controlsfx.dialog.Dialogs;

import usspg31.tourney.model.Event;
import usspg31.tourney.model.filemanagement.FileLoader;

@SuppressWarnings("deprecation")
public class MainMenuController {

	private static final Logger log = Logger.getLogger(MainMenuController.class
			.getName());

	@FXML
	private Button buttonNewEvent;
	@FXML
	private Button buttonOpenEvent;
	@FXML
	private Button buttonEditTournamentModules;
	@FXML
	private Button buttonOpenOptions;

	@FXML
	private VBox eventButtonsLeft;
	@FXML
	private VBox eventButtonsRight;
	@FXML
	private HBox eventButtonsContainer;

	@FXML
	private void initialize() {
		this.buttonNewEvent.setOnAction(event -> {
			// create a new event and open it
				log.finer("New Event Button was clicked");
				MainWindow.getInstance().getEventPhaseViewController()
						.loadEvent(new Event());
				MainWindow.getInstance().slideUp(
						MainWindow.getInstance().getEventPhaseView());
			});
		this.buttonOpenEvent
				.setOnAction(event -> {
					// open the file chooser dialog and load the chosen event
					log.finer("Open Event Button was clicked");
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Eventdatei öffnen");
					fileChooser.getExtensionFilters()
							.add(new ExtensionFilter("Tourney Eventdateien",
									"*.tef"));
					File selectedFile = fileChooser.showOpenDialog(EntryPoint
							.getPrimaryStage());
					if (selectedFile != null) {
						Event loadedEvent = null;

						try {
							loadedEvent = FileLoader
									.loadEventFromFile(selectedFile
											.getAbsolutePath());
						} catch (Exception e) {
							log.log(Level.SEVERE,
									"Could not load the specified tournament.",
									e);
							Dialogs.create()
									.owner(EntryPoint.getPrimaryStage())
									.title("Fehler")
									.message(
											"Die Eventdatei \""
													+ selectedFile.getName()
													+ "\" konnte nicht geladen werden.\nBitte stellen Sie sicher, dass es sich dabei um eine gültige Eventdatei handelt.")
									.showError();
						}

						if (loadedEvent != null) {
							MainWindow.getInstance()
									.getEventPhaseViewController()
									.loadEvent(loadedEvent);
							MainWindow.getInstance().slideUp(
									MainWindow.getInstance()
											.getEventPhaseView());
						}
					}
				});

		this.buttonOpenOptions
				.setOnAction(event -> {
					log.finer("Options Button was clicked");

					MainWindow
							.getInstance()
							.getOptionsViewController()
							.setExitProperties(
									"Hauptmenü",
									"Zurückkehren",
									"Kehren Sie zum Hauptmenü zurück",
									() -> {
										MainWindow.getInstance().slideDown(
												MainWindow.getInstance()
														.getMainMenu());
									});

					MainWindow.getInstance().slideUp(
							MainWindow.getInstance().getOptionsView());
				});
	}

}
