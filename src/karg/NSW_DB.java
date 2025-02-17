package karg;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.BibRecUtils;

public class NSW_DB {

	static final String NSW_DB = "D:/Analysen/karg/nsw.out";

	public static void main(final String[] args) throws IOException {
		final RecordReader reader = RecordReader.getMatchingReader(
				"D:/pica-0.22.0-x86_64-pc-windows-msvc/temp.dat.gz");
		final Set<String> nsws = new TreeSet<>();
		reader.forEach(record ->
		{
			final String abku = BibRecUtils.getAbkuerzungNSW(record);
			if (abku != null) {
				final boolean eingef = nsws.add(abku);
				if (!eingef)
					System.out.println(record.getId());
			} else {
				// System.out.println(record.getId());
			}

		});
		CollectionUtils.save(nsws, NSW_DB);
		System.out.println(nsws.size());
		System.out.println(nsws);

	}

}
