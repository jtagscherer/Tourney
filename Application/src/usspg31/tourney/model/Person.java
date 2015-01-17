package usspg31.tourney.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A basic class for all persons present in an event and its tournaments
 */
public class Person {

    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty mailAddress;

    /**
     * Create a new person and initialize all its properties
     */
    public Person() {
	this.firstName = new SimpleStringProperty("");
	this.lastName = new SimpleStringProperty("");
	this.mailAddress = new SimpleStringProperty("");

    }

    /**
     * Get the first name of this person
     * 
     * @return Current first name of this person
     */
    public String getFirstName() {
	return this.firstName.get();
    }

    /**
     * Set the first name of this person
     * 
     * @param value
     *            New first name of this person
     */
    public void setFirstName(String value) {
	this.firstName.set(value);
    }

    /**
     * Get the first name property of this person
     * 
     * @return First name property of this person
     */
    public StringProperty firstNameProperty() {
	return this.firstName;
    }

    /**
     * Get the last name of this person
     * 
     * @return Current last name of this person
     */
    public String getLastName() {
	return this.lastName.get();
    }

    /**
     * Set the last name of this person
     * 
     * @param value
     *            New last name of this person
     */
    public void setLastName(String value) {
	this.lastName.set(value);
    }

    /**
     * Get the last name property of this person
     * 
     * @return Last person property of this person
     */
    public StringProperty lastNameProperty() {
	return this.lastName;
    }

    /**
     * Get the mail address of this person
     * 
     * @return Current mail address of this person
     */
    public String getMailAddress() {
	return this.mailAddress.get();
    }

    /**
     * Set the mail address of this person
     * 
     * @param value
     *            New mail address of this person
     */
    public void setMailAdress(String value) {
	this.mailAddress.set(value);
    }

    /**
     * Get the mail address property of this person
     * 
     * @return Mail address property of this person
     */
    public StringProperty mailAdressProperty() {
	return this.mailAddress;
    }
}
