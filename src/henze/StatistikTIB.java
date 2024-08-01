/**
 *
 */
package henze;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.RecordUtils.STANDORT_DNB;
import de.dnb.gnd.utils.SGUtils;

/**
 * @author baumann
 *
 */
public class StatistikTIB {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final CrossProductFrequency database = new CrossProductFrequency();

		final List<String> jahre = Arrays.asList("2019", "2020");
		final List<DDC_SG> stm = DDC_SG.getSTM();
		stm.add(DDC_SG.SG_310);
		Collections.sort(stm);
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);
		reader.setStreamFilter(
				new ContainsTag("1100", 'a', "2020", BibTagDB.getDB())
						.or(new ContainsTag("1100", 'a', "2019",
								BibTagDB.getDB())));

		int i = 0;

		for (final Record record : reader) {

			final String jahr = BibRecUtils.getYearOfPublicationString(record);
			if (!jahre.contains(jahr))
				continue;

			if (!BibRecUtils.isRA(record))
				continue;

			// Sonderbehandlung Oaf:
			if (BibRecUtils.getPhysikalischeForm(record) == 'O') {
				final String anmerkung = RecordUtils
						.getContentOfSubfield(record, "4243", 'n');
				if (StringUtils.contains(anmerkung, "Druck-Ausgabe", true)) {
					// System.err.println(record);
					continue;
				}
			}

			if (!BibRecUtils.istHochschulschrift(record))
				continue;

			final DDC_SG dhs = SGUtils.getDDCDHS(record);
			if (!stm.contains(dhs))
				continue;

			final STANDORT_DNB standort = RecordUtils
					.getEingebenderStandort(record);

			database.addValues(dhs, jahr, standort);
			i++;
			// if (i == 100)
			// break;

		}

		System.out.println("\t\t\t2019\t\t\t2020");// Überschrift1
		System.out.println("Sachgruppe\tL\tF\tsonst\t\tL\tF\tsonst");
		for (final DDC_SG dhs : stm) {
			String out = dhs.toString() + "\t";
			for (final String jahr : jahre) {
				for (final STANDORT_DNB standort : STANDORT_DNB.values()) {
					final long count = database.getCount(dhs, jahr, standort);
					out += count + "\t";
				}
				out += "\t";
			}
			System.out.println(out);
		}

	}

}
