/**
 *
 */
package baumann.alteSWW;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import de.dnb.basics.applicationComponents.FileUtils;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.DownloadWorker;
import de.dnb.gnd.utils.SWDUtils;

/**
 * @author baumann
 *
 */
public final class SWD1 extends DownloadWorker {

	/**
	 *
	 */
	public static final String DATEI = "D:/analysen/baumann/swd.out";
	static Map<String, String> name2idn = null;

	@Override
	protected void processRecord(final Record record) {
		// System.err.println(record.getId());
		final String def = SWDUtils.getDefinition(record);
		if (def == null)
			return;
		if (def.contains("=DB") || def.contains("SWL")) {
			final String name = SWDUtils.getName(record, false);
			if (name != null) {

				name2idn.put(name, record.getId());
			}
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static Map<String, String> getSWD2IDN() throws IOException {
		if (name2idn == null) {
			try {
				loadMap();
			} catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
				try {
					createMap();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		return name2idn;
	}

	@SuppressWarnings("unchecked")
	private static void loadMap()
			throws FileNotFoundException, IOException, ClassNotFoundException {
		final ObjectInputStream objectInputStream = new ObjectInputStream(
				new FileInputStream(DATEI));
		name2idn = (Map<String, String>) objectInputStream.readObject();
		FileUtils.safeClose(objectInputStream);

	}

	private static void createMap() throws IOException, FileNotFoundException {
		name2idn = new HashedMap<>();
		final SWD1 swd1 = new SWD1();
		swd1.setInputFolder("D:/Normdaten/archiv/swd-div");
		swd1.setFilePrefix("swd-");
		swd1.processAllFiles();

		final OutputStream stream = new FileOutputStream(DATEI);
		final ObjectOutputStream out = new ObjectOutputStream(stream);
		out.writeObject(name2idn);
		StreamUtils.safeClose(out);
	}

	public static void main(final String[] args) throws IOException {
		System.out.println(getSWD2IDN().size());
	}

}
