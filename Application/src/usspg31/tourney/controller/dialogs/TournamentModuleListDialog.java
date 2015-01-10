package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import usspg31.tourney.model.TournamentModule;

public class TournamentModuleListDialog extends AnchorPane implements DialogContent<List<TournamentModule>, Object> {

	private static final Logger log = Logger.getLogger(TournamentModuleListDialog.class.getName());

	@FXML private TableView<String> tableTournamentModules;
	@FXML private Button buttonAddTournamentModule;
	@FXML private Button buttonRemoveTournamentModule;
	@FXML private Button buttonEditTournamentModule;

	public TournamentModuleListDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/fxml/dialogs/tournament-module-list-dialog.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public void setProperties(List<TournamentModule> properties) {
		// TODO: link the given list to the table
	}

}
