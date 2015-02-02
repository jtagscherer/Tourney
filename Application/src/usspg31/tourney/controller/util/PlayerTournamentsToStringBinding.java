package usspg31.tourney.controller.util;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;

public class PlayerTournamentsToStringBinding extends StringBinding {
    private final ObjectProperty<Player> player;
    private final ListProperty<Tournament> tournaments;

    public PlayerTournamentsToStringBinding(Player player,
            ObservableList<Tournament> tournaments) {
        this.player = new SimpleObjectProperty<Player>();
        this.player.set(player);
        this.tournaments = new SimpleListProperty<Tournament>(tournaments);
        this.bind(this.player);
        this.bind(this.tournaments);
    }

    public StringProperty getStringProperty() {
        SimpleStringProperty sp = new SimpleStringProperty();
        sp.bind(this);
        return sp;
    }

    @Override
    protected String computeValue() {
        StringBuilder ret = new StringBuilder();

        for (Tournament tournament : this.tournaments) {
            for (Player player : tournament.getRegisteredPlayers()) {
                if (player.getId().equals(this.player.getValue().getId())) {
                    ret.append(tournament.getName()).append(", ");
                }
            }
        }

        return ret.substring(0, ret.length() - 2);
    }
}
