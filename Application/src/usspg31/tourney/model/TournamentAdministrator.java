package usspg31.tourney.model;

/**
 * Represents a tournament administrator who runs a tournament
 */
public class TournamentAdministrator extends Administrator {
    @Override
    public Object clone() {
        TournamentAdministrator clone = new TournamentAdministrator();

        clone.setFirstName(this.getFirstName());
        clone.setLastName(this.getLastName());
        clone.setMailAdress(this.getMailAddress());
        clone.setPhoneNumber(this.getPhoneNumber());

        return clone;
    }
}
