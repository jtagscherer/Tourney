package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Pairing.PairingFlag;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.Tournament;

public class SingleElimination implements PairingStrategy {

    private static final Logger log = Logger.getLogger(SingleElimination.class
            .getName());

    /*
     * (non-Javadoc)Level.FINER,
     * @see
     * usspg31.tourney.model.pairingstrategies.PairingStrategy#generatePairing
     * (usspg31.tourney.model.Tournament)
     */
    @Override
    public ArrayList<Pairing> generatePairing(Tournament tournament) {

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
                for (PossibleScoring byeScore : tournament.getRuleSet()
                        .getPossibleScores()) {
                    partResult
                            .getScoreTable()
                            .get(0)
                            .getScore()
                            .set(byeScore.getPriority(), byeScore.getByeValue());
                }
                result.add(partResult);
            }

        } else {
            ArrayList<Player> allPlayers = new ArrayList<>();

            for (Pairing pairing : tournament.getRounds()
                    .get(tournament.getRounds().size() - 1).getPairings()) {
                allPlayers.addAll(PairingHelper.identifyWinner(pairing));
            }
            while (allPlayers.size() > PairingHelper.findPhase(
                    tournament.getRounds().size(), tournament)
                    .getNumberOfOpponents() - 1) {
                partResult = new Pairing();
                partResult.setFlag(PairingFlag.IGNORE);
                for (int i = 0; i < PairingHelper.findPhase(
                        tournament.getRounds().size(), tournament)
                        .getNumberOfOpponents(); i++) {

                    partResult.getScoreTable().add(
                            PairingHelper.generateEmptyScore(allPlayers.get(0),
                                    tournament.getRuleSet().getPossibleScores()
                                            .size()));

                    partResult.getOpponents().addAll(allPlayers.get(0));

                    allPlayers.remove(0);
                }

                result.add(partResult);

            }
            log.finer("Remaining player in the list which will receive a bye "
                    + allPlayers.size());
            for (int i = 0; i < allPlayers.size(); i++) {
                partResult = new Pairing();
                partResult.setFlag(PairingFlag.IGNORE);
                partResult.getOpponents().addAll(allPlayers.get(i));

                partResult.getScoreTable().add(
                        PairingHelper.generateEmptyScore(allPlayers.get(i),
                                tournament.getRuleSet().getPossibleScores()
                                        .size()));

                for (PossibleScoring byeScore : tournament.getRuleSet()
                        .getPossibleScores()) {
                    partResult
                            .getScoreTable()
                            .get(0)
                            .getScore()
                            .set(byeScore.getPriority(), byeScore.getByeValue());
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
