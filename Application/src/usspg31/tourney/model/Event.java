package usspg31.tourney.model;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents an event that bundles tournaments and players
 */
public class Event {

    /**
     * The phases an event can be in
     */
    public static enum EventPhase {
	EVENT_SETUP, PRE_REGISTRATION, REGISTRATION, TOURNAMENT_EXECUTION
    }

    /**
     * The flag with which this event was saved and how it is supposed to be
     * opened again
     */
    public static enum UserFlag {
	ADMINISTRATION, REGISTRATION, TOURNAMENT_EXECUTION
    }

    private final ObservableList<Tournament> tournaments;
    private final ObservableList<Player> registeredPlayers;
    private final StringProperty name;
    private final ObjectProperty<LocalDate> startDate;
    private final ObjectProperty<LocalDate> endDate;
    private final StringProperty location;
    private final ObservableList<EventAdministrator> administrators;
    private final ObjectProperty<EventPhase> eventPhase;

    private final ObjectProperty<Tournament> executedTournament;
    private int numberOfRegistrators;
    private final ObjectProperty<UserFlag> userFlag;

    /**
     * Create a new event and initialize all properties
     */
    public Event() {
	this.tournaments = FXCollections.observableArrayList();
	this.registeredPlayers = FXCollections.observableArrayList();
	this.name = new SimpleStringProperty("");
	this.startDate = new SimpleObjectProperty<LocalDate>();
	this.endDate = new SimpleObjectProperty<LocalDate>();
	this.location = new SimpleStringProperty("");
	this.administrators = FXCollections.observableArrayList();
	this.eventPhase = new SimpleObjectProperty<Event.EventPhase>();
	this.setEventPhase(Event.EventPhase.EVENT_SETUP);
	this.executedTournament = new SimpleObjectProperty<Tournament>();
	this.userFlag = new SimpleObjectProperty<Event.UserFlag>();
	this.setUserFlag(UserFlag.ADMINISTRATION);
    }

    /**
     * Get a list of all tournaments in this event
     * 
     * @return List of all tournaments
     */
    public ObservableList<Tournament> getTournaments() {
	return this.tournaments;
    }

    /**
     * Get a list of all registered players in this event
     * 
     * @return List of all registered players
     */
    public ObservableList<Player> getRegisteredPlayers() {
	return this.registeredPlayers;
    }

    /**
     * Get the name of this event
     * 
     * @return Current name of this event
     */
    public String getName() {
	return this.name.get();
    }

    /**
     * Set the name of this event
     * 
     * @param value
     *            New name of this event
     */
    public void setName(String value) {
	this.name.set(value);
    }

    /**
     * Get the name property of this event
     * 
     * @return Name property of this event
     */
    public StringProperty nameProperty() {
	return this.name;
    }

    /**
     * Get the start date of this event
     * 
     * @return Current start date of this event
     */
    public LocalDate getStartDate() {
	return this.startDate.get();
    }

    /**
     * Set the start date of this event
     * 
     * @param value
     *            New start date of this event
     */
    public void setStartDate(LocalDate value) {
	this.startDate.set(value);
    }

    /**
     * Get the start date property of this event
     * 
     * @return Start date property of this event
     */
    public ObjectProperty<LocalDate> startDateProperty() {
	return this.startDate;
    }

    /**
     * Get the end date of this event
     * 
     * @return Current end date of this event
     */
    public LocalDate getEndDate() {
	return this.endDate.get();
    }

    /**
     * Set the end date of this event
     * 
     * @param value
     *            New end date of this event
     */
    public void setEndDate(LocalDate value) {
	this.endDate.set(value);
    }

    /**
     * Get the end date property of this event
     * 
     * @return End date property of this event
     */
    public ObjectProperty<LocalDate> endDateProperty() {
	return this.endDate;
    }

    /**
     * Get the location of this event
     * 
     * @return Current location of this event
     */
    public String getLocation() {
	return this.location.get();
    }

    /**
     * Set the location of this event
     * 
     * @param value
     *            New location of this event
     */
    public void setLocation(String value) {
	this.location.set(value);
    }

    /**
     * Get the location property of this event
     * 
     * @return Location property of this event
     */
    public StringProperty locationProperty() {
	return this.location;
    }

    /**
     * Get a list of event administrators in this event
     * 
     * @return List of current event administrators in this event
     */
    public ObservableList<EventAdministrator> getAdministrators() {
	return this.administrators;
    }

    /**
     * Get the phase this event is currently in
     * 
     * @return Current phase of this event
     */
    public EventPhase getEventPhase() {
	return this.eventPhase.get();
    }

    /**
     * Set the event phase of this event
     * 
     * @param value
     *            New event phase of this event
     */
    public void setEventPhase(EventPhase value) {
	this.eventPhase.set(value);
    }

    /**
     * Get the event phase property of this event
     * 
     * @return Event phase property of this event
     */
    public ObjectProperty<EventPhase> eventPhaseProperty() {
	return this.eventPhase;
    }

    /**
     * Set the user flag of this event
     * 
     * @param value
     *            New user flag of this event
     */
    public void setUserFlag(UserFlag value) {
	this.userFlag.set(value);
    }

    /**
     * Get the user flag of this event
     * 
     * @return Current user flag of this event
     */
    public UserFlag getUserFlag() {
	return this.userFlag.get();
    }

    /**
     * Get the user flag property of this event
     * 
     * @return User flag property of this event
     */
    public ObjectProperty<UserFlag> userFlagProperty() {
	return this.userFlag;
    }

    /**
     * Set the tournament that is currently being executed
     * 
     * @param value
     *            Tournament that is currently being executed
     */
    public void setExecutedTournament(Tournament value) {
	this.executedTournament.set(value);
    }

    /**
     * Get the tournament that is currently being executed
     * 
     * @return Tournament that is currently being executed
     */
    public Tournament getExecutedTournament() {
	return this.executedTournament.get();
    }

    /**
     * Get the executed tournament property of this event
     * 
     * @return Executed tournament property of this event
     */
    public ObjectProperty<Tournament> executedTournamentProperty() {
	return this.executedTournament;
    }

    /**
     * Get the number of registration desktops using this event
     * 
     * @return Number of registration desktops
     */
    public int getNumberOfRegistrators() {
	return this.numberOfRegistrators;
    }

    /**
     * Set the number of registration desktops using this event
     * 
     * @param numberOfRegistrators
     *            New number of registration desktops
     */
    public void setNumberOfRegistrators(int numberOfRegistrators) {
	this.numberOfRegistrators = numberOfRegistrators;
    }
}
