package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Random;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;

public class FreeForAll implements PairingStrategy {

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
	Random randomGenerator = new Random();
	ArrayList<Player> randomList = new ArrayList<>();
	Pairing partResult;
	randomList.addAll(tournament.getRemainingPlayers());
	int randomNumber;

	// checks if this is the first round in the tournament
	if (tournament.getRounds().size() == 0) {
	    while (randomList.size() >= PairingHelper.findPhase(
		    tournament.getRounds().size(), tournament)
		    .getNumberOfOpponents()) {
		partResult = new Pairing();

		for (int i = 0; i < PairingHelper.findPhase(
			tournament.getRounds().size(), tournament)
			.getNumberOfOpponents(); i++) {
		    randomNumber = randomGenerator.nextInt(randomList.size());
		    // adds an empty score table to the Pairing
		    partResult.getScoreTable().add(
			    PairingHelper.generateEmptyScore(
				    randomList.get(randomNumber), tournament
					    .getRuleSet().getPossibleScores()
					    .get(0).getScores().size()));

		    // adds an opponent to the pairing
		    partResult.getOpponents().add(randomList.get(randomNumber));
		    randomList.remove(randomNumber);
		}
		result.add(partResult);
	    }
	    // checks if the round is the first in his game phase
	} else if (PairingHelper.isFirstInPhase(tournament.getRounds().size(),
		tournament, PairingHelper.findPhase(tournament.getRounds()
			.size(), tournament))) {
	    while (randomList.size() >= PairingHelper.findPhase(
		    tournament.getRounds().size(), tournament)
		    .getNumberOfOpponents()) {
		partResult = new Pairing();
		partResult.setFlag(Pairing.PairingFlag.IGNORE);

		for (int i = 0; i < PairingHelper.findPhase(
			tournament.getRounds().size(), tournament)
			.getNumberOfOpponents(); i++) {
		    randomNumber = randomGenerator.nextInt(randomList.size());

		    partResult.getScoreTable().add(
			    PairingHelper.generateEmptyScore(
				    randomList.get(randomNumber), tournament
					    .getRuleSet().getPossibleScores()
					    .get(0).getScores().size()));

		    partResult.getOpponents().add(randomList.get(randomNumber));
		    randomList.remove(randomNumber);
		}
		result.add(partResult);
	    }
	} else {

	    while (randomList.size() >= PairingHelper.findPhase(
		    tournament.getRounds().size(), tournament)
		    .getNumberOfOpponents()) {
		partResult = new Pairing();

		for (int i = 0; i < PairingHelper.findPhase(
			tournament.getRounds().size(), tournament)
			.getNumberOfOpponents(); i++) {
		    randomNumber = randomGenerator.nextInt(randomList.size());

		    partResult.getScoreTable().add(
			    PairingHelper.generateEmptyScore(
				    randomList.get(randomNumber), tournament
					    .getRuleSet().getPossibleScores()
					    .get(0).getScores().size()));

		    partResult.getOpponents().add(randomList.get(randomNumber));
		    randomList.remove(randomNumber);
		}
		// checking if there is an similar pairing in the tournament
		if (!PairingHelper.checkForSimiliarPairings(partResult,
			tournament)) {
		    // TODO finish checking for similar pairings in previous
		    // rounds in the same game phase
		    result.add(partResult);

		}
	    }
	}
	for (int i = 0; i < randomList.size(); i++) {
	    // TODO implement bye for players
	}
	return result;
    }

    @Override
    public String getName() {
	return "Jeder gegen Jeden";
    }

}
