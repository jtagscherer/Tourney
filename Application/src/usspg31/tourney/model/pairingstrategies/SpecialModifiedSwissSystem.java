package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Pairing.PairingFlag;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.PlayerScoreComperator;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.Tournament;

public class SpecialModifiedSwissSystem implements PairingStrategy {

    @Override
    public ArrayList<Pairing> generatePairing(Tournament tournament) {
        ArrayList<Pairing> result = new ArrayList<>();
        Pairing partResult;

        // TODO refactor name
        ArrayList<ArrayList<PlayerScore>> subList;

        if (tournament.getRounds().size() == 0) {
            // normal random generating process
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
            // splitting the score table for each bracket of points
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
            int count;
            // using the swiss system pairing strategy for each bracket

            for (ArrayList<PlayerScore> subScoreList : subList) {

                PlayerScoreComperator comperatorStartingNumber = new PlayerScoreComperator();
                Collections.sort(subScoreList, comperatorStartingNumber);
                while (subScoreList.size() >= PairingHelper.findPhase(
                        tournament.getRounds().size(), tournament)
                        .getNumberOfOpponents()) {
                    boolean doublePairing = false;
                    partResult = new Pairing();
                    partResult.setFlag(Pairing.PairingFlag.IGNORE);

                    for (int i = 0; i < PairingHelper.findPhase(
                            tournament.getRounds().size(), tournament)
                            .getNumberOfOpponents(); i++) {
                        count = 0;

                        if (i == 0) {
                            partResult.getOpponents().add(
                                    subScoreList.get(0).getPlayer());

                            partResult
                                    .getScoreTable()
                                    .add(PairingHelper
                                            .generateEmptyScore(
                                                    subScoreList.get(0)
                                                            .getPlayer(),
                                                    tournament
                                                            .getRuleSet()
                                                            .getPossibleScores()
                                                            .size()));
                            subScoreList.remove(0);

                        } else {

                            if (i % 2 == 0) {
                                partResult.getOpponents().add(
                                        subScoreList.get(
                                                subScoreList.size() - i)
                                                .getPlayer());
                                if (i == PairingHelper.findPhase(
                                        tournament.getRounds().size(),
                                        tournament).getNumberOfOpponents() - 1) {

                                    while (PairingHelper
                                            .isThereASimilarPairings(
                                                    partResult, tournament,
                                                    result)) {
                                        partResult.getOpponents().remove(
                                                subScoreList.get(subScoreList
                                                        .size() - 1 - count));
                                        if (count + 1 == subScoreList.size()) {
                                            count = 0;
                                            partResult.getOpponents().add(
                                                    subScoreList
                                                            .get(subScoreList
                                                                    .size()
                                                                    - 1
                                                                    - count)
                                                            .getPlayer());
                                            doublePairing = true;
                                            break;
                                        } else {
                                            count++;
                                        }
                                        partResult.getOpponents().add(
                                                subScoreList.get(
                                                        subScoreList.size() - 1
                                                                - count)
                                                        .getPlayer());
                                    }
                                    partResult
                                            .getScoreTable()
                                            .add(PairingHelper
                                                    .generateEmptyScore(
                                                            subScoreList
                                                                    .get(subScoreList
                                                                            .size() - 1)
                                                                    .getPlayer(),
                                                            tournament
                                                                    .getRuleSet()
                                                                    .getPossibleScores()
                                                                    .size()));
                                    subScoreList.remove(subScoreList.size() - 1
                                            - count);

                                }

                            } else {
                                partResult.getOpponents().add(
                                        subScoreList.get(0).getPlayer());
                                if (i == PairingHelper.findPhase(
                                        tournament.getRounds().size(),
                                        tournament).getNumberOfOpponents() - 1) {

                                    while (PairingHelper
                                            .isThereASimilarPairings(
                                                    partResult, tournament,
                                                    result)) {
                                        partResult.getOpponents().remove(
                                                subScoreList.get(count));
                                        if (count + 1 == subScoreList.size()) {
                                            count = 0;
                                            partResult.getOpponents().add(
                                                    subScoreList.get(count)
                                                            .getPlayer());
                                            doublePairing = true;
                                            break;
                                        } else {
                                            count++;
                                        }
                                        partResult.getOpponents().add(
                                                subScoreList.get(count)
                                                        .getPlayer());
                                    }
                                    partResult
                                            .getScoreTable()
                                            .add(PairingHelper
                                                    .generateEmptyScore(
                                                            subScoreList
                                                                    .get(count)
                                                                    .getPlayer(),
                                                            tournament
                                                                    .getRuleSet()
                                                                    .getPossibleScores()
                                                                    .size()));
                                    subScoreList.remove(count);

                                }
                            }
                        }

                    }
                    // if (doublePairing) {

                    // } else {
                    result.add(partResult);
                    // }
                }
                // put the remaining players in the next bracket or using the
                // bye
                if (subScoreList.size() > 0) {
                    if (subList.indexOf(subScoreList) == subList.size() - 1) {
                        for (int i = 0; i < subScoreList.size(); i++) {
                            partResult = new Pairing();
                            partResult.setFlag(PairingFlag.IGNORE);
                            partResult.getOpponents().add(
                                    subScoreList.get(i).getPlayer());
                            partResult
                                    .getScoreTable()
                                    .add(PairingHelper
                                            .generateEmptyScore(
                                                    subScoreList.get(i)
                                                            .getPlayer(),
                                                    tournament
                                                            .getRuleSet()
                                                            .getPossibleScores()
                                                            .size()));
                            for (PossibleScoring byeScore : tournament
                                    .getRuleSet().getPossibleScores()) {
                                partResult
                                        .getScoreTable()
                                        .get(0)
                                        .getScore()
                                        .add(byeScore.getPriority(),
                                                byeScore.getByeValue(
                                                        tournament,
                                                        subScoreList.get(i)
                                                                .getPlayer()));
                            }

                            result.add(partResult);
                        }
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
        return PreferencesManager.getInstance().localizeString(
                "pairingstrategy.specialswisssystem.name");
    }

}
