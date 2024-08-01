package henze;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubjectUtils;

public class StatistikMedienwerke1 extends DownloadWorker {

	static Frequency<String> swFreq = new Frequency<>();
	final static String RSWK = "mit RSWK-Schlagwörtern";
	final static String GND_FREMD = "GND aus Fremddaten";
	final static String SWW_ALT = "alte Schlagwörter";
	final static String DESC_FREMD = "Deskriptor aus Fremddaten";
	final static String SWW_AUTOM = "automatische verg. Schlagwörter";
	final static String OHNE_SW = "ohne Schlagwörter";

	static Frequency<String> sgFreq = new Frequency<>();

	final static String MIT_SG = "mit Sachgruppe";

	final static String OHNE_SG = "ohne Sachgruppe";

	static Frequency<String> ddcFreq = new Frequency<>();

	final static String MIT_DDC = "mit DDC";

	final static String OHNE_DDC = "ohne DDC";

	/**
	 * Position 1 von 0500
	 */
	//@formatter:off
	private static List<Character> korrekteGattung =
			Arrays.asList(	'A', /* Druckschrift */
							'C', /*Blindenschriftträger */
							'E', /* Mikroform */
							'S'  /* Elektronische Ressource auf Datenträger
									(einschließlich Compact	Discs)*/);

	/**
	 * Position 2 von 0500
	 */
	private static List<Character> falscheErscheinungsform =
			Arrays.asList(	'f', /* Unselbstständiger Teil (Band, Heft)
									eines mehrbändigen begrenzten
									Werkes oder einer Zeitschrift */
							'l', /* Unselbstständiger Teil (z.B. Sammelbandbeitrag)
									einer einteiligen Ressource, Zeitschriftenartikel
									oder Zeitschriftenheft, unselbstständige
									Werke der Musik */
							'v'); /* Verkürzte Bandaufführung in Form eines Untersatzes
									mit Hinweis auf die Ordnungsblöcke der betreffenden
									Stücktitelaufnahmen (in den Altdaten der DNB
									Frankfurt, Bibliografie-Jahrgänge 1972-1984,
									erstes Halbjahr und in den Altdaten des DMA,
									Bibliografie-Jahrgänge 1976-1996) */

	/**
	 * Position 3 von 0500
	 */
	private static List<Character> falscherStatus =
			Arrays.asList(	'a', /* Provisorischer Datensatz (Bestelldatensatz)*/
							'B', /* temporäre Kennzeichnung einer mögliche Dublette (s. 1698)*/
							'c', /* Datensatz Neuerscheinungsdienst (ND)*/
							'd', /* Hochschulschriften der Bibliografie-Jg. 1972-1993
									der Reihe H*/
							'f', /* Fremddaten (z.B. Online-Ressourcen Geschäftsgang NP,
									Edition Corvey*/
							'g', /* Konvertierte Daten der Hochschulschriften (RHS)
									1945-1970*/
							'i', /* Datensatz für EP-Image (Bereitstellungssystem,
									nur in Verbindung mit
									2. Position = "l")*/
							'm', /* Mahnung*/
							'q', /* Bibliografische Meldung, kein Exemplar vorhanden*/
							'v', /* Korrekturberechtigungsstatus im ZDB-Bestand*/
							'w'); /* Konvertierte Daten der Handbibliothek des DMA;
									 bei 1. Pos. G oder M:
									 Primärkatalogisierung nicht bibliografierelevant*/

	/**
	 * Position 4 von 0500
	 */
	private static List<Character> falscheZuordnung =
			Arrays.asList(	'h', /* Historischer Tonträger*/
							'l', /* Leihmaterial (Bonner Katalog)*/
							'm', /* DMA*/
							's', /* Datensatz ohne Bestand in DNB*/
							'o', /* Zeitschriftenartikel (neu 03/2009)*/
							'z'); /* Datensatz im ZDB-Bestand*/

	/**
	 * Shortcut
	 */
	private static List<String> falscheCodes =
			Arrays.asList(	"rc", /* Karten, Reihe C der DNB */
							"rh", /* Hochschulprüfungsarbeiten, Reihe H der DNB */
							"rb", /* Monografien und Periodica außerhalb des
									Verlagsbuchhandels, Reihe B der DNB*/
							"ro", /* Netzpublikationen ab Bibliografiejahrgang 2010, Reihe O
									der DNB*/
							"rm"); /* Musikalien und Musikschriften, Reihe M der
									  DNB (wird automatisch vergeben)*/


