package usspg31.tourney.model.filemanagement;

import java.time.LocalDate;
import java.util.ArrayList;

import usspg31.tourney.model.Event;
import usspg31.tourney.model.EventAdministrator;

/**
 * A data object that represents the meta data attached to an event
 * 
 * @author Jan Tagscherer
 */
public class EventMetaData {
	private String name;
	private String location;
	private LocalDate startDate;
	private LocalDate endDate;
	private Event.EventPhase eventPhase;
	private ArrayList<EventAdministrator> administrators;

	/**
	 * Create a new instance of the meta data class
	 */
	public EventMetaData() {
		this.administrators = new ArrayList<EventAdministrator>();
	}

	public Event.EventPhase getEventPhase() {
		return this.eventPhase;
	}

	public void setEventPhase(Event.EventPhase eventPhase) {
		this.eventPhase = eventPhase;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDate getStartDate() {
		return this.startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return this.endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public ArrayList<EventAdministrator> getAdministrators() {
		return this.administrators;
	}

	public void setAdministrators(ArrayList<EventAdministrator> administrators) {
		this.administrators = administrators;
	}
}
