package usspg31.tourney.controller;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class OptionsViewController {

	private static final Logger log = Logger.getLogger(OptionsViewController.class.getName());

	@FXML private Button buttonChangeLanguage;
	@FXML private Button buttonChangePassword;
	@FXML private Button buttonReturnToMainMenu;

	@FXML private void initialize() {
		this.buttonReturnToMainMenu.setOnAction(event -> {
			log.finer("ReturnToMainMenu Button was clicked");
			MainWindow.getInstance().displayMainMenu();
		});
	}

}
