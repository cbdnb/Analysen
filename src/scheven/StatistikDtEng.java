package scheven;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.WV;

public class StatistikDtEng extends DownloadWorker {

	static final CrossProductFrequency DATABASE = new CrossProductFrequency();

	static final Collection<String> JAHRE = Arrays.asList("13", "14");

	static final Collection<String> REIHEN = Arrays.asList("A", "B", "H", "O");

	static final Collection<String> SPRACHEN = Arrays.asList("ger", "eng");

	int i = 0;

	@Override
	protected void processRecord(final Record record) {

		// richtiges Jahr?
		final Collection<WV> jahrUndReihe = BibRecUtils.getWVs(record);
		if (jahrUndReihe.isEmpty())
			return;
		String jahr = null;
		String reihe = null;
		for (final WV pair : jahrUndReihe) {
			if (JAHRE.contains("" + pair.getYear())) {
				jahr = "" + pair.getYear();
				reihe = "" + pair.getSeries();
			}
		}
		if (jahr == null)
			return;

		// richtige Sprache, es werden beide Sprachen gezählt:
		final List<String> sprachen = BibRecUtils.getLanguagesOfText(record);
		sprachen.retainAll(SPRACHEN);
		if (sprachen.isEmpty())
			return;

		// Hauptsachgruppe:
		final String dhs = SGUtils.getDhsStringPair(record).first;
		if (dhs == null)
			return;
		i++;
		// System.err.println(record);
		for (final String sprache : sprachen) {
			DATABASE.addValues(dhs, jahr, reihe, sprache);
			System.err.println(StringUtils.concatenate(" / ", i, record.getId(),
					dhs, jahr, reihe, sprache));
		}

		// if (i == 200)
		// throw new IllegalArgumentException(record.getRawData());

	}

	public static void main(final String[] args) throws IOException {

		final StatistikDtEng dtEng = new StatistikDtEng();

		// vorab nach 1500 (Sprache) filtern (010@):
		final Predicate<String> titleFilter = new StringContains(
				Constants.RS + "010@ " + Constants.US);
		dtEng.setStreamFilter(titleFilter);

		System.err.println("Titeldaten flöhen:");
		try {
			dtEng.processGZipFile("D:/Normdaten/DNBtitelgesamt.dat.gz");
		} catch (final Exception e) {
			e.printStackTrace();
		}

		// Überschriften:
		String caption1 = "DHS\t";
		String caption2 = "\t";
		String caption3 = "\t";
		for (final String jahr : JAHRE) {
			caption1 += jahr;
			for (final String reihe : REIHEN) {
				caption2 += reihe;
				for (final String sprache : SPRACHEN) {
					caption1 += "\t";
					caption2 += "\t";
					caption3 += sprache + "\t";
				}
			}
		}
		System.out.println(caption1);
		System.out.println(caption2);
		System.out.println(caption3);

		// String greatTab = StringUtils.padding(6, '\t');
		// System.out.println("DHS\t2013" + greatTab + "2014" + greatTab);
		// String abh = "A\t\tB\t\tH\t\t";
		// System.out.println("\t" + StringUtils.repeat(2, abh));
		// String gerEng = "ger\teng\t";
		// System.out.println("\t" + StringUtils.repeat(6, gerEng));

		final Collection<String> sachgruppen = SGUtils.allDHSasString();
		for (final String dhs : sachgruppen) {
			String out = dhs;
			for (final String jahr : JAHRE) {
				for (final String reihe : REIHEN) {
					for (final String sprache : StatistikDtEng.SPRACHEN) {
						out += "\t"
								+ DATABASE.getCount(dhs, jahr, reihe, sprache);
					}
				}
			}
			System.out.println(out);
		}

		System.out.println(DATABASE);

	}

}
