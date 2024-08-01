/**
 *
 */
package baumann.statistiken;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SGUtils;

/**
 * @author baumann
 *
 */
public class AF_Statistik {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final List<String> jahre = Arrays.asList("2018", "2019", "2020", "2021",
				"2022");
		final TreeSet<String> dhss = new TreeSet<>();
		final CrossProductFrequency jahrSG2count = new CrossProductFrequency();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);
		final ContainsTag contAF = new ContainsTag("0500", '0', "AF",
				BibTagDB.getDB());
		reader.setStreamFilter(contAF);
		reader.forEach(record ->
		{
			if (!RecordUtils.getDatatype(record).equals("AF"))
				return;
			final String jahr = BibRecUtils.getYearOfPublicationString(record);
			if (!jahre.contains(jahr))
				return;
			final String dhs = SGUtils.getFullDHSString(record, null);
			if (dhs != null) {
				jahrSG2count.addValues(jahr, dhs);
				dhss.add(dhs);
			}
		});

		final String ueberschrift = "DHS" + "\t"
				+ StringUtils.concatenateTab(jahre.toArray());
		System.out.println(ueberschrift);

		dhss.forEach(dhs ->
		{
			final StringBuilder outBuilder = new StringBuilder(dhs);
			jahre.forEach(jahr -> outBuilder
					.append("\t" + jahrSG2count.getCount(jahr, dhs)));
			System.out.println(outBuilder);
		});

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main1(final String[] args) throws IOException {
		final List<String> jahre = Arrays.asList("2018", "2019", "2020", "2021",
				"2022");
		final TreeSet<String> dhss = new TreeSet<>();
		final CrossProductFrequency jahrSG2count = new CrossProductFrequency();
		jahrSG2count.addValues("2018", "510");
		System.out.println(jahrSG2count.getCount("2018", "510"));
		// System.out.println(jahrSG2count.getCount("2018", "531"));

	}

}
