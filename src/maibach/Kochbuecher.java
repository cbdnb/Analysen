package maibach;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.SubjectUtils;
import utils.Database;

public class Kochbuecher extends DownloadWorker {

	Database database;

	PrintStream stream;

	Kochbuecher() throws SQLException, IOException {
		database = new Database();
		stream = new PrintStream("D:/Analysen/maibach/kochbücher.txt");
	}

	@Override
	protected void finalize() throws Throwable {
		StreamUtils.safeClose(stream);
	};

	int i = 0;

	@Override
	protected void processRecord(final Record record) {

		final List<String> codes = RecordUtils.getContentsOfAllSubfields(record,
				"1131", 'a');
		if (!codes.contains("kob"))
			return;
		System.err.println(record);
		final String dhs = SGUtils.getDhsStringPair(record).first;
		if (!StringUtils.equals(dhs, "640"))
			return;
		final List<String> ddcs = SubjectUtils.getCompleteDDCNotations(record);
		if (ddcs.isEmpty())
			return;
		for (final String ddc : ddcs) {
			stream.println();
			stream.print(record.getId());
			stream.print("\t");
			stream.print(ddc);
			stream.print("\t");
			stream.print(database.getCaption(ddc));

			final Collection<Triplett<String, String, String>> sww = database
					.getCrissCrossSWW(ddc);
			printDDCCrissCrossSWW(sww);
			final Collection<String> registers = database.getRegisters(ddc);
			printRegisters(registers);
			final Collection<String> swIDs = SubjectUtils.getRSWKidsSet(record);
			printTitleSWW(swIDs);

		}

	}

	private void printTitleSWW(final Collection<String> swIDs) {
		final List<String> strings = new LinkedList<String>();
		for (final String swID : swIDs) {
			final String name = database.getNameForIDN(swID);
			if (name == null)
				continue;
			final String s = swID + " " + name;
			strings.add(s);
		}
		stream.print("\t");
		stream.print(StringUtils.makeExcelCellFromCollection(strings));
	}

	private void printRegisters(final Collection<String> registers) {
		stream.print("\t");
		stream.print(StringUtils.makeExcelCellFromCollection(registers));

	}

	private void printDDCCrissCrossSWW(
			final Collection<Triplett<String, String, String>> sww) {
		final List<String> strings = new LinkedList<String>();
		if (sww != null)
			for (final Triplett<String, String, String> sw : sww) {
				strings.add(sw.first + " " + sw.second + ", Det: " + sw.third);
			}
		stream.print("\t");
		stream.print(StringUtils.makeExcelCellFromCollection(strings));

	}

	public static void main(final String[] args) throws IOException {
		Kochbuecher kochbuecher = null;
		try {
			kochbuecher = new Kochbuecher();
		} catch (final SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// vorab nach 1131 filtern (013D):
		final Predicate<String> titleFilter = new StringContains(
				Constants.RS + "013D " + Constants.US);
		kochbuecher.setStreamFilter(titleFilter);

		System.err.println("Titeldaten flöhen:");
		try {
			kochbuecher.processGZipFile("D:/Normdaten/DNBtitelgesamt.dat.gz");
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

}
