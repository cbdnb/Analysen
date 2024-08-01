/**
 *
 */
package scheven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.tries.TST;
import de.dnb.basics.tries.Trie;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SystematikUtils;

/**
 * @author baumann
 *
 */
public class DoppelteQuellenIn670 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final Trie<String> nswTrie = new TST<>();

		final ArrayList<Record> records = new ArrayList<>();

		final RecordReader nswR = RecordReader
				.getMatchingReader("D:/Analysen/baumann/NSW.txt");

		nswR.forEach(record ->
		{
			final String nsw = BibRecUtils.getAbkuerzungNSW(record);
			if (nsw != null && nsw.length() > 2)
				nswTrie.put(nsw.trim(), "");
		});
		final RecordReader swReader = RecordReader
				.getMatchingReader(Constants.Ts);
		final AtomicInteger atomicInteger = new AtomicInteger();
		swReader.forEach(record ->
		{
			final int level = GNDUtils.getLevel(record);
			if (level != 1)
				return;
			final List<String> quellen = GNDUtils.getSourcesDataFound(record);

			quellen.forEach(quelle ->
			{
				if (!StringUtils.contains(quelle, ",", false)
						&& !StringUtils.contains(quelle, ";", false))
					return;
				boolean firstFound = false;
				for (int i = 0; i < quelle.length(); i++) {
					final String tail = quelle.substring(i);
					if (nswTrie.containsPrefixFor(tail)) {
						if (!firstFound)
							firstFound = true;
						else {
							records.add(record);
							atomicInteger.getAndIncrement();
							return;
						}
					}
				}
			});
		});

		System.err.println(atomicInteger.get());

		Collections.sort(records, SystematikUtils.recordSysComparator);

		records.forEach(record ->
		{
			final List<String> quellen = GNDUtils.getSourcesDataFound(record);
			final String quelle = StringUtils.concatenate(" / ", quellen);
			final String idn = record.getId();
			final List<String> systL = GNDUtils.getGNDClassifications(record);
			final String syst = StringUtils.concatenate(";", systL);
			final String name = GNDUtils.getNameOfRecord(record);
			System.out.println(
					StringUtils.concatenate("\t", idn, name, syst, quelle));
		});

	}

}
