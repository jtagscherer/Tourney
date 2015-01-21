package usspg31.tourney.controller.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.Tournament;

/**
 * Control used to display a pairing within a tournament.
 */
public class PairingNode extends VBox {

    private final Tournament tournament;
    private final Pairing pairing;
    private final int index;

    private BooleanProperty selected;

    private TableView<PlayerScore> opponentTable;

    /**
     * Initializes a new PairingNode.
     *
     * @param tournament
     *            the tournament to retrieve additional data from
     * @param pairing
     *            the actual pairing to display
     * @param index
     *            the id of the pairing
     */
    public PairingNode(Tournament tournament, Pairing pairing, int index) {
        super(5);

        this.tournament = tournament;
        this.pairing = pairing;
        this.index = index;

        this.initContents();
    }

    /**
     * Initializes the control's contents.
     */
    private void initContents() {
        this.getChildren().add(new Label("#" + this.index));

        this.opponentTable = new TableView<>();

        TableColumn<PlayerScore, String> playerNameColumn = new TableColumn<>(
                "Name");
        playerNameColumn.setCellValueFactory(score -> {
            return score.getValue().getPlayer().lastNameProperty();
        });

        this.opponentTable.getColumns().add(playerNameColumn);

        for (PossibleScoring scoring : this.tournament.getRuleSet()
                .getPossibleScores()) {
            TableColumn<PlayerScore, String> scoreColumn = new TableColumn<>(
                    Integer.toString(scoring.getPriority()));
            scoreColumn.setCellValueFactory(score -> {
                return new SimpleStringProperty(Integer.toString(score
                        .getValue().getScore().get(scoring.getPriority())));
            });
        }

        this.opponentTable.setItems(this.pairing.getScoreTable());
    }

    public Pairing getPairing() {
        return this.pairing;
    }

    /**
     * @return the selected property
     */
    public BooleanProperty selectedProperty() {
        if (this.selected == null) {
            this.selected = new SimpleBooleanProperty();
        }
        return this.selected;
    }

    /**
     * @return the value of the selected property
     */
    public boolean isSelected() {
        return this.selectedProperty().get();
    }

    /**
     * @param value
     *            sets the new value for the selected property
     */
    public void setSelected(boolean value) {
        this.selectedProperty().set(value);
    }

    @Override
    protected void finalize() throws Throwable {
        this.opponentTable.setItems(null);

        super.finalize();
    }

}
