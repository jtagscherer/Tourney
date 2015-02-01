package usspg31.tourney.controller.dialogs;

import usspg31.tourney.model.Player;

public class VictoryConfiguration {
    private Player winningPlayer;
    private String tournamentName;

    public Player getWinningPlayer() {
        return winningPlayer;
    }

    public void setWinningPlayer(Player winningPlayer) {
        this.winningPlayer = winningPlayer;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }
}
