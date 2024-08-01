package busse;

import java.io.IOException;

import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.SubjectUtils;

/**
 *
 *
 * @author baumann
 *
 */
public class DDCStatistik {

	private static final String TITELDATEN = "Z:/cbs/zen/vollabzug/aktuell/Pica+/DNBGNDtitel.dat.gz";
	private static final String STICHPROBE = "D:/Normdaten/DNBtitel_Stichprobe.dat.gz";
	private static final boolean DEBUG = false;

	public static void main(final String[] args) throws IOException {

		final Frequency<String> ddcF = new Frequency<>();

		String input;
		if (DEBUG)
			input = STICHPROBE;
		else
			input = TITELDATEN;
		final RecordReader reader = RecordReader.gzipReader(input);
		reader.setStreamFilter(new ContainsTag("5400", BibTagDB.getDB()));

		reader.forEach(record ->
		{
			ddcF.addCollection(SubjectUtils.getAllsimpleDDCNotations(record));
		});

		System.out.println(ddcF);
	}

}
