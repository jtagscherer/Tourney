package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Random;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Pairing.PairingFlag;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;

public class DoubleElimination implements PairingStrategy {

    @Override
    public ArrayList<Pairing> generatePairing(Tournament tournament) {
        ArrayList<Pairing> result = new ArrayList<>();
        ArrayList<Player> winnerBracket;
        ArrayList<Player> loserBracket;

        Pairing partResult;
        // random generation for the first round in the
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

        } else {

            // TODO refactor variable name
            boolean loserInterRound = true;

            // TODO refactor loop name
            roundDetermination: for (Pairing testPairing : tournament
                    .getRounds().get(tournament.getRounds().size() - 1)
                    .getPairings()) {
                if (testPairing.getFlag() == Pairing.PairingFlag.WINNER_BRACKET) {
                    loserInterRound = false;
                    break roundDetermination;
                }

            }

            if (PairingHelper.findPhase(tournament.getRounds().size(),
                    tournament).getNumberOfOpponents() == 2) {

                if (loserInterRound) {
                    ArrayList<Pairing> tmp = new ArrayList<>();
                    loserBracket = new ArrayList<>();
                    winnerBracket = new ArrayList<>();
                    tmp.addAll(tournament.getRounds()
                            .get(tournament.getRounds().size() - 1)
                            .getPairings());
                    for (Pairing pairing : tmp) {
                        if (pairing.getFlag() == PairingFlag.LOSER_BRACKET) {
                            loserBracket.add(PairingHelper
                                    .identifyWinner(pairing));
                        } else {
                            winnerBracket.add(PairingHelper
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

                            partResult
                                    .getScoreTable()
                                    .add(PairingHelper.generateEmptyScore(
                                            PairingHelper.identifyWinner(tmp
                                                    .get(0)), tournament
                                                    .getRuleSet()
                                                    .getPossibleScores().size()));

                            partResult.getOpponents().add(winnerBracket.get(0));

                            winnerBracket.remove(0);
                        }

                        result.add(partResult);

                    }

                } else {

                }
            } else {

            }
        }

        return result;
    }

    @Override
    public String getName() {
        return "Doppel-K.O.-System (not implemented)";
    }

}
