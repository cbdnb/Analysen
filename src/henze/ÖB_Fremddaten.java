package henze;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.StatusAndCodeFilter;
import de.dnb.gnd.utils.SubjectUtils;

public class ÖB_Fremddaten extends DownloadWorker {

	private static CrossProductFrequency data = new CrossProductFrequency();
	final static String RSWK = "RSWK";
	final static String DESC = "mindestens Descriptoren";
	final static String THEMA = "mindestens Thema-Klass.";
	final static String OHNE = "ohne Sacherschließung";

	final private static Map<String, String> ID_2_BESCHREIBUNG = new LinkedHashMap<>();

	//@formatter:on

	@Override
	protected void processRecord(final Record record) {

		final List<String> inhaltIDs = BibRecUtils
				.getNatureOfContentIDs(record);
		inhaltIDs.addAll(BibRecUtils.getIntendedAudienceIDs(record));

		// Schnittmenge:
		inhaltIDs.retainAll(ID_2_BESCHREIBUNG.keySet());

		if (inhaltIDs.isEmpty())
			return;

		final StatusAndCodeFilter filter = StatusAndCodeFilter
				.reiheA_selbststaendig();
		if (!filter.test(record))
			return;

		if (BibRecUtils.istHochschulschrift(record, false))
			return;

		final String jahr = BibRecUtils.getYearOfPublicationString(record);
		if (jahr == null)
			return;

		inhaltIDs.forEach(id ->
		{
			final String beschreibung = ID_2_BESCHREIBUNG.get(id);
			data.addValues(beschreibung, jahr);
			if (SubjectUtils.containsRSWK(record)) {
				data.addValues(beschreibung, jahr, RSWK);
			} else if (SubjectUtils.containsExternalDescriptor(record)) {
				data.addValues(beschreibung, jahr, DESC);
			} else if (SubjectUtils.containsThemaClassification(record)) {
				data.addValues(beschreibung, jahr, THEMA);
			} else
				data.addValues(beschreibung, jahr, OHNE);
		});
	}

	private static void output(final CrossProductFrequency frequency) {
		final NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
		ID_2_BESCHREIBUNG.values().forEach(beschreibung ->
		{
			System.out.println(beschreibung);
			System.out.println(StringUtils.concatenate("\t", "Jahr", "Gesamt",
					RSWK, "%", THEMA, "%", DESC, "%", OHNE, "%"));

			for (int i = 2006; i <= 2017; i++) {
				final String jahr = Integer.toString(i);
				final long rswk = frequency.getCount(beschreibung, jahr, RSWK);
				final long thema = frequency.getCount(beschreibung, jahr,
						THEMA);
				final long desc = frequency.getCount(beschreibung, jahr, DESC);
				final long ohne = frequency.getCount(beschreibung, jahr, OHNE);
				final long gesamt = frequency.getCount(beschreibung, jahr);

				final double prozRSWK = (double) rswk / (double) gesamt;
				final double prozThema = (double) thema / (double) gesamt;
				final double prozdesc = (double) desc / (double) gesamt;
				final double prozOhne = (double) ohne / (double) gesamt;

				//@formatter:off
				System.out.println(StringUtils.concatenate("\t",
						jahr, gesamt,
						rswk, format.format(prozRSWK),
						thema, format.format(prozThema),
						desc, format.format(prozdesc),
						ohne, format.format(prozOhne)));
				//@formatter:on
			}
		});
	}

