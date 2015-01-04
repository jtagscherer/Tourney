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
}
