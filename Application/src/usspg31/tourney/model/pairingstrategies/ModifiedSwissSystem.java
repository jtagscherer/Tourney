package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Collections;
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

		    partResult.getScoreTable().add(
			    PairingHelper.generateEmptyScore(
				    randomList.get(randomList.size()),
				    tournament.getRuleSet().getPossibleScores()
					    .get(0).getScores().size()));

		    partResult.getOpponents().add(randomList.get(randomNumber));
		    randomList.remove(randomNumber);
		}
		result.add(partResult);
	    }
	} else {
	    ArrayList<PlayerScore> tmp = new ArrayList<>();
	    ArrayList<PlayerScore> subTmp;
	    subList = new ArrayList<ArrayList<PlayerScore>>();
	    tmp = PairingHelper.mergeScoreRemainingPlayer(tournament);
	    Collections.sort(tmp);
	    while (tmp.size() > 0) {
		subTmp = new ArrayList<PlayerScore>();
		subTmp.add(tmp.get(tmp.size() - 1));
		tmp.remove(tmp.size() - 1);

		for (PlayerScore test : tmp) {
		    if (test.getScore().get(0) == subTmp.get(0).getScore()
			    .get(0)) {
			subTmp.add(test);
			tmp.remove(tmp);
		    } else {
			break;
		    }

		}
		subList.add(subTmp);

	    }
	    for (ArrayList<PlayerScore> subScoreList : subList) {
		while (subScoreList.size() >= PairingHelper.findPhase(
			tournament.getRounds().size(), tournament)
			.getNumberOfOpponents()) {
		    partResult = new Pairing();

		    for (int i = 0; i < PairingHelper.findPhase(
			    tournament.getRounds().size(), tournament)
			    .getNumberOfOpponents(); i++) {

			partResult.getScoreTable().add(
				PairingHelper.generateEmptyScore(subScoreList
					.get(subScoreList.size() - 1)
					.getPlayer(), tournament.getRuleSet()
					.getPossibleScores().get(0).getScores()
					.size()));
			if (i == 0) {
			    partResult.getOpponents().add(
				    subScoreList.get(subScoreList.size() - 1)
					    .getPlayer());
			    subScoreList.remove(subScoreList.size() - 1);
			} else {
			    partResult.getOpponents().add(
				    subScoreList.get(subScoreList.size() - i)
					    .getPlayer());
			    if (i == PairingHelper.findPhase(
				    tournament.getRounds().size(), tournament)
				    .getNumberOfOpponents() - 1) {
				if (PairingHelper.checkForSimiliarPairings(
					partResult, tournament)) {

				    // TODO finish the procedure for similar
				    // pairings
				} else {
				    subScoreList
					    .remove(subScoreList.size() - 1);

				}
			    }
			}

		    }
		    result.add(partResult);

		}
		if (subScoreList.size() > 0) {
		    if (subList.indexOf(subScoreList) == subList.size() - 1) {
			// TODO implement bye
		    } else {
			subList.get(subList.indexOf(subScoreList) + 1).addAll(
				subScoreList);
			Collections.sort(subList.get(subList
				.indexOf(subScoreList) + 1));

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
