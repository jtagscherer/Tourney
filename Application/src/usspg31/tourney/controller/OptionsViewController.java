package usspg31.tourney.controller;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class OptionsViewController {

	private static final Logger log = Logger.getLogger(OptionsViewController.class.getName());

	@FXML private Button buttonChangeLanguage;
	@FXML private Button buttonChangePassword;

	@FXML private Button buttonExit;
	@FXML private Label labelExitHeading;
	@FXML private Label labelExitDescription;
	@FXML private Label labelExitExplanation;

	@FXML private void initialize() {

	}

	public void setExitProperties(String title, String description,
			String explanation, Runnable exitCallback) {
		this.labelExitHeading.setText(title);
		this.labelExitDescription.setText(description);
		this.labelExitExplanation.setText(explanation);
		this.buttonExit.setOnAction(event -> exitCallback.run());
	}

	@FXML private void onButtonChangeLanguageClicked(ActionEvent event) {

	}

	@FXML private void onButtonChangePasswordClicked(ActionEvent event) {

	}

}
