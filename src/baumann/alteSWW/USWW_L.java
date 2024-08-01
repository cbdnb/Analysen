/**
 *
 */
package baumann.alteSWW;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class USWW_L {

	private static final String FOLDER = "D:/Analysen/baumann/";
	private static final String PREFIX = "USW-1";
	private static Frequency<String> frequencyL = new Frequency<>();
	private static Frequency<String> frequencyL_als_hsw = new Frequency<>();
	private static PrintStream stream_nsw_l;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		stream_nsw_l = new PrintStream(FOLDER + PREFIX + "_L.txt");

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_Z);
		reader.setStreamFilter(new ContainsTag("5530", BibTagDB.getDB()));

		reader.forEach(record ->
		{

			final ArrayList<Line> lines5530 = RecordUtils.getLines(record,
					"5530");
			final Set<String> hsww = new HashSet<>();
			hsww.addAll(
					SubfieldUtils.getContentsOfFirstSubfields(lines5530, 'a'));
			hsww.addAll(
					SubfieldUtils.getContentsOfFirstSubfields(lines5530, 'g'));

			for (final Line line5530 : lines5530) {

				final List<String> usws = SubfieldUtils
						.getContentsOfSubfields(line5530, 'h');
				usws.forEach(usw ->
				{

					final String[] splits = usw.split(" // ");
					for (final String split : splits) {
						frequencyL.add(split);
						if (hsww.contains(split))
							frequencyL_als_hsw.add(split);
					}
				});

			}

		});

		ausgeben();
		StreamUtils.safeClose(stream_nsw_l);

	}

	/**
	 * @param stream_nsw_f
	 * @param stream_nsw_l
	 */
	public static void ausgeben() {

		frequencyL.keySet().forEach(nsw ->
		{

			final long countL = frequencyL.get(nsw);
			final long countAuchHsw = frequencyL_als_hsw.get(nsw);
			stream_nsw_l.println(
					StringUtils.concatenate("\t", nsw, countL, countAuchHsw));

		});
	}

}
