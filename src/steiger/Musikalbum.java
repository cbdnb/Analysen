/**
 *
 */
package steiger;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.tries.TST;
import de.dnb.basics.tries.Trie;
import de.dnb.gnd.parser.Format;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.Subfield;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.WorkUtils;

/**
 * @author baumann
 *
 */
public class Musikalbum extends DownloadWorker {

	static Set<String> musikalben = new HashSet<>();
	static Set<String> keineAlben = new HashSet<>();
	static Trie<String> keineAlbenTrie = new TST<>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {

		final Musikalbum musikalbum = new Musikalbum();
		musikalbum.processGZipFile(Constants.Tu);

		musikalben.forEach(album ->
		{
			// System.err.println(album);
			if (keineAlben.contains(album))
				System.out.println(album);
		});

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.dnb.gnd.utils.DownloadWorker#processRecord(de.dnb.gnd.parser.Record)
	 */
	@Override
	protected void processRecord(final Record record) {
		if (!WorkUtils.isMusicalWork(record))
			return;

		final Line titleLine = WorkUtils.getTitleLine(record);
		final String dollarG = SubfieldUtils
				.getContentOfFirstSubfield(titleLine, 'g');
		final boolean isMusikalbum = (dollarG != null)
				&& dollarG.contains("Musikalbum");

		List<Subfield> subs = titleLine.getSubfields();
		if (isMusikalbum)
			subs = SubfieldUtils.removeSubfieldsFromCollection(subs, 'g');
		subs = SubfieldUtils.getRelevanteUnterfelder(subs);

		final String title = RecordUtils.toPicaWithoutTag(GNDTagDB.TAG_130,
				subs, Format.PICA3, false, '$');
		final String authorID = WorkUtils.getAuthorID(record);
		final String id = record.getId();
		final String out = StringUtils.concatenate("\t", authorID, title);

		if (isMusikalbum)
			musikalben.add(out);
		else
			keineAlben.add(out);

		// System.out.println(out + " / " + isMusikalbum);

	}

}
