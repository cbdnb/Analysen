import java.io.IOException;

import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;

public class Sample {

	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader.getMatchingReader(
				"D:/pica-0.22.0-x86_64-pc-windows-msvc/temp.dat.gz");
		int i = 0;
		for (final Record record : reader) {
			if (i == 10)
				return;
			System.err.println(record);
			System.err.println();
			System.err.println("------------------");
			System.err.println();
			i++;

		}

	}

}
