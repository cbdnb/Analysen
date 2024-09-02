package baumann.ddc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.marc.DDCMarcUtils;

public class SucheDDCNummer {

	public static void main(final String[] args) throws FileNotFoundException,
			IOException, ClassNotFoundException, SQLException {

		final InputStream input = new FileInputStream(
				"Z:/cbs_sync/ddc/ddc_zap.xml");
		final MarcReader marcReader = new MarcXmlReader(input);

		while (marcReader.hasNext()) {
			final Record record = marcReader.next();

			final String name = DDCMarcUtils.getCaption(record);

			if (DDCMarcUtils.isAddInstructionOld(record)) {

				final String number = DDCMarcUtils
						.getFullClassificationNumber(record);
				System.out
						.println(StringUtils.concatenate(" / ", name, number));
				System.out.println(record);
				System.out.println();
			}

		}

		MyFileUtils.safeClose(input);

	}
}
