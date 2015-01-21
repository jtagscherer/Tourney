package usspg31.tourney.controller.controls;

import usspg31.tourney.model.Tournament;

public interface TournamentUser {
    public void loadTournament(Tournament tournament);

    public void unloadTournament();
}
