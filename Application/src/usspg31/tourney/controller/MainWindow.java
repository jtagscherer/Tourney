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
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import usspg31.tourney.controller.controls.EventPhaseViewController;

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

	private Pane eventPhaseView;
	private EventPhaseViewController eventPhaseViewController;

	private DoubleProperty offset;

	private Timeline slideAnimation;

	private Node currentPane;

	private MainWindow() throws IOException {
		this.setMinWidth(0);
		this.setMinHeight(0);
		this.getStyleClass().add("main-window");
		this.getStylesheets().add("/ui/css/main.css");

		this.initTransitions();

		this.loadSubViews();

		this.getChildren().addAll(this.mainMenu, this.optionsView, this.eventPhaseView);
	}

	private void initTransitions() {
		this.offset = new SimpleDoubleProperty(0);

		this.slideAnimation = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(this.offset, 0)),
				new KeyFrame(transitionDuration, new KeyValue(this.offset, 1, transitionInterpolator)));
	}

	private void loadSubViews() throws IOException {
		FXMLLoader mainMenuLoader = new FXMLLoader(this.getClass()
				.getResource("/ui/fxml/main-menu.fxml"));
		this.mainMenu = mainMenuLoader.load();
		this.mainMenuController = mainMenuLoader.getController();
		this.mainMenu.setVisible(true);

		FXMLLoader optionsViewLoader = new FXMLLoader(this.getClass()
				.getResource("/ui/fxml/options-view.fxml"));
		this.optionsView = optionsViewLoader.load();
		this.optionsViewController = optionsViewLoader.getController();
		this.optionsView.setVisible(false);

		FXMLLoader eventPhaseViewLoader = new FXMLLoader(this.getClass()
				.getResource("/ui/fxml/controls/event-phase-view.fxml"));
		this.eventPhaseView = eventPhaseViewLoader.load();
		this.eventPhaseViewController = eventPhaseViewLoader.getController();
		this.eventPhaseView.setVisible(false);

		this.currentPane = this.mainMenu;
	}

	private void slide(final Node from, final Node to) {
		this.slideAnimation.setOnFinished(event -> {
			log.fine("Finished animation: from: " + from + " to: " + to);
			from.translateXProperty().unbind();
			from.translateYProperty().unbind();
			to.translateXProperty().unbind();
			to.translateYProperty().unbind();
			to.setMouseTransparent(false);
			from.setVisible(false);
			this.currentPane = to;
		});
		from.setVisible(true);
		to.setVisible(true);
		from.setMouseTransparent(true);
		to.setMouseTransparent(true);
		this.slideAnimation.play();
	}

	private void slideLeft(Node to) {
		this.slideLeft(this.currentPane, to);
	}

	private void slideRight(Node to) {
		this.slideRight(this.currentPane, to);
	}

	private void slideUp(Node to) {
		this.slideUp(this.currentPane, to);
	}

	private void slideDown(Node to) {
		this.slideDown(this.currentPane, to);
	}

	private void slideLeft(Node from, Node to) {
		from.translateXProperty().bind(this.widthProperty().multiply(this.offset).negate());
		to.translateXProperty().bind(this.widthProperty().multiply(this.offset).negate().add(this.widthProperty()));
		from.setTranslateY(0);
		to.setTranslateY(0);
		this.slide(from, to);
	}

	private void slideRight(Node from, Node to) {
		from.translateXProperty().bind(this.widthProperty().multiply(this.offset));
		to.translateXProperty().bind(this.widthProperty().multiply(this.offset).subtract(this.widthProperty()));
		from.setTranslateY(0);
		to.setTranslateY(0);
		this.slide(from, to);
	}

	private void slideUp(Node from, Node to) {
		from.translateYProperty().bind(this.heightProperty().multiply(this.offset).negate());
		to.translateYProperty().bind(this.heightProperty().multiply(this.offset).negate().add(this.heightProperty()));
		from.setTranslateX(0);
		to.setTranslateX(0);
		this.slide(from, to);
	}

	private void slideDown(Node from, Node to) {
		from.translateYProperty().bind(this.heightProperty().multiply(this.offset));
		to.translateYProperty().bind(this.heightProperty().multiply(this.offset).subtract(this.heightProperty()));
		from.setTranslateX(0);
		to.setTranslateX(0);
		this.slide(from, to);
	}

	public void displayMainMenu() {
		log.fine("Displaying main menu");
		this.slideDown(this.mainMenu);
	}

	public void displayOptionsView() {
		log.fine("Displaying options");
		this.slideUp(this.optionsView);
	}

	public void displayEventPhaseView() {
		log.fine("Displaying event phases");
		this.slideUp(this.eventPhaseView);
	}
}
