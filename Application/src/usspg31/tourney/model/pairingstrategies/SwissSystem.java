package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.Tournament;

public class SwissSystem implements PairingStrategy {

	@Override
	public ArrayList<Pairing> generatePairing(Tournament tournament) {

		ArrayList<Pairing> result = new ArrayList<>();
		Pairing partResult;
		ArrayList<PlayerScore> mergedScoreTable = new ArrayList<>();

		PlayerScore score;
		Integer[] numberOfScores;

		mergedScoreTable = PairingHelper.mergeScoreRemainingPlayer(tournament);
		Collections.sort(mergedScoreTable);

		if (tournament.getRounds().size() == 0) {

			Random randomGenerator = new Random();
			ArrayList<Player> randomList = new ArrayList<>();
			randomList.addAll(tournament.getRemainingPlayers());
			int randomNumber;

			while (randomList.size() >= PairingHelper.findPhase(
					tournament.getRounds().size(), tournament)
					.getNumberOfOpponents()) {
				partResult = new Pairing();

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
			}
		} else {
			while (mergedScoreTable.size() >= PairingHelper.findPhase(
					tournament.getRounds().size(), tournament)
					.getNumberOfOpponents()) {
				partResult = new Pairing();

				for (int i = 0; i < PairingHelper.findPhase(
						tournament.getRounds().size(), tournament)
						.getNumberOfOpponents(); i++) {
					score = new PlayerScore();
					score.setPlayer(mergedScoreTable.get(
							mergedScoreTable.size() - 1).getPlayer());
					numberOfScores = new Integer[tournament.getRuleSet()
							.getPossibleScores().size()];
					score.getScore().addAll(numberOfScores);
					partResult.getScoreTable().add(score);
					if (i == 0) {
						partResult.getOpponents().add(
								mergedScoreTable.get(
										mergedScoreTable.size() - 1)
										.getPlayer());
						mergedScoreTable.remove(mergedScoreTable.size() - 1);
					} else {
						partResult.getOpponents().add(
								mergedScoreTable.get(
										mergedScoreTable.size() - i)
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
								mergedScoreTable
										.remove(mergedScoreTable.size() - 1);

								result.add(partResult);
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
		return "Schweizer System";
	}
}
