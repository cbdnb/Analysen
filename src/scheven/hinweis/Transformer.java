/**
 *
 */
package scheven.hinweis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.collections.Multiset;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.Format;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.Subfield;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.line.LineParser;
import de.dnb.gnd.parser.tag.GNDTag;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.parser.tag.Tag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.formatter.RDAFormatter;
import de.dnb.gnd.utils.mx.LibraryDB;
import de.dnb.gnd.utils.mx.MXAddress;
import de.dnb.gnd.utils.mx.Mailbox;
import de.dnb.gnd.utils.mx.RedaktionsTyp;
import de.dnb.gnd.utils.mx.MXAddress.Adressierung;

/**
 * @author baumann
 *
 */
public abstract class Transformer {

	/**
	 * Eingabe von Datensätzen zu Testzwecken.
	 */
	protected static final boolean DEBUG = false;

	protected static final String TEXT_DEBUG = "$vTestTestTest";

	static final GNDTagDB TAG_DB = GNDTagDB.getDB();

	static final Map<Character, Tag> TYP_TO_1XX = new HashMap<>();
	static final Map<Character, Tag> TYP_TO_4XX = new HashMap<>();
	static {
		TYP_TO_1XX.put('b', TAG_DB.findTag("110"));
		TYP_TO_1XX.put('f', TAG_DB.findTag("111"));
		TYP_TO_1XX.put('g', TAG_DB.findTag("151"));
		TYP_TO_1XX.put('p', TAG_DB.findTag("100"));
		TYP_TO_1XX.put('s', TAG_DB.findTag("150"));
		TYP_TO_1XX.put('u', TAG_DB.findTag("130"));

		TYP_TO_4XX.put('b', TAG_DB.findTag("410"));
		TYP_TO_4XX.put('f', TAG_DB.findTag("411"));
		TYP_TO_4XX.put('g', TAG_DB.findTag("451"));
		TYP_TO_4XX.put('p', TAG_DB.findTag("400"));
		TYP_TO_4XX.put('s', TAG_DB.findTag("450"));
		TYP_TO_4XX.put('u', TAG_DB.findTag("430"));
	}

	public static final String MX_BODY_PL = "Dieser Datensatz wurde automatisch aus den "
			+ "Hinweisdatensätzen %s erzeugt. Die Hinweisdatensätze werden demnächst gelöscht. "
			+ "Bitte daher diesen neu erzeugten Datensatz aufarbeiten und gegebenenfalls "
			+ "Titel umverknüpfen.";
	public static final String MX_BODY_SG = "Dieser Datensatz wurde automatisch aus dem "
			+ "Hinweisdatensatz %s erzeugt. Der Hinweisdatensatz wird demnächst gelöscht. "
			+ "Bitte daher diesen neu erzeugten Datensatz aufarbeiten und gegebenenfalls "
			+ "Titel umverknüpfen.";

	public static final String KOMM_670_678 = "aus Hinweisdatensatz ";

	protected HinweisDBUtil db = new HinweisDBUtil();

	protected char typ;
	protected GNDTag feld1XX;
	protected GNDTag feld4XX;
	protected Line line008 = null;

	final Multiset<Character> sg = new Multiset<>('s', 'g');
	final Multiset<Character> bs = new Multiset<>('b', 's');
	final Multiset<Character> ssg = new Multiset<>('s', 's', 'g');
	final Multiset<Character> psg = new Multiset<>('p', 's', 'g');
	final Multiset<Character> ps = new Multiset<>('p', 's');
	final Multiset<Character> ssb = new Multiset<>('s', 's', 'b');

	/**
	 * @param typ
	 */
	protected Transformer(final char typ) {
		super();
		System.err.println();
		System.err.println("* Aufpassen: Die verwendeteten Datenbanken\r\n"
				+ "* GND_DB_UTIL.getppn2name() und GND_DB_UTIL.getTb2Ort()\r\n"
				+ "* müssen über GUI so geladen werden, dass sie auch die nötigen\r\n"
				+ "* Geografika enthalten.");
		this.typ = typ;
		feld1XX = (GNDTag) TYP_TO_1XX.get(typ);
		feld4XX = (GNDTag) TYP_TO_4XX.get(typ);
		filter();
	};

