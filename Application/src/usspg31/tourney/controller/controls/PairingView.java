package usspg31.tourney.controller.controls;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentRound;

public class PairingView implements TournamentUser {

	private static final Logger log = Logger.getLogger(PairingView.class.getName());

	@FXML private Button buttonScrollBreadcrumbsLeft;
	@FXML private Button buttonScrollBreadcrumbsRight;
	@FXML private HBox breadcrumbContainer;
	@FXML private FlowPane pairingContainer;

	private Tournament loadedTournament;

	private IntegerProperty selectedRound;

	public PairingView() {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/fxml/controls/pairing-view.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		this.selectedRound = new SimpleIntegerProperty(-1);
	}

	@FXML private void initialize() {
		this.selectedRound.addListener((ov, o, n) -> {
			if (n.intValue() < this.loadedTournament.getRounds().size()) {
				for (int i = 0; i < this.loadedTournament.getRounds().size(); i++) {
					Pairing p = this.loadedTournament.getRounds().get(n.intValue()).getPairings().get(i);
					this.pairingContainer.getChildren().add(this.createPairingNode(this.loadedTournament, p, i));
				}
			} else {
				this.pairingContainer.getChildren().clear();
			}
		});
	}

	@Override
	public void loadTournament(Tournament tournament) {
		this.loadedTournament = tournament;

		// automatically show newly added rounds
		tournament.getRounds()
		.addListener((ListChangeListener<? super TournamentRound>) change -> {
			if (change.next() && change.getAddedSize() == 1) {
				this.selectedRound.set(this.loadedTournament.getRounds().size() - 1);
				this.addBreadcrumb();
			}
		});
	}

	@Override
	public void unloadTournament() {
		this.loadedTournament = null;
	}

	private void addBreadcrumb() {
		final int pageNumber = this.loadedTournament.getRounds().size();
		Button breadcrumb = new Button("Runde " + pageNumber);
		breadcrumb.setOnAction(event -> {
			this.selectedRound.set(pageNumber - 1);
		});

		if (this.breadcrumbContainer.getChildren().size() == 1) {
			this.breadcrumbContainer.getChildren().get(0).getStyleClass().addAll("multi-button", "left");
			breadcrumb.getStyleClass().addAll("multi-button", "right");
		} else if (this.breadcrumbContainer.getChildren().size() > 1) {
			this.breadcrumbContainer.getChildren().get(
					this.breadcrumbContainer.getChildren().size() - 1)
					.getStyleClass().add("middle");
			breadcrumb.getStyleClass().addAll("multi-button", "right");
		}
		this.breadcrumbContainer.getChildren().add(breadcrumb);
	}

	private Node createPairingNode(Tournament tournament, Pairing pairing, int index) {
		VBox pairingNode = new VBox(5);

		pairingNode.getChildren().add(new Label("#" + index));

		TableView<PlayerScore> opponentTable = new TableView<>();
		TableColumn<PlayerScore, String> playerNameColumn = new TableColumn<>("Name");
		playerNameColumn.setCellValueFactory(score -> {
			return score.getValue().getPlayer().lastNameProperty();
		});
		opponentTable.getColumns().add(playerNameColumn);
		for (PossibleScoring scoring : tournament.getRuleSet().getPossibleScores()) {
			TableColumn<PlayerScore, String> scoreColumn = new TableColumn<>(
					Integer.toString(scoring.getPriority().get()));
			scoreColumn.setCellValueFactory(score -> {
				return new SimpleStringProperty(Integer.toString(
						score.getValue().getScore().get(scoring.getPriority().get())));
			});
		}

		return pairingNode;
	}

}
