package usspg31.tourney.controller.dialogs;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PdfOutputConfiguration {
    private BooleanProperty playerList;
    private BooleanProperty tournaments;
    private ObservableList<Boolean> tournamentList;
    
    public PdfOutputConfiguration() {
	this.playerList = new SimpleBooleanProperty();
	this.tournaments = new SimpleBooleanProperty();
	this.setTournamentList(FXCollections.observableArrayList());
    }

    public BooleanProperty getTournaments() {
	return tournaments;
    }

    public void setTournaments(BooleanProperty tournaments) {
	this.tournaments = tournaments;
    }

    public BooleanProperty getPlayerList() {
	return playerList;
    }

    public void setPlayerList(BooleanProperty playerList) {
	this.playerList = playerList;
    }

    public ObservableList<Boolean> getTournamentList() {
	return tournamentList;
    }

    public void setTournamentList(ObservableList<Boolean> tournamentList) {
	this.tournamentList = tournamentList;
    }
}
