package karg;

import java.io.IOException;

import de.dnb.basics.Misc;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.RecordUtils;

public class ListeNSW {

	public static void main(final String[] args) throws IOException {
		RecordReader.getMatchingReader("D:/Analysen/karg/NSW/nsw.txt")
				.forEach(rec ->
				{
					final Line line0604 = RecordUtils.getTheOnlyLine(rec,
							"0604");
					final String s0604 = RecordUtils.toPicaWithoutTag(line0604);
					System.out.println(StringUtils.makeExcelLine(s0604,
							Misc.createURI(rec.getId())));
				});

	}

}
