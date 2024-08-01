package baumann.ddc;

import java.io.IOException;
import java.util.ArrayList;
import de.dnb.basics.Constants;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.collections.StatisticUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.BibRecUtils.REIHE;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.SubjectUtils;

public class StatistikVergebeneDDCs {

	static final CrossProductFrequency freq = new CrossProductFrequency();

	static void processRecord(final Record record) {
		final String idn = record.getId();
		if (idn == null)
			return;

		final String dhs = SGUtils.getDhsStringPair(record).first;
		if (dhs == null)
			return;

		final REIHE reihe = BibRecUtils.getReihe(record);
		if (reihe == null)
			return;

		final ArrayList<Line> ddclines = SubjectUtils
				.getDDCMainScheduleLines(record);
		if (ddclines.isEmpty())
			return;

		freq.addValues(dhs, reihe);
		System.err.println(idn + ", " + dhs + ", " + reihe);

	}

	public static void main(final String[] args) throws IOException {

		final RecordReader recordReader = RecordReader
				.getMatchingReader(Constants.TITEL_STICHPROBE);
		recordReader.setStreamFilter(new ContainsTag("5400", BibTagDB.getDB()));
		//@formatter:off
		recordReader
			.stream()
			.forEach(StatistikVergebeneDDCs::processRecord);
		//@formatter:on

		System.out.println(StatisticUtils.getTableFrom(freq, "SG",
				SGUtils.allDHSasString(), REIHE.enumSet(), ""));

	}

}
