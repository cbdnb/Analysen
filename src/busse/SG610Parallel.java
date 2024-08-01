package busse;

import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Field;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.BibRecUtils;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

public class SG610Parallel extends DownloadWorker {

    PrintStream printSt;

    private SG610Parallel() throws IOException {

        printSt = new PrintStream("D:/Analysen/busse/610ap.txt");
    }

    @Override
    protected void processRecord(final Record record) {

        if (!RecordUtils.isBibliographic(record))
            return;

        final Field field550 = RecordUtils.getFieldGivenAsString(record, "5050");

        if (field550.size() < 2)
            return;

        String sgAbgeliefert = null;
        String sgParallel = null;
        for (final Line line : field550) {
            final String sg = SubfieldUtils.getContentOfFirstSubfield(line, 'e');
            final String dollarE = SubfieldUtils.getContentOfFirstSubfield(line, 'E');
            if (StringUtils.equals(dollarE, "a"))
                sgAbgeliefert = sg;
            else if (StringUtils.equals(dollarE, "p"))
                sgParallel = sg;

        }
        if (!StringUtils.equals(sgAbgeliefert, "610"))
            return;

        final String year = BibRecUtils.getYearOfPublicationString(record);

        System.out.println(StringUtils.concatenate("\t", sgAbgeliefert,
                sgParallel, record.getId()));
        printSt.println(StringUtils.concatenate("\t", sgAbgeliefert,
                sgParallel, record.getId(), year));

    }

    public static void main(final String[] args) throws IOException {

        final SG610Parallel sg610 = new SG610Parallel();

        // vorab nach 5050 610 filtern (045E):
		final Predicate<String> titleFilter =
                new StringContains(Constants.RS + "045E " + Constants.US
                        + "e610");
        sg610.setStreamFilter(titleFilter);

        try {
            sg610.processGZipFile("Z:/cbs/zen/vollabzug/aktuell/Pica+/DNBGNDtitel.dat.gz");
            // sg610.processGZipFile("D:/Normdaten/DNBtitel_Stichprobe.dat.gz");
        } catch (final Exception e) {

        }

    }
}
