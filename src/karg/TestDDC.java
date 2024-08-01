package karg;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;

import de.dnb.basics.marc.DDCMarcUtils;

public class TestDDC {

	public static void main(final String[] args) throws FileNotFoundException {
		final String FILENAME_DDC_XML = "Z:/cbs_sync/ddc/ddc.xml";

		final InputStream input = new FileInputStream(FILENAME_DDC_XML);
		final MarcReader marcReader = new MarcXmlReader(input);

		while (marcReader.hasNext()) {
			final org.marc4j.marc.Record record = marcReader.next();
			if (!DDCMarcUtils.isDDCRecord(record))
				continue;
			if (DDCMarcUtils.isOverview(record))
				continue;
			if (DDCMarcUtils.isSpan(record))
				continue;
			if (DDCMarcUtils.isSynthesizedNumber(record))
				continue; // ?
			if (DDCMarcUtils.isAddInstructionOld(record))
				continue;
			final String number = DDCMarcUtils
					.getFullClassificationNumber(record);
			final String caption = DDCMarcUtils.getCaption(record);
			if (caption == null)
				continue;

			if (number.equals("T2--6")) {
				System.out.println(record);
				System.out.println("---------------");
			}

		}

	}

}
