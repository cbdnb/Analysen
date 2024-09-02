/**
 *
 */
package baumann.musik;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.dnb.basics.Constants;
import de.dnb.basics.applicationComponents.MyFileUtils;
import de.dnb.basics.applicationComponents.StreamUtils;
import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.gnd.parser.RecordReader;
import de.dnb.gnd.utils.GNDUtils;

/**
 * @author baumann
 *
 */
public class OrteMitKoordinatenDB {

	private static Map<String, Pair<String, Point2D>> idn2Geo = null;
	private static Map<String, Pair<String, Point2D>> idn2GeoAngereichert = null;

	public static final String FOLDER = "D:/Normdaten/";
	private static final String ORTE = FOLDER + "Orte.out";
	private static final String ORTE_ANG = FOLDER + "OrteAngereichert.out";

	@SuppressWarnings("unchecked")
	private static void loadMap()
			throws FileNotFoundException, IOException, ClassNotFoundException {

		final ObjectInputStream objectInputStream = new ObjectInputStream(
				new FileInputStream(ORTE));
		idn2Geo = (Map<String, Pair<String, Point2D>>) objectInputStream
				.readObject();
		MyFileUtils.safeClose(objectInputStream);

	}

	/**
	 *
	 * @return Map oder null
	 */
	public static Map<String, Pair<String, Point2D>> getOrtsnamenPlusKoordinatenMap() {
		if (idn2Geo == null) {
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
		return idn2Geo;
	}

	private static void createMap() throws IOException, FileNotFoundException {
		idn2Geo = new HashMap<>();

		final RecordReader reader = RecordReader
				.getMatchingReader(Constants.Tg);
		reader.forEach(record ->
		{
			final Point2D point = GNDUtils.getCenterPointCoordinates(record);
			if (point == null)
				return;
			String name;
			try {
				name = GNDUtils.getNameOfRecord(record);
			} catch (final IllegalStateException e) {
				return;
			}
			final Pair<String, Point2D> pair = new Pair<String, Point2D>(name,
					point);
			System.err.println(pair);
			idn2Geo.put(record.getId(), pair);
		});

		final ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(ORTE));
		out.writeObject(idn2Geo);
		MyFileUtils.safeClose(out);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Map<String, Point2D> idn2koord = getKoordinatenMap();
		System.out.println(idn2koord.size());
		System.out.println(getAngereichernteMap().size());
	}

	/**
	 * @return
	 */
	public static Map<String, Point2D> getKoordinatenMap() {
		final Map<String, Pair<String, Point2D>> orte = getOrtsnamenPlusKoordinatenMap();
		final Map<String, Point2D> idn2koord = new HashMap<>();
		orte.forEach((idn, pair) -> idn2koord.put(idn, pair.second));
		return idn2koord;
	}

	/**
	 * @param idn2Koo
	 * @param idn2NameKoo
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Pair<String, Point2D>> getAngereichernteMap()
			throws IOException {

		if (idn2GeoAngereichert == null) {
			try {
				loadAngereichert();
			} catch (ClassNotFoundException | IOException e1) {
				try {
					createAngereichert();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}

		return idn2GeoAngereichert;
	}

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 *
	 */
	private static void loadAngereichert()
			throws FileNotFoundException, IOException, ClassNotFoundException {

		final ObjectInputStream objectInputStream = new ObjectInputStream(
				new FileInputStream(ORTE_ANG));
		idn2GeoAngereichert = (Map<String, Pair<String, Point2D>>) objectInputStream
				.readObject();
		MyFileUtils.safeClose(objectInputStream);

	}

	/**
	 * @throws IOException
	 *
	 */
	private static void createAngereichert() throws IOException {
		idn2GeoAngereichert = new HashMap<>();
		final RecordReader ortReader = RecordReader
				.getMatchingReader(Constants.Tg);
		final Map<String, Point2D> idn2Koo = getKoordinatenMap();
		ortReader.forEach(record ->
		{
			final Point2D koo = GNDUtils.getCenterPointCoordinates(record,
					idn2Koo);
			if (koo != null) {
				final String name = GNDUtils.getNameOfRecord(record);
				final Pair<String, Point2D> nameKo = new Pair<>(name, koo);
				idn2GeoAngereichert.put(record.getId(), nameKo);
			}
		});
		StreamUtils.safeClose(ortReader);
		final ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(ORTE_ANG));
		out.writeObject(idn2GeoAngereichert);
		MyFileUtils.safeClose(out);

	}

}
