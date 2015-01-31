package usspg31.tourney.controller;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;
import usspg31.tourney.controller.controls.eventphases.execution.TournamentExecutionProjectionController;

public class RoundTimer {

    private final int initialTimerDuration;
    private IntegerProperty timerDuration;

    private final StringProperty timerString;

    private final Timeline timer;
    private boolean paused = true;
    ArrayList<TournamentExecutionProjectionController> projectionControllers;

    public RoundTimer(int initialDuration) {
        this.projectionControllers = new ArrayList<TournamentExecutionProjectionController>();
        this.initialTimerDuration = initialDuration;
        this.timerDuration = new SimpleIntegerProperty(initialDuration);
        this.timerString = new SimpleStringProperty(String.format("%d:%02d",
                initialDuration / 60, initialDuration % 60));

        this.timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (this.timerDuration.get() > 0) {
                this.timerDuration.set(this.timerDuration.get() - 1);
                this.updateTimerString();
            } else {
                this.pause();
            }
        }));
        this.timer.setCycleCount(Timeline.INDEFINITE);
    }

    public void setProjectionControllers(
            ArrayList<TournamentExecutionProjectionController> controllers) {
        this.projectionControllers = controllers;
    }

    public void resume() {
        if (this.timerDuration.get() > 0) {
            this.timer.play();
            this.paused = false;
        }
    }

    public void pause() {
        this.timer.stop();
        this.paused = true;
    }

    public void reset() {
        this.timerDuration.set(this.initialTimerDuration);
        this.updateTimerString();
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void addTime() {
        this.timerDuration.set(this.timerDuration.get() + 30);
        this.updateTimerString();
    }

    public void subtractTime() {
        this.timerDuration.set(this.timerDuration.get() - 30);
        if (this.timerDuration.get() < 0) {
            this.timerDuration.set(0);
            this.pause();
        }
        this.updateTimerString();
    }

    public void updateTimerString() {
        String formattedTime = String.format("%d:%02d",
                this.timerDuration.get() / 60, this.timerDuration.get() % 60);
        this.timerString.set(formattedTime);

        for (TournamentExecutionProjectionController controller : this.projectionControllers) {
            controller.setTimeString(formattedTime);
        }
    }

    public IntegerProperty getTimerDuration() {
        return this.timerDuration;
    }

    public StringProperty timerStringProperty() {
        return this.timerString;
    }
}
