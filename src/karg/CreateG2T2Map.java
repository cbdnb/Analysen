package karg;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;

import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.ListMultimap;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * Erzeugt Abbildung <br>
 * id -> Liste(SW-Name, T2-Nummer, Determiniertheit) <br>
 * für Geografika als Multimap und speichert sie in {@link #FILENAME_IDN_TO_DDC}
 * .
 */
public class CreateG2T2Map {

    private static final String FILENAME_IDN_TO_DDC = "D:/Normdaten/g2t2.out";

    /*
     * Tg-id -> (Name, T2-Nummer, Determiniertheit)
     */
    private final ListMultimap<String, Triplett<String, String, String>> idn2T2 =
            new ListMultimap<>();

    int i = 0;

    CreateG2T2Map() {

        ddcWorker = new DownloadWorker() {

            @Override
            protected void processRecord(final Record record) {
                final String id = record.getId();

                // leeren Eintrag erzeugen:
                idn2T2.add(id);

                final Collection<Line> ddcLines = GNDUtils.getValidDDCLines(record);
                if (ddcLines.isEmpty())
                    System.err.println(id);
                for (final Line ddcLine : ddcLines) {
                    final String ddcNumber =
                            SubfieldUtils.getContentOfFirstSubfield(ddcLine,
                                    'c');
                    // passiert leider einige Male:
                    if (StringUtils.isNullOrEmpty(ddcNumber))
                        continue;
                    if (!ddcNumber.startsWith("T2"))
                        continue;
                    String det =
                            SubfieldUtils.getContentOfFirstSubfield(ddcLine,
                                    'd');
                    if (StringUtils.isNullOrEmpty(det))
                        det = "0";

                    final String swName = GNDUtils.getNameOfRecord(record);
                    // --------!:
                    final Triplett<String, String, String> triplett =
                            new Triplett<>(swName, ddcNumber, det);
                    idn2T2.add(id, triplett);

//                    System.err.println("" + i + triplett);
                    i++;
                    // if (i == 50000)
                    // throw new IllegalArgumentException("10000");
                }
            }
        };

        // 083 in Pica+
//        IPredicate<String> gndFilter =
//                new StringContains(Constants.RS + "037G " + Constants.US);
//        ddcWorker.setStreamFilter(gndFilter);
        ddcWorker.gzipSettings();
    }

    private final DownloadWorker ddcWorker;

    public static void main(final String[] args) throws IOException {
        final CreateG2T2Map gtoT2 = new CreateG2T2Map();

        try {
            gtoT2.ddcWorker.processFile("D:/Normdaten/DNBGND_g.dat.gz");
        } catch (final Exception e) {
            e.printStackTrace();
        }

        final ListMultimap<String, Triplett<String, String, String>> idnToT2 =
                gtoT2.idn2T2;

        final ObjectOutputStream out =
                new ObjectOutputStream(
                        new FileOutputStream(FILENAME_IDN_TO_DDC));
        out.writeObject(idnToT2);
        FileUtils.safeClose(out);
    }

}
