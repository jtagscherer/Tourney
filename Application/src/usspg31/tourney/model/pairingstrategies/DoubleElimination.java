package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Random;

import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;

public class DoubleElimination implements PairingStrategy {

    @Override
    public ArrayList<Pairing> generatePairing(Tournament tournament) {
        ArrayList<Pairing> result = new ArrayList<>();
        ArrayList<Pairing> winnerBracket;
        ArrayList<Pairing> loserBracket;

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
                                            .get(0).getScores().size()));

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

            if (loserInterRound) {
                loserBracket = new ArrayList<>();
                for (Pairing winnerInTheLoserBracket : tournament.getRounds()
                        .get(tournament.getRounds().size() - 1).getPairings()) {
                    if (winnerInTheLoserBracket.getFlag() == Pairing.PairingFlag.LOSER_BRACKET) {}
                }
                for (Pairing loserPairing : tournament.getRounds()
                        .get(tournament.getRounds().size() - 2).getPairings()) {
                    if (loserPairing.getFlag() == Pairing.PairingFlag.WINNER_BRACKET) {

                    }
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
