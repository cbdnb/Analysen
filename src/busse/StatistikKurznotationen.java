/**
 *
 */
package busse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.TrieFrequency;
import de.dnb.basics.tries.TST;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.SGUtils;

/**
 * @author baumann
 *
 */
public class StatistikKurznotationen {

	/**
	 *
	 */
	private static final String VERSION = "1.0";

	static final String PARENT_PATH = "V:/03_FB_EE/11_AEN/"
			+ "03_Erschliessungsverfahren/Klassifikation/"
			+ "Maschinelle_Kurznotationen/Vorrat";

	static final String TEST_PATH = "D:/temp/busse";

	static final boolean DEBUG = true;

	private static File parentFolder;

	private static TrieFrequency FREQ_ABRIDGED = new TrieFrequency();

	private static Frequency<String> FREQ_5400_2_Titles;

	private static final String FILENAME_5400_2_Titles = "D:/Normdaten/5400toTitles.out";

	private static final TreeMap<String, TreeMap<String, Long>> SG_2_ABR = new TreeMap<>();

	private static final TST<String> ALTERNATIVE = new TST<>();

	private static final String ARROW = "->";

	/**
	 * Alle Sachgruppen, die Herr Busse mir übriggelassen hat.
	 */
	private static Collection<String> SGS;

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String... args)
			throws ClassNotFoundException, IOException {
		System.err.println("1. einlesen");
		parentFolder = makeParentPath();
		FREQ_ABRIDGED.setErrorFN(ddc ->
		{
			final String alternative = ALTERNATIVE.getValueOfLongestPrefix(ddc);
			final long count = FREQ_5400_2_Titles.get(ddc);
			FREQ_ABRIDGED.get(alternative);
			if (FREQ_ABRIDGED.containsKey(alternative)) {
				FREQ_ABRIDGED.increment(alternative, count);
			} else
				System.err.println("konnte nichts tun mit: " + ddc);
		});
		readAbridged();

		System.out.println(FREQ_ABRIDGED);

		System.err.println("2. Titelstatistik erstellen");
		FREQ_5400_2_Titles = new Frequency<>(FILENAME_5400_2_Titles);

		FREQ_5400_2_Titles.forEach(ddc ->
		{
			if (ddc != null) {
				final String trimmed = ddc.trim();
				final long count = FREQ_5400_2_Titles.get(trimmed);
				FREQ_ABRIDGED.increment(trimmed, count);
			}
		});

		System.err.println("3. verteilen auf Sachgruppen");

		SGS.forEach(sg ->
		{
			final TreeMap<String, Long> sgMap = new TreeMap<>();
			SG_2_ABR.put(sg, sgMap);
		});

		FREQ_ABRIDGED.forEach(abr ->
		{
			final long count = FREQ_ABRIDGED.get(abr);
			final String sg = SGUtils.getSGNullSafe(abr).map(DDC_SG::getDDCString)
					.orElse("");
			final TreeMap<String, Long> sgMap = SG_2_ABR.get(sg);
			if (sgMap != null) {
				sgMap.put(abr, count);
			}
		});

		System.err.println("4. Abspeichern");
		saveStatistics();

	}

	/**
	 * @throws IOException
	 *
	 */
	private static void saveStatistics() {
		SGS.forEach(sg ->
		{
			try {
				saveExcel(sg);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});

	}

	/**
	 * @param sg
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void saveExcel(final String sg)
			throws FileNotFoundException, IOException {
		final TreeMap<String, Long> list = SG_2_ABR.get(sg);

		final Workbook workbook = new XSSFWorkbook();
		final Sheet sheet = workbook.createSheet();
		// set the sheet name
		workbook.setSheetName(0, VERSION);
		int i = 0;
		Row row = sheet.createRow(i);
		Cell cell0 = row.createCell(0);
		Cell cell1 = row.createCell(1);
		cell0.setCellValue("Kurznotation");
		cell1.setCellValue("Anzahl");
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		for (final String abbr : list.keySet()) {
			i++;
			final long count = list.get(abbr);
			row = sheet.createRow(i);
			cell0 = row.createCell(0);
			cell1 = row.createCell(1);
			cell0.setCellValue(abbr);
			cell1.setCellType(CellType.NUMERIC);
			cell1.setCellValue(count);
		}

		// erste Zeile fixieren:
		sheet.createFreezePane(0, 1);

		final File outFile = new File(makeSGFolder(sg),
				"SG_" + sg + "_Statistik_Abridged.xlsx");
		final FileOutputStream out = new FileOutputStream(outFile);
		workbook.write(out);
		workbook.close();
		out.close();
	}

	/**
	 *
	 */
	private static void readAbridged() {

		SGS = SGUtils.allDHSasString();
		SGUtils.allDHSasString().forEach(sg ->
		{

			final File sgFolder = makeSGFolder(sg);
			final File sourceFile = new File(sgFolder, makeSourceFilename(sg));
			if (sourceFile.exists()) {
				Workbook workbook = null;
				try {
					workbook = new XSSFWorkbook(
							new FileInputStream(sourceFile));

				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				final Sheet sourceSheet = workbook.getSheet("V_0.2");
				sourceSheet.forEach(row ->
				{
					final Cell cell = row.getCell(0);
					if (cell != null) {
						final String cellContent = cell.toString();

						String[] pair;
						if (cellContent.contains(ARROW)) {
							pair = cellContent.split(ARROW);
							ALTERNATIVE.put(pair[0].trim(), pair[1].trim());
						} else {
							String abridged = cellContent.trim();
							if (isMainDDC(abridged)) {
								if (abridged.contains(".")) {
									// 0 am Ende weg:
									final String truncatet = abridged
											.replaceAll("(\\.)?0*$", "");
									abridged = truncatet;
								}
								FREQ_ABRIDGED.addKey(abridged);
							}
						}
					}
				});
				try {
					workbook.close();
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				// Quelldatei existiert nicht
				SGS.remove(sg);
			}
		});

	}

	/**
	 * @return
	 */
	private static File makeParentPath() {
		if (DEBUG) {
			return new File(TEST_PATH);
		} else
			return new File(PARENT_PATH);
	}

	/**
	 * @param sg
	 * @return
	 */
	private static String makeSourceFilename(final String sg) {
		return "MK_" + sg + ".xlsx";
	}

	static String DDC_PAT_S = "\\d\\d\\d(\\.\\d+)?";

	static Pattern DDC_PAT = Pattern.compile(DDC_PAT_S);

	/**
	 * @param abridged
	 * @return
	 */
	private static boolean isMainDDC(final String abridged) {
		final Matcher matcher = DDC_PAT.matcher(abridged);
		return matcher.matches();
	}

	/**
	 * @param sg
	 * @return
	 */
	private static File makeSGFolder(final String sg) {
		return new File(parentFolder, "MK_" + sg);
	}

}
