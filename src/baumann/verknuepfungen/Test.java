package baumann.verknuepfungen;

import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.BibRecUtils;

public class Test {

	public static void main(final String[] args) {
		final Record record = BibRecUtils.readFromClip();
		System.out.println(record);

	}

}
