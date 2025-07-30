package scheven.koerperschaften;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.parser.tag.Tag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.formatter.HTMLFormatter;
import de.dnb.gnd.parser.Record;

public class Recorde {

	public static void main(final String[] args) throws IOException {

		final PrintWriter out = MyFileUtils
				.outputFile("D:/Analysen/scheven/Tb/rekorde.html", false);

		final GNDTagDB db = GNDTagDB.getDB();
		final Set<Tag> tags = new TreeSet<>(db.getTag5XX());
		tags.add(db.findTag("039"));
		tags.add(db.findTag("410"));
		tags.add(db.findTag("913"));
		final Map<Tag, Integer> maxima = new HashMap<>();
		final Map<Tag, Record> rekorde = new HashMap<>();

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tb);

		for (final Record record : reader) {
			for (final Tag tag : tags) {
				final ArrayList<Line> lines = RecordUtils.getLines(record, tag);
				final int size = lines.size();
				final int max = maxima.getOrDefault(tag, 0);
				if (size > max) {
					maxima.put(tag, size);
					rekorde.put(tag, record);
					System.err.println(tag.pica3 + ": " + size);
				}
			}
		}
		tags.forEach(tag ->
		{
			final Record record = rekorde.get(tag);
			out.println(tag + ": " + "Anzahl: " + maxima.get(tag) + "<br>");
			out.println("IDN: " + record.getId() + "<br>");
			out.println(HTMLFormatter.toHTML(record) + "<br>");
			out.println("<br>");
			out.println("<br>");
		});

		MyFileUtils.safeClose(out);

	}
}
