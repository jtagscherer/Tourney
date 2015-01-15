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
		ArrayList<ArrayList<PlayerScore>> subList = new ArrayList<>();

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
		} else if (PairingHelper.isFirstInPhase(tournament.getRounds().size(),
				tournament, PairingHelper.findPhase(tournament.getRounds()
						.size(), tournament))) {
			for (ArrayList<PlayerScore> sameScore : subList) {
				while (sameScore.size() >= PairingHelper.findPhase(
						tournament.getRounds().size(), tournament)
						.getNumberOfOpponents()) {
					partResult = new Pairing();

					for (int i = 0; i < PairingHelper.findPhase(
							tournament.getRounds().size(), tournament)
							.getNumberOfOpponents(); i++) {
						score = new PlayerScore();
						score.setPlayer(sameScore.get(sameScore.size() - 1)
								.getPlayer());
						numberOfScores = new Integer[tournament.getRuleSet()
								.getPossibleScores().size()];
						score.getScore().addAll(numberOfScores);
						partResult.getScoreTable().add(score);
						if (i == 0) {
							partResult.getOpponents().add(
									sameScore.get(sameScore.size() - 1)
											.getPlayer());
							sameScore.remove(sameScore.size() - 1);
						} else {
							partResult.getOpponents().add(
									sameScore.get(sameScore.size() - i)
											.getPlayer());
							if (i == PairingHelper.findPhase(
									tournament.getRounds().size(), tournament)
									.getNumberOfOpponents() - 1) {
								if (PairingHelper.checkForSimiliarPairings(
										partResult, tournament)) {

									// TODO finish the procedure for similar
									// pairings
									result.add(partResult);
								} else {
									sameScore.remove(sameScore.size() - 1);

									result.add(partResult);
								}
							}
						}

					}
				}
			}

		}
		return result;
	}

	@Override
	public String getName() {
		return "Modifiziertes Schweizer System (not implemented)";
	}

}
