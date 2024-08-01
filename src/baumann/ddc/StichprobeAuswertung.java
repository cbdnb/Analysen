package baumann.ddc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.basics.utils.DDC_Utils;
import de.dnb.basics.utils.NumberUtils;
import de.dnb.basics.utils.PoiUtils;
import de.dnb.gnd.utils.BibRecUtils.REIHE;
import de.dnb.gnd.utils.DDC_SG;

public class StichprobeAuswertung {

	private static final String MAINDIR = "V:/Projekte/DDC-Verbalisierung/";

	private static final String SGG_EXCEL_DIR = MAINDIR
			+ "06_Feld 5550/test_IE/Dateien/";

	private static CrossProductFrequency globalFrequency = new CrossProductFrequency();

	private static CrossProductFrequency referatFrequency = new CrossProductFrequency();

	private static CrossProductFrequency zehnerFrequency = new CrossProductFrequency();

	private static CrossProductFrequency sgFrequency = new CrossProductFrequency();

	private static List<REIHE> reihen;

	private static final String GUTE = "gute (3/4)";

	private static final String GESAMT = "gesamt";

	private static List<Integer> bewertungen = Arrays.asList(1, 2, 3, 4);

	public static void main(final String[] args) throws IOException {
		reihen = new LinkedList<>(Arrays.asList(REIHE.values()));
		reihen.remove(REIHE.O);

		makeStatistics();

		System.out.println(globalFrequency);

		reihen.forEach(reihe ->
		{
			System.out.println();
			System.out.println();
			bewertungen.forEach(bewertung ->
			{
				final String zeilenName = reihe + ", " + bewertung;
				System.out.println(StringUtils.concatenate("\t", zeilenName,
						referatFrequency.get(
								DDC_SG.REFERATE.HUMANITIES, reihe, bewertung),
						"",
						referatFrequency.get(DDC_SG.REFERATE.SOCIAL_SCIENCES,
								reihe, bewertung),
						"", referatFrequency.get(DDC_SG.REFERATE.STM, reihe,
								bewertung)));
			});
		});

		reihen.forEach(reihe ->
		{
			System.out.println();
			System.out.println();
			bewertungen.forEach(bewertung ->
			{
				String zeile = reihe + ", " + bewertung;
				final List<String> zehnerL = DDC_Utils.getMainClasses();
				for (final String zehner : zehnerL) {
					final Long count = zehnerFrequency.get(zehner, reihe,
							bewertung);
					zeile += "\t\t" + count;
				}
				System.out.println(zeile);
			});
		});

		final Collection<DDC_SG> sgs = DDC_SG.enumSet();

		reihen.forEach(reihe ->
		{
			System.out.println();
			System.out.println(reihe);

			sgs.forEach(sg ->
			{
				final Long gute = sgFrequency.get(sg, reihe, GUTE);
				final Long gesamt = sgFrequency.get(sg, reihe, GESAMT);

				double proz;
				if (gesamt == 0L)
					proz = 0;
				else
					proz = (double) gute / (double) gesamt;
				final String prozS = NumberFormat.getInstance().format(proz);
				System.out.println(StringUtils.concatenate("\t",
						sg.getDDCString() + ": " + sg.getDescription(), prozS,
						gute, gesamt));
			});
		});

		System.out.println();
		System.out.println("Rangfolge total");

		sgs.forEach(sg ->
		{
			final Long gute = sgFrequency.get(sg, GUTE);
			final Long gesamt = sgFrequency.get(sg, GESAMT);

			double proz;
			if (gesamt == 0L)
				proz = 0;
			else
				proz = (double) gute / (double) gesamt;
			final String prozS = NumberFormat.getInstance().format(proz);
			System.out.println(StringUtils.concatenate("\t",
					sg.getDDCString() + ": " + sg.getDescription(), prozS, gute,
					gesamt));
		});

	}

	/**
	 * @param recs
	 * @param dhs
	 * @param reihe
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void makeStatistics() {

		final Collection<DDC_SG> sgs = DDC_SG.enumSet();

		for (final DDC_SG sg : sgs) {

			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(new FileInputStream(
						new File(SGG_EXCEL_DIR + "Stichprobe_"
								+ sg.getDDCString() + ".xlsx")));
			} catch (final IOException e) {
				continue;
			}

			for (final REIHE reihe : reihen) {

				final Sheet sourceSheet = workbook.getSheet(reihe.toString());

				final int firstRow = 1;
				for (int i = firstRow; i < firstRow
						+ StichprobeDDCVerbalisierung.SAMPLE_SIZE; i++) {
					final Cell cell = PoiUtils.getCell(sourceSheet, i,
							StichprobeDDCVerbalisierung.BEWERTUNG);
					final String cellContent = cell.toString().trim();
					final Optional<Integer> bewertungOpt = NumberUtils
							.getFirstArabicInt(cellContent);
					bewertungOpt.ifPresent(bewertung ->
					{

						globalFrequency.addValues(reihe, bewertung);
						referatFrequency.addValues(sg.getReferat(), reihe,
								bewertung);
						final String ddc = sg.getDDCString();
						final String zehner = DDC_Utils.getDDCMainClass(ddc);
						zehnerFrequency.addValues(zehner, reihe, bewertung);

						sgFrequency.addValues(sg, reihe, GESAMT);
						sgFrequency.addValues(sg, GESAMT);
						if (bewertung.equals(3) || bewertung.equals(4)) {
							sgFrequency.addValues(sg, reihe, GUTE);
							sgFrequency.addValues(sg, GUTE);
						}
					});
				}
			}

			FileUtils.safeClose(workbook);

		}
		;

	}

}
