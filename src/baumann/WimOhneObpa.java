package baumann;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

public class WimOhneObpa extends DownloadWorker {

	int i = 0;

	@Override
	protected void processRecord(final Record record) {

		if (RecordUtils.isBibliographic(record))
			return;

		final int level = GNDUtils.getLevel(record);
		if(level != 1)
			return;

		// wim, wif?
		final List<String> ents = GNDUtils.getEntityTypes(record);
		final List<String> wims = Arrays.asList("wim", "wif");
		ents.retainAll(wims);
		if (ents.isEmpty())
			return;

		final Line heading = GNDUtils.getHeading(record);
		final String p = SubfieldUtils.getContentOfFirstSubfield(heading, 'p');

		if (p == null)
			return;

		final Pair<Line, Integer> obpa =
				RecordUtils.getFirstLineTagGivenAsString(record, "530");
		if (obpa.second != 0)
			return;

		i++;

		System.err.println(i + " / " + record.getId());

	}

	public static void main(final String[] args) throws IOException {

		final WimOhneObpa aut = new WimOhneObpa();

		try {
			aut.processGZipFile("D:/Normdaten/DNBGND_u.dat.gz");
		} catch (final Exception e) {

		}

		System.out.println(aut.i);

	}
}
