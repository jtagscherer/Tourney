package usspg31.tourney.controller.controls.eventphases;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import usspg31.tourney.controller.controls.PairingView;
import usspg31.tourney.controller.controls.TournamentUser;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.RoundGeneratorFactory;
import usspg31.tourney.model.Tournament;

public class TournamentExecutionPhaseController implements TournamentUser {

    private static final Logger log = Logger
	    .getLogger(TournamentExecutionPhaseController.class.getName());

    @FXML
    private PairingView pairingView;
    @FXML
    private Button buttonStartRound;
    @FXML
    private Button buttonEnterResult;

    private Tournament loadedTournament;
    private RoundGeneratorFactory roundGenerator = new RoundGeneratorFactory();

    @Override
    public void loadTournament(Tournament tournament) {
	this.loadedTournament = tournament;

	this.pairingView.loadTournament(tournament);

	this.buttonEnterResult.disableProperty().bind(
		this.pairingView.selectedPairingProperty().isNull());

	if (tournament.getRounds().size() == 0) {
	    this.generateRound();
	}
    }

    @Override
    public void unloadTournament() {
	this.pairingView.unloadTournament();
    }

    @FXML
    private void onButtonStartRoundClicked(ActionEvent event) {
	this.generateRound();
    }

    private void generateRound() {
	this.loadedTournament.getRounds().add(
		this.roundGenerator.generateRound(this.loadedTournament));
	this.buttonStartRound.setDisable(true);
    }

    @FXML
    private void onButtonEnterResultClicked(ActionEvent event) {
	// TODO: open dialog to enter score for every player

	boolean roundFinished = true;
	roundFinishCheck: for (Pairing pairing : this.loadedTournament
		.getRounds().get(this.loadedTournament.getRounds().size() - 1)
		.getPairings()) {
	    for (PlayerScore playerScore : pairing.getScoreTable()) {
		for (Integer score : playerScore.getScore()) {
		    if (score == null) {
			roundFinished = false;
			break roundFinishCheck;
		    }
		}
	    }
	}
	if (roundFinished) {
	    this.buttonStartRound.setDisable(false);
	}
    }
}
