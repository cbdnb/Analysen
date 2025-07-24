package scheven;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dnb.basics.Constants;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;

public class PID {

	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Ts);
		final Pattern pat = Pattern.compile(" (\\d+\\.){2,}(\\d+)?");
		reader.forEach(rec ->
		{
			final String name = GNDUtils.getNameOfRecord(rec);
			final Matcher matcher = pat.matcher(name);
			if (matcher.find()) {
				final List<String> uris = GNDUtils.getURIs(rec);
				System.out.print(uris.getFirst());
				System.out.print(": ");
				System.out.println(name);
			}
		});
	}

}
