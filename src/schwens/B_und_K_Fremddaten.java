package schwens;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.StatusAndCodeFilter;
import de.dnb.gnd.utils.SubjectUtils;

public class B_und_K_Fremddaten extends DownloadWorker {

	private static CrossProductFrequency belletristikFreq = new CrossProductFrequency();
	private static CrossProductFrequency kinderFreq = new CrossProductFrequency();
	final static String THEMA = "Thema-Klass.";
	final static String DESC = "Descriptoren";
	final static String OHNE = "ohne Fremddaten";

	//@formatter:on

	@Override
	protected void processRecord(final Record record) {

		final StatusAndCodeFilter filter = StatusAndCodeFilter
				.reiheA_selbststaendig();
		if (!filter.test(record))
			return;
		if (BibRecUtils.istHochschulschrift(record))
			return;

		final boolean isB = BibRecUtils.istBelletristik(record);
		final boolean isK = BibRecUtils.istKinderbuch(record);

		if (!isB && !isK)
			return;

		final String jahr = BibRecUtils.getYearOfPublicationString(record);
		if (jahr == null)
			return;

		if (isB) {
			fillFreq(belletristikFreq, record, jahr);
		} else {// dann K
			fillFreq(kinderFreq, record, jahr);
		}

	}

	/**
	 * @param record
	 * @param jahr
	 */
	public void fillFreq(final CrossProductFrequency freqency,
			final Record record, final String jahr) {
		// Gesamt:
		freqency.addValues(jahr);
		if (SubjectUtils.containsThemaClassification(record))
			freqency.addValues(jahr, THEMA);
		if (SubjectUtils.containsExternalDescriptor(record))
			freqency.addValues(jahr, DESC);
		if (!SubjectUtils.containsThemaClassification(record)
				&& !SubjectUtils.containsExternalDescriptor(record))
			freqency.addValues(jahr, OHNE);
	}

	public static void main(final String[] args) throws IOException {

		final B_und_K_Fremddaten medienwerke = new B_und_K_Fremddaten();

		medienwerke.gzipSettings();
		final Predicate<String> streamFilter = new ContainsTag("0500", '0', "A",
				BibTagDB.getDB());
		medienwerke.setStreamFilter(streamFilter);

		try {
			medienwerke.processFile(Constants.TITEL_STICHPROBE);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		System.out.println("Belletristik");
		output(belletristikFreq);
		System.out.println("\nKinderbücher");
		output(kinderFreq);
	}

	private static void output(final CrossProductFrequency frequency) {
		final NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
		for (int i = 2006; i <= 2017; i++) {
			final String jahr = Integer.toString(i);
			final long thema = frequency.getCount(jahr, THEMA);
			final long desc = frequency.getCount(jahr, DESC);
			final long ohne = frequency.getCount(jahr, OHNE);
			final long gesamt = frequency.getCount(jahr);
			final double prozThema = (double) thema / (double) gesamt;
			final double prozdesc = (double) desc / (double) gesamt;
			final double prozOhne = (double) ohne / (double) gesamt;
			System.out.println(StringUtils.concatenate("\t", jahr, gesamt,
					thema, format.format(prozThema), desc,
					format.format(prozdesc), ohne, format.format(prozOhne)));
		}
	}

}
