package scheven;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.strings.StringUtils;
import de.dnb.basics.collections.Frequency;
import de.dnb.basics.filtering.StringContains;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SubjectUtils;

public class T2_43_V2 extends DownloadWorker {

	int i = 0;

	Frequency<String> frequencyGermany = new Frequency<String>();

	@Override
	protected void processRecord(final Record record) {
		i++;

		final String prefix = "43";

		final List<String> list = SubjectUtils.getTable2Notations(record);
		if (list.isEmpty())
			return;
		if (!StringUtils.containsPrefix(list, prefix))
			return;

		final Set<String> table2 = new HashSet<String>(list);
		for (final String string : table2) {
			if (string.startsWith(prefix)) {
				frequencyGermany.add(string);
				System.err.println(i + "\t" + string);
			}
		}

	}

	public static void main(final String[] args) throws IOException {
		final DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
		Calendar now = Calendar.getInstance();
		System.out.println("Anfang: " + df.format(now.getTime()));
		final T2_43_V2 v2 = new T2_43_V2();
		final Predicate<String> streamFilter = new StringContains(
				Constants.RS
				+ "045F " + Constants.US);
		v2.setStreamFilter(streamFilter);
		try {
			v2.processGZipFile("Z:/cbs/zen/vollabzug/aktuell/Pica+/DNBtitelgesamt.dat.gz");
		} catch (final Exception e) {

		}
		now = Calendar.getInstance();
		System.out.println("Ende: " + df.format(now.getTime()));

		System.out.println(v2.frequencyGermany);

	}

}
