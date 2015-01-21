package usspg31.tourney.controller.controls;

import usspg31.tourney.model.Event;

public interface EventUser {
    public void loadEvent(Event event);

    public void unloadEvent();
}
