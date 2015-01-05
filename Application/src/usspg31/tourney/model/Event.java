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

}
