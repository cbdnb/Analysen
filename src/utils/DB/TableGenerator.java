/**
 *
 */
package utils.DB;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import de.dnb.basics.collections.CollectionUtils;
import de.dnb.gnd.parser.Record;

abstract class TableGenerator {

	static final String FOLDER = "D:/Normdaten/gndDatabase/";

	public TableGenerator(final String path, final Object data,
			final String description) {
		this.path = path;
		this.data = data;
		this.description = description;
	}

	/**
	 * Wohin abgespeichert wird.
	 */
	public final String path;
	/**
	 * in der Regel eine Map oder ein Set.
	 */
	final Object data;

	public final String description;

	public void save() {
		try {
			CollectionUtils.save(data, path);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	abstract void process(Record record);

	public abstract Object getTable()
			throws ClassNotFoundException, IOException;

	public abstract Predicate<String> getStreamFilter();

	@Override
	public String toString() {
		return description;
	}

	static final OB_UB_Generator UB_OB_GENERATOR = new OB_UB_Generator();

	static final Name_Generator NAMES_GENERATOR = new Name_Generator();

	static final Idn2BioName_Generator BIO_NAMES_GENERATOR = new Idn2BioName_Generator();

	static final Idn2RDA_Name_Generator RDA_NAMES_GENERATOR = new Idn2RDA_Name_Generator();

	static final Idn2BioVerweisung_Generator BIO_VERWEISUNG_GENERATOR = new Idn2BioVerweisung_Generator();

	static final Idn2RDA_Verweisungen_Generator RDA_VERWEISUNG_GENERATOR = new Idn2RDA_Verweisungen_Generator();

	static final Person_Instrument_Generator P_INSTR_GENERATOR = new Person_Instrument_Generator();

	static final Person_Profession_Generator P_PROF_GENERATOR = new Person_Profession_Generator();

	static final Werk_Instrument_Generator WORK_INSTR_GENERATOR = new Werk_Instrument_Generator();

	static final Werke_Autor_Generator WORK_AUTOR_GENERATOR = new Werke_Autor_Generator();

	static final Titel_Werke_Generator TITLE_WORK_GENERATOR = new Titel_Werke_Generator();

	static final Musiktitel_Personen_Generator TITLE_PERSON_GENERATOR = new Musiktitel_Personen_Generator();

	static final Titel_BT_Generator TITLE_BT_GENERATOR = new Titel_BT_Generator();

	static final Status_Generator DHS_GENERATOR = new Status_Generator();

	static final Tb_orta_Generator TB_ORTA_GENERATOR = new Tb_orta_Generator();

	/**
	 * Erzeugen die Tabellen der Datenbank.
	 */

	static final List<TableGenerator> TABLES = Arrays.asList(NAMES_GENERATOR,
			RDA_NAMES_GENERATOR, RDA_VERWEISUNG_GENERATOR, BIO_NAMES_GENERATOR,
			BIO_VERWEISUNG_GENERATOR, UB_OB_GENERATOR, P_INSTR_GENERATOR,
			P_PROF_GENERATOR, WORK_INSTR_GENERATOR, WORK_AUTOR_GENERATOR,
			TITLE_WORK_GENERATOR, TITLE_PERSON_GENERATOR, TITLE_BT_GENERATOR,
			DHS_GENERATOR, TB_ORTA_GENERATOR);

}