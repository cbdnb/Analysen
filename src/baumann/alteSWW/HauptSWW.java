/**
 *
 */
package baumann.alteSWW;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 *
 *
 *
 * @author baumann
 *
 */
public class HauptSWW {

	/**
	 *
	 */
	public static final String FOLDER = "D:/Analysen/baumann/";
	private static Frequency<String> frequencyF = new Frequency<>();
	private static Frequency<String> frequencyL = new Frequency<>();
	private static PrintStream stream_hsw_f;
	private static PrintStream stream_hsw_l;
	private static PrintStream stream_hsw_gesamt;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		stream_hsw_f = new PrintStream(FOLDER + "HSW_F.txt");
		stream_hsw_l = new PrintStream(FOLDER + "HSW_L.txt");
		stream_hsw_gesamt = new PrintStream(FOLDER + "HSW_Gesamt.txt");

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_Z);
		reader.setStreamFilter(new ContainsTag("5530", BibTagDB.getDB()));
		reader.forEach(record ->
		{

			final ArrayList<Line> lines5530 = RecordUtils.getLines(record,
					"5530");

			for (final Line line : lines5530) {
				final String f = SubfieldUtils.getContentOfFirstSubfield(line,
						'a');
				if (f != null) {
					frequencyF.add(f);
					// System.err.println(f);
				}
				final String l = SubfieldUtils.getContentOfFirstSubfield(line,
						'g');
				if (l != null)
					frequencyL.add(l);

			}

		});

		ausgeben();

		StreamUtils.safeClose(stream_hsw_f);
		StreamUtils.safeClose(stream_hsw_l);
		StreamUtils.safeClose(stream_hsw_gesamt);
	}

	/**
	 * @param stream_hsw_f
	 * @param stream_hsw_l
	 */
	public static void ausgeben() {
		stream_hsw_f.println(frequencyF);
		stream_hsw_l.println(frequencyL);
		final Set<String> gesamt = new TreeSet<>(
				StringUtils.getGermanComparator());
		gesamt.addAll(frequencyF.keySet());
		gesamt.addAll(frequencyL.keySet());

		stream_hsw_gesamt.println(
				StringUtils.concatenate("\t", "HSW", "Titel F", "Titel L"));
		gesamt.forEach(hsw ->
		{
			final long countF = frequencyF.get(hsw);
			final long countL = frequencyL.get(hsw);
			stream_hsw_gesamt.println(
					StringUtils.concatenate("\t", hsw, countF, countL));
		});
	}

}
