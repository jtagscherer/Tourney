package usspg31.tourney.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.model.undo.UndoManager;

public class EntryPoint extends Application {
    private static final Logger log = Logger.getLogger(EntryPoint.class
            .getName());

    private static Stage primaryStage;
    private boolean closeRequested;

    public static void main(String[] args) {
        log.info("Starting Application");
        log.info("Running JavaFX Version "
                + System.getProperty("javafx.runtime.version"));

        try {
            launch(args);
        } catch (Throwable t) { // catch anything the application could throw at
                                // us
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

            /* Set the icon set */
            int[] iconSizes = { 1024, 512, 256, 128, 96, 64, 48, 32, 24, 16 };
            for (int iconSize : iconSizes) {
                primaryStage.getIcons().add(
                        new Image(this.getClass().getResourceAsStream(
                                "/ui/icon/icon-" + iconSize + ".png")));
            }

            primaryStage.minWidthProperty().bind(root.minWidthProperty());
            primaryStage.minHeightProperty().bind(root.minHeightProperty());

            /*
             * Catch the close event and display a warning if there is unsaved
             * data left
             */
            Platform.setImplicitExit(false);
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                UndoManager undoManager = MainWindow.getInstance()
                        .getEventPhaseViewController().getActiveUndoManager();
                if (undoManager != null && !EntryPoint.this.closeRequested) {
                    this.requestSaveBeforeClose();
                } else {
                    EntryPoint.this.closeRequested = false;
                    primaryStage.close();
                    Platform.exit();
                }
            });

            primaryStage.show();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            System.exit(1);
        }
    }

    private void requestSaveBeforeClose() {
        if (MainWindow.getInstance().getEventPhaseViewController()
                .getActiveUndoManager().undoAvailable()) {
            EntryPoint.this.closeRequested = true;
            new SimpleDialog<>("Es sind ungesicherte Änderungen vorhanden.\n"
                    + "Möchten Sie diese vor dem Beenden speichern?")
                    .modalDialog()
                    .title("dialogs.titles.warning")
                    .dialogButtons(DialogButtons.YES_NO_CANCEL)
                    .onResult(
                            (result, returnValue) -> {
                                switch (result) {
                                case CANCEL:
                                    EntryPoint.this.closeRequested = false;
                                    return;
                                case YES:
                                    DialogResult saveResponse = MainWindow
                                            .getInstance()
                                            .getEventPhaseViewController()
                                            .saveEvent();

                                    EntryPoint.this.closeRequested = false;

                                    if (saveResponse != DialogResult.OK) {
                                        return;
                                    }
                                default: // Fall through if there was no return
                                         // yet
                                    EntryPoint.this.closeRequested = false;
                                    primaryStage.close();
                                    Platform.exit();
                                    break;
                                }
                            }).show();
        } else {
            EntryPoint.this.closeRequested = false;
            primaryStage.close();
            Platform.exit();
        }
    }
}
