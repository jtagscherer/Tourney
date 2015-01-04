package usspg31.tourney.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class PlayerScore {

	private final Player player;
	private final ObservableList<Integer> score;

	public PlayerScore() {
		this.player = new Player();
		this.score = new ObservableList<Integer>() {

			@Override
			public boolean add(Integer arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void add(int arg0, Integer arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean addAll(Collection<? extends Integer> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addAll(int arg0, Collection<? extends Integer> arg1) {
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
			public Integer get(int arg0) {
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
			public Iterator<Integer> iterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int lastIndexOf(Object arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public ListIterator<Integer> listIterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ListIterator<Integer> listIterator(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean remove(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Integer remove(int arg0) {
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
			public Integer set(int arg0, Integer arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public List<Integer> subList(int arg0, int arg1) {
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
			public boolean addAll(Integer... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void addListener(ListChangeListener<? super Integer> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void remove(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean removeAll(Integer... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeListener(ListChangeListener<? super Integer> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean retainAll(Integer... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(Integer... arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean setAll(Collection<? extends Integer> arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}
}
