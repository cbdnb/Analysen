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

import utils.Database;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.applicationComponents.tuples.Triplett;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.marc.DDCMarcUtils;

public class AbridgedNumbersVerbalisierung {

    public static void main(final String[] args) throws FileNotFoundException,
            IOException, ClassNotFoundException, SQLException {

        final TreeMap<String, Collection<Triplett<String, String, String>>> kurze =
                new TreeMap<>();
        final Database database = new Database();
        final Frequency<String> kurzeTitelzahl = new Frequency<>();

        int gesamtZahlTitel = 0;
        int zahlTitelOhneCriss = 0;

        final InputStream input =
                new FileInputStream("Z:/cbs_sync/ddc/ddc.xml");
        final MarcReader marcReader = new MarcXmlReader(input);

        while (marcReader.hasNext()) {
            final Record record = marcReader.next();

            if (!DDCMarcUtils.isDisplayedInStandardSchedulesOrTables(record))
                continue;

            final String name = DDCMarcUtils.getCaption(record);
            if (name == null)
                continue;

            final String abridged =
                    DDCMarcUtils.getAbridgedClassificationNumber(record);

            // um über alle untergeordneten zu summieren:
            final String number =
                    DDCMarcUtils.getFullClassificationNumber(record);
            final int titelZahl = database.getTitleIDsForDDC(number).size();
            kurzeTitelzahl.increment(abridged, titelZahl);

            if (!kurze.containsKey(abridged)) {
                final Collection<Triplett<String, String, String>> crisscross =
                        database.getCrissCrossSWW(abridged);
                kurze.put(abridged, crisscross);
            }

        }

        System.out.println();
        System.out.println();
        for (final String ddcKurz : kurze.keySet()) {
            if (ddcKurz != null && ddcKurz.length() >= 3) {
                final char last = ddcKurz.charAt(ddcKurz.length() - 1);
                if (Character.isDigit(last) && last != '0') {
                    final long titleCount = kurzeTitelzahl.get(ddcKurz);
                    final boolean isHaupttafel = !ddcKurz.startsWith("T");
                    if (isHaupttafel)
                        gesamtZahlTitel += titleCount;

                    if (titleCount > 0) {
                        final Collection<Triplett<String, String, String>> crisscross =
                                kurze.get(ddcKurz);
                        if (crisscross == null && isHaupttafel)
                            zahlTitelOhneCriss += titleCount;
                        final String excel =
                                StringUtils
                                        .makeExcelCellFromCollection(crisscross);
                        System.out.println(StringUtils.concatenate("\t",
                                ddcKurz, database.getCaption(ddcKurz), excel,
                                titleCount));
                    }
                }

            }
        }

        System.out.println();
        System.out.println();
        System.out.println("Zahl aller Titel: " + gesamtZahlTitel);
        System.out.println("Zahl der Titel ohne CrissCross: "
                + zahlTitelOhneCriss);

        FileUtils.safeClose(input);

    }
}
