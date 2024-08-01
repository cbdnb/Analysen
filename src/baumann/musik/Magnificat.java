package baumann.musik;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.dnb.basics.applicationComponents.strings.Table;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.WorkUtils;

public class Magnificat extends DownloadWorker {

    private static final String EXTENSION = ".txt";

    static int i = 0;

    Table<String> neu2alt;

    HashMap<String, PrintStream> idn2File;

    public static final String DIRECTORY = "D:/Analysen/baumann/380";

    public static final int NAME = 0;

    public static final int IDN_ALT = 1;

    public static final int IDN_NEU = 2;

    public Magnificat() throws FileNotFoundException {
        this.neu2alt =
                Table.getStringTableFromFile("//DNB-FS01/"
                        + "DNB-Gesamt/Standardisierung/_GND/Fachthemen/"
                        + "DMA/380-musik/" + "Liste_380_neu.txt");

        this.idn2File = new HashMap<>();

        for (final List<String> row : this.neu2alt) {
            final String idn_alt = row.get(IDN_ALT);
            final String idn_neu = row.get(IDN_NEU);
            final String name = row.get(NAME) + EXTENSION;
            final File file = new File(DIRECTORY, name);
            final PrintStream printStream = new PrintStream(file);
            this.idn2File.put(idn_alt, printStream);
            printStream.println(idn_alt);
            printStream.println(idn_neu);
            printStream.println("---");
        }

    }

    @Override
    protected void processRecord(final Record record) {
        final Collection<String> formsIDs = WorkUtils.get380IDNs(record);
        for (final String formID : formsIDs) {
            final PrintStream stream = this.idn2File.get(formID);
            if (stream != null)
                stream.println(record.getId());
        }
        // if (formsIDs.contains("041845919"))
        // println(record.getId());
    }

    public static void main(final String[] args) throws IOException {

        final Magnificat mag = new Magnificat();

        mag.setStreamFilter(new ContainsTag("380", GNDTagDB.getDB()));
        // mag.setOutputFile("D:/Analysen/baumann/380/tedeum.txt");

        System.err.println("Tu flöhen:");
        try {
            mag.processGZipFile("D:/Normdaten/DNBGND_u.dat.gz");

        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

}
