package usspg31.tourney.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Administrator extends Person {

	private final StringProperty phoneNumber;

	public Administrator() {
		super();
		this.phoneNumber = new SimpleStringProperty("");
	}

	public String getPhoneNumber() {
		return this.phoneNumber.get();
	}

	public void setPhoneNumber(String value) {
		this.phoneNumber.set(value);
	}

	public StringProperty phoneNumberProperty() {
		return this.phoneNumber;
	}
}
