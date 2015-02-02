package usspg31.tourney.model;

import java.util.ArrayList;
import java.util.Collections;

public class RoundGeneratorFactory {

    public TournamentRound generateRound(Tournament value) {
        if (value.getRounds().size() > 0) {

            TournamentRound currentRound = value.getRounds().get(
                    value.getRounds().size() - 1);
            for (Pairing pairing : currentRound.getPairings()) {
                for (PlayerScore score : pairing.getScoreTable()) {
                    value.addAScore(score);
                }
            }

            value.calculateTableStrength();

        }

        if (PairingHelper.cutOffAfterRound(value.getRounds().size(), value)) {
            ArrayList<PlayerScore> cloneScoreTable = new ArrayList<>();
            cloneScoreTable.addAll(value.getScoreTable());
            Collections.sort(cloneScoreTable);
            value.getRemainingPlayers().clear();
            for (int i = 0; i < PairingHelper.findPhase(
                    value.getRounds().size() - 1, value).getCutoff(); i++) {
                value.getRemainingPlayers().add(
                        cloneScoreTable.get(cloneScoreTable.size() - 1 - i)
                                .getPlayer());
            }
        }

        TournamentRound round = new TournamentRound(value.getRounds().size());
        round.getPairings().addAll(
                PairingHelper.findPhase(value.getRounds().size(), value)
                        .getPairingMethod().generatePairing(value));
        return round;
    }
}
