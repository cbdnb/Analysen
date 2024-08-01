/**
 *
 */
package langer.instrumentDesJahres;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.basics.collections.RankingQueue;
import de.dnb.gnd.utils.IDNUtils;

/**
 * @author baumann
 *
 */
public class EinspielungenVonMusikerInnen {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args) throws IOException {

		final PrintWriter pWriter = FileUtils.oeffneAusgabeDatei(Utils.OUT_FILE,
				true);

		for (int i = 0; i < Utils.BERUFE_IDNS.size(); i++) {
			final String beruf = Utils.BERUFE.get(i);
			final String berufIDN = Utils.BERUFE_IDNS.get(i);
			final Set<Integer> personenMitBerufInstrumentalist = Utils
					.getPers2beruf().searchKeys(IDNUtils.idn2int(berufIDN));

			final Set<Integer> titeldatenMitMusikern = Utils.getTitel2musiker()
					.searchKeys(personenMitBerufInstrumentalist);
			pWriter.println("Einspielungen mit Beteiligung von " + beruf + ": "
					+ titeldatenMitMusikern.size());
			pWriter.println(
					CollectionUtils.shortView(titeldatenMitMusikern, 5));

			final RankingQueue<Integer> personenMitMeistenEinspielungen = Utils
					.machRangfolge(Utils.getTitel2musiker(),
							personenMitBerufInstrumentalist);
			pWriter.println(
					"Rangfolge " + beruf + " mit den meisten Einspielungen:");
			pWriter.println(personenMitMeistenEinspielungen);
			pWriter.println();
			pWriter.println();
		}

		FileUtils.safeClose(pWriter);

	}

}
