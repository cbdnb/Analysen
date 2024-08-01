/**
 *
 */
package baumann.musik;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * @author baumann
 *
 */
public class Musikberufe {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final PrintStream out = new PrintStream(
				"D:/analysen/baumann/" + "musikpersonen_ohne_14.4p.txt");

		// Hole alle MusikSWW:
		final Map<String, String> idn2Beruf = new HashMap<>();
		final RecordReader readerSachSWW = RecordReader
				.getMatchingReader(Constants.Ts);
		readerSachSWW.stream()
				.filter(record -> GNDUtils
						.containsGNDClassificationsTrunk(record, "14."))
				.forEach(record ->
				{
					final String nameOfRecord = GNDUtils
							.getNameOfRecord(record);
					idn2Beruf.put(record.getId(), nameOfRecord);
				});

		System.err.println("---Tp-----");
		// Hole alle Berufe und trage sie in eine Häufigkeitsverteilung ein,
		// wenn es MusikSWW sind:
		final Frequency<String> berufAnz = new Frequency<>();
		final Frequency<String> einzigerBerufAnz = new Frequency<>();
		final Frequency<String> count14_p = new Frequency<>();
		final RecordReader readerPers = RecordReader
				.getMatchingReader(Constants.Tp);
		readerPers.forEach(record ->
		{
			final boolean mit14_4p = GNDUtils.containsGNDClassification(record,
					"14.4p");
			final List<Line> linesBer = RecordUtils.getLinesWithSubfield(record,
					"550", '4', "beru|berc");
			if (mit14_4p && linesBer.size() == 0)
				count14_p.add("14.4p ohne Berufe");
			if (mit14_4p && linesBer.size() != 0)
				count14_p.add("14.4p mit Berufen");
			final List<String> idnsBeruf = FilterUtils.map(linesBer,
					line -> SubfieldUtils.getContentOfFirstSubfield(line, '9'));

			final List<String> musikberufe = new ArrayList<>();
			for (final String idn : idnsBeruf) {
				if (idn2Beruf.containsKey(idn)) {
					final String beruf = idn2Beruf.get(idn);
					musikberufe.add(beruf);
					berufAnz.add(beruf);
				}
			}
			final boolean hatMusikBeruf = !musikberufe.isEmpty();
			if (hatMusikBeruf && !mit14_4p) {
				count14_p.add("mit Musikberuf ohne 14.4p");
				final String name = GNDUtils.getNameOfRecord(record);
				final String outStr = StringUtils.concatenate("\t", name,
						record.getId(), musikberufe);
				out.println(outStr);
				System.err.println(outStr);
			}
			if (hatMusikBeruf && mit14_4p)
				count14_p.add("mit 14.4p und Musikberuf");
			if (musikberufe.size() == 1)
				einzigerBerufAnz.add(musikberufe.get(0));
		});

		berufAnz.forEach(beruf -> System.out.println(
				StringUtils.concatenate("\t", beruf, berufAnz.get(beruf),
						einzigerBerufAnz.get(beruf))));
		System.out.println();
		System.out.println(count14_p);
		System.out.println();
		StreamUtils.safeClose(out);
	}

}
