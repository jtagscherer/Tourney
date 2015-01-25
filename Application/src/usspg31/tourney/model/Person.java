package usspg31.tourney.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A basic class for all persons present in an event and its tournaments
 */
public class Person {

    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty mailAddress;

    private static Pattern EMAIL_PATTERN;
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Create a new person and initialize all its properties
     */
    public Person() {
        this.firstName = new SimpleStringProperty("");
        this.lastName = new SimpleStringProperty("");
        this.mailAddress = new SimpleStringProperty("");

        if (Person.EMAIL_PATTERN == null) {
            Person.EMAIL_PATTERN = Pattern.compile(Person.EMAIL_REGEX);
        }
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

    /**
     * Check if the mail address of this player is valid using a simple regex
     * 
     * @return True if the mail address is valid, false otherwise
     */
    public boolean hasValidMailAddress() {
        Matcher mailRegexMatcher = Person.EMAIL_PATTERN.matcher(this
                .getMailAddress());
        return mailRegexMatcher.matches();
    }
}
