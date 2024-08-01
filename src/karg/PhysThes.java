/**
 *
 */
package karg;

import java.io.IOException;
import java.util.List;

import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class PhysThes {

	private static String ZWEITER = "Phys-Thes.";
	private static String OHNE = "Phys-Thes";
	private static String ALLE = "Phys.-Thes.";
	private static String ERSTER = "Phys.-Thes";

	public static void main(final String[] args) throws IOException {

		final RecordReader reader = RecordReader
				.getMatchingReader("D:/Normdaten/DNBGND_s.dat.gz");
		reader.setStreamFilter(new ContainsTag("670", GNDTagDB.getDB()));
		final Frequency<String> physT = new Frequency<>();
		reader.forEach(record ->
		{
			final List<String> quellen = GNDUtils.getSourcesDataFound(record);
			quellen.forEach(quelle ->
			{
				if (quelle.contains(ZWEITER))
					physT.add(ZWEITER);
				else if (quelle.contains(OHNE))
					physT.add(OHNE);
				else if (quelle.contains(ALLE))
					physT.add(ALLE);
				else if (quelle.contains(ERSTER))
					physT.add(ERSTER);
			});
		});
		System.out.println(physT);

		System.out.println();
	}

}
