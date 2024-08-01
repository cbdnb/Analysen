package karg;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubjectUtils;
import utils.Database;

/**
 * Sucht in einem ersten Durchgang nach T2-Notationen, die an Titeln hängen.
 *
 * Diese werden in einer Frequency-Struktur frequencyT2 gezählt.
 *
 * Dann werden die Sach-SW durchforstet, ob sie eine T2-Notation tragen, die in
 * frequencyT2 vorkommt. Ist das der Fall, wird das Sach-SW in einer Multimap
 * unter der T2-Notation eingetragen.
 *
 * @author baumann
 *
 */
public class T2AmTitel {

    Database database;

    final String t2Prefix = "T2--";

    Frequency<String> frequencyT2 = new Frequency<String>();

    /**
     * Enthält eine Zuordnung T2 -> (idn, Schlagwortname, Determiniertheit).
     */
    final Multimap<String, Triplett<String, String, String>> t2Multimap =
            new ListMultimap<>();

    WorkerTitle workerTitle = new WorkerTitle();

    WorkerGND workerGND = new WorkerGND();

    T2AmTitel() throws SQLException {
        // vorab nach DDC filtern (045F):
		final Predicate<String> titleFilter =
                new StringContains(Constants.RS + "045F " + Constants.US);
        workerTitle.setStreamFilter(titleFilter);

        // 083 in Pica+
		final Predicate<String> gndFilter =
                new StringContains(Constants.RS + "037G " + Constants.US);
        workerGND.setStreamFilter(gndFilter);

        database = new Database();
    }

    /**
     * Findet alle T2-Notationen am Titel und erstellt ihre Statistik in der
     * Variablen {@link #frequencyT2}.
     *
     * @author baumann
     *
     */
    class WorkerTitle extends DownloadWorker {

        int i = 0;

        @Override
        protected void processRecord(final Record record) {

            i++;
            // if (i > 1000)
            // throw new IllegalArgumentException();
            // System.err.println(i);

            final List<String> t2List = SubjectUtils.getTable2Notations(record);
            if (t2List.isEmpty())
                return;

            // Set, um doppelte auszuschließen
            final Set<String> table2 = new HashSet<String>(t2List);
            for (final String notation : table2) {
                frequencyT2.add(t2Prefix + notation);
                // System.err.println(record.getId());
                // System.err.println(t2Prefix + notation);

            }
        }
    };

    class WorkerGND extends DownloadWorker {

        @Override
        protected void processRecord(final Record record) {

            if (GNDUtils.isUseCombination(record))
                return;

            final List<Line> ddcs = GNDUtils.getValidDDCLines(record);
            if (ddcs.isEmpty())
                return;

            for (final Line ddcline : ddcs) {
                final String ddc = GNDUtils.getDDCNumber(ddcline);
                if (ddc == null)
                    continue;
                String det = GNDUtils.getDDCDeterminacy(ddcline);
                if (det == null)
                    det = "0";
                if (frequencyT2.contains(ddc)) {
                    final String name = GNDUtils.getNameOfRecord(record);
                    final String idn = record.getId();
                    final Triplett<String, String, String> idnNamePair =
                            new Triplett<String, String, String>(idn, name, det);
                    t2Multimap.add(ddc, idnNamePair);
                }
            }
        }
    };

    public static void main(final String[] args) throws IOException, SQLException {

        final T2AmTitel t2AmTitel = new T2AmTitel();
        // System.err.println("Titeldaten flöhen:");
        try {
            t2AmTitel.workerTitle
                    .processGZipFile("D:/Normdaten/DNBtitelgesamt.dat.gz");
        } catch (final Exception e) {
        }

        // System.err.println("vgl. GND");
        try {
            t2AmTitel.workerGND.processGZipFile("D:/Normdaten/DNBGND_s.dat.gz");
            t2AmTitel.workerGND.processGZipFile("D:/Normdaten/DNBGND_g.dat.gz");
        } catch (final Exception e) {
        }

        final Frequency<String> frequencyT2 = t2AmTitel.frequencyT2;
        final Multimap<String, Triplett<String, String, String>> t2Multimap =
                t2AmTitel.t2Multimap;

        final PrintStream stream =
                new PrintStream("D:/Analysen/karg/T2_am_Titel.txt");


        for (final String t2Notation : frequencyT2) {
            final Collection<Triplett<String, String, String>> tripletts =
                    t2Multimap.get(t2Notation);

            stream.print(t2Notation);
            stream.print("\t");
            stream.print(t2AmTitel.database.getCaption(t2Notation));
            stream.print("\t");
            stream.print(frequencyT2.get(t2Notation));
            stream.print("\t");
            stream.print(StringUtils.makeExcelCellFromCollection(tripletts));
            stream.println();
        }

        StreamUtils.safeClose(stream);

    }
}
