package usspg31.tourney.model;

/**
 * Represents an administrator that manages an event
 */
public class EventAdministrator extends Administrator {
    @Override
    public Object clone() {
        EventAdministrator clone = new EventAdministrator();

        clone.setFirstName(this.getFirstName());
        clone.setLastName(this.getLastName());
        clone.setMailAdress(this.getMailAddress());
        clone.setPhoneNumber(this.getPhoneNumber());

        return clone;
    }
}
