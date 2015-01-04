package usspg31.tourney.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class TournamentModule {

	private final StringProperty name;
	private final StringProperty description;
	private final ObservableMap<String, Integer> possibleScores;
	private final ObservableList<GamePhase> phaseList;

	public TournamentModule() {
		this.name = new SimpleStringProperty();
		this.description = new SimpleStringProperty();
		this.possibleScores = new ObservableMap<String, Integer>() {

			@Override
			public void clear() {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean containsKey(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean containsValue(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Set<java.util.Map.Entry<String, Integer>> entrySet() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Integer get(Object arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Set<String> keySet() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Integer put(String arg0, Integer arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void putAll(Map<? extends String, ? extends Integer> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public Integer remove(Object arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Collection<Integer> values() {
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
			public void addListener(
					MapChangeListener<? super String, ? super Integer> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeListener(
					MapChangeListener<? super String, ? super Integer> arg0) {
				// TODO Auto-generated method stub

			}
		};
		this.phaseList = new ObservableList<GamePhase>() {

			@Override
			public boolean add(GamePhase arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void add(int arg0, GamePhase arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean addAll(Collection<? extends GamePhase> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addAll(int arg0, Collection<? extends GamePhase> arg1) {
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
			public GamePhase get(int arg0) {
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
			public Iterator<GamePhase> iterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int lastIndexOf(Object arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public ListIterator<GamePhase> listIterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ListIterator<GamePhase> listIterator(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean remove(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public GamePhase remove(int arg0) {
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
			public GamePhase set(int arg0, GamePhase arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public List<GamePhase> subList(int arg0, int arg1) {
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
			public boolean addAll(GamePhase... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void addListener(ListChangeListener<? super GamePhase> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void remove(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean removeAll(GamePhase... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeListener(
					ListChangeListener<? super GamePhase> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean retainAll(GamePhase... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(GamePhase... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(Collection<? extends GamePhase> arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		};

	}
}
