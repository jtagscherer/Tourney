package usspg31.tourney.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import usspg31.tourney.controller.controls.OptionsViewController;

public class MainWindow extends StackPane {

	private static final Logger log = Logger.getLogger(MainWindow.class.getName());

	private static MainWindow instance;

	public static MainWindow getInstance() {
		if (instance == null) {
			try {
				instance = new MainWindow();
			} catch (IOException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return instance;
	}

	private Pane mainMenu;
	private MainMenuController mainMenuController;

	private Pane optionsView;
	private OptionsViewController optionsViewController;

	private MainWindow() throws IOException {
		this.getStyleClass().add("main-window");
		this.getStylesheets().add("/ui/css/main.css");

		this.loadSubViews();

		this.getChildren().addAll(this.mainMenu, this.optionsView);

		this.displayMainMenu();
	}

	private void loadSubViews() throws IOException {
		FXMLLoader mainMenuLoader = new FXMLLoader(this.getClass()
				.getResource("/ui/fxml/main-menu.fxml"));
		this.mainMenu = mainMenuLoader.load();
		this.mainMenuController = mainMenuLoader.getController();

		FXMLLoader optionsViewLoader = new FXMLLoader(this.getClass()
				.getResource("/ui/fxml/options-view.fxml"));
		this.optionsView = optionsViewLoader.load();
		this.optionsViewController = optionsViewLoader.getController();
	}

	public void displayMainMenu() {
		log.fine("Displaying main menu");
		this.mainMenu.setVisible(true);
		this.optionsView.setVisible(false);
	}

	public void displayOptionsView() {
		log.fine("Displaying options");
		this.mainMenu.setVisible(false);
		this.optionsView.setVisible(true);
	}

	public void displayEventPhaseView() {
		log.fine("Displaying event phases");
		this.mainMenu.setVisible(false);
		this.optionsView.setVisible(false);
	}
}
