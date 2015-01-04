package usspg31.tourney.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TournamentRound {

	private final ObservableList<Pairing> pairings;
	private final int roundNumber;

	/**
	 * @param roundNumber
	 */
	public TournamentRound(int roundNumber) {
		this.pairings = new ObservableList<Pairing>() {

			@Override
			public boolean add(Pairing e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void add(int index, Pairing element) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean addAll(Collection<? extends Pairing> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addAll(int index, Collection<? extends Pairing> c) {
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
			public Pairing get(int index) {
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
			public Iterator<Pairing> iterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int lastIndexOf(Object o) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public ListIterator<Pairing> listIterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ListIterator<Pairing> listIterator(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean remove(Object o) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Pairing remove(int index) {
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
			public Pairing set(int index, Pairing element) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public List<Pairing> subList(int fromIndex, int toIndex) {
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
			public boolean addAll(Pairing... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void addListener(ListChangeListener<? super Pairing> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void remove(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean removeAll(Pairing... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeListener(ListChangeListener<? super Pairing> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean retainAll(Pairing... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(Pairing... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(Collection<? extends Pairing> arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		this.roundNumber = roundNumber;
	}
}
