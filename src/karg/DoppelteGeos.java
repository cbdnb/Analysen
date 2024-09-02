package karg;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.collections.TreeMultimap;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.BibTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubjectUtils;

public class DoppelteGeos {

	Multimap<String, String> name2ids = new TreeMultimap<>();

	Frequency<String> idn2TitlefreFrequency = new Frequency<>();

	HashSet<String> knownIDs = new HashSet<>();

	GeoWorker geoWorker;

	TitleWorker titleWorker;

	class GeoWorker extends DownloadWorker {

		@Override
		protected void processRecord(final Record record) {
			String name = null;
			try {
				name = GNDUtils.getNameOfRecord(record);
			} catch (final IllegalStateException e) {
				e.printStackTrace();
				return;
			}
			final String id = record.getId();
			name2ids.add(name, id);
			knownIDs.add(id);
		}
	}

	class TitleWorker extends DownloadWorker {

		@Override
		protected void processRecord(final Record record) {
			final Collection<String> rswkIds = SubjectUtils
					.getRSWKidsSet(record);
			// System.err.println(record.getId() + " / " + rswkIds);
			for (final String id : rswkIds) {
				// als Geographicum bekannt
				if (knownIDs.contains(id)) {
					idn2TitlefreFrequency.add(id);
					System.err.println(record.getId() + " / " + rswkIds);
					// i++;
					// if (i > 1000) {
					// throw new IllegalArgumentException();
					// }

				}
			}
		}
	}

	DoppelteGeos() {
		geoWorker = new GeoWorker();
		titleWorker = new TitleWorker();
	}

	public static void main(final String[] args) throws IOException {

		final DoppelteGeos doppelte = new DoppelteGeos();
		final GeoWorker geoWorker = doppelte.geoWorker;
		final TitleWorker titleWorker = doppelte.titleWorker;
		final Frequency<String> idn2TitleFrequency = doppelte.idn2TitlefreFrequency;
		final Multimap<String, String> name2ids = doppelte.name2ids;

		System.err.println("Geo-SW durchgehen");
		geoWorker.gzipSettings();
		geoWorker.processFile(Constants.Tg);

		System.err.println("Titeldaten");
		// vorab nach SW-Ketten filtern:
		final Predicate<String> titleFilter = new ContainsTag("5100",
				BibTagDB.getDB());
		titleWorker.setStreamFilter(titleFilter);
		try {
			titleWorker.gzipSettings();
			titleWorker.processFile(Constants.GND_TITEL_GESAMT_Z);
		} catch (final Exception e) {
			System.err.println(e);
		}

		System.err.println("ausgeben");
		final PrintStream stream = new PrintStream(
				"D:/Analysen/karg/DoppelteGeografica1.txt");

		for (final String name : name2ids) {
			final Collection<String> ids = name2ids.getNullSafe(name);
			if (ids.size() > 1) {
				int titleCount = 0;
				for (final String id : ids) {
					titleCount += idn2TitleFrequency.get(id);
				}
				stream.println(titleCount + "\t" + name + "\t"
						+ StringUtils.concatenate("\t", ids));
			}
		}
		MyFileUtils.safeClose(stream);
	}

}
