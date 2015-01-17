package usspg31.tourney.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.model.undo.UndoManager;

public class EntryPoint extends Application {
    private static final Logger log = Logger.getLogger(EntryPoint.class
	    .getName());

    private static Stage primaryStage;

    public static void main(String[] args) {
	log.info("Starting Application");
	log.info("Running JavaFX Version "
		+ System.getProperty("javafx.runtime.version"));

	try {
	    launch(args);
	} catch (Throwable t) {
	    log.log(Level.SEVERE, t.getMessage(), t);
	}
    }

    public static Stage getPrimaryStage() {
	return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
	EntryPoint.primaryStage = primaryStage;
	try {
	    Pane root = MainWindow.getInstance();
	    Scene scene = new Scene(root);
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("Tourney");

	    primaryStage.minWidthProperty().bind(root.minWidthProperty());
	    primaryStage.minHeightProperty().bind(root.minHeightProperty());

	    Platform.setImplicitExit(false);

	    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		@Override
		public void handle(WindowEvent event) {
		    event.consume();
		    UndoManager undoManager = MainWindow.getInstance()
			    .getEventPhaseViewController()
			    .getActiveUndoManager();
		    if (undoManager != null) {
			if (undoManager.undoAvailable()) {
			    new SimpleDialog<>(
				    "Es sind ungesicherte Änderungen vorhanden.\n"
					    + "Möchten Sie diese vor dem Beenden speichern?")
				    .modalDialog()
				    .title("Warnung")
				    .dialogButtons(DialogButtons.YES_NO_CANCEL)
				    .onResult(
					    (result, returnValue) -> {
						switch (result) {
						case CANCEL:
						    return;
						case YES:
						    DialogResult saveResponse = MainWindow
							    .getInstance()
							    .getEventPhaseViewController()
							    .saveEvent();

						    if (saveResponse != DialogResult.OK) {
							return;
						    }

						    /*
						     * Fall through if there was
						     * no return yet
						     */
						default:
						    primaryStage.close();
						    Platform.exit();
						    break;
						}
					    }).show();
			}
		    }
		}
	    });

	    primaryStage.show();
	} catch (Exception e) {
	    log.log(Level.SEVERE, e.getMessage(), e);
	    System.exit(1);
	}
    }
}
