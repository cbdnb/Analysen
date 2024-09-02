/**
 *
 */
package scheven;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.Subfield;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * @author baumann
 *
 */
public class Motive2 {

	private static final String MOTIV_150 = "D:/Analysen/scheven/150_Motiv.txt";
	private static final String MOTIV_150_450 = "D:/Analysen/scheven/150_Motiv_450.txt";
	private static final String MOTIV_450 = "D:/Analysen/scheven/450_Motiv.txt";
	private static final String NUR_SYST = "D:/Analysen/scheven/syst_kein_Motiv.txt";
	private static PrintStream stream_mot_150;
	private static PrintStream stream_mot_150_450;
	private static PrintStream stream_mot_nur_450;
	private static PrintStream stream_nur_syst;
	private static Set<String> motivIDNs = new HashSet<>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		stream_mot_150 = new PrintStream(MOTIV_150);
		stream_mot_150_450 = new PrintStream(MOTIV_150_450);
		stream_mot_nur_450 = new PrintStream(MOTIV_450);
		stream_nur_syst = new PrintStream(NUR_SYST);

		System.out.println("Anfang: " + TimeUtils.getActualTimehhMM());

		RecordReader.getMatchingReader(Constants.GND).forEach(record ->
		{
			Line heading;
			try {
				heading = GNDUtils.getHeading(record);
			} catch (final Exception e) {
				return;
			}
			final boolean isMotiv1xx = containsMotiv(heading);

			boolean isMotiv4xx = false;
			final List<Line> lines4xx = GNDUtils.getLines4XX(record);
			for (final Line line4xx : lines4xx) {
				if (containsMotiv(line4xx)) {
					isMotiv4xx = true;
					break;
				}
			}

			final char typ = GNDUtils.getRecordType(record);
			final boolean isSyst = GNDUtils.containsGNDClassificationsTrunk(
					record, "13.1c", "12.4") && typ == 's';

			if (!isMotiv1xx && !isMotiv4xx && !isSyst)
				return;

			// eine der Bedingungen ist also erfüllt.

			final String nid = GNDUtils.getNID(record);
			final String idn = record.getId();

			final ArrayList<String> verw = FilterUtils.map(lines4xx,
					RecordUtils::toPicaWithoutTag);
			final String verwS = StringUtils.concatenate(" // ", verw);

			final List<String> syst = GNDUtils.getGNDClassifications(record);
			final String systS = StringUtils.concatenate(";", syst);

			final String name = GNDUtils.getNameOfRecord(record);

			// Fall 1:
			if (isMotiv1xx) {
				String out = StringUtils.concatenate("\t", nid, name);
				stream_mot_150.println(out);
				motivIDNs.add(idn);

				// Fall 2:
				if (!verw.isEmpty()) {
					out = StringUtils.concatenate("\t", nid, name, verwS);
					stream_mot_150_450.println(out);
				}
			}

			// Fall 3:
			if (isMotiv4xx && !isMotiv1xx) {
				final String out = StringUtils.concatenate("\t", nid, name,
						verwS);
				stream_mot_nur_450.println(out);
				motivIDNs.add(idn);
			}

			// Fall 4:
			if (isSyst && !isMotiv1xx && !isMotiv4xx) {
				final String out = StringUtils.concatenate("\t", nid, name,
						verwS, systS);
				stream_nur_syst.println(out);
				motivIDNs.add(idn);
			}

		});

		System.err.println("Titel----------");

		final AtomicInteger ai = new AtomicInteger();

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_Z);
		reader.setStreamFilter(new ContainsTag("5100", BibTagDB.getDB()));
		reader.forEach(record ->
		{
			final Collection<String> rswk = SubjectUtils.getRSWKidsSet(record);
			rswk.retainAll(motivIDNs);
			if (!rswk.isEmpty()) {
				ai.incrementAndGet();
				// System.err.println(record.getId());
			}

		});

		System.out.println("Anzahl: " + ai.get());

		MyFileUtils.safeClose(stream_mot_150);
		MyFileUtils.safeClose(stream_mot_150_450);
		MyFileUtils.safeClose(stream_mot_nur_450);
		MyFileUtils.safeClose(stream_nur_syst);

		System.out.println("Ende: " + TimeUtils.getActualTimehhMM());

	}

	private static Pattern motivPattern = Pattern.compile("Motiv\\b");

	/**
	 * @param isMotiv
	 * @param heading
	 * @return
	 */
	public static boolean containsMotiv(final Line heading) {
		final List<Subfield> subs = heading.getSubfields();
		for (final Subfield sub : subs) {
			if (sub.getIndicator().indicatorChar == 'g') {
				final String content = sub.getContent();
				final Matcher matcher = motivPattern.matcher(content);
				if (matcher.find())
					return true;
			}

		}
		return false;
	}

	public static void main1(final String[] args) {

	}

}
