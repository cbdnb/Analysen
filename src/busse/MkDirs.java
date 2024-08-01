/**
 *
 */
package busse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.dnb.basics.utils.PoiUtils;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.SGUtils;

/**
 * @author baumann
 *
 */
public class MkDirs {

	/**
	 *
	 */
	private static final String EXCEL_MUSTER = "//DNBF-NSC1-P01/DNB-Gesamt"
			+ "/03_FB_EE/11_AEN/03_Erschliessungsverfahren"
			+ "/Klassifikation/Maschinelle_Kurznotationen"
			+ "/MK_Beispiel/MK_Beispiel.xlsx";

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final File busse = new File("d:/temp/busse");
		busse.mkdirs();

		final Collection<String> sgs = SGUtils.allDHSasString();
		for (final String sg : sgs) {
			System.err.println(sg);
			if (sg.equals("K"))
				return;
			if (sg.equals("S"))
				return;
			final File mk_sg = new File(busse, "MK_" + sg);
			mk_sg.mkdirs();
			new File(mk_sg, "01_Statistik").mkdirs();
			new File(mk_sg, "02_Trainingskorpora").mkdirs();
			new File(mk_sg, "03_Testkorpus").mkdirs();
			new File(mk_sg, "04_Ausschlusslisten").mkdirs();
			new File(mk_sg, "05_Mappingdateien").mkdirs();
			new File(mk_sg, "06_Test").mkdirs();
			new File(mk_sg, "07_Datenanalyse").mkdirs();
			try {
				final File excelF = new File(mk_sg, "MK_" + sg + ".xlsx");

				final Workbook workbook = new XSSFWorkbook(
						new FileInputStream(new File(EXCEL_MUSTER)));

				final Sheet sourceSheet = workbook
						.getSheet("nur die in 5401 zugelassenen");
				final Sheet targetSheet = workbook.getSheet("V_0.1");
				for (final Row sourceRow : sourceSheet) {
					final String ddc = sourceRow.getCell(0)
							.getStringCellValue();
					final String sg2ddc = SGUtils.getSGNullSafe(ddc.trim())
							.map(DDC_SG::getDDCString).orElse("");
					if (Objects.equals(sg2ddc, sg)) {
						PoiUtils.append(targetSheet, sourceRow);
					}
				}
				final FileOutputStream out = new FileOutputStream(excelF);
				workbook.write(out);
				workbook.close();
				out.close();

			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final EncryptedDocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		;

	}

}
