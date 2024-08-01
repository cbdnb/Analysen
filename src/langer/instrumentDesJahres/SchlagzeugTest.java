/**
 *
 */
package langer.instrumentDesJahres;

import java.io.IOException;
import java.util.Set;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.CollectionUtils;

/**
 * @author baumann
 *
 */
public class SchlagzeugTest {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {
		final BiMultimap<Integer, Integer> ob2ub = Utils.getub2ob();
		final Set<Integer> treffer = ob2ub.searchKeys(94430375);
		System.out.println(treffer.size());
		System.out.println(CollectionUtils.shortView(treffer));
	}

}
