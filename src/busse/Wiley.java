package busse;

import java.io.IOException;
import java.util.ArrayList;

import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;
import de.dnb.gnd.utils.formatter.Pica3Formatter;

public class Wiley extends DownloadWorker {

    Pica3Formatter formatter = new Pica3Formatter();

    @Override
    protected void processRecord(final Record record) {
        final ArrayList<Line> lines5050 = RecordUtils.getLines(record, "5050");
        boolean found = false;
        String line5050 = null;
        for (final Line line : lines5050) {
            final String subfieldE =
                    SubfieldUtils.getContentOfFirstSubfield(line, 'E');
            if (subfieldE.equals("m")) {
                found = true;
                line5050 = this.formatter.format(line);
                break;
            }
        }
        if (!found) {
            System.err.println(record);
            return;
        }
        final String title =
                RecordUtils.getContentOfSubfield(record, "4000", 'a');
        final Line line4085 = RecordUtils.getTheOnlyLine(record, "4085");
        String format;
        if (line4085 != null)
            format = "\"" + this.formatter.formatWithoutTag(line4085) + "\"";
        else
            format = "null";
        final String config =
                RecordUtils.getContentOfSubfield(record, "5051", 'K');
        println(StringUtils.concatenate("\t", title, format, line5050, config));
    }

    public static void main(final String[] args) throws IOException {
        final Wiley wiley = new Wiley();
        final String year = "2015";
        wiley.setOutputFile("D:/Analysen/busse/Wiley" + year + ".txt");
        // wiley.setHandler(new IgnoringHandler());
        wiley.processFile("D:/Analysen/busse/Wiley" + year + ".dwl");

    }

}
