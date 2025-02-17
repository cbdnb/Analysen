package karg;

import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;

public class NSW_Count {

	public static void main(final String[] args)
			throws ClassNotFoundException, IOException {
		final TreeSet<String> nsws = CollectionUtils.loadTreeSet(NSW_DB.NSW_DB);
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND);
		reader.setStreamFilter(new ContainsTag("670", GNDTagDB.getDB()));
		final Frequency<String> nsw2count = new Frequency<>();
		// initialisieren:
		nsws.forEach(nsw2count::addKey);
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
					for (String fragment : fragments) {
						fragment = fragment.trim();
						if (nsws.contains(fragment))
							nsw2count.add(fragment);
					}
					// 3. Versuch:
					fragments = quelle.split(",");
					for (String fragment : fragments) {
						fragment = fragment.trim();
						if (nsws.contains(fragment))
							nsw2count.add(fragment);
					}
				}

			});
		});
		System.out.println(nsw2count);

	}

}
