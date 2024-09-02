package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.marc.DDCMarcUtils;

/**
 * Erzeugt eine Abbildung ddc -> namen als Map und speichert in
 *
 * D:/Normdaten/ddcNamesAsTreeMap.out .
 *
 * @author baumann
 *
 */
public class DDCNames {

	public static void main(final String[] args)
			throws IOException, ClassNotFoundException {

		/*
		 * ddc -> Registereinträge
		 */
		final TreeMap<String, String> ddc2Caption = new TreeMap<>();

		final InputStream input = new FileInputStream(Constants.DDC_XML);
		final MarcReader marcReader = new MarcXmlReader(input);

		while (marcReader.hasNext()) {
			final Record record = marcReader.next();
			if (!DDCMarcUtils.isDDCRecord(record))
				continue;
			if (DDCMarcUtils.isOverview(record))
				continue;
			if (DDCMarcUtils.isSpan(record))
				continue;
			if (DDCMarcUtils.isSynthesizedNumber(record))
				continue; // ?
			if (DDCMarcUtils.isAddInstructionOld(record))
				continue;
			final String number = DDCMarcUtils
					.getFullClassificationNumber(record);
			final String caption = DDCMarcUtils.getCaption(record);
			ddc2Caption.put(number, caption);
		}

		final ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream("D:/Normdaten/ddcNamesAsTreeMap.out"));
		out.writeObject(ddc2Caption);
		MyFileUtils.safeClose(out);
		System.err.println("Saved");
		final Set<Entry<String, String>> entrySet = ddc2Caption.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			System.err.println(entry);
		}

	}
}
