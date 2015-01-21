package usspg31.tourney.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents the rules of a tournament
 */
public class TournamentModule {

    private final StringProperty name;
    private final StringProperty description;
    private final ObservableList<PossibleScoring> possibleScorings;
    private final ObservableList<GamePhase> phaseList;
    private final ObservableList<Bye> byeList;

    /**
     * Create a new tournament module and initialize all its properties
     */
    public TournamentModule() {
        this.name = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.possibleScorings = FXCollections.observableArrayList();
        this.phaseList = FXCollections.observableArrayList();
        this.byeList = FXCollections.observableArrayList();
    }

    /**
     * Get the name of this tournament module
     * 
     * @return Current name of this tournament module
     */
    public String getName() {
        return this.name.get();
    }

    /**
     * Set the name of this tournament module
     * 
     * @param value
     *            New name of this tournament module
     */
    public void setName(String value) {
        this.name.set(value);
    }

    /**
     * Get the name property of this tournament module
     * 
     * @return Name property of this tournament module
     */
    public StringProperty nameProperty() {
        return this.name;
    }

    /**
     * Get the description of this tournament module
     * 
     * @return Current description of this tournament module
     */
    public String getDescription() {
        return this.description.get();
    }

    /**
     * Set the description of this tournament module
     * 
     * @param value
     *            New description of this tournament module
     */
    public void setDescription(String value) {
        this.description.set(value);
    }

    /**
     * Get the description property of this tournament module
     * 
     * @return Description property of this tournament module
     */
    public StringProperty descriptionProperty() {
        return this.description;
    }

    /**
     * Get all possible scores in this tournament module
     * 
     * @return A list of possible scorings in this tournament module
     */
    public ObservableList<PossibleScoring> getPossibleScores() {
        return this.possibleScorings;
    }

    /**
     * Get all game phases in this tournament module
     * 
     * @return A list of all game phases in this tournament module
     */
    public ObservableList<GamePhase> getPhaseList() {
        return this.phaseList;
    }

    /**
     * Get the bye list property of this tournament module
     * 
     * @return Current bye list property
     */
    public ObservableList<Bye> byeListProperty() {
        return this.byeList;
    }

    /**
     * Get a list of all possible byes in this tournament module
     * 
     * @return List of all possible byes
     */
    public ObservableList<Bye> getByeList() {
        return this.byeList;
    }
}
