/**
 *
 */
package baumann.musik;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.filtering.FilterUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.line.Line;
import de.dnb.gnd.utils.GNDUtils;
import de.dnb.gnd.utils.RecordUtils;
import de.dnb.gnd.utils.SubfieldUtils;

/**
 * @author baumann
 *
 */
public class MusikberufeDatabase {

	public static final String FOLDER = "D:/Normdaten/";
	public static final String MUSIK_BERUFE = FOLDER + "Musikberufe.out";
	private static Map<String, String> idn2Musikberuf = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Map<String, String> musber = getMusikberufe();
		musber.forEach((idn, name) -> System.out.println(idn + " -> " + name));
	}

	/**
	 *
	 * @return Map oder null
	 */
	public static Map<String, String> getMusikberufe() {
		if (idn2Musikberuf == null) {
			try {
				loadMap();
			} catch (ClassNotFoundException | IOException e1) {
				try {
					createMap();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		return idn2Musikberuf;
	}

	@SuppressWarnings("unchecked")
	private static void loadMap()
			throws IOException, FileNotFoundException, ClassNotFoundException {
		final ObjectInputStream objectInputStream = new ObjectInputStream(
				new FileInputStream(MUSIK_BERUFE));
		idn2Musikberuf = (HashMap<String, String>) objectInputStream
				.readObject();
		MyFileUtils.safeClose(objectInputStream);
	}

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void createMap() throws IOException, FileNotFoundException {
		// Hole alle MusikSWW:
		final Map<String, String> idn2Beruf = new HashMap<>();
		final RecordReader readerSachSWW = RecordReader
				.getMatchingReader(Constants.Ts);
		readerSachSWW.stream()
				.filter(record -> GNDUtils
						.containsGNDClassificationsTrunk(record, "14."))
				.forEach(record ->
				{
					final String nameOfRecord = GNDUtils
							.getNameOfRecord(record);
					idn2Beruf.put(record.getId(), nameOfRecord);
				});

		System.err.println("---Tp-----");
		idn2Musikberuf = new HashMap<>();
		final RecordReader readerPers = RecordReader
				.getMatchingReader(Constants.Tp);
		readerPers.forEach(record ->
		{

			final List<Line> linesBer = RecordUtils.getLinesWithSubfield(record,
					"550", '4', "beru|berc");

			final List<String> idnsBeruf = FilterUtils.map(linesBer,
					line -> SubfieldUtils.getContentOfFirstSubfield(line, '9'));

			for (final String idn : idnsBeruf) {
				if (idn2Beruf.containsKey(idn)) {
					final String beruf = idn2Beruf.get(idn);
					idn2Musikberuf.put(idn, beruf);
				}
			}
		});
		final ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(MUSIK_BERUFE));
		out.writeObject(idn2Musikberuf);
		MyFileUtils.safeClose(out);
	}

}
