package usspg31.tourney.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a tournament that can be carried out in an event
 */
public class Tournament implements Cloneable {

    private final ObservableList<Player> registeredPlayers;
    private final ObservableList<Player> attendingPlayers;
    private final ObservableList<Player> remainingPlayers;
    private final ObservableList<Player> receivedByePlayers;
    private final ObservableList<TournamentRound> rounds;
    private final StringProperty name;
    private final ObservableList<PlayerScore> scoreTable;
    private final ObservableList<TournamentAdministrator> administrators;
    private final StringProperty id;
    private final ObjectProperty<TournamentModule> ruleSet;

    /**
     * new TournamentModule() Create a new tournament and initialize all its
     * properties
     */
    public Tournament() {
	this.registeredPlayers = FXCollections.observableArrayList();
	this.attendingPlayers = FXCollections.observableArrayList();
	this.remainingPlayers = FXCollections.observableArrayList();
	this.receivedByePlayers = FXCollections.observableArrayList();
	this.rounds = FXCollections.observableArrayList();
	this.name = new SimpleStringProperty("");
	this.scoreTable = FXCollections.observableArrayList();
	this.administrators = FXCollections.observableArrayList();
	this.id = new SimpleStringProperty("");
	this.ruleSet = new SimpleObjectProperty<TournamentModule>(
		new TournamentModule());
    }

    /**
     * Get a list of all registered players in this tournament
     * 
     * @return List of all registered players in this tournament
     */
    public ObservableList<Player> getRegisteredPlayers() {
	return this.registeredPlayers;
    }

    /**
     * Get a list of all attending players in this tournament
     * 
     * @return List of all attending players in this tournament
     */
    public ObservableList<Player> getAttendingPlayers() {
	return this.attendingPlayers;
    }

    /**
     * Get a list of all players that have not been knocked out of this
     * tournament yet
     * 
     * @return List of all remaining players in this tournament
     */
    public ObservableList<Player> getRemainingPlayers() {
	return this.remainingPlayers;
    }

    /**
     * Get a list of all tournament rounds in this tournament
     * 
     * @return List of all tournament rounds in this tournament
     */
    public ObservableList<TournamentRound> getRounds() {
	return this.rounds;
    }

    /**
     * Get the list of all player which received a bye in the tournament
     * 
     * @return List of all player who received a bye
     */
    public ObservableList<Player> getReceivedByePlayers() {
	return this.receivedByePlayers;
    }

    /**
     * Get the name of this tournament
     * 
     * @return Current name of this tournament
     */
    public String getName() {
	return this.name.get();
    }

    /**
     * Set the name of this tournament
     * 
     * @param value
     *            New name of this tournament
     */
    public void setName(String value) {
	this.name.set(value);
    }

    /**
     * Get the name property of this tournament
     * 
     * @return Name property of this tournament
     */
    public StringProperty nameProperty() {
	return this.name;
    }

    /**
     * Get all player scores in this tournament
     * 
     * @return A list of all player scores in this tournament
     */
    public ObservableList<PlayerScore> getScoreTable() {
	return this.scoreTable;
    }

    /**
     * Get all tournament administrators in this event
     * 
     * @return A list of all tournament administrators in this event
     */
    public ObservableList<TournamentAdministrator> getAdministrators() {
	return this.administrators;
    }

    /**
     * Get the ID of this event
     * 
     * @return Current ID of this event
     */
    public String getId() {
	return this.id.get();
    }

    /**
     * Set the ID of this event
     * 
     * @param id
     *            New ID of this event
     */
    public void setId(String id) {
	this.id.set(id);
    }

    /**
     * Get the tournament module that describes the rules of this tournament
     * 
     * @return The current rule set of this tournament
     */
    public TournamentModule getRuleSet() {
	return this.ruleSet.get();
    }

    /**
     * Set the tournament module that describes the rules of this tournament
     * 
     * @param value
     *            The new rule set of this tournament
     */
    public void setRuleSet(TournamentModule value) {
	this.ruleSet.set(value);
    }

    /**
     * Get the rule set property of this event
     * 
     * @return Rule set property of this event
     */
    public ObjectProperty<TournamentModule> ruleSetProperty() {
	return this.ruleSet;
    }

    @Override
    public Object clone() {
	Tournament clone = new Tournament();
	clone.setName(this.getName());
	clone.setId(this.getId());
	clone.setRuleSet(this.getRuleSet());

	clone.getRegisteredPlayers().addAll(this.getRegisteredPlayers());
	clone.getAttendingPlayers().addAll(this.getAttendingPlayers());
	clone.getRounds().addAll(this.getRounds());
	clone.getScoreTable().addAll(this.getScoreTable());
	clone.getAdministrators().addAll(this.getAdministrators());

	return clone;
    }
}
