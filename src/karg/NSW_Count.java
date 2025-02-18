package karg;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.tries.TST;
import de.dnb.basics.tries.Trie;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import static java.lang.System.out;
import static java.lang.System.err;

public class NSW_Count {

	private static TreeSet<String> nsws;
	private static Trie<Integer> trie;
	private static Frequency<String> nsw2count;
	private static final boolean TRIE = true;

	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {
		nsws = CollectionUtils.loadTreeSet(NSW_DB.NSW_DB);
		trie = new TST<>();
		// für Phrase "M unter Quelle", damit nicht auch "Muntere Quelle" unter
		// M eingetragen wird
		nsws.forEach(s -> trie.putValue(s + " ", 0));
		err.println(trie.size());
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND);
		reader.setStreamFilter(new ContainsTag("670", GNDTagDB.getDB()));
		nsw2count = new Frequency<>();
		// initialisieren:
		nsws.forEach(nsw2count::addKey);
		err.println(nsw2count.size());
		reader.forEach(record ->
		{
			final List<String> quellen = GNDUtils.getSourcesDataFound(record);
			quellen.forEach(quelle ->
			{
				// 1. Versuch
				quelle = quelle.trim();
				if (nsws.contains(quelle))
					nsw2count.add(quelle);
				else {
					// 2. Versuch
					String[] fragments = quelle.split(";");
					queryFragments(fragments);
					// 3. Versuch:
					fragments = quelle.split(",");
					queryFragments(fragments);
				}

			});
		});
		out.println(StringUtils.unicodeComposition(nsw2count.toString()));

	}

	private static void queryFragments(final String[] fragments) {
		for (String fragment : fragments) {
			fragment = fragment.trim();
			if (nsws.contains(fragment))
				nsw2count.add(fragment);
			else if (TRIE) {
				final String prefix = trie.longestPrefixOf(fragment);
				if (prefix != null)
					nsw2count.add(prefix.trim());
			}
		}
	}

}