	private static List<String> korrekteCodes =
			Arrays.asList(	"fn", /* zeitungsähnliche Periodika oder früher Zeitung
									(Code wird mit RDA-Umstieg nicht mehr vergeben)*/
							"li", /* Hinweis auf weiterführende Literaturangaben - Altdaten*/
							"lo", /* Loseblattausgaben*/
							"mc", /* Mikroformen - Altdaten*/
							"öb", /* relevant für Öffentliche Bibliotheken - Altdaten*/
							"pn", /* andere Ausgabe*/
							"pb", /* parallele Ausgaben*/
							"ra", /* Monografien und Periodica des Verlagsbuchhandels,
									 Reihe A der DNB*/
							"rg", /* Kennzeichnung fremdsprachiger Germanica: bis Ende
									 2003 Reihe G der DNB, Teil 1, ab 2004 Reihe A*/
							"ru", /* Übersetzungen deutschsprachiger Werke: bis Ende
									 2003 Reihe G der DNB, Teil 2, ab 2004 Reihe A*/
							"vt", /* verfilmte Tageszeitung*/
							"zs", /* Zeitschrift und zeitschriftenartige Reihe*/
							"zt"); /*Zeitung*/
	//@formatter:on

	public static boolean istKorrekteReihe(final Record record) {

		if (BibRecUtils.istHochschulschrift(record))
			return false;

		final char c1 = RecordUtils.getDatatypeCharacterAt(record, 0);
		if (!korrekteGattung.contains(c1))
			return false;

		final char c2 = RecordUtils.getDatatypeCharacterAt(record, 1);
		if (falscheErscheinungsform.contains(c2))
			return false;

		final char c3 = RecordUtils.getDatatypeCharacterAt(record, 2);
		if (falscherStatus.contains(c3))
			return false;

		final char c4 = RecordUtils.getDatatypeCharacterAt(record, 3);
		if (falscheZuordnung.contains(c4))
			return false;

		final List<String> codeAngaben = BibRecUtils.getCodes(record);
		// leere 0600 werden toleriert, da bei alten Titeln denkbar
		if (!codeAngaben.isEmpty()) {

			for (final String code : codeAngaben) {
				if (falscheCodes.contains(code))
					return false;
			}

			for (final String code : codeAngaben) {
				if (korrekteCodes.contains(code))
					return true;
			}
			return false;
		}

		// alte Titel haben keine 0600, Hochschulschriften
		// aber eine 4204:
		if (!RecordUtils.containsField(record, "4204"))
			return true;

		return false;
	}

	@Override
	protected void processRecord(final Record record) {
		if (!istKorrekteReihe(record))
			return;

		if (SubjectUtils.containsDDC(record)) {
			ddcFreq.add(MIT_DDC);
			// System.err.println("ddc: " + record.getId());
		} else
			ddcFreq.add(OHNE_DDC);

		if (SubjectUtils.containsDHS(record))
			sgFreq.add(MIT_SG);
		else
			sgFreq.add(OHNE_SG);

		if (SubjectUtils.containsRSWK(record))
			swFreq.add(RSWK);
		else if (SubjectUtils.containsExternalGND(record))
			swFreq.add(GND_FREMD);
		else if (SubjectUtils.containsOldSWW(record))
			swFreq.add(SWW_ALT);
		else if (SubjectUtils.containsExternalDescriptor(record))
			swFreq.add(DESC_FREMD);
		else if (SubjectUtils.containsAutomaticSWW(record))
			swFreq.add(SWW_AUTOM);
		else
			swFreq.add(OHNE_SW);

	}

	public static void main(final String[] args) throws IOException {

		final StatistikMedienwerke1 medienwerke = new StatistikMedienwerke1();

		medienwerke.gzipSettings();
		final Predicate<String> streamFilter = new ContainsTag("0500", '0', "A",
				BibTagDB.getDB())
						.or(new ContainsTag("0500", '0', "C", BibTagDB.getDB()))
						.or(new ContainsTag("0500", '0', "E", BibTagDB.getDB()))
						.or(new ContainsTag("0500", '0', "S",
								BibTagDB.getDB()));
		medienwerke.setStreamFilter(streamFilter);

		try {
			medienwerke.processFile(Constants.GND_TITEL_GESAMT_D);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		System.out.println(swFreq);
		System.out.println(sgFreq);
		System.out.println(ddcFreq);

	}

}
