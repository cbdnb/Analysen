package henze;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.DDC_SG.REFERATE;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;
import de.dnb.ie.utils.AcDatabase;
import de.dnb.gnd.utils.WV;

public class ErschlosseneMedienwerke extends DownloadWorker {

	private static final String ZEIT = "zeit";

	private static final CrossProductFrequency DATABASE = new CrossProductFrequency();

	private static final Collection<String> JAHRE = Arrays.asList("13", "14");

	int i = 0;

	@Override
	protected void processRecord(final Record record) {

		// richtiges Jahr?
		final Collection<WV> wvs = BibRecUtils.getWVs(record);
		if (wvs.isEmpty())
			return;
		String jahr = null;

		for (final WV wv : wvs) {
			final int year = wv.getYear();
			if (JAHRE.contains(year + "")) {
				jahr = "" + wv.getYear();
				// keine Reihe o
				if (wv.getSeries() == 'O')
					return;
			}
		}
		if (jahr == null)
			return;

		// Hauptsachgruppe:
		String dhs = null;
		TIEFE status = SubjectUtils.getErschliessungsTiefe(record);
		if (status == null) { // übergeordneten versuchen
			final String idnBroader = BibRecUtils.getBroaderTitleIDN(record);
			final Pair<String, TIEFE> pair = AcDatabase.getStatus(idnBroader);
			// System.err.println(pair);
			if (pair != null) {
				status = pair.second;
			}
		}
		if (status == null)
			return;

		if (dhs == null)
			dhs = SGUtils.getDhsStringPair(record).first;
		if (dhs == null)
			return;

		final REFERATE referat = SGUtils.getReferat(dhs);
		// wenn die DHS unbekannt ist:
		if (referat == null)
			return;

		final int workingTime = SubjectUtils.getProcessingTime(record);

		i++;
		// System.err.println(record);

		DATABASE.addValues(referat, jahr);
		DATABASE.addValues(referat, jahr, status);
		DATABASE.incrementValues(workingTime, referat, jahr, ZEIT);
		System.err.println(StringUtils.concatenate(" / ", i, record.getId(),
				dhs, jahr, referat, workingTime));

		// if (i == 50) {
		// System.out.println(record.getRawData());
		// throw new IllegalArgumentException(record.getRawData());
		// }

	}

	public static void main(final String[] args) throws IOException {

		final ErschlosseneMedienwerke medienwerke = new ErschlosseneMedienwerke();

		// vorab nach 2105 (Lieferungsnummer) filtern (006U):
		final Predicate<String> titleFilter = new StringContains(
				Constants.RS + "006U " + Constants.US + "01");
		medienwerke.setStreamFilter(titleFilter);
		medienwerke.gzipSettings();
		// debug:
		// medienwerke.setHandler(new WrappingHandler());

		System.err.println("Titeldaten flöhen:");
		medienwerke.setOutputFile(
				"D:/Analysen/Henze/Erschlossene Medienwerke3.txt");
		try {
			// medienwerke.processFile("D:/Normdaten/DNBtitel_Stichprobe.dat.gz");
			medienwerke.processFile(
					"Z:/cbs/zen/vollabzug/aktuell/Pica+/DNBtitelgesamt.dat.gz");
		} catch (final Exception e) {
			e.printStackTrace();
		}

		// Überschriften:
		final TIEFE[] statusses = TIEFE.values();

		final String caption1 = "\t" + StringUtils.concatenate(
				"\t\t\t" + StringUtils.padding(statusses.length, '\t'), "2013",
				"2014");
		String caption2 = "\t";
		for (final TIEFE status : statusses) {
			caption2 += status.verbal + "\t";
		}
		caption2 += StringUtils.concatenate("\t", "gesamt", "Stunden", "VZÄ");
		medienwerke.println(caption1);
		medienwerke.println(StringUtils.repeat(2, caption2));
		for (final DDC_SG.REFERATE referat : DDC_SG.REFERATE.values()) {
			final String referatName = referat.name;
			medienwerke.print(referatName);
			for (final String jahr : JAHRE) {
				for (final TIEFE status : TIEFE.values()) {
					medienwerke
							.tab("" + DATABASE.getCount(referat, jahr, status));
				}
				medienwerke.tab("" + DATABASE.getCount(referat, jahr));
				final long minutes = DATABASE.getCount(referat, jahr, ZEIT);
				final int h = (int) (minutes / 60);
				final int min = (int) (minutes % 60);
				final String time = "" + h + ":" + min;
				medienwerke.tab(time);
				// 118800 min pro Jahr für eine Vollzeitstelle
				final float vzä = (float) (minutes / 118800.0);
				medienwerke.tab("" + vzä);
			}
			medienwerke.println();
		}
		System.out.println();
		System.out.println();
		System.out.println(DATABASE);

	}

}
