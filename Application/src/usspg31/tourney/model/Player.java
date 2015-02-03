package usspg31.tourney.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a player that can be registered in an event and play a tournament
 */
public class Player extends Person implements Cloneable {

    private final StringProperty nickName;
    private final StringProperty startingNumber;
    private final BooleanProperty payed;
    private final BooleanProperty disqualified;
    private final StringProperty id;

    /**
     * Create a new player and initialize all its properties
     */
    public Player() {
        super();
        this.nickName = new SimpleStringProperty("");
        this.startingNumber = new SimpleStringProperty("");
        this.payed = new SimpleBooleanProperty();
        this.disqualified = new SimpleBooleanProperty();
        this.id = new SimpleStringProperty("");
    }

    /**
     * Get the nick name of this player
     * 
     * @return Current nick name of this player
     */
    public String getNickName() {
        return this.nickName.get();

    }

    /**
     * Set the nick name of this player
     * 
     * @param value
     *            New nick name of this player
     */
    public void setNickName(String value) {
        this.nickName.set(value);
    }

    /**
     * Get the nick name property of this player
     * 
     * @return Nick name property of this player
     */
    public StringProperty nickNameProperty() {
        return this.nickName;
    }

    /**
     * Get the starting number of this player
     * 
     * @return Current starting number of this player
     */
    public String getStartingNumber() {
        return this.startingNumber.get();
    }

    /**
     * Set the starting number of this player
     * 
     * @param value
     *            New starting number of this player
     */
    public void setStartingNumber(String value) {
        this.startingNumber.set(value);
    }

    /**
     * Get the starting number property of this player
     * 
     * @return Starting number property of this player
     */
    public StringProperty startingNumberProperty() {
        return this.startingNumber;
    }

    /**
     * Get the payment status of this player
     * 
     * @return Current payment status of this player
     */
    public boolean hasPayed() {
        return this.payed.get();
    }

    /**
     * Set the payment status of this player
     * 
     * @param value
     *            New payment status of this player
     */
    public void setPayed(boolean value) {
        this.payed.set(value);
    }

    /**
     * Get the payment status property of this player
     * 
     * @return Payment status property of this player
     */
    public BooleanProperty payedProperty() {
        return this.payed;
    }

    /**
     * Get the disqualification status of this player
     * 
     * @return Current disqualification status of this player
     */
    public boolean isDisqualified() {
        return this.disqualified.get();
    }

    /**
     * Set the disqualification status of this player
     * 
     * @param value
     *            New disqualification status of this player
     */
    public void setDisqualified(boolean value) {
        this.disqualified.set(value);
    }

    /**
     * Get the disqualification status property of this player
     * 
     * @return Disqualification status property of this player
     */
    public BooleanProperty disqualifiedProperty() {
        return this.disqualified;
    }

    /**
     * Get the ID of this player
     * 
     * @return Current ID of this player
     */
    public String getId() {
        return this.id.get();
    }

    /**
     * Set the ID of this player
     * 
     * @param value
     *            New ID of this player
     */
    public void setId(String value) {
        this.id.set(value);
    }

    /**
     * Get the ID property of this player
     * 
     * @return ID property of this player
     */
    public StringProperty idProperty() {
        return this.id;
    }

    @Override
    public Object clone() {
        Player clone = new Player();

        clone.setFirstName(this.getFirstName());
        clone.setLastName(this.getLastName());
        clone.setMailAdress(this.getMailAddress());
        clone.setNickName(this.getNickName());
        clone.setStartingNumber(this.getStartingNumber());
        clone.setPayed(this.hasPayed());
        clone.setDisqualified(this.isDisqualified());
        clone.setId(this.getId());

        return clone;

    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
