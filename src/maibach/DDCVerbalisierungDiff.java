package maibach;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

import utils.Database;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.marc.DDCMarcUtils;

public class DDCVerbalisierungDiff {

	/**
	 * Was wurde früher akzeptiert?
	 * 
	 * @param record
	 * @return
	 */
	public static boolean isAcceptedOld(final Record record) {

		if (!DDCMarcUtils.isDDCRecord(record))
			return false;
		if (DDCMarcUtils.isOverview(record))
			return false;
		if (DDCMarcUtils.isSpan(record))
			return false;
		// if (DDCMarcUtils.isSynthesizedNumber(record))
		// continue; // ?
		if (!DDCMarcUtils.isDisplayedInStandardSchedulesOrTables(record))
			return false;
		if (DDCMarcUtils.isAddInstructionOld(record))
			return false;
		final String name = DDCMarcUtils.getCaption(record);
		if (name == null)
			return false;

		return true;

	}

	public static void main(final String[] args) throws FileNotFoundException,
			IOException, ClassNotFoundException, SQLException {
		final Database database = new Database();
		final Frequency<String> frequency = new Frequency<>();

		final InputStream input = new FileInputStream(
				"Z:/cbs_sync/ddc/ddc_zap.xml");
		final MarcReader marcReader = new MarcXmlReader(input);

		while (marcReader.hasNext()) {
			final Record record = marcReader.next();
			final boolean accOld = isAcceptedOld(record);
			final boolean accNew = DDCMarcUtils.isUsedInWinIBW(record);
			// weiter, wenn früher verworfen und heute akzeptiert:
			final boolean diff = !accOld && accNew;
			if (!diff)
				continue;
			final String number = DDCMarcUtils
					.getFullClassificationNumber(record);

			final List<String> indexTerms = DDCMarcUtils
					.getFullIndexTerms(record);
			final Collection<Pair<String, String>> crissCross = database
					.getCrissCrossHighDet(number);
			frequency.increment(number, indexTerms.size());

			int crissCrossSize;
			if (crissCross == null)
				crissCrossSize = 0;
			else
				crissCrossSize = crissCross.size();
			final String name = DDCMarcUtils.getCaption(record);

			System.out.println(StringUtils.makeExcelLine(number, name,
					indexTerms, indexTerms.size(), crissCross, crissCrossSize,
					database.getTitleIDsForDDC(number).size()));
		}

		MyFileUtils.safeClose(input);

	}
}
