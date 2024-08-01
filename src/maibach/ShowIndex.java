package maibach;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

import de.dnb.basics.marc.DDCMarcUtils;
import de.dnb.basics.marc.MarcUtils;

public class ShowIndex {

	public static void main(String[] args) throws FileNotFoundException {
		InputStream input = new FileInputStream("Z:/cbs_sync/ddc/ddc.xml");
		MarcReader marcReader = new MarcXmlReader(input);

		while (marcReader.hasNext()) {
			Record record = marcReader.next();
			if (!DDCMarcUtils.isDDCRecord(record))
				System.out.println(MarcUtils.readableFormat(record));
		}

	}

}
