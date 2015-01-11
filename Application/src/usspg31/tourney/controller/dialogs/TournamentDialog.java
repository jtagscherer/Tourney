package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.controls.TournamentUser;
import usspg31.tourney.controller.controls.UndoTextField;
import usspg31.tourney.model.Tournament;

public class TournamentDialog extends VBox implements TournamentUser, IModalDialogProvider<Tournament, Tournament> {

	private static final Logger log = Logger.getLogger(TournamentDialog.class.getName());

	@FXML private UndoTextField textFieldTournamentTitle;

	@FXML private Button buttonLoadTournamentModule;
	@FXML private Button buttonEditTournamentModules;

	@FXML private TableView<String> tableTournamentPhases;

	@FXML private Button buttonMoveTournamentPhaseUp;
	@FXML private Button buttonMoveTournamentPhaseDown;

	@FXML private Button buttonAddTournamentPhase;
	@FXML private Button buttonRemoveTournamentPhase;
	@FXML private Button buttonEditTournamentPhase;

	@FXML private TableView<String> tablePossibleScores;

	@FXML private Button buttonMovePossibleScoreUp;
	@FXML private Button buttonMovePossibleScoreDown;

	@FXML private Button buttonAddPossibleScore;
	@FXML private Button buttonRemovePossibleScore;
	@FXML private Button buttonEditPossibleScore;

	private Tournament loadedTournament;

	public TournamentDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/fxml/dialogs/tournament-dialog.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public void setProperties(Tournament properties) {
		this.unloadTournament();
		this.loadTournament(properties);
	}

	@Override
	public Tournament getReturnValue() {
		return this.loadedTournament;
	}

	@Override
	public void initModalDialog(ModalDialog<Tournament, Tournament> modalDialog) {
		modalDialog.title("Turniere").dialogButtons(DialogButtons.OK_CANCEL);
	}

	@Override
	public void loadTournament(Tournament tournament) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unloadTournament() {
		// TODO Auto-generated method stub
	}

	@FXML private void onButtonLoadTournamentModuleClicked(ActionEvent event) {

	}

	@FXML private void onButtonEditTournamentModulesClicked(ActionEvent event) {

	}

	@FXML private void onButtonMoveTournamentPhaseUpClicked(ActionEvent event) {

	}

	@FXML private void onButtonMoveTournamentPhaseDownClicked(ActionEvent event) {

	}

	@FXML private void onButtonAddTournamentPhaseClicked(ActionEvent event) {

	}

	@FXML private void onButtonRemoveTournamentPhaseClicked(ActionEvent event) {

	}

	@FXML private void onButtonEditTournamentPhaseClicked(ActionEvent event) {

	}

	@FXML private void onButtonMovePossibleScoreUpClicked(ActionEvent event) {

	}

	@FXML private void onButtonMovePossibleScoreDownClicked(ActionEvent event) {

	}

	@FXML private void onButtonAddPossibleScoreClicked(ActionEvent event) {

	}

	@FXML private void onButtonRemovePossibleScoreClicked(ActionEvent event) {

	}

	@FXML private void onButtonEditPossibleScoreClicked(ActionEvent event) {

	}
}
