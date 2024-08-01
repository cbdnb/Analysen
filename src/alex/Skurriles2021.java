
package alex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.List;

import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * @author baumann
 *
 */
public class Skurriles2021 extends DownloadWorker {

	static List<Pair<String, String>> idnAndDDCs = new ArrayList<>();
	static Comparator<Pair<String, String>> ddcComp = Comparator
			.comparing(Pair::getSecond, Comparator.comparing(String::length));

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Skurriles2021 skurriles = new Skurriles2021();
		skurriles.setStreamFilter(new StringContains(null));
		skurriles.processGZipFile(
				"Z:/cbs/stages/prod/vollabzug/aktuell/Pica+/DNBtitel_reichwein.dat.gz");
		Collections.sort(idnAndDDCs, ddcComp.reversed());
		idnAndDDCs.stream().limit(20).forEach(System.out::println);
		System.out.println();

	}

	@Override
	protected void processRecord(final Record record) {
		final ArrayList<Line> ieStatLines = BibRecUtils.getIEStatistik(record);
		if (ieStatLines.isEmpty())
			return;
		final String id = record.getId();
		final List<String> ddcs = SubjectUtils.getCompleteDDCNotations(record);
		ddcs.forEach(ddc ->
		{
			if (ddc != null)
				idnAndDDCs.add(new Pair<>(id, ddc));
		});

	}

}
