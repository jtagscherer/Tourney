package usspg31.tourney.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {

	private final StringProperty firstName;
	private final StringProperty lastName;
	private final StringProperty mailAddress;

	public Person() {
		this.firstName = new SimpleStringProperty("");
		this.lastName = new SimpleStringProperty("");
		this.mailAddress = new SimpleStringProperty("");

	}

	public String getFirstName() {
		return this.firstName.get();
	}

	public void setFirstName(String value) {
		this.firstName.set(value);
	}

	public StringProperty firstNameProperty() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName.get();
	}

	public void setLastName(String value) {
		this.lastName.set(value);
	}

	public StringProperty lastNameProperty() {
		return this.lastName;
	}

	public String getMailAddress() {
		return this.mailAddress.get();
	}

	public void setMailAdress(String value) {
		this.mailAddress.set(value);
	}

	public StringProperty mailAdressProperty() {
		return this.mailAddress;
	}
}
