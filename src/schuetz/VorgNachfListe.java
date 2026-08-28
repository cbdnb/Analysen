package schuetz;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;
import de.dnb.gnd.utils.RecordUtils;

public class VorgNachfListe {

	public static void main(final String[] args) throws IOException {
		final Set<Integer> vorNach = new HashSet<>();
		final RecordReader tbReader = RecordReader
				.getMatchingReader(Constants.Tb);
		tbReader.setStreamFilter(new ContainsTag("510", GNDTagDB.getDB()));
		tbReader.forEach(rec ->
		{
			if (!GNDUtils.getPrePostLines(rec).isEmpty())
				vorNach.add(IDNUtils.idn2int(rec.getId()));
		});

		System.out.println(
				"Anzahl Tb mit Vorgänger/Nachfolger: " + vorNach.size());

		final Set<Integer> verknuepfte = new HashSet<>();
		final RecordReader titelReader = RecordReader.getMatchingReader(
				"D:/pica-0.22.0-x86_64-pc-windows-msvc/temp.dat.gz");

		Predicate<String> titleFilter = new ContainsTag("3100", '9', "",
				BibTagDB.getDB());
		titleFilter = titleFilter
				.or(new ContainsTag("3110", '9', "", BibTagDB.getDB()));
		titelReader.setStreamFilter(titleFilter);
		titelReader.forEach(rec ->
		{
			final String datatype = RecordUtils.getDatatype(rec);
			if (!datatype.endsWith("bvz") && !datatype.endsWith("dvz"))
				return;
			final List<String> idnStrs = RecordUtils
					.getContentsOfFirstSubfield(rec, '9', "3100", "3110");
			IDNUtils.idns2ints(idnStrs).forEach(idn ->
			{
				if (vorNach.contains(idn))
					verknuepfte.add(idn);
			});
		});

		System.out.println(
				"Anzahl Tb mit Vorgänger/Nachfolger und Verknüpfung in 3100/3110: "
						+ verknuepfte.size());
	}

}
