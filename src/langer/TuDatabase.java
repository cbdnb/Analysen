/**
 *
 */
package langer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.filtering.Between;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.WorkUtils;

/**
 * @author baumann
 *
 */
public class TuDatabase {

	public static Multimap<String, String> tu2Form = new ListMultimap<>();
	public static Multimap<String, String> tu2Instr = new ListMultimap<>();
	public static Multimap<String, Musikepoche> tu2Epoche = new ListMultimap<>();

	public static void main(final String... args) throws IOException {
		fill();
	}

	public static void fill() throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tu);
		reader.setStreamFilter(
				new ContainsTag("008", 'a', "wim", GNDTagDB.getDB()));

		reader.forEach(record ->
		{

			if (!WorkUtils.isMusicalWork(record))
				return;

			final String tuID = record.getId();
			final Collection<Line> formen = WorkUtils.getLines380(record);
			formen.addAll(GNDUtils.getInstantielleSachOBB(record));
			formen.forEach(line ->
			{
				final String formID = line.getIdnRelated();
				final String form = SubfieldUtils
						.getContentOfFirstSubfield(line, 'a');
				final String sache = StringUtils.concatenate("/", form, formID);
				tu2Form.add(tuID, sache);
			});

			final Collection<Line> instrumente = WorkUtils.getLines382(record);
			instrumente.forEach(line ->
			{
				final String instrID = line.getIdnRelated();
				if (instrID == null)
					return;
				final String instr = SubfieldUtils
						.getContentOfFirstSubfield(line, 'a');
				final String anz = SubfieldUtils.getContentOfFirstSubfield(line,
						'n');
				String sache = StringUtils.concatenate("/", instr, instrID);
				if (anz != null)
					sache += "(" + anz + ")";
				tu2Instr.add(tuID, sache);
			});

			Between<LocalDate> erstellDaten;
			try {
				erstellDaten = WorkUtils.getErstellungsDaten(record);
			} catch (final Exception e) {
				return;
			}

			final List<Musikepoche> musikepochen = Musikepoche
					.getEpochenKorrigiert(erstellDaten);

			musikepochen.forEach(epoche ->
			{
				tu2Epoche.add(tuID, epoche);
			});

		});

	}

}
