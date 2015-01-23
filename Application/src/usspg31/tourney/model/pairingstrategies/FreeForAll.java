package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Random;

import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.model.Bye;
import usspg31.tourney.model.Bye.ByeType;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Pairing.PairingFlag;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;

public class FreeForAll implements PairingStrategy {

    /*
     * (non-Javadoc)
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

        if (tournament.getRounds().size() == 0
                || PairingHelper.isFirstInPhase(tournament.getRounds().size(),
                        tournament, PairingHelper.findPhase(tournament
                                .getRounds().size(), tournament))) {

            while (randomList.size() >= PairingHelper.findPhase(
                    tournament.getRounds().size(), tournament)
                    .getNumberOfOpponents()) {
                partResult = new Pairing();
                partResult.setFlag(PairingFlag.IGNORE);
                for (int i = 0; i < PairingHelper.findPhase(
                        tournament.getRounds().size(), tournament)
                        .getNumberOfOpponents(); i++) {
                    randomNumber = randomGenerator.nextInt(randomList.size());
                    // adds an empty score table to the Pairing
                    partResult.getScoreTable().add(
                            PairingHelper.generateEmptyScore(
                                    randomList.get(randomNumber), tournament
                                            .getRuleSet().getPossibleScores()
                                            .size()));

                    // adds an opponent to the pairing
                    partResult.getOpponents().add(randomList.get(randomNumber));
                    randomList.remove(randomNumber);
                }
                result.add(partResult);
            }
            // checks if the round is the first in his game phase
        } else {

            while (randomList.size() >= PairingHelper.findPhase(
                    tournament.getRounds().size(), tournament)
                    .getNumberOfOpponents()) {
                partResult = new Pairing();
                partResult.setFlag(PairingFlag.IGNORE);
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
                    if (i == PairingHelper.findPhase(
                            tournament.getRounds().size(), tournament)
                            .getNumberOfOpponents() - 1) {
                        int count = 0;
                        while (PairingHelper.isThereASimilarPairings(
                                partResult, tournament)) {
                            partResult.getOpponents().remove(
                                    randomList.get(randomNumber));
                            if (count + 1 == randomList.size()) {
                                partResult.getOpponents().add(
                                        randomList.get(randomNumber));
                                break;
                            } else {
                                count++;

                            }
                            randomNumber = randomGenerator.nextInt(randomList
                                    .size());
                            partResult.getOpponents().add(
                                    randomList.get(randomNumber));
                        }
                        randomList.remove(randomNumber);

                    } else {

                        randomList.remove(randomNumber);
                    }
                }
                result.add(partResult);
            }
        }
        for (int i = 0; i < randomList.size(); i++) {
            partResult = new Pairing();
            partResult.setFlag(PairingFlag.IGNORE);
            partResult.getOpponents().add(randomList.get(i));
            partResult
                    .getScoreTable()
                    .add(PairingHelper.generateEmptyScore(randomList.get(i),
                            tournament.getRuleSet().getPossibleScores().size()));
            for (Bye byeTest : tournament.getRuleSet().getByeList()) {
                if (byeTest.getByeType() == ByeType.NORMAL_BYE) {
                    partResult.getScoreTable().get(0).getScore()
                            .addAll(byeTest.byePointsProperty());
                    break;
                }
            }

            result.add(partResult);
        }
        return result;
    }

    @Override
    public String getName() {
        return PreferencesManager.getInstance().localizeString(
                "pairingstrategy.freeforall.name");
    }

}
