package karg;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SubjectUtils;

public class GeograficaAmTitel {

    /**
     * Findet alle Tg*-Schlagwörter am Titel und erstellt ihre Statistik in der
     * Variablen {@link #frequencyGeo}.
     *
     * @author baumann
     *
     */
    DownloadWorker workerTitel;

    G2T2Reader reader = new G2T2Reader();

    /**
     * Zahl der an der idn hängenden Titel.
     */
    Frequency<String> titlesPerGeo = new Frequency<>();

    int i = 0;

    public GeograficaAmTitel() {
        workerTitel = new DownloadWorker() {

            @Override
            protected void processRecord(final Record record) {

                final Collection<String> rswkIds = SubjectUtils.getRSWKidsSet(record);

                for (final String id : rswkIds) {
                    final Collection<Triplett<String, String, String>> swBekannt =
                            GeograficaAmTitel.this.reader.getTable2ForIDN(id);
                    if (swBekannt != null) {
                        // i++;
                        // if (i > 100)
                        // throw new IllegalArgumentException();
                        System.err.println(record.getId() + " / " + swBekannt);
                        titlesPerGeo.add(id);
                    }
                }

            }
        };

        // vorab nach SW-Ketten filtern (041A = 5100):
		final Predicate<String> titleFilter =
                new StringContains(Constants.RS + "041A " + Constants.US);
        workerTitel.setStreamFilter(titleFilter);
    }

    public static void main(final String[] args) throws IOException {

        final GeograficaAmTitel geograficaAmTitel = new GeograficaAmTitel();

        final PrintStream stream =
                new PrintStream("D:/Analysen/karg/GeograficaMitDDC.txt");

        System.err.println("Titeldaten flöhen:");
        try {
            // geograficaAmTitel.workerTitel
            // .processGZipFile("D:/Normdaten/DNBtitelgesamt.dat.gz");
            geograficaAmTitel.workerTitel
                    .processGZipFile("D:/Normdaten/DNBtitel_Stichprobe.dat.gz");
        } catch (final Exception e) {
        }

        final Frequency<String> titlesPerGeo = geograficaAmTitel.titlesPerGeo;
        System.out.println(titlesPerGeo);
        for (final String geoId : titlesPerGeo) {
            final long titleCount = titlesPerGeo.get(geoId);
            final Collection<Triplett<String, String, String>> tripletts =
                    geograficaAmTitel.reader.getTable2ForIDN(geoId);
            for (final Triplett<String, String, String> triplett : tripletts) {
                final String s =
                        StringUtils.concatenate("\t", Long.toString(titleCount),
                                geoId, triplett.first, triplett.second,
                                triplett.third);

                System.out.println(s);
                stream.println(s);
            }
        }

        MyFileUtils.safeClose(stream);

    }

}
