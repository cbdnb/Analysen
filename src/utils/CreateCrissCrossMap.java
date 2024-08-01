package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * Erzuegt Abbildung ddc-Nummer -> (SW-id, SW-Name, DDC-Determiniertheit)
 *
 * als Multimap und speichert in
 *
 * D:/Normdaten/numbersAllDet.out.
 */
public class CreateCrissCrossMap {

	/*
	 * ddc-Nummer -> (id, name, determiniertheit)
	 */
	private final ListMultimap<String, Triplett<String, String, String>> numbers = new ListMultimap<>();

	CreateCrissCrossMap() {

		ddcWorker = new DownloadWorker() {

			@Override
			protected void processRecord(final Record record) {
				try {
					final Collection<Line> ddcLines = GNDUtils
							.getValidDDCLines(record);
					for (final Line line : ddcLines) {
						String det = SubfieldUtils
								.getContentOfFirstSubfield(line, 'd');
						if (StringUtils.isNullOrEmpty(det))
							det = "0";
						final String number = SubfieldUtils
								.getContentOfFirstSubfield(line, 'c');
						final String id = record.getId();
						final String name = GNDUtils.getNameOfRecord(record);
						final Triplett<String, String, String> triplett = new Triplett<>(
								id, name, det);
						numbers.add(number, triplett);
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		};

		// 083 in Pica+
		final Predicate<String> gndFilter = new StringContains(
				Constants.RS + "037G " + Constants.US);
		ddcWorker.setStreamFilter(gndFilter);

		ddcWorker.gzipSettings();

	}

	private final DownloadWorker ddcWorker;

	public static void main(final String[] args) throws IOException {
		final CreateCrissCrossMap ccm = new CreateCrissCrossMap();
		ccm.ddcWorker.processFile("D:/Normdaten/DNBGND_s.dat.gz");
		ccm.ddcWorker.processFile("D:/Normdaten/DNBGND_g.dat.gz");
		ccm.ddcWorker.processFile("D:/Normdaten/DNBGND_u.dat.gz");
		ccm.ddcWorker.processFile("D:/Normdaten/DNBGND_p.dat.gz");
		ccm.ddcWorker.processFile("D:/Normdaten/DNBGND_b.dat.gz");
		ccm.ddcWorker.processFile("D:/Normdaten/DNBGND_f.dat.gz");

		System.out.println(ccm.numbers);

		final ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(Database.FILENAME_DDC_2_SWW));
		out.writeObject(ccm.numbers);
		FileUtils.safeClose(out);
	}

}
