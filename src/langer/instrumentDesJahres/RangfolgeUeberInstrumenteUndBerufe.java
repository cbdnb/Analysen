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
 *
 *
 */
public class RangfolgeUeberInstrumenteUndBerufe {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args) throws IOException {

		final PrintWriter pWriter = FileUtils.oeffneAusgabeDatei(Utils.OUT_FILE,
				true);

		// Erstmal die einfache Sache: Rangliste Werke mit dem Instrument

		System.err.println("Verwandte und Unterbegriffe suchen");
		final Set<Integer> instrumenteUndUBB = Utils
				.findeAlleUnterbegriffeZuInstrumenten(Utils.INSTRUMENTE_IDNS);

		pWriter.println(Utils.INSTRUMENTE_NAMEN + Utils.UND_UNTERBEGRIFFE
				+ instrumenteUndUBB.size());
		pWriter.println(CollectionUtils.shortView(instrumenteUndUBB));

		// Mit Instrument des Jahres relationierte Werke:
		final Set<Integer> werkeAus382 = Utils.getWerk2Instrument()
				.searchKeys(instrumenteUndUBB);
		final Set<Integer> werkeAus550 = Utils.getub2ob()
				.searchKeys(IDNUtils.idns2ints(Utils.GATTUNGS_IDNS));
		final Set<Integer> werkeMitInstrument = CollectionUtils
				.union(werkeAus382, werkeAus550);
		pWriter.println("Werke mit " + Utils.INSTRUMENTE_NAMEN + ": "
				+ werkeMitInstrument.size());
		pWriter.println(CollectionUtils.shortView(werkeMitInstrument));

		// Rangliste meisteingespielte Werke mit dem Instrument:
		final RankingQueue<Integer> meistEingespielteWerke = Utils
				.machRangfolge(Utils.getTitel2werke(), werkeMitInstrument);
		pWriter.println("Rangfolge meisteingespielte Werke mit "
				+ Utils.INSTRUMENTE_NAMEN + ":");
		pWriter.println(meistEingespielteWerke);
		pWriter.println();

		// Jetzt der kompliziertere Teil: Die Personen,
		// erstmal die, die das Instrument spielen ($4 istr):
		final Set<Integer> personenDieInstrumentSpielen = Utils
				.getPers2Instrument().searchKeys(instrumenteUndUBB);
		final Set<Integer> personenMitInstrumentAlsBeruf = Utils.getPers2beruf()
				.searchKeys(IDNUtils.idns2ints(Utils.BERUFE_IDNS));
		final Set<Integer> personenMitInstrument = CollectionUtils.union(
				personenDieInstrumentSpielen, personenMitInstrumentAlsBeruf);
		pWriter.println(Utils.INSTRUMENTE_NAMEN + "-Spieler: "
				+ personenMitInstrument.size());
		pWriter.println(CollectionUtils.shortView(personenMitInstrument));
		pWriter.println();

		// Von Spielern der Instrumente komponierte Werke:
		final Set<Integer> werkeVonSpielernKomponiert = Utils
				.getWerk2komponist().searchKeys(personenMitInstrument);
		pWriter.println("Werke, komponiert von Personen, die "
				+ CollectionUtils.shortView(Utils.INSTRUMENTE_NAMEN)
				+ " spielen: " + werkeVonSpielernKomponiert.size());
		pWriter.println(CollectionUtils.shortView(werkeVonSpielernKomponiert));
		pWriter.println();

		// Rangliste Komponisten mit meisten Werken:
		final RankingQueue<Integer> personenMitMeistenWerken = Utils
				.machRangfolge(Utils.getWerk2komponist(),
						personenMitInstrument);
		pWriter.println("Rangfolge Komponisten mit meisten Werken:");
		pWriter.println(personenMitMeistenWerken);
		pWriter.println();
		// Titeldatensätze über 321X:
		final Set<Integer> titelMitInstrument = Utils.getTitel2werke()
				.searchKeys(werkeAus382);

		pWriter.println("Anzahl der Titel mit "
				+ CollectionUtils.shortView(Utils.INSTRUMENTE_NAMEN) + ": "
				+ titelMitInstrument.size());
		pWriter.println(CollectionUtils.shortView(titelMitInstrument, 6));
		pWriter.println();
		final Set<Integer> titelMitSpieler = Utils.getTitel2musiker()
				.searchKeys(personenMitInstrument);

		pWriter.println("Anzahl der Titel mit "
				+ CollectionUtils.shortView(Utils.INSTRUMENTE_NAMEN)
				+ "-Spielern: " + titelMitSpieler.size());
		pWriter.println(CollectionUtils.shortView(titelMitSpieler, 6));
		pWriter.println();
		// Rangliste meisteingespielter Instrumentalist:
		final RankingQueue<Integer> personenMitMeistenEinspielungen = Utils
				.machRangfolge(Utils.getTitel2musiker(), personenMitInstrument);
		pWriter.println(
				"Rangfolge Instrumentalist(in) mit meisten Einspielungen:");
		pWriter.println(personenMitMeistenEinspielungen);

		FileUtils.safeClose(pWriter);
	}

}
