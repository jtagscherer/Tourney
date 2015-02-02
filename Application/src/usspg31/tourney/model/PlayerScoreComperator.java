package usspg31.tourney.model;

import java.util.Comparator;

public class PlayerScoreComperator implements Comparator<PlayerScore> {
    @Override
    public int compare(PlayerScore o1, PlayerScore o2) {
        if (o1.getPlayer().getStartingNumber()
                .equals(o2.getPlayer().getStartingNumber())) {
            return 0;
        } else if (Integer.valueOf(o1.getPlayer().getStartingNumber()) > Integer
                .valueOf(o2.getPlayer().getStartingNumber())) {
            return 1;
        } else {
            return -1;
        }
    }
}
