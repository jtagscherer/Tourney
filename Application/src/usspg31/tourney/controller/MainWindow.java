package usspg31.tourney.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class MainWindow extends StackPane {

	private static final Logger log = Logger.getLogger(MainWindow.class.getName());

	private static final Duration transitionDuration = Duration.millis(300);
	private static Interpolator transitionInterpolator = Interpolator.SPLINE(.4, 0, 0, 1);

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

	private DoubleProperty rightOffset;

	private Timeline moveLeft;
	private Timeline moveRight;

	private MainWindow() throws IOException {
		this.getStyleClass().add("main-window");
		this.getStylesheets().add("/ui/css/main.css");

		this.initTransitions();

		this.loadSubViews();

		this.getChildren().addAll(this.mainMenu, this.optionsView);

		this.displayMainMenu();
	}

	private void initTransitions() {
		this.rightOffset = new SimpleDoubleProperty(0);

		this.moveLeft = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(this.rightOffset, 0)),
				new KeyFrame(transitionDuration, new KeyValue(this.rightOffset, 1, transitionInterpolator)));

		this.moveRight = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(this.rightOffset, 1)),
				new KeyFrame(transitionDuration, new KeyValue(this.rightOffset, 0, transitionInterpolator)));
	}

	private void loadSubViews() throws IOException {
		FXMLLoader mainMenuLoader = new FXMLLoader(this.getClass()
				.getResource("/ui/fxml/main-menu.fxml"));
		this.mainMenu = mainMenuLoader.load();
		this.mainMenuController = mainMenuLoader.getController();
		this.mainMenu.translateXProperty().bind(this.widthProperty().multiply(this.rightOffset).negate());
		this.mainMenu.setVisible(true);

		FXMLLoader optionsViewLoader = new FXMLLoader(this.getClass()
				.getResource("/ui/fxml/options-view.fxml"));
		this.optionsView = optionsViewLoader.load();
		this.optionsViewController = optionsViewLoader.getController();
		this.optionsView.translateXProperty().bind(this.widthProperty().multiply(this.rightOffset).negate().add(this.widthProperty()));
		this.optionsView.setVisible(true);
	}

	public void displayMainMenu() {
		log.fine("Displaying main menu");
		this.moveRight.play();
	}

	public void displayOptionsView() {
		log.fine("Displaying options");
		this.moveLeft.play();
	}

	public void displayEventPhaseView() {
		log.fine("Displaying event phases");
		this.mainMenu.setVisible(false);
		this.optionsView.setVisible(false);
	}
}
