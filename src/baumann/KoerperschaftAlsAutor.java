package baumann;

import java.io.IOException;
import java.util.List;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.WorkUtils;

public class KoerperschaftAlsAutor extends DownloadWorker {

	static int i = 0;

	@Override
	protected void processRecord(final Record record) {
		final Line authL = WorkUtils.getAuthorLine(record);
		if (authL == null)
			return;

		final String tags = authL.getTag().pica3;

		if (!tags.equals("510"))
			return;

		final List<String> ents = GNDUtils.getEntityTypes(record);
		if (!ents.contains("wim"))
			return;

		final String out = StringUtils.concatenate("\t", record.getId(),
				GNDUtils.getNameOfRecord(record));
		System.out.println(out);

	}

	public static void main(final String[] args) throws IOException {

		final KoerperschaftAlsAutor dlw = new KoerperschaftAlsAutor();

		dlw.setStreamFilter(new ContainsTag("510", GNDTagDB.getDB()));

		System.err.println("Tg flöhen:");
		try {
			dlw.processGZipFile("D:/Normdaten/DNBGND_u.dat.gz");

		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

}
