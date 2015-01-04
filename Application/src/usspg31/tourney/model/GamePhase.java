package usspg31.tourney.model;

import java.time.Duration;

import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import usspg31.tourney.model.pairingstrategies.PairingStrategy;

public class GamePhase {

	private final IntegerProperty cutoff;
	private final ObjectProperty<PairingStrategy> pairingMethod;
	private final IntegerProperty roundCount;
	private final IntegerProperty phaseNumber;
	private final ObjectProperty<Duration> roundDuration;

	public GamePhase() {
		this.cutoff = new SimpleIntegerProperty();
		this.roundCount = new SimpleIntegerProperty();
		this.phaseNumber = new SimpleIntegerProperty();
		this.pairingMethod = new ObjectProperty<PairingStrategy>() {

			@Override
			public void bind(ObservableValue<? extends PairingStrategy> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isBound() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void unbind() {
				// TODO Auto-generated method stub

			}

			@Override
			public Object getBean() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void addListener(ChangeListener<? super PairingStrategy> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeListener(
					ChangeListener<? super PairingStrategy> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void addListener(InvalidationListener arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeListener(InvalidationListener arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public PairingStrategy get() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void set(PairingStrategy arg0) {
				// TODO Auto-generated method stub

			}
		};
		this.roundDuration = new ObjectProperty<Duration>() {

			@Override
			public void bind(ObservableValue<? extends Duration> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isBound() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void unbind() {
				// TODO Auto-generated method stub

			}

			@Override
			public Object getBean() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void addListener(ChangeListener<? super Duration> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeListener(ChangeListener<? super Duration> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void addListener(InvalidationListener arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeListener(InvalidationListener arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public Duration get() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void set(Duration arg0) {
				// TODO Auto-generated method stub

			}
		};
	}
}
