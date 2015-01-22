package usspg31.tourney.model;

/**
 * This class holds static methods for ID generation
 */
public class IdentificationManager {
    private static char SEPARATOR = '-';

    /**
     * Generate a unique ID for a player
     * 
     * @param player
     *            Player to be used
     * @return The generated ID
     */
    public static String generateId(Player player) {
        return String.valueOf(new String(player.getFirstName()
                + IdentificationManager.SEPARATOR + player.getLastName()
                + IdentificationManager.SEPARATOR + player.getMailAddress()
                + IdentificationManager.SEPARATOR + player.getNickName()
                + IdentificationManager.SEPARATOR
                + String.valueOf((int) (Math.random() * 100000))).hashCode());
    }
}
