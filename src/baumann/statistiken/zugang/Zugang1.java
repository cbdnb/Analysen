/**
 *
 */
package baumann.statistiken.zugang;

import java.io.IOException;
import java.util.ArrayList;
import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.StatusAndCodeFilter;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.RecordUtils.STANDORT_DNB;

/**
 * @author baumann
 *
 */
public class Zugang1 {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final CrossProductFrequency zugangsStat = new CrossProductFrequency();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_STICHPROBE);
		reader.setStreamFilter(
				new StringContains(Constants.MARC_SUB_SEP + "z" + "StatZUG"));
		final StatusAndCodeFilter ismusi = StatusAndCodeFilter.filterMusikalie()
				.setIgnoreStatus(true);

		reader.forEach(record ->
		{
			if (ismusi.test(record))
				return;
			if (BibRecUtils.isMagazine(record))
				return;

			final ArrayList<Line> zugLines = BibRecUtils
					.getZUGStatistik(record);
			FilterUtils.filter(zugLines, line -> SubfieldUtils
					.getActionscode(line).equalsIgnoreCase("NEU"));
			if (zugLines.isEmpty())
				return;

			final STANDORT_DNB standort = BibRecUtils.getStandort(zugLines);
			if (standort == STANDORT_DNB.U) {
				System.err.println("Nicht DNB:" + record.getId());
				return;
			}

			final Pair<String, String> sd = RecordUtils
					.getSourceAndDateEntered(record);
			if (sd == null)
				return;

			final STANDORT_DNB standortErst = RecordUtils.getStandort(sd.first);

			final boolean gleicherStandort = standort == standortErst;
			if (!gleicherStandort)
				System.err.println("Unleich: " + record.getId());
			zugangsStat.addValues(gleicherStandort);

		});

		System.out.println(zugangsStat.toString());

	}

}
