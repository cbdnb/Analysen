/**
 *
 */
package baumann.skurriles;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.RecordUtils.STANDORT_DNB;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * @author baumann
 *
 */
public class Stachanow extends DownloadWorker {

	final static Map<String, PrintStream> streams = new HashMap<>();
	/*
	 * Die einzelne Frequency enthält sowohl eine Zuordnung (datum, standort) ->
	 * int, als auch die Zuordnung (standort) -> int
	 */
	final static Map<String, CrossProductFrequency> frequencies = new HashMap<>();
	final static TreeSet<String> tage = new TreeSet<>();

	private static final List<String> TYPEN = Arrays.asList("StatIE", "StatFOE",
			"StatZUG");
	private static final String JJJJ = "2022";

	/**
	 *
	 */
	private static final String PATH = "D:/Analysen/baumann/skurriles/stachanow_"
			+ JJJJ + "_";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		for (final String typ : TYPEN) {
			final PrintStream ps = new PrintStream(PATH + typ + ".txt");
			streams.put(typ, ps);
			final CrossProductFrequency f = new CrossProductFrequency();
			frequencies.put(typ, f);
		}
		final Stachanow stach = new Stachanow();
		stach.setStreamFilter(
				new StringContains("Stat").and(new StringContains(JJJJ + "-")));

		stach.processGZipFile(Constants.TITEL_PLUS_EXEMPLAR_Z);
		TYPEN.forEach(typ ->
		{
			final CrossProductFrequency f = frequencies.get(typ);
			final PrintStream stream = streams.get(typ);
			stream.println(StringUtils.concatenate("\t", "Datum", "Anzahl F",
					"Anzahl L", "Gesamt"));
			tage.forEach(date ->
			{
				final long anzahl = f.getCount(date);
				if (anzahl > 0) {
					final long anzahlF = f.getCount(date, STANDORT_DNB.F);
					final long anzahlL = f.getCount(date, STANDORT_DNB.L);
					stream.println(StringUtils.concatenate("\t", date, anzahlF,
							anzahlL, anzahl));
				}
			});
			StreamUtils.safeClose(stream);
		});
	}

	@Override
	protected void processRecord(final Record record) {
		final ArrayList<Line> statLines = BibRecUtils.getStatistik(record);
		statLines.forEach(line ->
		{
			final String dateStr = SubfieldUtils.getDollarD(line);
			final String nutzerkennung = SubfieldUtils.getNutzerkennung(line);
			final STANDORT_DNB standort = RecordUtils
					.getStandort(nutzerkennung);
			if (dateStr.startsWith(JJJJ)) {
				final String typ = SubfieldUtils.getGeschaeftsgang(line);
				final CrossProductFrequency f = frequencies.get(typ);
				if (f != null) {
					tage.add(dateStr);
					f.addValues(dateStr, standort);
					f.addValues(dateStr);
					System.err.println(typ + " " + dateStr + " " + standort);
				}
			}
		});

	}

}
