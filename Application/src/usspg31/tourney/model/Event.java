package usspg31.tourney.model;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Event {

	public static enum EventPhase {
		EVENTSETUP, PREREGISTRATION, REGISTATION, TOURNAMENTEXECUTION
	}

	private final ObservableList<Tournament> tournaments;
	private final ObservableList<Player> registeredPlayers;
	private final StringProperty name;
	private final ObjectProperty<LocalDate> startDate;
	private final ObjectProperty<LocalDate> endDate;
	private final StringProperty location;
	private final ObservableList<EventAdministrator> administrators;
	private final ObjectProperty<EventPhase> eventPhase;

	public Event() {
		this.tournaments = FXCollections.observableArrayList();
		this.registeredPlayers = FXCollections.observableArrayList();
		this.name = new SimpleStringProperty();
		this.startDate = new SimpleObjectProperty<LocalDate>();
		this.endDate = new SimpleObjectProperty<LocalDate>();
		this.location = new SimpleStringProperty();
		this.administrators = FXCollections.observableArrayList();
		this.eventPhase = new SimpleObjectProperty<Event.EventPhase>();
	}

	public ObservableList<Tournament> getTournaments() {
		return this.tournaments;
	}

	public ObservableList<Player> getRegisteredPlayers() {
		return this.registeredPlayers;
	}

	public String getName() {
		return this.name.get();
	}

	public void setName(String value) {
		this.name.set(value);
	}

	public StringProperty nameProperty() {
		return this.name;
	}

	public LocalDate getStartDate() {
		return this.startDate.get();
	}

	public void setStartDate(LocalDate value) {
		this.startDate.set(value);
	}

	public ObjectProperty<LocalDate> startDateProperty() {
		return this.startDate;
	}

	public LocalDate getEndDate() {
		return this.endDate.get();
	}

	public void setEndDate(LocalDate value) {
		this.endDate.set(value);
	}

	public ObjectProperty<LocalDate> endDateProperty() {
		return this.endDate;
	}

	public String getLocation() {
		return this.location.get();
	}

	public void setLocation(String value) {
		this.location.set(value);
	}

	public StringProperty locationProperty() {
		return this.location;
	}

	public ObservableList<EventAdministrator> getAdministrators() {
		return this.administrators;
	}

	public EventPhase getEventPhase() {
		return this.eventPhase.get();
	}

	public void setEventPhase(EventPhase value) {
		this.eventPhase.set(value);
	}

	public ObjectProperty<EventPhase> eventPhaseProperty() {
		return this.eventPhase;
	}
}
