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

public class DoubleElimination implements PairingStrategy {
    private enum ActualRoundState {
        SECOND_ROUND,
        INTER_ROUND,
        COMPLETE_ROUND;
    }

    private static Logger log = Logger.getLogger(DoubleElimination.class
            .getName());

    @Override
    public ArrayList<Pairing> generatePairing(Tournament tournament) {

        ArrayList<Pairing> result = new ArrayList<>();
        ArrayList<Player> winnerBracket;
        ArrayList<Player> loserBracket;

        Pairing partResult;
        // random generation for the first round in the tournament
        if (tournament.getRounds().size() == 0
                || PairingHelper.isFirstInPhase(tournament.getRounds().size(),
                        tournament, PairingHelper.findPhase(tournament
                                .getRounds().size(), tournament))) {

            Random randomGenerator = new Random();
            ArrayList<Player> randomList = new ArrayList<>();
            randomList.addAll(tournament.getRemainingPlayers());
            int randomNumber;
            // generate the first pairing random
            while (randomList.size() >= PairingHelper.findPhase(
                    tournament.getRounds().size(), tournament)
                    .getNumberOfOpponents()) {
                partResult = new Pairing();
                partResult.setFlag(Pairing.PairingFlag.WINNER_BRACKET);

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
            // every player left gets his own pairing and receives a bye and an
            // entry in the receiveByePlayer list
            for (int i = 0; i < randomList.size(); i++) {
                partResult = new Pairing();
                partResult.setFlag(PairingFlag.WINNER_BRACKET);
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
                tournament.getReceivedByePlayers().add(randomList.get(i));
            }

        } else {

            ActualRoundState roundState; // differentiate between the
            // different possible outcome for a
            // double elimination round

            boolean firstRound = true;
            boolean interRound = true;
            for (Pairing previousRoundPairing : tournament.getRounds()
                    .get(tournament.getRounds().size() - 1).getPairings()) {
                if (previousRoundPairing.getFlag() == Pairing.PairingFlag.WINNER_BRACKET) {
                    interRound = false;
                } else if (previousRoundPairing.getFlag() == PairingFlag.LOSER_BRACKET) {
                    firstRound = false;
                }

            }

            if (firstRound) {
                roundState = ActualRoundState.SECOND_ROUND;
            } else if (interRound) {
                roundState = ActualRoundState.COMPLETE_ROUND;
            } else {
                roundState = ActualRoundState.INTER_ROUND;
            }

            // if (PairingHelper.findPhase(tournament.getRounds().size(),
            // tournament).getNumberOfOpponents() == 2) {
            loserBracket = new ArrayList<>();
            winnerBracket = new ArrayList<>();
            ArrayList<Player> winnerLoserBracket = new ArrayList<>();
            // deciding between the possible outcome for the generated
            // tournament rounds
            switch (roundState) {

            case COMPLETE_ROUND:

                ArrayList<Pairing> tmp = new ArrayList<>();

                tmp.addAll(tournament.getRounds()
                        .get(tournament.getRounds().size() - 1).getPairings());
                for (Pairing pairing : tmp) {
                    if (pairing.getFlag() == PairingFlag.LOSER_BRACKET) {
                        loserBracket.addAll(PairingHelper
                                .identifyWinner(pairing));
                    }
                }

                tmp = new ArrayList<>();

                tmp.addAll(tournament.getRounds()
                        .get(tournament.getRounds().size() - 2).getPairings());
                for (Pairing pairing : tmp) {
                    if (pairing.getFlag() == PairingFlag.WINNER_BRACKET) {
                        winnerBracket.addAll(PairingHelper
                                .identifyWinner(pairing));
                    }
                }
                if (winnerBracket.size() + loserBracket.size() == PairingHelper
                        .findPhase(tournament.getRounds().size(), tournament)
                        .getNumberOfOpponents()) {
                    partResult = new Pairing();
                    partResult.setFlag(PairingFlag.WINNER_BRACKET);
                    partResult.getOpponents().addAll(winnerBracket);
                    partResult.getOpponents().addAll(loserBracket);
                    for (Player participantFinalRound : winnerBracket) {
                        partResult.getScoreTable().add(
                                PairingHelper.generateEmptyScore(
                                        participantFinalRound, tournament
                                                .getRuleSet()
                                                .getPossibleScores().size()));
                    }
                    for (Player participantFinalRound : loserBracket) {
                        partResult.getScoreTable().add(
                                PairingHelper.generateEmptyScore(
                                        participantFinalRound, tournament
                                                .getRuleSet()
                                                .getPossibleScores().size()));
                    }
                    winnerBracket.clear();
                    loserBracket.clear();
                    result.add(partResult);
                } else {
                    while (winnerBracket.size() > PairingHelper.findPhase(
                            tournament.getRounds().size(), tournament)
                            .getNumberOfOpponents() - 1) {
                        partResult = new Pairing();
                        partResult.setFlag(PairingFlag.WINNER_BRACKET);
                        for (int i = 0; i < PairingHelper.findPhase(
                                tournament.getRounds().size(), tournament)
                                .getNumberOfOpponents(); i++) {

                            partResult
                                    .getScoreTable()
                                    .add(PairingHelper.generateEmptyScore(
                                            winnerBracket.get(0), tournament
                                                    .getRuleSet()
                                                    .getPossibleScores().size()));

                            partResult.getOpponents().add(winnerBracket.get(0));

                            winnerBracket.remove(0);
                        }

                        result.add(partResult);

                    }

                    while (loserBracket.size() > PairingHelper.findPhase(
                            tournament.getRounds().size(), tournament)
                            .getNumberOfOpponents() - 1) {
                        partResult = new Pairing();
                        partResult.setFlag(PairingFlag.LOSER_BRACKET);
                        for (int i = 0; i < PairingHelper.findPhase(
                                tournament.getRounds().size(), tournament)
                                .getNumberOfOpponents(); i++) {

                            partResult
                                    .getScoreTable()
                                    .add(PairingHelper.generateEmptyScore(
                                            loserBracket.get(0), tournament
                                                    .getRuleSet()
                                                    .getPossibleScores().size()));

                            partResult.getOpponents().add(loserBracket.get(0));

                            loserBracket.remove(0);
                        }

                        result.add(partResult);

                    }
                }
                break;
            case INTER_ROUND:

                tmp = new ArrayList<>();
                loserBracket = new ArrayList<>();

                tmp.addAll(tournament.getRounds()
                        .get(tournament.getRounds().size() - 1).getPairings());
                for (Pairing pairing : tmp) {
                    if (pairing.getFlag() == PairingFlag.LOSER_BRACKET) {
                        loserBracket.addAll(PairingHelper
                                .identifyWinner(pairing));
                    } else if (pairing.getFlag() == PairingFlag.WINNER_BRACKET) {
                        winnerLoserBracket.addAll(PairingHelper
                                .identifyLoser(pairing));
                    }
                }

                log.finer("Loser list size before generating the pairings is: "
                        + loserBracket.size());
                log.finer("WinnerLoser list size before generating the pairings is: "
                        + winnerLoserBracket.size());
                while (winnerLoserBracket.size() + loserBracket.size() > PairingHelper
                        .findPhase(tournament.getRounds().size(), tournament)
                        .getNumberOfOpponents() - 1) {
                    partResult = new Pairing();
                    partResult.setFlag(PairingFlag.LOSER_BRACKET);

                    log.finer("Next pairing gets generated");
                    for (int i = 0; i < PairingHelper.findPhase(
                            tournament.getRounds().size(), tournament)
                            .getNumberOfOpponents(); i++) {
                        if (i == 0) {
                            partResult
                                    .getScoreTable()
                                    .add(PairingHelper.generateEmptyScore(
                                            winnerLoserBracket.get(0),
                                            tournament.getRuleSet()
                                                    .getPossibleScores().size()));

                            partResult.getOpponents().add(
                                    winnerLoserBracket.get(0));
                            winnerLoserBracket.remove(0);

                        } else {
                            partResult
                                    .getScoreTable()
                                    .add(PairingHelper.generateEmptyScore(
                                            loserBracket.get(0), tournament
                                                    .getRuleSet()
                                                    .getPossibleScores().size()));

                            partResult.getOpponents().add(loserBracket.get(0));
                            loserBracket.remove(0);
                        }
                    }

                    result.add(partResult);

                }
                break;
            case SECOND_ROUND:
                tmp = new ArrayList<>();
                loserBracket = new ArrayList<>();
                winnerBracket = new ArrayList<>();
                tmp.addAll(tournament.getRounds()
                        .get(tournament.getRounds().size() - 1).getPairings());
                for (Pairing pairing : tmp) {
                    if (pairing.getFlag() == PairingFlag.LOSER_BRACKET) {

                    } else if (pairing.getFlag() == PairingFlag.WINNER_BRACKET) {
                        winnerBracket.addAll(PairingHelper
                                .identifyWinner(pairing));
                        loserBracket.addAll(PairingHelper
                                .identifyLoser(pairing));
                    }
                }
                while (winnerBracket.size() > PairingHelper.findPhase(
                        tournament.getRounds().size(), tournament)
                        .getNumberOfOpponents() - 1) {
                    partResult = new Pairing();
                    partResult.setFlag(PairingFlag.WINNER_BRACKET);
                    for (int i = 0; i < PairingHelper.findPhase(
                            tournament.getRounds().size(), tournament)
                            .getNumberOfOpponents(); i++) {

                        partResult.getScoreTable().add(
                                PairingHelper.generateEmptyScore(
                                        winnerBracket.get(0), tournament
                                                .getRuleSet()
                                                .getPossibleScores().size()));

                        partResult.getOpponents().add(winnerBracket.get(0));

                        winnerBracket.remove(0);
                    }

                    result.add(partResult);

                }

                while (loserBracket.size() > PairingHelper.findPhase(
                        tournament.getRounds().size(), tournament)
                        .getNumberOfOpponents() - 1) {
                    partResult = new Pairing();
                    partResult.setFlag(PairingFlag.LOSER_BRACKET);
                    for (int i = 0; i < PairingHelper.findPhase(
                            tournament.getRounds().size(), tournament)
                            .getNumberOfOpponents(); i++) {

                        partResult.getScoreTable().add(
                                PairingHelper.generateEmptyScore(
                                        loserBracket.get(0), tournament
                                                .getRuleSet()
                                                .getPossibleScores().size()));

                        partResult.getOpponents().add(loserBracket.get(0));

                        loserBracket.remove(0);
                    }

                    result.add(partResult);

                }

                break;
            }

            log.finer("Winner bracket list size is : " + winnerBracket.size());
            log.finer("Loser bracket list size is : " + loserBracket.size());
            log.finer("WinnerLoser list size is : " + winnerLoserBracket.size());
            for (int i = 0; i < winnerBracket.size(); i++) {
                partResult = new Pairing();
                partResult.setFlag(PairingFlag.WINNER_BRACKET);
                partResult.getOpponents().add(winnerBracket.get(i));
                partResult.getScoreTable().add(
                        PairingHelper.generateEmptyScore(winnerBracket.get(i),
                                tournament.getRuleSet().getPossibleScores()
                                        .size()));
                for (PossibleScoring byeScore : tournament.getRuleSet()
                        .getPossibleScores()) {
                    partResult
                            .getScoreTable()
                            .get(0)
                            .getScore()
                            .add(byeScore.getPriority(), byeScore.getByeValue());
                }

                result.add(partResult);
            }
            for (int i = 0; i < loserBracket.size(); i++) {
                partResult = new Pairing();
                partResult.setFlag(PairingFlag.LOSER_BRACKET);
                partResult.getOpponents().add(loserBracket.get(i));
                partResult.getScoreTable().add(
                        PairingHelper.generateEmptyScore(loserBracket.get(i),
                                tournament.getRuleSet().getPossibleScores()
                                        .size()));
                for (PossibleScoring byeScore : tournament.getRuleSet()
                        .getPossibleScores()) {
                    partResult
                            .getScoreTable()
                            .get(0)
                            .getScore()
                            .add(byeScore.getPriority(), byeScore.getByeValue());
                }

                result.add(partResult);
            }

            for (int i = 0; i < winnerLoserBracket.size(); i++) {
                partResult = new Pairing();
                partResult.setFlag(PairingFlag.LOSER_BRACKET);
                partResult.getOpponents().add(winnerLoserBracket.get(i));
                partResult.getScoreTable().add(
                        PairingHelper.generateEmptyScore(
                                winnerLoserBracket.get(i), tournament
                                        .getRuleSet().getPossibleScores()
                                        .size()));
                for (PossibleScoring byeScore : tournament.getRuleSet()
                        .getPossibleScores()) {
                    partResult
                            .getScoreTable()
                            .get(0)
                            .getScore()
                            .add(byeScore.getPriority(), byeScore.getByeValue());
                }

                result.add(partResult);
            }
            // } else {

            // }
        }

        return result;
    }

    @Override
    public String getName() {
        return PreferencesManager.getInstance().localizeString(
                "pairingstrategy.doubleelimination.name");
    }

}
