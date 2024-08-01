package baumann.ddc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeMap;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.marc.DDCMarcUtils;
import utils.Database;

public class AbridgedDeterminiertheit {

	public static void main(final String[] args) throws FileNotFoundException,
			IOException, ClassNotFoundException, SQLException {

		final TreeMap<String, Collection<Pair<String, String>>> kurze2highdets = new TreeMap<>();

		final Database database = new Database();

		final InputStream input = new FileInputStream(
				"Z:/cbs_sync/ddc/ddc.xml");
		final MarcReader marcReader = new MarcXmlReader(input);

		while (marcReader.hasNext()) {
			final Record record = marcReader.next();

			if (!DDCMarcUtils.isUsedInWinIBW(record))
				continue;

			final String name = DDCMarcUtils.getCaption(record);
			if (name == null)
				continue;

			final String abridged = DDCMarcUtils
					.getAbridgedClassificationNumber(record);
			if (DDCMarcUtils.isAbridged(record)) {
				final Collection<Pair<String, String>> highdet = database
						.getCrissCrossHighDet(abridged);
				kurze2highdets.put(abridged, highdet);
			}

		}

		System.out.println();
		System.out.println();
		kurze2highdets.forEach((ddcKurz, dets) ->
		{
			System.out.println(StringUtils.concatenate("\t", ddcKurz, dets));
		});

	}
}