	/**
	 *
	 */
	public ÖB_Fremddaten() {
		//@formatter:off
		ID_2_BESCHREIBUNG.put("041414519", "Adressbuch");
//		ID_2_BESCHREIBUNG.put("960106200", "Altkarte");
//		ID_2_BESCHREIBUNG.put("041423003", "Amtliche Publikation");
		ID_2_BESCHREIBUNG.put("041425278", "Anleitung");
//		ID_2_BESCHREIBUNG.put("041427386", "Antiquariatskatalog");
		ID_2_BESCHREIBUNG.put("040022145", "Anthologie");
//		ID_2_BESCHREIBUNG.put("041427610", "Anzeigenblatt");
//		ID_2_BESCHREIBUNG.put("041433033", "Atlas");
//		ID_2_BESCHREIBUNG.put("041434137", "Aufsatzsammlung");
		ID_2_BESCHREIBUNG.put("041433890", "Aufgabensammlung");
//		ID_2_BESCHREIBUNG.put("04143482X", "Auktionskatalog");
		ID_2_BESCHREIBUNG.put("041354672", "Ausstellungskatalog");
		ID_2_BESCHREIBUNG.put("040039390", "Autobiografie");
//		ID_2_BESCHREIBUNG.put("040039420", "Autograf");
		ID_2_BESCHREIBUNG.put("1071926306", "Backbuch");
//		ID_2_BESCHREIBUNG.put("041443845", "Beispielsammlung");
//		ID_2_BESCHREIBUNG.put("041280229", "Bericht");
//		ID_2_BESCHREIBUNG.put("041449509", "Bestimmungsbuch");
//		ID_2_BESCHREIBUNG.put("040065685", "Bild");
//		ID_2_BESCHREIBUNG.put("040064328", "Bibliografie");
		ID_2_BESCHREIBUNG.put("041453956", "Bildband");
		ID_2_BESCHREIBUNG.put("040066045", "Bilderbuch");
//		ID_2_BESCHREIBUNG.put("040066274", "Bildnis");
//		ID_2_BESCHREIBUNG.put("040068048", "Biografie");
//		ID_2_BESCHREIBUNG.put("041455053", "Bildwörterbuch");
//		ID_2_BESCHREIBUNG.put("042825040", "Blindendruck");
//		ID_2_BESCHREIBUNG.put("041466098", "Briefsammlung");
//		ID_2_BESCHREIBUNG.put("94656860X", "Checkliste");
		ID_2_BESCHREIBUNG.put("040104273", "Comic");
//		ID_2_BESCHREIBUNG.put("04148875X", "Datensammlung");
//		ID_2_BESCHREIBUNG.put("040111199", "Datenbank");
//		ID_2_BESCHREIBUNG.put("040120449", "Diagramm");
//		ID_2_BESCHREIBUNG.put("1071864416", "Diskografie");
//		ID_2_BESCHREIBUNG.put("04127976X", "Drehbuch");
//		ID_2_BESCHREIBUNG.put("041512367", "Einblattdruck");
//		ID_2_BESCHREIBUNG.put("041512782", "Einführung");
//		ID_2_BESCHREIBUNG.put("04152408X", "Entscheidungssammlung");
//		ID_2_BESCHREIBUNG.put("040149862", "Enzyklopädie");
		ID_2_BESCHREIBUNG.put("041332547", "Erlebnisbericht");
//		ID_2_BESCHREIBUNG.put("041534883", "Fachkunde");
//		ID_2_BESCHREIBUNG.put("041535634", "Fahrplan");
//		ID_2_BESCHREIBUNG.put("041536169", "Fallsammlung");
//		ID_2_BESCHREIBUNG.put("955154278", "Fallstudiensammlung");
//		ID_2_BESCHREIBUNG.put("040169286", "Festschrift");
//		ID_2_BESCHREIBUNG.put("040171027", "Film");
//		ID_2_BESCHREIBUNG.put("1071861980", "Filmografie");
		ID_2_BESCHREIBUNG.put("1071854844", "Fiktionale Darstellung");
//		ID_2_BESCHREIBUNG.put("04071280X", "Flugblatt");
//		ID_2_BESCHREIBUNG.put("041547705", "Flugschrift");
//		ID_2_BESCHREIBUNG.put("1098579690", "Forschungsdaten");
//		ID_2_BESCHREIBUNG.put("041550080", "Formelsammlung");
//		ID_2_BESCHREIBUNG.put("04155034X", "Formularsammlung");
//		ID_2_BESCHREIBUNG.put("041550439", "Forschungsbericht");
//		ID_2_BESCHREIBUNG.put("040458954", "Fotografie");
		ID_2_BESCHREIBUNG.put("041555694", "Führer");
//		ID_2_BESCHREIBUNG.put("041556429", "Fundstellenverzeichnis");
//		ID_2_BESCHREIBUNG.put("041565827", "Genealogische Tafel");
//		ID_2_BESCHREIBUNG.put("040207137", "Gespräch");
//		ID_2_BESCHREIBUNG.put("041576330", "Globus");
//		ID_2_BESCHREIBUNG.put("040218457", "Grafik");
//		ID_2_BESCHREIBUNG.put("1032889837", "Graphzine");
//		ID_2_BESCHREIBUNG.put("040232875", "Handschrift");
//		ID_2_BESCHREIBUNG.put("040723496", "Haushaltsplan");
//		ID_2_BESCHREIBUNG.put("940089343", "Hörbuch");
//		ID_2_BESCHREIBUNG.put("040254356", "Hörspiel");
//		ID_2_BESCHREIBUNG.put("041139372", "Hochschulschrift");
//		ID_2_BESCHREIBUNG.put("041607996", "Humoristische Darstellung");
//		ID_2_BESCHREIBUNG.put("040270416", "Inkunabel");
//		ID_2_BESCHREIBUNG.put("040275035", "Interview");
//		ID_2_BESCHREIBUNG.put("04027540X", "Inventar");
		ID_2_BESCHREIBUNG.put("043062520", "Jugendbuch");
		ID_2_BESCHREIBUNG.put("040289338", "Jugendsachbuch");
		ID_2_BESCHREIBUNG.put("040296709", "Karikatur");
//		ID_2_BESCHREIBUNG.put("040292908", "Kalender");
//		ID_2_BESCHREIBUNG.put("040297837", "Karte");
//		ID_2_BESCHREIBUNG.put("041634179", "Katalog");
		ID_2_BESCHREIBUNG.put("043032516", "Kinderbuch");
		ID_2_BESCHREIBUNG.put("041638549", "Kindersachbuch");
		ID_2_BESCHREIBUNG.put("041142403", "Kochbuch");
//		ID_2_BESCHREIBUNG.put("041650018", "Konkordanz");
//		ID_2_BESCHREIBUNG.put("1071862448", "Kolumnensammlung");
//		ID_2_BESCHREIBUNG.put("041367103", "Kommentar");
//		ID_2_BESCHREIBUNG.put("1071861417", "Konferenzschrift");
		ID_2_BESCHREIBUNG.put("041660293", "Kunstführer");
//		ID_2_BESCHREIBUNG.put("042290538", "Künstlerbuch");
//		ID_2_BESCHREIBUNG.put("941101215", "Laudatio");
//		ID_2_BESCHREIBUNG.put("041236238", "Lehrbuch");
		ID_2_BESCHREIBUNG.put("041671686", "Lehrerhandbuch");
		ID_2_BESCHREIBUNG.put("040741117", "Lehrmittel");
//		ID_2_BESCHREIBUNG.put("040351173", "Lehrplan");
//		ID_2_BESCHREIBUNG.put("041264649", "Lernsoftware");
//		ID_2_BESCHREIBUNG.put("040354350", "Lesebuch");
//		ID_2_BESCHREIBUNG.put("041678702", "Literaturbericht");
//		ID_2_BESCHREIBUNG.put("041676343", "Liederbuch");
//		ID_2_BESCHREIBUNG.put("941475360", "Loseblattsammlung");
//		ID_2_BESCHREIBUNG.put("041701739", "Mitgliederverzeichnis");
//		ID_2_BESCHREIBUNG.put("041799984", "Monografische Reihe");
//		ID_2_BESCHREIBUNG.put("040408477", "Musikhandschrift");
//		ID_2_BESCHREIBUNG.put("041285409", "Nachruf");
//		ID_2_BESCHREIBUNG.put("94827171X", "Norm");
//		ID_2_BESCHREIBUNG.put("040678709", "Ortsverzeichnis");
//		ID_2_BESCHREIBUNG.put("040445712", "Papyrus");
//		ID_2_BESCHREIBUNG.put("041735366", "Patentschrift");
//		ID_2_BESCHREIBUNG.put("041885554", "Plan");
//		ID_2_BESCHREIBUNG.put("04046198X", "Plakat");
//		ID_2_BESCHREIBUNG.put("040469026", "Postkarte");
//		ID_2_BESCHREIBUNG.put("04127380X", "Praktikum");
//		ID_2_BESCHREIBUNG.put("041590430", "Pressendruck");
//		ID_2_BESCHREIBUNG.put("041756010", "Predigthilfe");
//		ID_2_BESCHREIBUNG.put("944257690", "Programmheft");
//		ID_2_BESCHREIBUNG.put("041756622", "Pressestimme");
//		ID_2_BESCHREIBUNG.put("041240057", "Puzzle");
		ID_2_BESCHREIBUNG.put("041359526", "Quelle");
		ID_2_BESCHREIBUNG.put("040484769", "Ratgeber");
//		ID_2_BESCHREIBUNG.put("040488829", "Rede");
		ID_2_BESCHREIBUNG.put("040766454", "Reisebericht");
//		ID_2_BESCHREIBUNG.put("107186257X", "Reportagensammlung ");
//		ID_2_BESCHREIBUNG.put("040490076", "Regest");
//		ID_2_BESCHREIBUNG.put("040497127", "Rezension");
//		ID_2_BESCHREIBUNG.put("041773225", "Referateorgan");
//		ID_2_BESCHREIBUNG.put("041378148", "Richtlinie");
//		ID_2_BESCHREIBUNG.put("042275903", "Röntgenbild");
//		ID_2_BESCHREIBUNG.put("041786459", "Rückläufiges Wörterbuch");
		ID_2_BESCHREIBUNG.put("042218608", "Sachbilderbuch");
//		ID_2_BESCHREIBUNG.put("040517780", "Satzung");
//		ID_2_BESCHREIBUNG.put("041794842", "Schematismus");
		ID_2_BESCHREIBUNG.put("040534588", "Schulbuch");
//		ID_2_BESCHREIBUNG.put("040553825", "Software");
//		ID_2_BESCHREIBUNG.put("040564460", "Sprachatlas");
//		ID_2_BESCHREIBUNG.put("040562182", "Spiel");
//		ID_2_BESCHREIBUNG.put("04077211X", "Schulprogramm");
//		ID_2_BESCHREIBUNG.put("041300106", "Sprachführer");
//		ID_2_BESCHREIBUNG.put("041298454", "Stadtplan");
//		ID_2_BESCHREIBUNG.put("040569950", "Statistik");
//		ID_2_BESCHREIBUNG.put("041843037", "Tabelle");
		ID_2_BESCHREIBUNG.put("041843355", "Tafel");
		ID_2_BESCHREIBUNG.put("040589005", "Tagebuch");
//		ID_2_BESCHREIBUNG.put("041846435", "Telefonbuch");
//		ID_2_BESCHREIBUNG.put("041848349", "Testmaterial");
//		ID_2_BESCHREIBUNG.put("041339142", "Technische Zeichnung");
		ID_2_BESCHREIBUNG.put("043040802", "Theaterstück");
//		ID_2_BESCHREIBUNG.put("041851722", "Thesaurus");
		ID_2_BESCHREIBUNG.put("042222087", "Übungssammlung");
//		ID_2_BESCHREIBUNG.put("040052273", "Umfrage");
//		ID_2_BESCHREIBUNG.put("041870743", "Unterrichtseinheit");
//		ID_2_BESCHREIBUNG.put("041881710", "Verzeichnis");
//		ID_2_BESCHREIBUNG.put("041877888", "Verkaufskatalog");
//		ID_2_BESCHREIBUNG.put("041886844", "Vorlesungsverzeichnis");
//		ID_2_BESCHREIBUNG.put("041896807", "Werkverzeichnis");
//		ID_2_BESCHREIBUNG.put("959344357", "Website");
//		ID_2_BESCHREIBUNG.put("040653684", "Weltkarte");
//		ID_2_BESCHREIBUNG.put("964066505", "Weblog");
		ID_2_BESCHREIBUNG.put("040667243", "Wörterbuch");
//		ID_2_BESCHREIBUNG.put("953075109", "Mehrsprachiges Wörterbuch");
//		ID_2_BESCHREIBUNG.put("041896823", "Werkzeitschrift");
//		ID_2_BESCHREIBUNG.put("04127900X", "Zeichnung");
//		ID_2_BESCHREIBUNG.put("040674886", "Zeitschrift");
//		ID_2_BESCHREIBUNG.put("041906314", "Zeittafel");
//		ID_2_BESCHREIBUNG.put("04117724X", "Zitatensammlung");
//		ID_2_BESCHREIBUNG.put("040675106", "Zeitung");

		ID_2_BESCHREIBUNG.put("040288595", "Jugend");
		ID_2_BESCHREIBUNG.put("040305503", "Kind");
		ID_2_BESCHREIBUNG.put("040350886", "Lehrer");
		ID_2_BESCHREIBUNG.put("1070543659", "Leseanfänger");
		ID_2_BESCHREIBUNG.put("040533697", "Schüler");
//		ID_2_BESCHREIBUNG.put("041807030", "Sehbehinderter");
		ID_2_BESCHREIBUNG.put("040640175", "Vorschulkind");

		//@formatter:on
	}

	public static void main(final String[] args) throws IOException {

		final ÖB_Fremddaten medienwerke = new ÖB_Fremddaten();

		medienwerke.gzipSettings();
		final Predicate<String> streamFilter = new ContainsTag("1131",
				BibTagDB.getDB()).or(new ContainsTag("1133", BibTagDB.getDB()));
		medienwerke.setStreamFilter(streamFilter);

		try {
			medienwerke.processFile(Constants.GND_TITEL_GESAMT_D);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		output(data);

	}

}
