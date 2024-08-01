/**
 *
 */
package betz;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.BiMap;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.collections.ListUtils;

/**
 * @author baumann
 *
 */
public class Betz1 {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {
		final Collection<String> maxe = StringUtils.readLinesFromClip();
		final HashSet<String> top500 = CollectionUtils
				.loadHashSet(NSogg.TOP_500_PPN);
		final BiMultimap<String, String> mid2ppn = CollectionUtils
				.loadBiMultimap(NSogg.MID2PPN);
		final BiMultimap<String, String> ppn2name = CollectionUtils
				.loadBiMultimap(NSogg.PPN2NAME);
		final BiMap<String, String> ppn2nid = CollectionUtils
				.loadBimap(NSogg.PPN2NID);
		maxe.forEach(mid ->
		{
			final String midtrim = mid.trim();
			// System.err.println(midtrim);
			final Set<String> ppns = mid2ppn.getValueSet(midtrim);
			// System.err.println(ppns);
			String s = "";
			for (final String ppn : ppns) {
				final String top = top500.contains(ppn) ? "+" : "-";
				s += StringUtils.concatenateTab(ppn2nid.get(ppn), //
						// ppn2name.getValueSet(ppn),
						ListUtils.getFirst(ppn2name.getValueSet(ppn)), top);
			}
			System.out.println(s);
		});

	}

}
