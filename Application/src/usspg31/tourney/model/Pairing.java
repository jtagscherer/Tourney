package usspg31.tourney.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Pairing {

	private final ObservableList<Player> opponents;
	private final ObservableList<PlayerScore> scoreTable;

	public Pairing() {
		this.opponents = new ObservableList<Player>() {

			@Override
			public boolean add(Player e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void add(int index, Player element) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean addAll(Collection<? extends Player> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addAll(int index, Collection<? extends Player> c) {
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
			public Player get(int index) {
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
			public Iterator<Player> iterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int lastIndexOf(Object o) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public ListIterator<Player> listIterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ListIterator<Player> listIterator(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean remove(Object o) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Player remove(int index) {
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
			public Player set(int index, Player element) {
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
		this.scoreTable = new ObservableList<PlayerScore>() {

			@Override
			public void removeListener(InvalidationListener arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void addListener(InvalidationListener arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public <T> T[] toArray(T[] a) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object[] toArray() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<PlayerScore> subList(int fromIndex, int toIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public PlayerScore set(int index, PlayerScore element) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public PlayerScore remove(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean remove(Object o) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public ListIterator<PlayerScore> listIterator(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ListIterator<PlayerScore> listIterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int lastIndexOf(Object o) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Iterator<PlayerScore> iterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public int indexOf(Object o) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public PlayerScore get(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean contains(Object o) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void clear() {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean addAll(int index, Collection<? extends PlayerScore> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addAll(Collection<? extends PlayerScore> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void add(int index, PlayerScore element) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean add(PlayerScore e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(Collection<? extends PlayerScore> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(PlayerScore... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean retainAll(PlayerScore... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeListener(
					ListChangeListener<? super PlayerScore> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean removeAll(PlayerScore... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void remove(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void addListener(ListChangeListener<? super PlayerScore> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean addAll(PlayerScore... arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}
}
