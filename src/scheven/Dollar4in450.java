/**
 *
 */
package scheven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.SystematikUtils;

/**
 * @author baumann
 *
 */
public class Dollar4in450 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final LinkedHashSet<Record> recordSet = new LinkedHashSet<>();
		final AtomicInteger atomicInteger = new AtomicInteger();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Ts);
		reader.forEach(record ->
		{
			final int level = GNDUtils.getLevel(record);
			if (level != 1)
				return;
			final ArrayList<Line> lines = RecordUtils.getLines(record, "450");
			lines.forEach(line ->
			{
				if (!SubfieldUtils.containsIndicator(line, 'x'))
					return;
				recordSet.add(record);
				atomicInteger.getAndIncrement();

			});

		});

		System.err.println(atomicInteger.get());

		final ArrayList<Record> records = new ArrayList<>(recordSet);

		Collections.sort(records, SystematikUtils.recordSysComparator);

		records.forEach(record ->
		{

			final String idn = record.getId();
			final List<String> systL = GNDUtils.getGNDClassifications(record);
			final String syst = StringUtils.concatenate(";", systL);
			final String name = GNDUtils.getNameOfRecord(record);
			final ArrayList<Line> syns = RecordUtils.getLines(record, "450");
			final String syn = StringUtils.concatenate(" / ",
					FilterUtils.map(syns, RecordUtils::toPicaWithoutTag));
			System.out.println(
					StringUtils.concatenate("\t", idn, name, syst, syn));
		});

		System.err.println(atomicInteger.get());

	}

}
