package scheven.verbuende;

import java.util.Collection;
import java.util.HashMap;

import de.dnb.basics.collections.Multimap;

public class MaximumMap extends Multimap<String, Integer> {

	private static final long serialVersionUID = 7749015556993764899L;

	public MaximumMap() {
		super(new HashMap<String, Collection<Integer>>());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.dnb.basics.statistics.Multimap#getNewValueCollection()
	 */
	@Override
	protected Collection<Integer> getNewValueCollection() {
		return new MaxCollection();
	}

	@Override
	public Collection<Integer> get(final String key) {
		final Collection<Integer> collectionForKey = map.get(key);
		return collectionForKey;
	}

	public static void main(final String[] args) {
		final MaximumMap map = new MaximumMap();

		map.add("a", 1);
		map.add("a", 2);
		map.add("a", 2);
		map.add("b", Integer.MAX_VALUE);

		System.out.println(map.toString());

	}

}
