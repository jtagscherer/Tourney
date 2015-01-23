package usspg31.tourney.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;

public class RoundTimer {

    private final int initialTimerDuration;
    private IntegerProperty timerDuration;

    private final Timeline timer;

    public RoundTimer(int initialDuration) {
        this.initialTimerDuration = initialDuration;
        this.timerDuration = new SimpleIntegerProperty(initialDuration);
        this.timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (this.timerDuration.get() > 0) {
                this.timerDuration.set(this.timerDuration.get() - 1);
            } else {
                this.pause();
            }
        }));
        this.timer.setCycleCount(Timeline.INDEFINITE);
    }

    public void resume() {
        if (this.timerDuration.get() > 0) {
            this.timer.play();
        }
    }

    public void pause() {
        this.timer.stop();
    }

    public void reset() {
        this.timerDuration.set(this.initialTimerDuration);
    }

    public void addTime() {
        this.timerDuration.set(this.timerDuration.get() + 30);
    }

    public void subtractTime() {
        this.timerDuration.set(this.timerDuration.get() - 30);
        if (this.timerDuration.get() < 0) {
            this.timerDuration.set(0);
            this.pause();
        }
    }

    public IntegerProperty getTimerDuration() {
        return this.timerDuration;
    }

}
