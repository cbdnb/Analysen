/**
 *
 */
package scheven.verbuende;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author baumann
 *
 */
final class MaxCollection implements Collection<Integer> {

	/**
	 *
	 */
	public MaxCollection() {
		// count++;
		// System.out.println("Anzahl: " + count);
	}

	private int value = Integer.MIN_VALUE;

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(final Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<Integer> iterator() {
		final List<Integer> asList = Arrays.asList(value);
		return asList.iterator();
	}

	@Override
	public Object[] toArray() {
		return new Integer[value];
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(final Integer e) {
		if (e > value) {
			value = e;

			return true;
		}
		return false;
	}

	@Override
	public boolean remove(final Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(final Collection<? extends Integer> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		value = 0;
	}
}