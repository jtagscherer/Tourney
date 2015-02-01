package usspg31.tourney.controller.dialogs;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import usspg31.tourney.model.Tournament;

public class PdfOutputConfiguration {
    public class TournamentEntry {
        private ObjectProperty<Tournament> tournament;
        private BooleanProperty exported;

        public TournamentEntry() {
            this.tournament = new SimpleObjectProperty<Tournament>();
            this.exported = new SimpleBooleanProperty();
        }

        public BooleanProperty getExportedProperty() {
            return exported;
        }

        public boolean shouldBeExported() {
            return this.exported.get();
        }

        public ObjectProperty<Tournament> getTournamentProperty() {
            return tournament;
        }

        public Tournament getCorrespondingTournament() {
            return this.tournament.get();
        }
    }

    private BooleanProperty playerList;
    private BooleanProperty tournaments;
    private ObservableList<TournamentEntry> tournamentList;

    public PdfOutputConfiguration() {
        this.playerList = new SimpleBooleanProperty();
        this.tournaments = new SimpleBooleanProperty();
        this.setTournamentList(FXCollections.observableArrayList());
    }

    public void setTournaments(ObservableList<Tournament> tournaments) {
        for (Tournament tournament : tournaments) {
            TournamentEntry entry = new TournamentEntry();
            entry.getTournamentProperty().set(tournament);
            entry.getExportedProperty().set(true);
            this.tournamentList.add(entry);
        }
    }

    public BooleanProperty tournamentProperty() {
        return this.tournaments;
    }

    public void exportTournaments(boolean value) {
        this.tournaments.set(value);
    }

    public boolean exportTournaments() {
        return this.tournaments.get();
    }

    public boolean exportPlayerList() {
        return this.playerList.get();
    }

    public void setTournaments(BooleanProperty tournaments) {
        this.tournaments = tournaments;
    }

    public BooleanProperty playerListProperty() {
        return this.playerList;
    }

    public void exportPlayerList(boolean value) {
        this.playerList.set(value);
    }

    public void setPlayerList(BooleanProperty playerList) {
        this.playerList = playerList;
    }

    public ObservableList<TournamentEntry> getTournamentList() {
        return tournamentList;
    }

    public void setTournamentList(ObservableList<TournamentEntry> tournamentList) {
        this.tournamentList = tournamentList;
    }
}
