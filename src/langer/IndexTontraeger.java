/**
 *
 */
package langer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.collections.TreeMultimap;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.StatusAndCodeFilter;

/**
 * @author baumann
 *
 */
public class IndexTontraeger {

	static Multimap<String, String> form2tonID = new TreeMultimap<>();
	static Multimap<String, String> instr2tonID = new TreeMultimap<>();
	static Multimap<Musikepoche, String> epoche2tonID = new TreeMultimap<>();

	static String directory = "D:/Analysen/langer";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		System.err.println("Tu-Datenbank aufbauen");
		TuDatabase.fill();
		System.err.println("Titeldaten flöhen");
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.GND_TITEL_GESAMT_Z);
		final Predicate<String> filter2050 = new ContainsTag("2050",
				BibTagDB.getDB());
		final Predicate<String> filterOaxm = new ContainsTag("0500", '0',
				"Oaxm", BibTagDB.getDB());
		final Predicate<String> filterGaxm = new ContainsTag("0500", '0',
				"Gaxm", BibTagDB.getDB());
		final Predicate<String> filter3210 = new ContainsTag("3210",
				BibTagDB.getDB());
		final Predicate<String> filter3211 = new ContainsTag("3211",
				BibTagDB.getDB());

		final Predicate<String> streamFilter1 = filterOaxm.or(filterGaxm);
		final Predicate<String> streamFilter2 = filter3210.or(filter3211);

		final StatusAndCodeFilter statusAndCodeFilter = StatusAndCodeFilter
				.filterTontraeger();

		reader.setStreamFilter(
				filter2050.and(streamFilter1).and(streamFilter2));

		reader.forEach(record ->
		{

			if (!statusAndCodeFilter.test(record))
				return;
			final String uri = RecordUtils.getContentOfSubfield(record, "2050",
					'0');
			if (uri == null)
				return;
			final List<String> idnsTu = BibRecUtils.getWorkIds(record);
			if (idnsTu.isEmpty())
				return;

			System.err.println(record);
			final String tonID = record.getId();
			final String idPlusUri = tonID + "\t" + uri;

			idnsTu.forEach(idnTu ->
			{
				// Formen
				final Collection<String> formen = TuDatabase.tu2Form
						.getNullSafe(idnTu);
				System.err.println(formen);
				formen.forEach(form -> form2tonID.add(form, idPlusUri));

				// Instrumente
				final Collection<String> instrumente = TuDatabase.tu2Instr
						.getNullSafe(idnTu);
				System.err.println(instrumente);
				instrumente.forEach(instr -> instr2tonID.add(instr, idPlusUri));

				// Epochen
				final Collection<Musikepoche> epochen = TuDatabase.tu2Epoche
						.getNullSafe(idnTu);
				System.err.println(epochen);
				epochen.forEach(epoche -> epoche2tonID.add(epoche, idPlusUri));
			});

		});

		// Ausgeben:
		final PrintWriter writerForm = new PrintWriter(
				directory + "/" + "form2tonID" + ".txt");
		form2tonID.forEach(form ->
		{
			form2tonID.get(form).forEach(idPlusUri -> writerForm
					.println(StringUtils.concatenate("\t", form, idPlusUri)));
		});

		final PrintWriter writerInstr = new PrintWriter(
				directory + "/" + "instr2tonID" + ".txt");
		instr2tonID.forEach(instr ->
		{
			instr2tonID.get(instr).forEach(idPlusUri -> writerInstr
					.println(StringUtils.concatenate("\t", instr, idPlusUri)));
		});

		final PrintWriter writerEpoche = new PrintWriter(
				directory + "/" + "epoche2tonID" + ".txt");
		epoche2tonID.forEach(epoche ->
		{
			epoche2tonID.get(epoche).forEach(idPlusUri -> writerEpoche
					.println(StringUtils.concatenate("\t", epoche, idPlusUri)));
		});

		StreamUtils.safeClose(writerForm);
		StreamUtils.safeClose(writerInstr);
		StreamUtils.safeClose(writerEpoche);

	}

}
