package usspg31.tourney.model.pairingstrategies;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

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

        } else {
            ArrayList<Pairing> tmp = new ArrayList<>();
            tmp.addAll(tournament.getRounds()
                    .get(tournament.getRounds().size() - 1).getPairings());
            while (tmp.size() > PairingHelper.findPhase(
                    tournament.getRounds().size(), tournament)
                    .getNumberOfOpponents() - 1) {
                partResult = new Pairing();
                partResult.setFlag(PairingFlag.IGNORE);
                for (int i = 0; i < PairingHelper.findPhase(
                        tournament.getRounds().size(), tournament)
                        .getNumberOfOpponents(); i++) {

                    partResult.getScoreTable().add(
                            PairingHelper.generateEmptyScore(
                                    PairingHelper.identifyWinner(tmp.get(0)),
                                    tournament.getRuleSet().getPossibleScores()
                                            .size()));

                    partResult.getOpponents().add(
                            PairingHelper.identifyWinner(tmp.get(0)));

                    tmp.remove(0);
                }

                result.add(partResult);

            }

        }
        return result;
    }

    @Override
    public String getName() {
        return "K.O.-System";
    }
}