	/**
	 * Reichert record mit den Daten aus kombi an. Dazu wird auch der Typ
	 * benutzt.
	 *
	 * Macht aus der Kombination und dem Typ typ des Zieldatensatzes den Rumpf
	 * eines neuen Datensatzes.
	 *
	 * @param kombi
	 * @param record
	 * @param typ2
	 */
	abstract void make1XX(final Set<Pair<String, String>> kombi,
			final Record record);

	/**
	 * Lässt nur die Kombinationen übrig, die in diesem Verarbeitungschritt
	 * benutzt werden sollen. Benutzt
	 * {@link HinweisDBUtil#retainIfAllRecords(java.util.function.Predicate)},
	 * {@link HinweisDBUtil#retainIfAnyRecord(java.util.function.Predicate)},
	 * {@link HinweisDBUtil#retainIfRecords(java.util.function.Predicate)} oder
	 * {@link HinweisDBUtil#retainIfKombi(java.util.function.Predicate)}
	 *
	 */
	protected abstract void filter();

	/**
	 * Sammelt alle Informationen aus den Datensätzen und führt sie in einem
	 * zusammen.
	 *
	 * @param recordGroup
	 * @throws IllFormattedLineException
	 * @throws OperationNotSupportedException
	 */
	void mergeRecords(final Record newRecord,
			final Collection<Record> recordGroup)
			throws OperationNotSupportedException, IllFormattedLineException {
		// Ländercode + Syst.:
		final Set<String> ccs = new LinkedHashSet<>();
		final Set<String> systs = new LinkedHashSet<>();
		for (final Record record : recordGroup) {
			final List<String> cc = GNDUtils.getCountryCodes(record);
			ccs.addAll(cc);
			final List<String> sy = GNDUtils.getGNDClassifications(record);
			systs.addAll(sy);
		}
		if (!ccs.isEmpty()) {
			newRecord.add(LineParser
					.parseGND("043 " + StringUtils.concatenate(";", ccs)));
		}
		if (!systs.isEmpty()) {
			newRecord.add(LineParser
					.parseGND("065 " + StringUtils.concatenate(";", systs)));
		}

		// 4XX:
		for (final Record record : recordGroup) {
			final String heading = RDAFormatter.getPureRDAHeading(record)
					+ "$valte Vorzugsbenennung aus dem Hinweisdatensatz "
					+ GNDUtils.getNID(record);
			final Line line4XX = LineParser.parse(feld4XX, Format.PICA3,
					heading, false);
			newRecord.add(line4XX);
		}

		// 548:
		for (final Record record : recordGroup) {
			final ArrayList<Line> lines548 = RecordUtils.getLines(record,
					"548");
			// System.err.println(lines548);
			lines548.forEach(line548 ->
			{
				try {
					newRecord.addWithoutDuplicates(line548);
				} catch (final OperationNotSupportedException e) {
					e.printStackTrace();
				}
			});
		}

		// 667
		final String nids = "<" + recordGroup.stream().map(GNDUtils::getNID)
				.collect(Collectors.joining(", ")) + ">";
		newRecord.add(
				LineParser.parseGND("667 maschinell erzeugter Datensatz aus "
						+ (recordGroup.size() == 1 ? "dem Hinweisdatensatz "
								: "den Hinweisdatensätzen ")
						+ nids + "; " + "bei Aufgreifen bitte aufarbeiten."));

		final Set<String> isils = recordGroup.stream()
				.map(GNDUtils::getIsilVerbund).collect(Collectors.toSet());
		newRecord.add(LineParser.parseGND("667 Beteiligte Redaktion(en): "
				+ StringUtils.concatenate(", ", isils)));

		// Sammle 670 + 677 (-> 678)
		recordGroup.forEach(record ->
		{
			GNDUtils.getSourceLines(record).forEach(quelle ->
			{

				try {
					final String mitKomma = mitKomma(quelle);
					final Line line670Neu = LineParser
							.parseGND("670 " + mitKomma + "$b" + KOMM_670_678);
					newRecord.addWithoutDuplicates(line670Neu);
				} catch (final OperationNotSupportedException
						| IllFormattedLineException e) {
				}
			});
			RecordUtils.getLines(record, "677").forEach(def ->
			{
				def = SubfieldUtils.getNewLineRemovingSubfields(def, 'v', 'C',
						'5');
				final String contentS = RecordUtils.toPicaWithoutTag(def);
				try {
					final Line line678 = LineParser
							.parseGND("678 " + contentS + "$b" + KOMM_670_678);
					newRecord.addWithoutDuplicates(line678);
				} catch (IllFormattedLineException
						| OperationNotSupportedException e) {
					e.printStackTrace();
				}
			});
		});

		// makeMX(newRecord, recordGroup);
	}

