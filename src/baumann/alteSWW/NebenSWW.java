/**
 *
 */
package baumann.alteSWW;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

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
public class NebenSWW {

	/**
	 *
	 */
	private static final String FOLDER = "D:/Analysen/baumann/";
	private static final String PREFIX = "USW";
	private static Frequency<String> frequencyF = new Frequency<>();
	private static Frequency<String> frequencyL = new Frequency<>();
	private static PrintStream stream_nsw_f;
	private static PrintStream stream_nsw_l;
	private static PrintStream stream_nsw_gesamt;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		stream_nsw_f = new PrintStream(FOLDER + PREFIX + "_F.txt");
		stream_nsw_l = new PrintStream(FOLDER + PREFIX + "_L.txt");
		stream_nsw_gesamt = new PrintStream(FOLDER + PREFIX + "_Gesamt.txt");

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_Z);
		reader.setStreamFilter(new ContainsTag("5530", BibTagDB.getDB()));
		final AtomicInteger doubleSlashs = new AtomicInteger();
		reader.forEach(record ->
		{

			final ArrayList<Line> lines5530 = RecordUtils.getLines(record,
					"5530");

			for (final Line line5530 : lines5530) {
				final List<String> fs = SubfieldUtils
						.getContentsOfSubfields(line5530, 'f');
				fs.forEach(dollarF ->
				{
					frequencyF.add(dollarF);
				});

				final List<String> ls = SubfieldUtils
						.getContentsOfSubfields(line5530, 'h');
				ls.forEach(dollarH ->
				{
					if (dollarH.contains("//"))
						doubleSlashs.incrementAndGet();
					final String[] splits = dollarH.split(" // ");
					for (final String split : splits) {
						// System.err.println(split);
						frequencyL.add(split);
					}
				});

			}

		});

		System.out.println("Zahl der '//': " + doubleSlashs);

		ausgeben();

		StreamUtils.safeClose(stream_nsw_f);
		StreamUtils.safeClose(stream_nsw_l);
		StreamUtils.safeClose(stream_nsw_gesamt);
	}

	/**
	 * @param stream_nsw_f
	 * @param stream_nsw_l
	 */
	public static void ausgeben() {
		stream_nsw_f.println(frequencyF);
		stream_nsw_l.println(frequencyL);
		final Set<String> gesamt = new TreeSet<>(
				StringUtils.getGermanComparator());
		gesamt.addAll(frequencyF.keySet());
		gesamt.addAll(frequencyL.keySet());

		stream_nsw_gesamt.println(
				StringUtils.concatenate("\t", "NSW", "Titel F", "Titel L"));
		gesamt.forEach(nsw ->
		{
			final long countF = frequencyF.get(nsw);
			final long countL = frequencyL.get(nsw);
			stream_nsw_gesamt.println(
					StringUtils.concatenate("\t", nsw, countF, countL));
		});
	}

}
