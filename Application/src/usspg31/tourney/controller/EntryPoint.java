package usspg31.tourney.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.model.undo.UndoManager;

public class EntryPoint extends Preloader {
    private static final Logger log = Logger.getLogger(EntryPoint.class
            .getName());

    private static Stage primaryStage;
    private static StackPane root;
    private static Pane mainWindow;
    private static StackPane modalOverlay;

    private static Animation blurTransition;
    private static GaussianBlur backgroundBlur;

    private static boolean applicationLocked;

    private boolean closeRequested;

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
        EntryPoint.unlockApplication();
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
            modalOverlay.visibleProperty().bind(
                    Bindings.size(modalOverlay.getChildren()).greaterThan(0));

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
            primaryStage.minHeightProperty().bind(
                    mainWindow.minHeightProperty());

            /*
             * Catch the close event and display a warning if there is unsaved
             * data left
             */
            Platform.setImplicitExit(false);
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                // suppress the close request when the application is locked
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

            backgroundBlur = new GaussianBlur(0);

            blurTransition = new Timeline(new KeyFrame(Duration.ZERO,
                    new KeyValue(backgroundBlur.radiusProperty(), 0)),
                    new KeyFrame(Duration.millis(300), new KeyValue(
                            backgroundBlur.radiusProperty(), 10)));

            mainWindow.setEffect(backgroundBlur);

            primaryStage.show();
            SplashScreen.hide();
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

    public static void blurMainWindow() {
        if (modalOverlay.getChildren().size() > 1) {
            return;
        }
        blurTransition.playFromStart();
    }

    public static void unblurMainWindow() {
        if (modalOverlay.getChildren().size() != 1) {
            return;
        }
        // play the blur transition in reverse
        blurTransition.stop();
        blurTransition.setRate(-1);
        blurTransition.jumpTo(blurTransition.getTotalDuration());
        blurTransition.play();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            System.out.println("started");
        }
    }
}
