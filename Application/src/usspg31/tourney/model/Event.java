package usspg31.tourney.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Event {

	public static enum EventPhase {
		EVENTSETUP, PREREGISTRATION, REGISTATION, TOURNAMENTEXECUTION
	}

	private final ObservableList<Tournament> tournaments;
	private final ObservableList<Player> registeredPlayers;
	private final StringProperty name;
	private final ObjectProperty<LocalDate> startDate;
	private final ObjectProperty<LocalDate> endDate;
	private final StringProperty location;
	private final ObservableList<EventAdministrator> administrators;
	private final ObjectProperty<EventPhase> eventPhase;

	public Event() {
		this.tournaments = new ObservableList<Tournament>() {

			@Override
			public boolean add(Tournament arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void add(int arg0, Tournament arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean addAll(Collection<? extends Tournament> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addAll(int arg0,
					Collection<? extends Tournament> arg1) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void clear() {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean contains(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean containsAll(Collection<?> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Tournament get(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int indexOf(Object arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Iterator<Tournament> iterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int lastIndexOf(Object arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public ListIterator<Tournament> listIterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ListIterator<Tournament> listIterator(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean remove(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Tournament remove(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean removeAll(Collection<?> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean retainAll(Collection<?> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Tournament set(int arg0, Tournament arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public List<Tournament> subList(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object[] toArray() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> T[] toArray(T[] arg0) {
				// TODO Auto-generated method stub
				return null;
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
			public boolean addAll(Tournament... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void addListener(ListChangeListener<? super Tournament> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void remove(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean removeAll(Tournament... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeListener(
					ListChangeListener<? super Tournament> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean retainAll(Tournament... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(Tournament... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(Collection<? extends Tournament> arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		this.registeredPlayers = new ObservableList<Player>() {

			@Override
			public boolean add(Player arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void add(int arg0, Player arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean addAll(Collection<? extends Player> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addAll(int arg0, Collection<? extends Player> arg1) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void clear() {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean contains(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean containsAll(Collection<?> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Player get(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int indexOf(Object arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Iterator<Player> iterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int lastIndexOf(Object arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public ListIterator<Player> listIterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ListIterator<Player> listIterator(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean remove(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Player remove(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean removeAll(Collection<?> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean retainAll(Collection<?> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Player set(int arg0, Player arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public List<Player> subList(int fromIndex, int toIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object[] toArray() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> T[] toArray(T[] a) {
				// TODO Auto-generated method stub
				return null;
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
			public boolean addAll(Player... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void addListener(ListChangeListener<? super Player> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void remove(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean removeAll(Player... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeListener(ListChangeListener<? super Player> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean retainAll(Player... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(Player... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(Collection<? extends Player> arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		this.name = new SimpleStringProperty();
		this.startDate = new ObjectProperty<LocalDate>() {

			@Override
			public void bind(ObservableValue<? extends LocalDate> arg0) {
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
			public void addListener(ChangeListener<? super LocalDate> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeListener(ChangeListener<? super LocalDate> arg0) {
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
			public LocalDate get() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void set(LocalDate arg0) {
				// TODO Auto-generated method stub

			}
		};
		this.endDate = new ObjectProperty<LocalDate>() {

			@Override
			public void bind(ObservableValue<? extends LocalDate> arg0) {
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
			public void addListener(ChangeListener<? super LocalDate> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeListener(ChangeListener<? super LocalDate> arg0) {
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
			public LocalDate get() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void set(LocalDate arg0) {
				// TODO Auto-generated method stub

			}
		};
		this.location = new SimpleStringProperty();
		this.administrators = new ObservableList<EventAdministrator>() {

			@Override
			public boolean add(EventAdministrator e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void add(int index, EventAdministrator element) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean addAll(Collection<? extends EventAdministrator> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addAll(int index,
					Collection<? extends EventAdministrator> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void clear() {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean contains(Object o) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public EventAdministrator get(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int indexOf(Object o) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Iterator<EventAdministrator> iterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int lastIndexOf(Object o) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public ListIterator<EventAdministrator> listIterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ListIterator<EventAdministrator> listIterator(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean remove(Object o) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public EventAdministrator remove(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public EventAdministrator set(int index, EventAdministrator element) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public List<EventAdministrator> subList(int fromIndex, int toIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object[] toArray() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> T[] toArray(T[] a) {
				// TODO Auto-generated method stub
				return null;
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
			public boolean addAll(EventAdministrator... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void addListener(
					ListChangeListener<? super EventAdministrator> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void remove(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean removeAll(EventAdministrator... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeListener(
					ListChangeListener<? super EventAdministrator> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean retainAll(EventAdministrator... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(EventAdministrator... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(Collection<? extends EventAdministrator> arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		this.eventPhase = new ObjectProperty<Event.EventPhase>() {

			@Override
			public void bind(ObservableValue<? extends EventPhase> arg0) {
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
			public void addListener(ChangeListener<? super EventPhase> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeListener(ChangeListener<? super EventPhase> arg0) {
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
			public EventPhase get() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void set(EventPhase arg0) {
				// TODO Auto-generated method stub

			}
		};
	}
}
