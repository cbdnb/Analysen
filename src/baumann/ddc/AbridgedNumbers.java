package baumann.ddc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

import utils.Database;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.marc.DDCMarcUtils;

public class AbridgedNumbers {

    public static void main(final String[] args) throws FileNotFoundException,
            IOException, ClassNotFoundException, SQLException {

        final Set<String> kurze = new TreeSet<>();
        final Frequency<String> kurzeTitelzahl = new Frequency<>();

        final Database database = new Database();

        final InputStream input =
                new FileInputStream("Z:/cbs_sync/ddc/ddc.xml");
        final MarcReader marcReader = new MarcXmlReader(input);

        while (marcReader.hasNext()) {
            final Record record = marcReader.next();

            // if (!DDCMarcUtils.isDisplayedInStandardSchedulesOrTables(record))
            // continue;
            if (!DDCMarcUtils.isUsedInWinIBW(record))
                continue;

            final String name = DDCMarcUtils.getCaption(record);
            if (name == null)
                continue;

            final String number =
                    DDCMarcUtils.getFullClassificationNumber(record);

            final String abridged =
                    DDCMarcUtils.getAbridgedClassificationNumber(record);

            kurze.add(abridged);

            final int titelZahl = database.getTitleIDsForDDC(number).size();
            kurzeTitelzahl.increment(abridged, titelZahl);

            final String trunk =
                    StringUtils.equals(number, abridged) ? "" : "x";

            // System.out.println(StringUtils.concatenate("\t", name, number,
            // abridged, trunk));

        }

        System.out.println();
        System.out.println();
        for (final String ddcKurz : kurze) {
            if (ddcKurz != null && ddcKurz.length() >= 3) {
                final char last = ddcKurz.charAt(ddcKurz.length() - 1);
                if (Character.isDigit(last) && last != '0')
                    if (kurzeTitelzahl.get(ddcKurz) > 0)
                        System.out.println(StringUtils.concatenate("\t",
                                ddcKurz, database.getCaption(ddcKurz),
                                kurzeTitelzahl.get(ddcKurz)));
            }
        }

        MyFileUtils.safeClose(input);

    }
}
