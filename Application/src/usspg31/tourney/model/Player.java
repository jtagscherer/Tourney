package usspg31.tourney.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Player extends Person {

	private final StringProperty nickName;
	private final StringProperty startingNumber;
	private final BooleanProperty payed;
	private final BooleanProperty disqualified;
	private final StringProperty id;

	public Player() {
		super();
		this.nickName = new SimpleStringProperty();
		this.startingNumber = new SimpleStringProperty();
		this.payed = new SimpleBooleanProperty();
		this.disqualified = new SimpleBooleanProperty();
		this.id = new SimpleStringProperty();
	}

	public String getNickName() {
		return this.nickName.get();

	}

	public void setNickName(String value) {
		this.nickName.set(value);
	}

	public StringProperty nickNameProperty() {
		return this.nickName;
	}

	public String getStartingNumber() {
		return this.startingNumber.get();
	}

	public void setStartingNumber(String value) {
		this.startingNumber.set(value);
	}

	public StringProperty startingNumberProperty() {
		return this.startingNumber;
	}

	public boolean getPayed() {
		return this.payed.get();
	}

	public void setPayed(boolean value) {
		this.payed.set(value);
	}

	public BooleanProperty payedProperty() {
		return this.payed;
	}

	public boolean getDisqualified() {
		return this.disqualified.get();
	}

	public void setDisqualified(boolean value) {
		this.disqualified.set(value);
	}

	public BooleanProperty disqualifiedProperty() {
		return this.disqualified;
	}

	public String getId() {
		return this.id.get();
	}

	public void setId(String value) {
		this.id.set(value);
	}

	public StringProperty idProperty() {
		return this.id;
	}
}