	/**
	 * @param newRecord
	 * @param isil2hinweise
	 * @param isil
	 */
	public void makeMX(final Record newRecord,
			final Collection<Record> recordGroup) {
		final Multimap<String, String> isil2hinweise = new ListMultimap<>();
		recordGroup.forEach(rec ->
		{
			final String isil = GNDUtils.getIsilVerbund(rec);
			isil2hinweise.add(isil, rec.getId());
		});

		isil2hinweise.forEach(isil ->
		{
			final Collection<String> hinweise = isil2hinweise.get(isil);
			final String message = hinweise.size() > 1
					? String.format(MX_BODY_PL, hinweise.toString())
					: String.format(MX_BODY_SG, hinweise.toString());
			final MXAddress abs = new MXAddress();
			abs.setLibrary(LibraryDB.getLibraryByISIL("DE-101"));
			abs.setAdressierung(Adressierung.ABSENDER);
			abs.setRedaktion(RedaktionsTyp.SE);
			abs.setSubadress("F");

			final MXAddress empf = new MXAddress();
			empf.setLibrary(LibraryDB.getLibraryByISIL(isil));
			empf.setAdressierung(Adressierung.EMPFAENGER);
			empf.setRedaktion(RedaktionsTyp.SE);

			final Mailbox mailbox = new Mailbox();
			mailbox.addBeteiligten(empf);
			mailbox.setAbsender(abs);

			mailbox.setDate(new Date());
			mailbox.setText(message);

			final Line mxLine = mailbox.toLine();
			try {
				newRecord.add(mxLine);
			} catch (final OperationNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	public Set<String> idnsHinweis = new HashSet<>();

	/**
	 * @param kombi
	 *            Kombination
	 * @throws IllFormattedLineException
	 * @throws OperationNotSupportedException
	 */
	public final Record transform(final Set<Pair<String, String>> kombi)
			throws IllFormattedLineException, OperationNotSupportedException {

		final Collection<Record> recordGroup = db.getRecords(kombi);
		if (recordGroup == null)
			return null;

		recordGroup.forEach(rec -> idnsHinweis.add(rec.getId()));

		// Allgemeine Felder:
		final Record newRecord = new Record("1234", TAG_DB);
		newRecord.add(LineParser.parseGND("005 T" + typ + "6"));
		newRecord.add(line008);
		newRecord.add(LineParser.parseGND("011 s"));
		newRecord.add(LineParser.parseGND("040 $frswk"));

		// 6XX
		newRecord.add(LineParser.parseGND("670 SWD"));
		String einfeugung = kombi.stream().map(pair ->
		{
			if (pair.second != null)
				return "260 !" + pair.first + "!" + pair.second;
			else
				return "260 " + pair.first;
		}).collect(Collectors.joining(" "));
		// Die $in 680 nicht erlaubt:
		einfeugung = einfeugung.replaceAll("\\$.", " / ");
		newRecord.add(LineParser.parseGND("680 früher wurde die Kombination "
				+ einfeugung
				+ " verwendet; ggf. wurden die Titel nicht umgehängt."));

		// 5XX:
		for (final Pair<String, String> pair : kombi) {
			final String idn260 = pair.first;
			final String expans260 = pair.second;
			if (HinweisDBUtil.getIndikator(pair) == 'p')
				veraerbeiteP(newRecord, idn260, expans260);
			if (HinweisDBUtil.getIndikator(pair) == 's')
				newRecord.add(LineParser.parseGND(
						"550 !" + idn260 + "!" + expans260 + "$4obin"));
			if (HinweisDBUtil.getIndikator(pair) == 'g')
				newRecord.add(LineParser.parseGND(
						"551 !" + idn260 + "!" + expans260 + "$4orta" + "$X1"));
			if (HinweisDBUtil.getIndikator(pair) == 'b') {
				newRecord.add(LineParser.parseGND(
						"510 !" + idn260 + "!" + expans260 + "$4rela"));
				final Integer geoidInt = HinweisDBUtil.getGeoIdInt(idn260);
				final String geoIDN = IDNUtils.int2PPN(geoidInt);
				final String geoName = HinweisDBUtil.getGeoName(idn260);
				newRecord.add(LineParser.parseGND(
						"551 !" + geoIDN + "!" + geoName + "$4orta" + "$X1"));
			}

		}

		make1XX(kombi, newRecord);
		mergeRecords(newRecord, recordGroup);
		return newRecord;
	}

	/**
	 * Personen relationieren.
	 *
	 * @param newRecord
	 * @param idn260
	 * @param expans260
	 * @throws OperationNotSupportedException
	 * @throws IllFormattedLineException
	 */
	public abstract void veraerbeiteP(final Record newRecord,
			final String idn260, final String expans260)
			throws OperationNotSupportedException, IllFormattedLineException;

	/**
	 *
	 * @param line
	 *            nicht null
	 * @return Nur relevante Unterfelder (ohne $v..), ohne $g. Alle verbunden
	 *         durch Blank.
	 */
	protected String ohneDollarGmitBlank(final Line line) {
		final List<Subfield> relevantsubs = SubfieldUtils
				.getNamingRelevantSubfields(line);
		return relevantsubs.stream()
				.filter(sub -> sub.getIndicator().indicatorChar != 'g')
				.map(Subfield::getContent).collect(Collectors.joining(" "));
	}

	/**
	 *
	 * @param line
	 *            nicht null
	 * @return Nur relevante Unterfelder (ohne $v..), ohne $g. Alle verbunden
	 *         durch Komma.
	 */
	protected String ohneDollarGmitKomma(final Line line) {
		final List<Subfield> relevantsubs = SubfieldUtils
				.getNamingRelevantSubfields(line);
		return relevantsubs.stream()
				.filter(sub -> sub.getIndicator().indicatorChar != 'g')
				.map(Subfield::getContent).collect(Collectors.joining(", "));
	}

	/**
	 *
	 * @param line
	 *            nicht null
	 * @return Nur relevante Unterfelder (ohne $v..). Alle verbunden durch
	 *         Blank.
	 */
	protected String mitBlank(final Line line) {
		final List<Subfield> relevantsubs = SubfieldUtils
				.getNamingRelevantSubfields(line);
		return relevantsubs.stream().map(Subfield::getContent)
				.collect(Collectors.joining(" "));
	}

	/**
	 *
	 * @param line
	 *            nicht null
	 * @return Nur relevante Unterfelder (ohne $v..). Alle verbunden durch
	 *         Komma.
	 */
	protected String mitKomma(final Line line) {
		final List<Subfield> relevantsubs = SubfieldUtils
				.getNamingRelevantSubfields(line);
		return relevantsubs.stream().map(Subfield::getContent)
				.collect(Collectors.joining(", "));
	}

}
