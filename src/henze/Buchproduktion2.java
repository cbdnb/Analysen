/**
 *
 */
package henze;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.LogiParser;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.CrossProductFrequency;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DDC_SG;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SGUtils;
import de.dnb.gnd.utils.StatusAndCodeFilter;
import de.dnb.gnd.utils.SubjectUtils;
import de.dnb.gnd.utils.SubjectUtils.TIEFE;
import de.dnb.ie.utils.AcDatabase;

/**
 * Wieviele Bücher wurden im Jahr 2010-2019 in den MINT-Fächern gedruckt?
 *
 * @author baumann
 *
 */
public class Buchproduktion2 extends DownloadWorker {

	private static List<Integer> jahre = IntStream.rangeClosed(2010, 2019)
			.boxed().collect(Collectors.toList());

	private static final CrossProductFrequency frequency = new CrossProductFrequency();

	private static final String path = "D:\\Analysen\\Henze\\Buchproduktion 2.txt";

	private static List<DDC_SG> meineSGG = new ArrayList<>();

	private static Map<String, Predicate<String>> verlag2pred = new LinkedHashMap<>();

	private static StatusAndCodeFilter statusUndCodeFilter = StatusAndCodeFilter
			.reiheA_Gedruckt_ImBestand();

	static {
		// Mal sehen, wo der Unterschied liegt!
		statusUndCodeFilter.auchNichtImBestand();

		meineSGG.add(DDC_SG.getSG("510"));
		meineSGG.add(DDC_SG.getSG("530"));
		if (meineSGG.size() != 2)
			throw new IllegalStateException();

		verlag2pred.put(
				"Atlantis Press (Teil der Fachverlagsgruppe Springer Nature)",
				LogiParser.getPredicate("Atlantis Press", true));
		verlag2pred.put(
				"Birkhäuser Mathematik (Teil der Fachverlagsgruppe Springer Nature)",
				LogiParser.getPredicate("birkhäuser", true));
		verlag2pred.put("Cambridge University Press",
				LogiParser.getPredicate("Cambridge University Press", true));
		verlag2pred.put("CRC Press (Teil der Verlagsgruppe Taylor & Francis)",
				LogiParser.getPredicate("CRC Press", true));
		verlag2pred.put("Deutscher Wissenschafts-Verlag (DWV)",
				LogiParser.getPredicate("dwv", true));
		verlag2pred.put("Dover Publications",
				LogiParser.getPredicate("Dover", true));
		verlag2pred.put("E. Schweizerbart’sche Verlagsbuchhandlung",
				LogiParser.getPredicate("Schweizerbart*", true));
		verlag2pred.put("Edition am Gutenbergplatz Leipzig (EAG.LE)",
				LogiParser.getPredicate("gutenbergplatz", true));
		verlag2pred.put("Elsevier", LogiParser.getPredicate("Elsevier", true));
		verlag2pred.put("Enke Verlag", LogiParser.getPredicate("enke", true));
		verlag2pred.put("European Mathematical Society Publishing House",
				LogiParser.getPredicate("European Mathematical", true));
		verlag2pred.put("Harvard University Press",
				LogiParser.getPredicate("Harvard University Press", true));
		verlag2pred.put("Heldermann Verlag",
				LogiParser.getPredicate("Heldermann", true));
		verlag2pred.put("Institute of Mathematical Statistics", LogiParser
				.getPredicate("Institute of Mathematical Statistics", true));
		verlag2pred.put("International Press of Boston",
				LogiParser.getPredicate("International Press of Boston", true));
		verlag2pred.put("John Wiley & Sons, Inc.",
				LogiParser.getPredicate("john wiley", true));
		verlag2pred.put(
				"Mathematical Association of America (MAA Publications)",
				LogiParser.getPredicate("Mathematical Association of America",
						true));
		verlag2pred.put("Mathematical Society of Japan",
				LogiParser.getPredicate("Mathematical Society of Japan", true));
		verlag2pred.put("Matrix Editions (Ithaca, NY.)",
				LogiParser.getPredicate("Matrix Editions", true));
		verlag2pred.put("Narosa Publishing House",
				LogiParser.getPredicate("Narosa", true));
		verlag2pred.put("Nova Publishers",
				LogiParser.getPredicate("Nova Publishers", true));
		verlag2pred.put("now Publishers",
				LogiParser.getPredicate("now Publishers", true));
		verlag2pred.put("Ontos Verlag", LogiParser.getPredicate("ontos", true));
		verlag2pred.put("Oxford University Press",
				LogiParser.getPredicate("Oxford University", true));
		verlag2pred.put("Pearson Studium",
				LogiParser.getPredicate("Pearson", true));
		verlag2pred.put("Princeton University Press",
				LogiParser.getPredicate("Princeton University", true));
		verlag2pred.put("Société Mathématique de France (SMF)",
				LogiParser.getPredicate("Soci* Math* france", true));
		verlag2pred.put("Society for Industrial and Applied Mathematics (SIAM)",
				LogiParser.getPredicate("Society Industrial Applied", true));
		verlag2pred.put("Springer (Teil der Fachverlagsgruppe Springer Nature)",
				LogiParser.getPredicate("springer not spektrum", true));
		verlag2pred.put(
				"Springer Spektrum (Teil der Fachverlagsgruppe Springer Nature)",
				LogiParser.getPredicate("springer spektrum", true));
		verlag2pred.put("The MIT Press",
				LogiParser.getPredicate("MIT Press", true));
		verlag2pred.put("The University of Chicago Press",
				LogiParser.getPredicate("University Chicago press", true));
		verlag2pred.put("Thieme Gruppe",
				LogiParser.getPredicate("thieme", true));
		verlag2pred.put("Verlag Franzbecker",
				LogiParser.getPredicate("Franzbecker", true));
		verlag2pred.put("Verlag Harri Deutsch",
				LogiParser.getPredicate("deutsch frankfurt", true));
		verlag2pred.put("Walter de Gruyter",
				LogiParser.getPredicate("gruyter", true));
		verlag2pred.put("Wichmann-Verlag",
				LogiParser.getPredicate("wichmann", true));
		verlag2pred.put(
				"Wiley-VCH (Teil der Verlagsgruppe John Wiley & Sons, Inc.)",
				LogiParser.getPredicate("vch", true));
		verlag2pred.put("Wissenschaftliche Buchgesellschaft (wbg)",
				LogiParser.getPredicate(
						"(wissenschaftlich* and buchgesell*) or wbg", true));
		verlag2pred.put("Wissenschaftliche Verlagsgesellschaft Stuttgart",
				LogiParser.getPredicate(
						"Wissenschaftliche Verlagsgesellschaft Stuttgart",
						true));
		verlag2pred.put("World Scientific",
				LogiParser.getPredicate("World Scientific", true));
		verlag2pred.put("WTM-Verlag Münster",
				LogiParser.getPredicate("wtm", true));

		verlag2pred.put("Cuvillier Verlag",
				LogiParser.getPredicate("Cuvillier", true));
		verlag2pred.put("Diplomica Verlag",
				LogiParser.getPredicate("Diplomica", true));
		verlag2pred.put("Peter-Lang-Verlagsgruppe",
				LogiParser.getPredicate("Peter Lang", true));
		verlag2pred.put("Shaker Verlag",
				LogiParser.getPredicate("shaker", true));
		verlag2pred.put("Tectum Wissenschaftsverlag",
				LogiParser.getPredicate("tectum", true));
		verlag2pred.put("Verlag Dr. Kovač",
				LogiParser.getPredicate("Kova* dr.", true));

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final PrintStream printStream = new PrintStream(path);

		final Buchproduktion2 buchprod = new Buchproduktion2();

		final Predicate<String> titleFilter = new ContainsTag("2105", '0', "1",
				BibTagDB.getDB());

		buchprod.setStreamFilter(titleFilter);
		buchprod.gzipSettings();

		System.err.println("Titeldaten flöhen:");

		try {
			buchprod.processFile(Constants.GND_TITEL_GESAMT_Z);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		meineSGG.forEach(sg ->
		{
			printStream.println();
			printStream.println(sg.getDDCString());

			// Titelzeile
			printStream.println("\t" + StringUtils.concatenate("\t", jahre));

			verlag2pred.keySet().forEach(verl ->
			{
				final List<String> zellen = new ArrayList<>();

				zellen.add(verl);
				jahre.forEach(jahr ->
				{
					final Long count = frequency.get(sg, jahr, verl);
					zellen.add(Long.toString(count));
				});
				printStream.println(StringUtils.concatenate("\t", zellen));

			});

		});

		FileUtils.safeClose(printStream);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.dnb.gnd.utils.DownloadWorker#processRecord(de.dnb.gnd.parser.Record)
	 */
	@Override
	protected void processRecord(final Record record) {

		// final String idn = record.getId();

		if (!statusUndCodeFilter.test(record))
			return;

		final Collection<Integer> wvJahre = BibRecUtils.getWVYears(record);
		if (BibRecUtils.isPHeft(record))
			return;

		final Set<Integer> schnitt = new LinkedHashSet<>(jahre);
		schnitt.retainAll(wvJahre);
		if (schnitt.isEmpty())
			return;

		// nimm erstes
		final int jahr = schnitt.iterator().next();

		TIEFE status = SubjectUtils.getErschliessungsTiefe(record);
		if (status == null) { // übergeordneten versuchen
			final String idnBroader = BibRecUtils.getBroaderTitleIDN(record);
			final Pair<String, TIEFE> pair = AcDatabase.getStatus(idnBroader);
			if (pair != null) {
				status = pair.second;
			}
		}

		DDC_SG dhs = SGUtils.getDDCDHS(record);
		if (dhs == null) {
			final String idnBroader = BibRecUtils.getBroaderTitleIDN(record);
			final Pair<String, TIEFE> pair = AcDatabase.getStatus(idnBroader);
			if (pair != null) {
				final String ddcStr = pair.first;
				dhs = SGUtils.getSG(ddcStr);
			}
		}
		if (!meineSGG.contains(dhs))
			return;

		final String ortundverlag = BibRecUtils.getVerlageUndOrte(record);
		if (StringUtils.isNullOrEmpty(ortundverlag))
			return;
		String verlagFound = null;
		final Set<String> verlage = verlag2pred.keySet();
		for (final String verlag : verlage) {
			final Predicate<String> pred = verlag2pred.get(verlag);
			if (pred.test(ortundverlag)) {
				verlagFound = verlag;
				break;
			}
		}
		if (verlagFound == null)
			return;
		System.err.println(
				StringUtils.concatenate(" / ", dhs, jahr, verlagFound));
		frequency.addValues(dhs, jahr, verlagFound);

	}

}
