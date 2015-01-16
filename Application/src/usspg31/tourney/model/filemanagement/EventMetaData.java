package usspg31.tourney.model.filemanagement;

import java.time.LocalDate;
import java.util.ArrayList;

import usspg31.tourney.model.Event;
import usspg31.tourney.model.EventAdministrator;

/**
 * A data object that represents the meta data attached to an event. It is used
 * as a wrapper class while loading an event from a file
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

	/**
	 * Get the event phase of this meta data object
	 * 
	 * @return The current event phase
	 */
	public Event.EventPhase getEventPhase() {
		return this.eventPhase;
	}

	/**
	 * Set the event phase of this meta data object
	 * 
	 * @param eventPhase
	 *            New event phase
	 */
	public void setEventPhase(Event.EventPhase eventPhase) {
		this.eventPhase = eventPhase;
	}

	/**
	 * Get the event name
	 * 
	 * @return Current event name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the event name
	 * 
	 * @param name
	 *            New event name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the location of this event meta data
	 * 
	 * @return Current location
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * Set the location of this event meta data
	 * 
	 * @param location
	 *            New location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Get the start date of this event meta data
	 * 
	 * @return Current start date
	 */
	public LocalDate getStartDate() {
		return this.startDate;
	}

	/**
	 * Set the start date of this event meta data
	 * 
	 * @param startDate
	 *            New start date
	 */
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	/**
	 * Get the end date of this event meta data
	 * 
	 * @return Current end date
	 */
	public LocalDate getEndDate() {
		return this.endDate;
	}

	/**
	 * Set the end date of this event meta data
	 * 
	 * @param endDate
	 *            New end date
	 */
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	/**
	 * Get a list of administrators of this event meta data
	 * 
	 * @return List of current event administrators
	 */
	public ArrayList<EventAdministrator> getAdministrators() {
		return this.administrators;
	}

	/**
	 * Set the list of current administrators of this event meta data
	 * 
	 * @param administrators
	 *            New list of event administrators
	 */
	public void setAdministrators(ArrayList<EventAdministrator> administrators) {
		this.administrators = administrators;
	}
}
