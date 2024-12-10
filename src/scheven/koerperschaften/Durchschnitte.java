package scheven.koerperschaften;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.parser.tag.Tag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.parser.Record;

public class Durchschnitte {

	public static void main(final String[] args) throws IOException {

		final GNDTagDB db = GNDTagDB.getDB();
		final Set<Tag> tags = new TreeSet<>(db.getTag5XX());
		tags.add(db.findTag("039"));
		tags.add(db.findTag("410"));
		tags.add(db.findTag("500"));
		final Map<Tag, Frequency<Integer>> tag2freq = new HashMap<>();
		tags.forEach(tag ->
		{
			tag2freq.put(tag, new Frequency<Integer>());
		});

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tb);

		for (final Record record : reader) {
			for (final Tag tag : tags) {
				final ArrayList<Line> lines = RecordUtils.getLines(record, tag);
				final int zeilenZahl = lines.size();
				if (zeilenZahl > 0) {
					(tag2freq.get(tag)).add(zeilenZahl);
				}
			}
		}

		for (final Tag tag : tags) {
			final Frequency<Integer> freq = tag2freq.get(tag);
			double gesamt = 0;
			for (final Integer feldlaenge : freq) {
				gesamt += feldlaenge * freq.get(feldlaenge);
			}
			System.out.println(
					StringUtils.concatenate(": ", tag, gesamt / freq.getSum()));
		}

	}
}
