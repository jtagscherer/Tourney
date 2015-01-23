package usspg31.tourney.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a general administrator
 */
public class Administrator extends Person implements Cloneable {

    private final StringProperty phoneNumber;

    /**
     * Create a new administrator
     */
    public Administrator() {
        super();
        this.phoneNumber = new SimpleStringProperty("");
    }

    /**
     * Get the phone number of this administrator
     * 
     * @return Phone number of this administrator
     */
    public String getPhoneNumber() {
        return this.phoneNumber.get();
    }

    /**
     * Set the phone number of this administrator
     * 
     * @param value
     *            New phone number
     */
    public void setPhoneNumber(String value) {
        this.phoneNumber.set(value);
    }

    /**
     * Get the phone number property of this administrator
     * 
     * @return Phone number property of this administrator
     */
    public StringProperty phoneNumberProperty() {
        return this.phoneNumber;
    }

    @Override
    public Object clone() {
        Administrator clone = new Administrator();

        clone.setFirstName(this.getFirstName());
        clone.setLastName(this.getLastName());
        clone.setMailAdress(this.getMailAddress());
        clone.setPhoneNumber(this.getPhoneNumber());

        return clone;
    }
}
