package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * Erzuegt Abbildung ddc-Nummer -> (SW-id, SW-Name)
 *
 * als Multimap und speichert in
 *
 * D:/Normdaten/numbers3and4.out.
 */
public class CreateDet3And4CrissCrossMap {

	/*
	 * ddc-Nummer -> (id, name)
	 */
	ListMultimap<String, Pair<String, String>> numbers = new ListMultimap<>();

	CreateDet3And4CrissCrossMap() {

		ddcWorker = new DownloadWorker() {

			final List<String> dets = Arrays.asList("3", "4");

			@Override
			protected void processRecord(final Record record) {
				final Collection<Line> ddcLines = GNDUtils.getValidDDCLines(record);
				for (final Line line : ddcLines) {
					final String det =
							SubfieldUtils.getContentOfFirstSubfield(line, 'd');
					if (det == null || !dets.contains(det))
						continue;

					final String number =
							SubfieldUtils.getContentOfFirstSubfield(line, 'c');
					final String id = record.getId();
					final String name = GNDUtils.getNameOfRecord(record);
					final Pair<String, String> pair =
							new Pair<String, String>(id, name);
					numbers.add(number, pair);
				}
			}
		};

		// 083 in Pica+
		final Predicate<String> gndFilter =
				new StringContains(Constants.RS + "037G " + Constants.US);
		ddcWorker.setStreamFilter(gndFilter);

		ddcWorker.gzipSettings();

	}

	DownloadWorker ddcWorker;

	public static void main(final String[] args) throws IOException {
		final CreateDet3And4CrissCrossMap ccm =
				new CreateDet3And4CrissCrossMap();
		ccm.ddcWorker.processFile("D:/Normdaten/DNBGND_s.dat.gz");
		ccm.ddcWorker.processFile("D:/Normdaten/DNBGND_g.dat.gz");
		ccm.ddcWorker.processFile("D:/Normdaten/DNBGND_u.dat.gz");
		ccm.ddcWorker.processFile("D:/Normdaten/DNBGND_p.dat.gz");
		ccm.ddcWorker.processFile("D:/Normdaten/DNBGND_b.dat.gz");
		ccm.ddcWorker.processFile("D:/Normdaten/DNBGND_f.dat.gz");

		System.out.println(ccm.numbers);

		final ObjectOutputStream out =
				new ObjectOutputStream(new FileOutputStream(
						"D:/Normdaten/numbers3and4.out"));
		out.writeObject(ccm.numbers);
		FileUtils.safeClose(out);
	}

}
