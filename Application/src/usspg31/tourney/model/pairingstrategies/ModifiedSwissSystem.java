package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Random;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.Tournament;

public class ModifiedSwissSystem implements PairingStrategy {

	@Override
	public ArrayList<Pairing> generatePairing(Tournament tournament) {
		ArrayList<Pairing> result = new ArrayList<>();
		ArrayList<PlayerScore> opponents = new ArrayList<>();

		Pairing partResult;

		Integer[] numberOfScores;
		PlayerScore score;

		// TODO refactor name
		ArrayList<ArrayList<PlayerScore>> subList;

		if (tournament.getRounds().size() == 0) {

			Random randomGenerator = new Random();
			ArrayList<Player> randomList = new ArrayList<>();
			randomList.addAll(tournament.getRemainingPlayers());
			int randomNumber;

			while (randomList.size() >= PairingHelper.findPhase(
					tournament.getRounds().size(), tournament)
					.getNumberOfOpponents()) {
				partResult = new Pairing();
				partResult.getScoreTable().addAll(opponents);

				for (int i = 0; i < PairingHelper.findPhase(
						tournament.getRounds().size(), tournament)
						.getNumberOfOpponents(); i++) {
					randomNumber = randomGenerator.nextInt(randomList.size());

					score = new PlayerScore();
					score.setPlayer(randomList.get(randomNumber));
					numberOfScores = new Integer[tournament.getRuleSet()
							.getPossibleScores().size()];
					score.getScore().addAll(numberOfScores);
					partResult.getScoreTable().add(score);

					partResult.getOpponents().add(randomList.get(randomNumber));
					randomList.remove(randomNumber);
				}
				result.add(partResult);
			}
		} else {
			ArrayList<PlayerScore> tmp = new ArrayList<>();
			tmp = PairingHelper.mergeScoreRemainingPlayer(tournament);
			while (tmp.size() > 0) {

			}
		}
		return result;
	}

	@Override
	public String getName() {
		return "Modifiziertes Schweizer System (not implemented)";
	}

}
