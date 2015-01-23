package usspg31.tourney.model.pairingstrategies;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Accumulates all available pairing strategies
 * 
 * @author Jan Tagscherer
 */
public class PairingStrategies {
    public static ObservableList<PairingStrategy> getPairingStrategyInstances() {
        ObservableList<PairingStrategy> pairingStrategies = FXCollections
                .observableArrayList();

        pairingStrategies.addAll(new DoubleElimination(),
                new ModifiedSwissSystem(), new SingleElimination(),
                new FreeForAll(), new SwissSystem());

        return pairingStrategies;
    }
}
