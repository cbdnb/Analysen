/**
 *
 */
package karg;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dnb.basics.Constants;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class IMDB extends DownloadWorker {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final IMDB imdb = new IMDB();
		imdb.setStreamFilter(new ContainsTag("670", GNDTagDB.getDB()));
		imdb.setOutputFile("D:/Analysen/karg/imdb.txt");
		imdb.processGZipFile(Constants.Tu);
		System.err.println("---");
		imdb.processGZipFile(Constants.Tp);

	}

	Pattern dbPattern = Pattern.compile("[Mm]ovie\\s+[Dd]atabase");

	@Override
	protected void processRecord(final Record record) {
		final List<String> quellen = GNDUtils.getSourcesDataFound(record);
		for (final String quelle : quellen) {
			final Matcher matcher = dbPattern.matcher(quelle);
			if (matcher.find()) {
				println(record.getId());
				System.err.println(record.getId());
				return;
			}
		}

	}

}
