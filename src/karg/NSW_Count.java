package karg;

import java.io.IOException;
import java.util.List;
import java.util.Set;
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

public class NSW_Count {

	private static final int DUMMY = 0;
	private static Set<String> nsws;
	private static Trie<Integer> trie;
	private static Frequency<String> nsw2count;
	private static final boolean TRIE = true;

	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {
		nsws = CollectionUtils.loadTreeSet(NSW_DB.NSW_DB);
		trie = new TST<>();
		nsw2count = new Frequency<>();

		nsws.forEach(s ->
		{
			String sNeu = s;
			sNeu = StringUtils.unicodeComposition(sNeu);
			sNeu = sNeu.replaceAll("\\(A\\)", "");
			sNeu = sNeu.replaceAll("\\(J\\)", "");
			sNeu = sNeu.trim();
			if (sNeu.contains("(J)") || sNeu.contains("(A)"))
				System.err.println(sNeu);
			nsw2count.addKey(sNeu);
			// für Phrase "M unter Quelle", damit nicht auch "Muntere Quelle"
			// unter M eingetragen wird
			final Integer found = trie.put(sNeu + " ", DUMMY);
			if (found != null)
				System.err.println("doppelt:" + sNeu);

		});

		final Set<String> triekeys = trie.keySet();
		System.err.println(triekeys.size());
		final Set<String> freqKeys = nsw2count.keySet();
		System.err.println(freqKeys.size());
		nsws = freqKeys;

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND);
		reader.setStreamFilter(new ContainsTag("670", GNDTagDB.getDB()));

		reader.forEach(record ->
		{
			final List<String> quellen = GNDUtils.getSourcesDataFound(record);
			quellen.forEach(quelle ->
			{
				// 1. Versuch
				quelle = quelle.trim();
				quelle = StringUtils.unicodeComposition(quelle);
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
		nsw2count.safe(NSW_DB.FOLDER + "/nsw2count.out");
		System.out.println(nsw2count.toString());

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
