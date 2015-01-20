package usspg31.tourney.controller.dialogs.modal;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import usspg31.tourney.controller.EntryPoint;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.layout.RelativityPane;

public final class ModalDialog<P, R> extends StackPane {

    private final static Logger log = Logger.getLogger(ModalDialog.class
	    .getName());

    @FXML
    private StackPane mainWindowRootContainer;

    @FXML
    private VBox dialogRoot;
    @FXML
    private RelativityPane dialogBackground;

    @FXML
    private Label labelTitle;
    @FXML
    private StackPane contentContainer;
    @FXML
    private HBox dialogButtonContainer;

    private DialogContent<P, R> dialogContent;
    private DialogResultListener<R> dialogResultListener;

    private Timeline fadeInTransition;
    private Timeline fadeOutTransition;

    private static final double backgroundBlurAmount = 10;
    private static final double scaleInFrom = .7;
    private static final double scaleOutTo = .7;
    private static final Interpolator fadeInterpolator = Interpolator.SPLINE(
	    .4, 0, 0, 1);
    private GaussianBlur backgroundBlur;
    private static final Duration transitionDuration = Duration.millis(300);

    public ModalDialog(DialogContent<P, R> dialogContent) {
	this.dialogContent = dialogContent;
	this.dialogContent.setDialogRoot(this);

	this.loadDialog();

	this.contentContainer.getChildren().add(this.dialogContent.getRoot());
    }

    private void loadDialog() {
	try {
	    FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
		    "/ui/fxml/dialogs/modal-dialog.fxml"));
	    loader.setController(this);
	    loader.setRoot(this);
	    loader.load();
	} catch (IOException e) {
	    log.log(Level.SEVERE, e.getMessage(), e);
	}
    }

    @FXML
    private void initialize() {
	this.backgroundBlur = new GaussianBlur();

	this.fadeInTransition = new Timeline(new KeyFrame(Duration.ZERO,
		new KeyValue(this.backgroundBlur.radiusProperty(), 0.0),
		new KeyValue(this.dialogRoot.scaleXProperty(), scaleInFrom),
		new KeyValue(this.dialogRoot.scaleYProperty(), scaleInFrom),
		new KeyValue(this.dialogBackground.opacityProperty(), 0.0)),
		new KeyFrame(transitionDuration, new KeyValue(
			this.backgroundBlur.radiusProperty(),
			backgroundBlurAmount), new KeyValue(this.dialogRoot
			.scaleXProperty(), 1.0, fadeInterpolator),
			new KeyValue(this.dialogRoot.scaleYProperty(), 1.0,
				fadeInterpolator), new KeyValue(
				this.dialogBackground.opacityProperty(), 1.0,
				fadeInterpolator)));

	this.fadeOutTransition = new Timeline(new KeyFrame(Duration.ZERO,
		new KeyValue(this.backgroundBlur.radiusProperty(),
			backgroundBlurAmount), new KeyValue(
			this.dialogRoot.scaleXProperty(), 1.0), new KeyValue(
			this.dialogRoot.scaleYProperty(), 1.0), new KeyValue(
			this.dialogBackground.opacityProperty(), 1.0)),
		new KeyFrame(transitionDuration, new KeyValue(
			this.backgroundBlur.radiusProperty(), 0.0),
			new KeyValue(this.dialogRoot.scaleXProperty(),
				scaleOutTo, fadeInterpolator), new KeyValue(
				this.dialogRoot.scaleYProperty(), scaleOutTo,
				fadeInterpolator), new KeyValue(
				this.dialogBackground.opacityProperty(), 0.0,
				fadeInterpolator)));

	this.mainWindowRootContainer.setEffect(this.backgroundBlur);
    }

    public ModalDialog<P, R> title(String title) {
	this.labelTitle.setText(PreferencesManager.getInstance()
		.localizeString(title));
	return this;
    }

    public ModalDialog<P, R> dialogButtons(DialogButtons dialogButtons) {
	this.dialogButtonContainer.getChildren().clear();

	if (dialogButtons.containsYes()) {
	    this.addDialogButton("dialogs.buttons.yes", DialogResult.YES);
	}
	if (dialogButtons.containsNo()) {
	    this.addDialogButton("dialogs.buttons.no", DialogResult.NO,
		    !dialogButtons.containsCancel());
	}
	if (dialogButtons.containsOk()) {
	    this.addDialogButton("dialogs.buttons.ok", DialogResult.OK);
	}
	if (dialogButtons.containsCancel()) {
	    this.addDialogButton("dialogs.buttons.cancel", DialogResult.CANCEL,
		    true);
	}
	return this;
    }

    private void addDialogButton(String text, DialogResult result) {
	this.addDialogButton(text, result, false);
    }

    private void addDialogButton(String text, DialogResult result,
	    boolean isCancel) {
	Button button = new Button(PreferencesManager.getInstance()
		.localizeString(text));
	button.getStyleClass().add("dialog-button");
	button.setOnAction(event -> {
	    this.exitWith(result);
	});
	button.setCancelButton(isCancel);
	this.dialogButtonContainer.getChildren().add(button);
    }

    public ModalDialog<P, R> properties(P properties) {
	this.dialogContent.setProperties(properties);
	return this;
    }

    public ModalDialog<P, R> onResult(DialogResultListener<R> listener) {
	this.dialogResultListener = listener;
	return this;
    }

    public void show() {
	long time = System.currentTimeMillis();

	Parent mainWindowRoot = EntryPoint.getPrimaryStage().getScene()
		.getRoot();
	mainWindowRoot.setDisable(true);

	EntryPoint.getPrimaryStage().getScene().setRoot(this);
	this.mainWindowRootContainer.getChildren().add(mainWindowRoot);

	log.finer("Showing dialog for "
		+ this.dialogContent.getClass().getSimpleName() + " ("
		+ (System.currentTimeMillis() - time) + "ms)");

	this.fadeInTransition.play();
    }

    public void exitWith(DialogResult result) {
	if (this.dialogContent.getInputErrorString() != null
		&& result == DialogResult.OK) {
	    new SimpleDialog<>(this.dialogContent.getInputErrorString())
		    .modalDialog().dialogButtons(DialogButtons.OK)
		    .title("dialogs.titles.error").show();
	} else {
	    this.hide();
	    if (this.dialogResultListener != null) {
		this.dialogResultListener.onDialogClosed(result,
			this.dialogContent.getReturnValue());
	    }
	}
    }

    private void hide() {
	((Parent) this.mainWindowRootContainer.getChildren().get(0))
		.setDisable(false);

	this.fadeOutTransition.setOnFinished(event -> {
	    Parent mainWindowRoot = (Parent) this.mainWindowRootContainer
		    .getChildren().get(0);

	    this.mainWindowRootContainer.getChildren().clear();
	    EntryPoint.getPrimaryStage().getScene().setRoot(mainWindowRoot);
	});
	this.fadeOutTransition.play();
    }
}
