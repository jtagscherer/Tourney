package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Pairing.PairingFlag;
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

        // generate the score table for the remaining player in the tournament
        mergedScoreTable = PairingHelper.mergeScoreRemainingPlayer(tournament);
        Collections.sort(mergedScoreTable);

        if (tournament.getRounds().size() == 0) {
            // random pairings in the first round of the tournament
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
        } else {
            while (mergedScoreTable.size() >= PairingHelper.findPhase(
                    tournament.getRounds().size(), tournament)
                    .getNumberOfOpponents()) {
                partResult = new Pairing();
                partResult.setFlag(PairingFlag.IGNORE);
                for (int i = 0; i < PairingHelper.findPhase(
                        tournament.getRounds().size(), tournament)
                        .getNumberOfOpponents(); i++) {

                    partResult.getScoreTable().add(
                            PairingHelper.generateEmptyScore(mergedScoreTable
                                    .get(mergedScoreTable.size() - 1)
                                    .getPlayer(), tournament.getRuleSet()
                                    .getPossibleScores().size()));
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
                            } else {
                                mergedScoreTable
                                        .remove(mergedScoreTable.size() - 1);

                            }
                        }
                    }

                }
                result.add(partResult);

            }

        }
        return result;

    }

    @Override
    public String getName() {
        return "Schweizer System";
    }
}
