package karg;

import java.io.IOException;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.utils.TimeUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.StatusAndCodeFilter;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.gnd.parser.Record;

public class K10Plus {

	public static void main(final String[] args) throws IOException {
		System.out.println(TimeUtils.getActualTimehhMM());
		long count = 0;
		final CrossProductFrequency statistik = new CrossProductFrequency();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.TITEL_GESAMT_Z);
		final StatusAndCodeFilter filter = StatusAndCodeFilter.imBestand();
		for (final Record record : reader) {
			if (!filter.test(record))
				continue;
			count++;
			final boolean ddc = SubjectUtils.containsDDC(record);
			final boolean bk = SubjectUtils.containsEigeneBK(record)
					|| SubjectUtils.containsFremdBK(record);
			final boolean gnd = SubjectUtils.containsRSWK(record);
			final boolean sg = SubjectUtils.containsDHS(record);
			statistik.addValues(gnd, ddc, bk, sg);
			// if (count == 1_000)
			// break;
		}

		System.out.println("Anzahl: " + count);
		System.out.println();
		System.out.println(
				StringUtils.concatenateTab("GND", "DDC", "BK", "SGG", "#"));
		System.out.println(statistik);
		System.out.println(TimeUtils.getActualTimehhMM());
	}

}
