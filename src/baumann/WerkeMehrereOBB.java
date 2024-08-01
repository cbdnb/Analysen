/**
 *
 */
package baumann;

import java.io.IOException;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.WorkUtils;

/**
 * @author baumann
 *
 */
public class WerkeMehrereOBB {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tu);
		reader.stream().filter(WorkUtils::isMusicalWork).forEach(mus ->
		{
			final List<Line> obb = WorkUtils.getPartitiveOBB(mus);
			if (obb.size() > 1)
				System.out.println(mus.getId());
		});

	}

}
