package busse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.marc.DDCMarcUtils;

public class DDC610List {

	public static void main(final String[] args) throws FileNotFoundException,
			IOException, ClassNotFoundException, SQLException {
		final InputStream input = new FileInputStream(
				"Z:/cbs_sync/ddc/ddc_zap.xml");
		final MarcReader marcReader = new MarcXmlReader(input);

		while (marcReader.hasNext()) {
			final Record record = marcReader.next();
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
			final String name = DDCMarcUtils.getCaption(record);
			/*
			 * Ist nötig, da manche seltsamen Daten herumfliegen. Sie haben eine
			 * Nummer, keinen Namen und diverse 953-Felder. Diese beschreiben
			 * offensichtlich die Unterbegriffe, die folgerichtig auch in
			 * WebDewey angezeigt werden. Eventuell muss man das programmieren.
			 */
			// if (name == null)
			// continue;
			if (record.getVariableField("953") != null)
				continue;
			final String number = DDCMarcUtils
					.getFullClassificationNumber(record);
			if (!number.startsWith("61"))
				continue;

			System.out.println(StringUtils.concatenate("\t", number, name));
			// System.out.println(MarcUtils.readableFormat(record));
		}

		FileUtils.safeClose(input);

	}
}
