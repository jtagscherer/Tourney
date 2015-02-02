package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Random;

import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Pairing.PairingFlag;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PossibleScoring;
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

                    partResult.getOpponents().add(randomList.get(randomNumber));
                    if (i == PairingHelper.findPhase(
                            tournament.getRounds().size(), tournament)
                            .getNumberOfOpponents() - 1) {
                        int count = 0;
                        // tries a limited time to avoid the conflict of a
                        // similar pairing, after n runs the pairing will be
                        // submitted no matter if it�s a similar pairing

                        while (PairingHelper.isThereASimilarPairings(
                                partResult, tournament, result)) {
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
                        partResult.getScoreTable().add(
                                PairingHelper.generateEmptyScore(
                                        randomList.get(randomNumber),
                                        tournament.getRuleSet()
                                                .getPossibleScores().size()));
                        randomList.remove(randomNumber);

                    } else {
                        partResult.getScoreTable().add(
                                PairingHelper.generateEmptyScore(
                                        randomList.get(randomNumber),
                                        tournament.getRuleSet()
                                                .getPossibleScores().size()));
                        randomList.remove(randomNumber);
                    }
                }
                result.add(partResult);
            }
        }

        // creates a pairing with only one player who receives the predefined
        // bye score
        // also adds the player to the receivedByePlayer list
        for (int i = 0; i < randomList.size(); i++) {
            partResult = new Pairing();
            partResult.setFlag(PairingFlag.IGNORE);
            partResult.getOpponents().add(randomList.get(i));
            partResult
                    .getScoreTable()
                    .add(PairingHelper.generateEmptyScore(randomList.get(i),
                            tournament.getRuleSet().getPossibleScores().size()));
            for (PossibleScoring byeScore : tournament.getRuleSet()
                    .getPossibleScores()) {
                partResult.getScoreTable().get(0).getScore()
                        .add(byeScore.getPriority(), byeScore.getByeValue());
            }

            result.add(partResult);

            tournament.getReceivedByePlayers().add(randomList.get(i));
        }
        return result;
    }

    @Override
    public String getName() {
        return PreferencesManager.getInstance().localizeString(
                "pairingstrategy.freeforall.name");
    }

}
