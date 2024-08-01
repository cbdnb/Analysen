/**
 *
 */
package betz;

import java.io.IOException;

import de.dnb.basics.collections.BiMap;
import de.dnb.basics.collections.BiMultimap;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.formatter.RDAFormatter;

/**
 * @author baumann
 *
 */
public class GND_DB {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(NSogg.FOLDER + "gnd_extrakt.gzip");
		final BiMap<String, String> ppn2nid = new BiMap<>();
		final BiMultimap<String, String> ppn2name = BiMultimap.createListMap();

		reader.forEach(record ->
		{
			final String ppn = record.getId();
			final String nid = GNDUtils.getNID(record);
			String name;
			try {
				name = RDAFormatter.getRDAHeading(record);
			} catch (final IllFormattedLineException e) {
				name = GNDUtils.getNameOfRecord(record);
			}

			ppn2nid.add(ppn, nid);
			ppn2name.add(ppn, name);
		});

		CollectionUtils.save(ppn2nid, NSogg.PPN2NID);
		CollectionUtils.save(ppn2name, NSogg.PPN2NAME);
	}

}
