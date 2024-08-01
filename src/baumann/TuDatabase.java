/**
 *
 */
package baumann;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.parser.tag.GNDTagDB;
import de.dnb.gnd.utils.ContainsTag;
import de.dnb.gnd.utils.WorkUtils;

/**
 * @author baumann
 *
 */
public class TuDatabase {

	private static Map<String, String> tu2Komp = null;
	public static final String FOLDER = "D:/Normdaten/";
	public static final String TITEL_KOMP = FOLDER + "titel2komponist.out";

	/**
	 *
	 * @return Map oder null
	 */
	public static Map<String, String> getTu2Komponist() {
		if (tu2Komp == null) {
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
		return tu2Komp;
	}

	/**
	 * @throws IOException
	 * @throws ClassNotFoundException
	 *
	 */
	@SuppressWarnings("unchecked")
	private static void loadMap() throws ClassNotFoundException, IOException {
		final ObjectInputStream objectInputStream = new ObjectInputStream(
				new FileInputStream(TITEL_KOMP));
		tu2Komp = (HashMap<String, String>) objectInputStream.readObject();
		FileUtils.safeClose(objectInputStream);
	}

	/**
	 * @throws IOException
	 *
	 */
	private static void createMap() throws IOException {
		tu2Komp = new HashMap<>();
		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tu);
		reader.setStreamFilter(
				new ContainsTag("008", 'a', "wim", GNDTagDB.getDB()));

		reader.forEach(record ->
		{

			if (!WorkUtils.isMusicalWork(record))
				return;
			final String tuId = record.getId();

			System.err.println(tuId);
			String kompId;
			try {
				kompId = WorkUtils.getAuthorID(record);
			} catch (final Exception e) {
				return;
			}
			if (kompId == null)
				return;

			tu2Komp.put(tuId, kompId);

		});

		final ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(TITEL_KOMP));
		out.writeObject(tu2Komp);
		FileUtils.safeClose(out);
	}

	public static void main(final String... args) throws IOException {
		final Map<String, String> map = getTu2Komponist();
		System.out.println(map.size());
		// map.entrySet().forEach(System.out::println);

	}

}
