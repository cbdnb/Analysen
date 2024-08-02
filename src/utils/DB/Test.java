/**
 *
 */
package utils.DB;

import java.io.IOException;
import java.util.Collection;

import de.dnb.basics.collections.BiMultimap;
import de.dnb.gnd.utils.IDNUtils;

/**
 * @author baumann
 *
 */
public class Test {

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {

		// final String pack = (new Test()).getClass().getPackage().getName();
		//
		// final List<Object> cls = ClassFinder.getInstances(pack, null);
		// System.out.println(cls.size());
		// cls.forEach(cl ->
		// {
		// if (cl instanceof TableGenerator)
		// System.out.println(cl);
		// });

		final BiMultimap<Integer, String> table_names = GND_DB_UTIL
				.getppn2bioName();
		table_names.getValueSet().forEach(name ->
		{
			final Collection<Integer> keys = table_names.getKeys(name);
			if (keys.size() > 1)
				System.out.println(name + ": " + IDNUtils.ints2ppns(keys));
		});

		final BiMultimap<Integer, String> table_vw = GND_DB_UTIL
				.getppn2bioVerweisungen();
		table_vw.getValueSet().forEach(name ->
		{
			final Collection<Integer> keys = table_vw.getKeys(name);
			if (keys.size() > 1)
				System.out.println(name + ": " + IDNUtils.ints2ppns(keys));
		});
	}

}
