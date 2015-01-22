package usspg31.tourney.controller.controls.eventphases;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.EventUser;
import usspg31.tourney.controller.controls.eventphases.execution.TournamentExecutionController;
import usspg31.tourney.controller.controls.eventphases.execution.TournamentSelectionController;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.Tournament;

public class TournamentExecutionPhaseController implements EventUser {

    private static final Logger log = Logger
            .getLogger(TournamentExecutionPhaseController.class.getName());

    @FXML private VBox contentBox;

    private Node selectionPhase;
    private TournamentSelectionController selectionController;

    private Node executionPhase;
    private TournamentExecutionController executionController;

    private Event loadedEvent;

    @FXML
    public void initialize() {
        try {
            /* Load the selection view */
            FXMLLoader selectionLoader = new FXMLLoader(
                    this.getClass()
                            .getResource(
                                    "/ui/fxml/controls/eventphases/execution/tournament-selection.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                            .getLanguageBundle());
            this.selectionPhase = selectionLoader.load();
            this.selectionController = selectionLoader.getController();
            this.selectionController.setExecutionSuperController(this);
            this.selectionPhase.setVisible(true);

            /* Load the execution view */
            FXMLLoader executionLoader = new FXMLLoader(
                    this.getClass()
                            .getResource(
                                    "/ui/fxml/controls/eventphases/execution/tournament-execution.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                            .getLanguageBundle());
            this.executionPhase = executionLoader.load();
            this.executionController = executionLoader.getController();
            this.executionPhase.setVisible(true);

            this.showSelectionView();
        } catch (IOException e) {
            log.log(Level.SEVERE,
                    "Could not initialize the tournament execution views.", e);
            e.printStackTrace();
        }
    }

    @Override
    public void loadEvent(Event event) {
        this.loadedEvent = event;
        this.selectionController.loadEvent(this.loadedEvent);
    }

    @Override
    public void unloadEvent() {
        // TODO: Unload and unbind everything
    }

    public void showSelectionView() {
        this.contentBox.getChildren().clear();
        this.contentBox.getChildren().add(this.selectionPhase);
    }

    public void showTournamentExecutionView(Tournament tournament) {
        this.contentBox.getChildren().clear();
        // TODO: Give the tournament to the execution controller. Currently
        // freezes the application
        // this.executionController.loadTournament(tournament);
        this.contentBox.getChildren().add(this.executionPhase);
    }
}
