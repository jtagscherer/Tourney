package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import usspg31.tourney.controller.EntryPoint;

public final class ModalDialog<P, R> extends StackPane {

	private final static Logger log = Logger.getLogger(ModalDialog.class.getName());

	private @FXML StackPane mainWindowRootContainer;

	private @FXML Label labelTitle;
	private @FXML StackPane contentContainer;
	private @FXML HBox dialogButtonContainer;

	private DialogContent<P, R> dialogContent;
	private DialogResultListener<R> dialogResultListener;

	public ModalDialog(DialogContent<P, R> dialogContent) {
		this.dialogContent = dialogContent;
		this.dialogContent.setDialogRoot(this);

		this.loadDialog();

		this.contentContainer.getChildren().add(this.dialogContent.getRoot());
	}

	private void loadDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/fxml/dialogs/modal-dialog.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@FXML private void initialize() {
		this.mainWindowRootContainer.setEffect(new GaussianBlur(10));
	}

	public ModalDialog<P, R> title(String title) {
		this.labelTitle.setText(title);
		return this;
	}

	public ModalDialog<P, R> dialogButtons(DialogButtons dialogButtons) {
		this.dialogButtonContainer.getChildren().clear();

		if (dialogButtons.containsYes()) {
			this.addDialogButton("Ja", DialogResult.YES);
		}
		if (dialogButtons.containsNo()) {
			this.addDialogButton("Nein", DialogResult.NO);
		}
		if (dialogButtons.containsOk()) {
			this.addDialogButton("Ok", DialogResult.OK);
		}
		if (dialogButtons.containsCancel()) {
			this.addDialogButton("Abbrechen", DialogResult.CANCEL);
		}
		return this;
	}

	private void addDialogButton(String text, DialogResult result) {
		Button button = new Button(text);
		button.getStyleClass().add("dialog-button");
		button.setOnAction(event -> {
			this.exitWith(result);
		});
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
		Parent mainWindowRoot = EntryPoint.getPrimaryStage().getScene().getRoot();

		EntryPoint.getPrimaryStage().getScene().setRoot(this);
		this.mainWindowRootContainer.getChildren().add(mainWindowRoot);
	}

	public void exitWith(DialogResult result) {
		this.hide();
		this.dialogResultListener.onDialogClosed(result, this.dialogContent.getReturnValue());
	}

	private void hide() {
		Parent mainWindowRoot = (Parent) this.mainWindowRootContainer.getChildren().get(0);

		this.mainWindowRootContainer.getChildren().clear();
		EntryPoint.getPrimaryStage().getScene().setRoot(mainWindowRoot);
	}
}
