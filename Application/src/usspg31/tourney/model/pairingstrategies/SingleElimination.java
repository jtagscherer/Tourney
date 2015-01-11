package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Random;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.Tournament;

public class SingleElimination implements PairingStrategy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * usspg31.tourney.model.pairingstrategies.PairingStrategy#generatePairing
	 * (usspg31.tourney.model.Tournament)
	 */
	@Override
	public ArrayList<Pairing> generatePairing(Tournament tournament) {

		ArrayList<Pairing> result = new ArrayList<>();
		Pairing partResult = new Pairing();
		PlayerScore scoreTable;
		ArrayList<PlayerScore> opponents = new ArrayList<>();
		PlayerScore score;
		Integer[] numberOfScores;

		// checks if this round is the first round in the gamephase and modify
		// the pairing strategy for this round
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
					partResult.getOpponents().add(randomList.get(randomNumber));
					partResult.getScoreTable().add(score);
					randomList.remove(randomNumber);
				}
				result.add(partResult);
			}
		} else if (PairingHelper.isFirstInPhase(tournament.getRounds().size(),
				tournament, PairingHelper.findPhase(tournament.getRounds()
						.size(), tournament))) {
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
					partResult.getOpponents().add(randomList.get(randomNumber));
					partResult.getScoreTable().add(score);

					partResult.getOpponents().add(randomList.get(randomNumber));
					randomList.remove(randomNumber);
				}
				result.add(partResult);
			}
		} else {
			ArrayList<Pairing> tmp = new ArrayList<>();
			tmp.addAll(tournament.getRounds()
					.get(tournament.getRounds().size() - 1).getPairings());
			while (tmp.size() > PairingHelper.findPhase(
					tournament.getRounds().size(), tournament)
					.getNumberOfOpponents() - 1) {
				partResult = new Pairing();
				partResult.getScoreTable().addAll(opponents);

				for (int i = 0; i < PairingHelper.findPhase(
						tournament.getRounds().size(), tournament)
						.getNumberOfOpponents(); i++) {
					score = new PlayerScore();
					score.setPlayer(PairingHelper.identifyWinner(tmp.get(0)));
					numberOfScores = new Integer[tournament.getRuleSet()
							.getPossibleScores().size()];

					score.getScore().addAll(numberOfScores);

					partResult.getScoreTable().add(score);

					partResult.getOpponents().add(
							PairingHelper.identifyWinner(tmp.get(0)));
					tmp.remove(0);
				}

				result.add(partResult);

			}

		}
		return result;
	}

	@Override
	public String getName() {
		return "K.O.-System";
	}
}
