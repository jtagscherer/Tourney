package usspg31.tourney.model;

public class RoundGeneratorFactory {

    public TournamentRound generateRound(Tournament value) {
        TournamentRound round = new TournamentRound(value.getRounds().size());
        round.getPairings().addAll(
                PairingHelper.findPhase(value.getRounds().size(), value)
                        .getPairingMethod().generatePairing(value));
        return round;
    }
}
