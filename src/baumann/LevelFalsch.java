/**
 *
 */
package baumann;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * @author baumann
 *
 */
public class LevelFalsch {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final PrintWriter out = MyFileUtils
				.outputFile("D:/Analysen/baumann/level_falsch.txt", false);
		final RecordReader reader1 = RecordReader
				.getMatchingReader(Constants.GND);
		final Set<Integer> level1 = new HashSet<>();
		reader1.forEach(rec ->
		{
			final int level = GNDUtils.getLevel(rec);
			if (level <= 1)
				level1.add(IDNUtils.ppn2int(rec.getId()));
		});

		System.err.println("Level1 gesammelt");

		final RecordReader reader2 = RecordReader
				.getMatchingReader(Constants.GND);
		reader2.forEach(rec ->
		{
			final int level = GNDUtils.getLevel(rec);
			if (level > 1)
				return;
			// keine Personen!
			final char recordType = GNDUtils.getRecordType(rec);
			if (recordType == 'p')
				return;

			final List<Integer> idns = GNDUtils.getRelatedLines5XX(rec).stream()
					.map(line -> SubfieldUtils.getContentOfFirstSubfield(line,
							'9'))
					.filter(IDNUtils::isKorrektePPN).map(IDNUtils::ppn2int)
					.collect(Collectors.toList());
			idns.forEach(idn ->
			{
				if (!level1.contains(idn)) {
					out.println(rec.getId() + ", T" + recordType + ": "
							+ GNDUtils.getNameOfRecord(rec));
					return;
				}
			});
		});

	}

}
