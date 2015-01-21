package usspg31.tourney.controller.util;

import java.util.Map.Entry;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableMap;

public class MapToStringBinding<K, V> extends StringBinding {
    private final ObservableMap<K, V> map;

    public MapToStringBinding(ObservableMap<K, V> map) {
        this.map = map;
        this.bind(this.map);
    }

    public StringProperty getStringProperty() {
        SimpleStringProperty sp = new SimpleStringProperty();
        sp.bind(this);
        return sp;
    }

    @Override
    protected String computeValue() {
        StringBuilder ret = new StringBuilder();
        int i = 0;
        for (Entry<K, V> entry : this.map.entrySet()) {
            ret.append(entry.getKey() + ": " + entry.getValue());
            if (++i != this.map.size()) {
                ret.append(", ");
            }
        }
        return ret.toString();
    }
}
