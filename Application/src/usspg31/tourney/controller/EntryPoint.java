package usspg31.tourney.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.model.undo.UndoManager;

public class EntryPoint extends Application {
    private static final Logger log = Logger.getLogger(EntryPoint.class
            .getName());

    private static Stage primaryStage;
    private static StackPane root;
    private static Pane mainWindow;
    private static StackPane modalOverlay;

    private static boolean applicationLocked;

    private boolean closeRequested;

    public static void main(String[] args) {
        log.info("Starting Application");
        log.info("Running JavaFX Version "
                + System.getProperty("javafx.runtime.version") + " on "
                + System.getProperty("os.name"));

        try {
            launch(args);
        } catch (Throwable t) { // catch anything the application could throw at
                                // us
            log.log(Level.SEVERE, t.getMessage(), t);
        }

        applicationLocked = false;
    }

    public static void lockApplication() {
        applicationLocked = true;
    }

    public static void unlockApplication() {
        applicationLocked = false;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        EntryPoint.primaryStage = primaryStage;
        try {
            root = new StackPane();
            mainWindow = MainWindow.getInstance();
            root.getChildren().add(mainWindow);
            modalOverlay = new StackPane();
            root.getChildren().add(modalOverlay);

            // disable the main window as long as there are modal dialogs opened
            mainWindow.disableProperty().bind(
                    Bindings.size(modalOverlay.getChildren()).greaterThan(0));

            // hide the modalOverlay as long it is empty
            modalOverlay.visibleProperty().bind(Bindings.size(modalOverlay.getChildren()).greaterThan(0));

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            /* Available icon sizes: 1024, 512, 256, 128, 96, 64, 48, 32, 24, 16 */
            int[] iconSizes = { 1024 };

            /* Set the icon set */
            for (int iconSize : iconSizes) {
                primaryStage.getIcons().add(
                        new Image(this.getClass().getResourceAsStream(
                                "/ui/icon/icon-" + iconSize + ".png")));
            }

            primaryStage.minWidthProperty().bind(mainWindow.minWidthProperty());
            primaryStage.minHeightProperty().bind(mainWindow.minHeightProperty());

            /*
             * Catch the close event and display a warning if there is unsaved
             * data left
             */
            Platform.setImplicitExit(false);
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                // surpress the close request when the application is locked
                    if (applicationLocked) {
                        return;
                    }
                    UndoManager undoManager = MainWindow.getInstance()
                            .getEventPhaseViewController().getUndoManager();
                    if (undoManager != null
                            && !EntryPoint.this.closeRequested
                            && MainWindow.getInstance()
                                    .getEventPhaseViewController()
                                    .hasLoadedEvent()) {
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
                .saveAvailable()) {
            EntryPoint.this.closeRequested = true;
            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "dialogs.messages.unsavedchanges"))
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

    public static StackPane getModalOverlay() {
        return modalOverlay;
    }
}
