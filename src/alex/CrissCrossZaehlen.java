package alex;

import java.io.IOException;

import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;

public class CrissCrossZaehlen extends DownloadWorker {

	long countAll = 0;
	// aaa
	long countValid = 0;

	long countDeprecated = 0;

	@Override
	protected void processRecord(final Record record) {
		countAll += GNDUtils.getAllDDCLines(record).size();
		countValid += GNDUtils.getValidDDCLines(record).size();
		countDeprecated += GNDUtils.getDeprecatedDDCLines(record).size();
	}

	public static void main(final String[] args) throws IOException {
		final CrissCrossZaehlen zaehlen = new CrissCrossZaehlen();
		zaehlen.processGZipFile("D:/Normdaten/DNBGND_Stichprobe.dat.gz");

		System.out.println("Alle: " + zaehlen.countAll);
		System.out.println("Gültige: " + zaehlen.countValid);
		System.out.println("Veraltete: " + zaehlen.countDeprecated);

	}

}
