package bernhard;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.Frequency;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

public class Tag5XXOhneLink extends DownloadWorker {

	int i = 0;

	Frequency<String> frequency5XX = new Frequency<String>();

	@Override
	protected void processRecord(final Record record) {
		i++;

		if (RecordUtils.isBibliographic(record)) {
			return;
		}

		final List<Line> lines5XX = GNDUtils.getRelatedLines5XX(record);
		if (lines5XX.isEmpty()) {
			return;
		}

		// Fehlermeldung 999 weist auf Probleme beim Finden der 5XX hin
		if (RecordUtils.containsField(record, "999")) {
			return;
		}

		final String datatype = RecordUtils.getDatatype(record);

		for (final Line line5XX : lines5XX) {
			if (!SubfieldUtils.containsIndicator(line5XX, '9')) {
				// System.err.println(record);
				final String entry = datatype + "\t" + line5XX.getTag().pica3;
				frequency5XX.add(entry);
			}

		}

	}

	public static void main(final String[] args) throws IOException {
		final DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
		Calendar now = Calendar.getInstance();
		System.out.println("Anfang: " + df.format(now.getTime()));

		final Tag5XXOhneLink t = new Tag5XXOhneLink();

		// Relationierte Felder haben folgendes Pica+-Muster
		// (z.B. 550 = 041R):
		final Pattern pattern = Pattern
				.compile(Constants.RS + "0\\d\\dR " + Constants.US);

		final Predicate<String> predicate = x ->
		{
			final Matcher m = pattern.matcher(x);
			return m.find();
		};

		t.setStreamFilter(predicate);
		t.gzipSettings();
		try {
			t.processFile("D:/Normdaten/DNBGND.dat.gz");
		} catch (final Exception e) {

		}

		now = Calendar.getInstance();
		System.out.println("Ende: " + df.format(now.getTime()));

		System.out.println(t.frequency5XX);

	}
}
