package baumann.ddc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.dnb.basics.collections.CrossProductMultimap;
import de.dnb.basics.collections.SamplingMultimap;
import de.dnb.basics.utils.PoiUtils;
import de.dnb.basics.utils.PortalUtils;
import de.dnb.gnd.parser.Format;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.gnd.utils.BibRecUtils.REIHE;

public class StichprobeDDCVerbalisierung {

	static final int SAMPLE_SIZE = 50;
	private static final String DATENABZUG = "D:/Normdaten/DNBtitelgesamt.dat.gz";

	static final CrossProductMultimap<Record> DHS_REIHE_2_RECORDS = new SamplingMultimap<>(
			SAMPLE_SIZE);
	private static final Path HERNANDEZ_FILE = FileSystems.getDefault()
			.getPath("D:/Normdaten/ddc_appr.ppns.txt");
	private static Set<String> idnsProcessed;

	private static final String MYDIR = "D:/temp/ddcVerb/";
	private static final String EXCEL_MUSTER = MYDIR + "Muster.xlsx";

	private static final String SGG_EXCEL_DIR = MYDIR + "sggFiles/";

	static final int IDN = 0;
	static final int TITEL = 1;
	static final int RSWK = 2;
	static final int DDC = 3;
	static final int SWW_VERBAL = 4;
	static final int BEWERTUNG = 5;
	static final int BEMERKUNG = 6;

	static void processRecord(final Record record) {
		final String idn = record.getId();
		if (idn == null)
			return;

		final String dhs = SGUtils.getFullDHSString(record, null);
		if (dhs == null)
			return;

		final REIHE reihe = BibRecUtils.getReihe(record);
		if (reihe == null)
			return;

		final ArrayList<Line> ddclines = SubjectUtils
				.getDDCMainScheduleLines(record);
		if (ddclines.isEmpty())
			return;

		DHS_REIHE_2_RECORDS.addValue(record, dhs, reihe);

	}

	public static void main(final String[] args) throws IOException {

		idnsProcessed = new HashSet<>(Files.readAllLines(HERNANDEZ_FILE));

		final RecordReader recordReader = RecordReader
				.getMatchingReader(DATENABZUG);
		recordReader.setStreamFilter(new ContainsTag("5400", BibTagDB.getDB()));
		//@formatter:off
		recordReader
			.stream()
			.filter(rec -> idnsProcessed.contains(rec.getId()))
//			.limit(300)
			.forEach(StichprobeDDCVerbalisierung::processRecord);
		//@formatter:on

		processSamples();

	}

	/**
	 * @param recs
	 * @param dhs
	 * @param reihe
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void processSamples()
			throws FileNotFoundException, IOException {

		final Collection<String> sgs = SGUtils.allDHSasString();

		for (final String dhs : sgs) {

			final XSSFWorkbook workbook = new XSSFWorkbook(
					new FileInputStream(new File(EXCEL_MUSTER)));
			final File dhs_excel = new File(
					SGG_EXCEL_DIR + "Stichprobe_" + dhs + ".xlsx");

			final List<REIHE> rlist = Arrays.asList(REIHE.values());
			rlist.forEach(reihe ->
			{
				final Sheet sourceSheet = workbook
						.getSheet("Reihe " + reihe.name());
				final CellStyle cs = workbook.createCellStyle();
				cs.setWrapText(true);
				final CreationHelper helper = workbook.getCreationHelper();

				final Iterable<Record> recs = DHS_REIHE_2_RECORDS
						.getNullSafe(dhs, reihe);
				int i = 0;
				for (final Record record : recs) {

					i++;

					final String idn = record.getId();
					Cell cell = PoiUtils.getCell(sourceSheet, i, IDN);
					cell.setCellStyle(cs);
					cell.setCellValue(idn);

					final String title = BibRecUtils.getMainTitle(record);
					cell = PoiUtils.getCell(sourceSheet, i, TITEL);
					cell.setCellStyle(cs);
					final Hyperlink hyperlink = helper
							.createHyperlink(HyperlinkType.URL);
					hyperlink.setAddress(PortalUtils.getPortalUriString(idn));
					cell.setHyperlink(hyperlink);
					cell.setCellValue(title);

					final Collection<Line> rswks = SubjectUtils
							.getAllRSWKLines(record);
					final String rswk = RecordUtils.toPica(rswks, Format.PICA3,
							true, "\r\n", '$');
					cell = PoiUtils.getCell(sourceSheet, i, RSWK);
					cell.setCellStyle(cs);
					cell.setCellValue(rswk);

					final Collection<Line> ddcs = SubjectUtils
							.getDDCSegment(record);
					final String ddc = RecordUtils.toPica(ddcs, Format.PICA3,
							true, "\r\n", '$');
					cell = PoiUtils.getCell(sourceSheet, i, DDC);
					cell.setCellStyle(cs);
					cell.setCellValue(ddc);
				}
			});

			final FileOutputStream out = new FileOutputStream(dhs_excel);
			workbook.write(out);
			workbook.close();
			out.close();

		}
		;

	}

}
