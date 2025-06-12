package jahns;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.formatter.ExcelFormatter;

public class Professoren {

	private static final String FILE = "professorinnen";
	private static final String FOLDER = "D:/Analysen/jahns/";

	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(FOLDER + FILE + ".txt");
		final List<Record> records = reader.stream()
				.collect(Collectors.toList());
		final PrintWriter out = MyFileUtils
				.oeffneAusgabeDatei(FOLDER + FILE + ".tab", false);
		final String format = ExcelFormatter.format(records, false);
		System.err.println(format);
		out.println(format);

	}

}
