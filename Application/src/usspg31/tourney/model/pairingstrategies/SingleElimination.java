package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.model.Bye;
import usspg31.tourney.model.Bye.ByeType;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Pairing.PairingFlag;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;

public class SingleElimination implements PairingStrategy {

    private static final Logger log = Logger.getLogger(SingleElimination.class
	    .getName());

    /*
     * (non-Javadoc)Level.FINER,
     * 
     * @see
     * usspg31.tourney.model.pairingstrategies.PairingStrategy#generatePairing
     * (usspg31.tourney.model.Tournament)
     */
    @Override
    public ArrayList<Pairing> generatePairing(Tournament tournament) {
	log.finer(this.getClass().toString());

	ArrayList<Pairing> result = new ArrayList<>();
	Pairing partResult = new Pairing();

	// checks if this round is the first round in the gamephase and modify
	// the pairing strategy for this round
	if (tournament.getRounds().size() == 0
		|| PairingHelper.isFirstInPhase(tournament.getRounds().size(),
			tournament, PairingHelper.findPhase(tournament
				.getRounds().size(), tournament))) {

	    Random randomGenerator = new Random();
	    ArrayList<Player> randomList = new ArrayList<>();
	    randomList.addAll(tournament.getRemainingPlayers());
	    int randomNumber;

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
					    .size()));

		    partResult.getOpponents().add(randomList.get(randomNumber));
		    randomList.remove(randomNumber);
		}
		result.add(partResult);
	    }

	    for (int i = 0; i < randomList.size(); i++) {
		partResult = new Pairing();
		partResult.setFlag(PairingFlag.IGNORE);
		partResult.getOpponents().add(randomList.get(i));
		partResult.getScoreTable().add(
			PairingHelper.generateEmptyScore(randomList.get(i),
				tournament.getRuleSet().getPossibleScores()
					.size()));
		for (Bye byeTest : tournament.getRuleSet().getByeList()) {
		    if (byeTest.getByeType() == ByeType.NORMAL_BYE) {
			partResult.getScoreTable().get(0).getScore()
				.addAll(byeTest.byePointsProperty());
			break;
		    }
		}
		result.add(partResult);
	    }

	} else {
	    ArrayList<Pairing> allPreviousPairings = new ArrayList<>();
	    allPreviousPairings.addAll(tournament.getRounds()
		    .get(tournament.getRounds().size() - 1).getPairings());
	    while (allPreviousPairings.size() > PairingHelper.findPhase(
		    tournament.getRounds().size(), tournament)
		    .getNumberOfOpponents() - 1) {
		partResult = new Pairing();
		partResult.setFlag(PairingFlag.IGNORE);
		for (int i = 0; i < PairingHelper.findPhase(
			tournament.getRounds().size(), tournament)
			.getNumberOfOpponents(); i++) {

		    for (Player winner : PairingHelper
			    .identifyWinner(allPreviousPairings.get(0))) {
			partResult.getScoreTable().add(
				PairingHelper.generateEmptyScore(winner,
					tournament.getRuleSet()
						.getPossibleScores().size()));
		    }

		    partResult.getOpponents().addAll(
			    PairingHelper.identifyWinner(allPreviousPairings
				    .get(0)));

		    allPreviousPairings.remove(0);
		}

		result.add(partResult);

	    }

	    for (int i = 0; i < allPreviousPairings.size(); i++) {
		partResult = new Pairing();
		partResult.setFlag(PairingFlag.IGNORE);
		partResult.getOpponents()
			.addAll(PairingHelper
				.identifyWinner(allPreviousPairings.get(i)));

		for (Player winner : PairingHelper
			.identifyWinner(allPreviousPairings.get(i))) {
		    partResult.getScoreTable().add(
			    PairingHelper.generateEmptyScore(winner, tournament
				    .getRuleSet().getPossibleScores().size()));
		}

		for (Bye byeTest : tournament.getRuleSet().getByeList()) {
		    if (byeTest.getByeType() == ByeType.NORMAL_BYE) {
			partResult.getScoreTable().get(0).getScore()
				.addAll(byeTest.byePointsProperty());
			break;
		    }
		}

		result.add(partResult);
	    }

	}

	return result;
    }

    @Override
    public String getName() {
	return PreferencesManager.getInstance().localizeString(
		"pairingstrategy.singleelimination.name");
    }
}
