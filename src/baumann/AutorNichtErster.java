/**
 *
 */
package baumann;

import java.io.IOException;
import java.util.ArrayList;

import de.dnb.basics.Constants;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.Tag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.WorkUtils;

/**
 * @author baumann
 *
 */
public class AutorNichtErster {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tu);

		reader.forEach(record ->
		{
			Line authorline;
			try {
				authorline = WorkUtils.getAuthorLine(record);
			} catch (final IllegalStateException e) {
				return;
			}
			if (authorline == null)
				return;
			final Tag authTag = authorline.getTag();

			final ArrayList<Line> personLines = RecordUtils.getLines(record,
					authTag);
			if (authorline != personLines.get(0))
				System.out.println(record.getId() + ": " + authorline);
		});

	}

}
