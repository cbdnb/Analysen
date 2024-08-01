package maibach;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubjectUtils;

/**
 * Sucht in einem ersten Durchgang nach t6-Notationen, die an mehr als 5 Titeln
 * hängen.
 *
 * Diese werden in ein Multimap frequentt6 eingetragen.
 *
 * Dann werden die Sach-SW durchforstet, ob sie eine t6-Notation tragen, die in
 * frequentt6 vorkommt. Ist das der Fall, wird das Sach-SW unter der t6-Notation
 * eingetragen.
 *
 * @author baumann
 *
 */
public class T6 {

	/**
	 * Findet alle t6-Notationen am Titel und erstellt ihre Statistik in der
	 * Variablen {@link #frequencyT6}.
	 *
	 * @author baumann
	 *
	 */
	static class WorkerTitle extends DownloadWorker {

		final String t6Prefix = "T6--";

		Frequency<String> frequencyT6 = new Frequency<String>();

		int i = 0;

		@Override
		protected void processRecord(final Record record) {

			// i++;
			// if (i > 10000)
			// throw new IllegalArgumentException();
			// System.err.println(i);

			final List<String> t6List = SubjectUtils.getTable6Notations(record);
			if (t6List.isEmpty())
				return;

			// System.err.println(list);
			// Set, um doppelte auszuschließen
			final Set<String> table6 = new HashSet<String>(t6List);
			for (final String notation : table6) {
				frequencyT6.add(t6Prefix + notation);
			}
		}
	};

	/**
	 *
	 * @author baumann
	 *
	 */
	static class WorkerGND extends DownloadWorker {

		private WorkerGND(final Multimap<String, Pair<String, String>> multimap) {
			t6Multimap = multimap;
		}

		final Multimap<String, Pair<String, String>> t6Multimap;

		@Override
		protected void processRecord(final Record record) {

			if (GNDUtils.isUseCombination(record))
				return;

			final List<String> ddcs = GNDUtils.getValidDDCNumbers(record);

			if (ddcs.isEmpty())
				return;
			// doppelte vermeiden:
			final TreeSet<String> ddcSet = new TreeSet<>(ddcs);
			// System.err.println(ddcs);
			for (final String ddc : ddcSet) {
				if (t6Multimap.containsKey(ddc)) {
					final String name = GNDUtils.getNameOfRecord(record);
					final String idn = record.getId();
					final Pair<String, String> idnNamePair =
							new Pair<String, String>(idn, name);
					t6Multimap.add(ddc, idnNamePair);
				}
			}

		}
	};

	public static void main(final String[] args) throws IOException {

		// vorab nach DDC filtern (045F):
		final Predicate<String> titleFilter =
				new StringContains(Constants.RS + "045F " + Constants.US);
		final WorkerTitle workerTitle = new WorkerTitle();
		workerTitle.setStreamFilter(titleFilter);
		System.err.println("Titeldaten flöhen:");
		try {
			workerTitle.processGZipFile("D:/Normdaten/DNBtitelgesamt.dat.gz");
		} catch (final Exception e) {
		}

		/*
		 * Enthält eine Zuornung t6 -> (idn, Schlagwortname). Es sind aber nur
		 * die t6-Terme enthalten, die mehr als 5-mal an einem Titel vorkommen.
		 */
		final Multimap<String, Pair<String, String>> t6Map = new ListMultimap<>();

		final Frequency<String> frequencyT6 = workerTitle.frequencyT6;
		System.out.println("sehe nach > 5");
		// Häufigkeitsverteilung filtern und in t6Map übertragen:
		for (final String t6Notation : frequencyT6) {
			final long count = frequencyT6.get(t6Notation);
			if (count >= 5) {
				t6Map.add(t6Notation);
			}
		}

		System.err.println(t6Map);
		System.err.println("vgl. GND");

		final WorkerGND workerGND = new WorkerGND(t6Map);
		// 083 in Pica+
		final Predicate<String> gndFilter =
				new StringContains(Constants.RS + "037G " + Constants.US);
		workerGND.setStreamFilter(gndFilter);

		try {
			workerGND.processGZipFile("D:/Normdaten/DNBGND_s.dat.gz");
			workerGND.processGZipFile("D:/Normdaten/DNBGND_g.dat.gz");
		} catch (final Exception e) {
		}

		for (final String t6Notation : t6Map) {
			String s = "\"" + t6Notation + "\"\t";
			final Collection<Pair<String, String>> gndPairs = t6Map.get(t6Notation);
			s += StringUtils.makeExcelCellFromCollection(gndPairs);
			System.out.println(s);
		}

		System.out.println();
		System.out.println("-----------");
		System.out.println(t6Map);

	}

}
