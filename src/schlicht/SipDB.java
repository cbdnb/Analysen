package schlicht;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.IDNUtils;

public class SipDB {

	static final String FOLDER = "D:/Analysen/schlicht/";
	static final String RECORDS_RELATED = FOLDER + "relRecords.set";
	static final String SIP_RECORDS = FOLDER + "sip.txt";
	static final String IDNS_RELATED = FOLDER + "relIDN.set";

	public static void main(final String[] args) throws IOException {
		final Set<Integer> relIDs = new HashSet<>();
		RecordReader.getMatchingReader(SIP_RECORDS).forEach(sipRecord ->
		{
			relIDs.addAll(GNDUtils.getRelIdns(sipRecord));
		});
		System.err.println("Relationierte: " + relIDs.size());
		CollectionUtils.save(relIDs, IDNS_RELATED);
		final Set<Record> relRecords = new HashSet<>();
		RecordReader.getMatchingReader(Constants.GND).forEach(gndRec ->
		{
			final int idn = IDNUtils.ppn2int(gndRec.getId());
			if (relIDs.contains(idn)) {
				relRecords.add(gndRec);
				relIDs.remove(idn);
			}
		});
		System.err.println("Verbliebene: " + relIDs);
		CollectionUtils.save(relRecords, RECORDS_RELATED);

	}

}
