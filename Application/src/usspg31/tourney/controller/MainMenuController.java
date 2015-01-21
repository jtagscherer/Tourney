package usspg31.tourney.controller;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import org.controlsfx.dialog.Dialogs;

import usspg31.tourney.controller.controls.EventPhaseViewController;
import usspg31.tourney.controller.dialogs.TournamentModuleListDialog;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.model.filemanagement.FileLoader;

@SuppressWarnings("deprecation")
public class MainMenuController {

    private static final Logger log = Logger.getLogger(MainMenuController.class
            .getName());

    @FXML private Button buttonNewEvent;
    @FXML private Button buttonOpenEvent;
    @FXML private Button buttonEditTournamentModules;
    @FXML private Button buttonOpenOptions;

    @FXML private VBox eventButtonsLeft;
    @FXML private VBox eventButtonsRight;
    @FXML private HBox eventButtonsContainer;

    private ModalDialog<ObservableList<TournamentModule>, Object> tournamentModuleListDialog;

    @FXML
    private void initialize() {
        this.tournamentModuleListDialog = new TournamentModuleListDialog()
                .modalDialog();
    }

    @FXML
    private void onButtonNewEventClicked(ActionEvent event) {
        // create a new event and open it
        log.fine("New Event Button was clicked");
        MainWindow.getInstance().getEventPhaseViewController()
                .loadEvent(new Event());
        MainWindow.getInstance().slideUp(
                MainWindow.getInstance().getEventPhaseView());
    }

    @FXML
    private void onButtonOpenEventClicked(ActionEvent event) {
        // open the file chooser dialog and load the chosen event
        log.fine("Open Event Button was clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Eventdatei öffnen");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("Tourney Eventdateien (*.tef)", "*.tef"));
        File selectedFile = fileChooser.showOpenDialog(EntryPoint
                .getPrimaryStage());
        if (selectedFile != null) {
            Event loadedEvent = null;

            try {
                loadedEvent = FileLoader.loadEventFromFile(selectedFile
                        .getAbsolutePath());
            } catch (Exception e) {
                log.log(Level.SEVERE, "Could not load the specified event.", e);
                Dialogs.create()
                        .owner(EntryPoint.getPrimaryStage())
                        .title("Fehler")
                        .message(
                                "Die Eventdatei \""
                                        + selectedFile.getName()
                                        + "\" konnte nicht geladen werden.\nBitte stellen Sie sicher, dass es sich dabei um eine gültige Eventdatei handelt.")
                        .showError();
            }

            if (loadedEvent != null) {
                EventPhaseViewController eventPhaseViewController = MainWindow
                        .getInstance().getEventPhaseViewController();

                eventPhaseViewController.loadEvent(loadedEvent);
                eventPhaseViewController.setLoadedEventFile(selectedFile);

                MainWindow.getInstance().slideUp(
                        MainWindow.getInstance().getEventPhaseView());
            }
        }
    }

    @FXML
    private void onButtonOpenTournamentModuleEditorClicked(ActionEvent event) {
        log.fine("Open Tournament Module Editor Button was clicked");

        this.tournamentModuleListDialog.properties(null) // TODO: get all
                                                         // available tournament
                                                         // modules from the
                                                         // preferencesManager
                .show();
    }

    @FXML
    private void onButtonOpenOptionsClicked(ActionEvent event) {
        log.fine("Options Button was clicked");

        MainWindow
                .getInstance()
                .getOptionsViewController()
                .setExitProperties(
                        "settings.returnto.mainmenu",
                        "settings.returnto.mainmenu.description",
                        "settings.returnto.mainmenu.explanation",
                        () -> {
                            MainWindow.getInstance().slideDown(
                                    MainWindow.getInstance().getMainMenu());
                        });

        MainWindow.getInstance().slideUp(
                MainWindow.getInstance().getOptionsView());
    }
}
