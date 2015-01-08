package usspg31.tourney.model;

public class RoundGeneratorFactory {
	public TournamentRound generateRound(Tournament value) {
		TournamentRound round = new TournamentRound(value.getRounds().size());
		round.getPairings().addAll(
				value.getRuleSet().getPhaseList().get(value.getRounds().size())
						.getPairingMethod().generatePairing(value));
		return round;
	}
}
