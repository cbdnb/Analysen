/**
 *
 */
package scheven.hinweis;

import java.util.Set;

import javax.naming.OperationNotSupportedException;

import de.dnb.basics.applicationComponents.tuples.Pair;
import de.dnb.gnd.exceptions.IllFormattedLineException;
import de.dnb.gnd.parser.Record;
import de.dnb.gnd.utils.RecordUtils;

/**
 * @author baumann
 *
 */
public class Test {

	/**
	 * @param args
	 * @throws IllFormattedLineException
	 * @throws OperationNotSupportedException
	 */
	public static void main(final String[] args)
			throws OperationNotSupportedException, IllFormattedLineException {
		final Transformer tr = new Transformer313ab('g');
		final Record record = RecordUtils.readFromClip();
		final Set<Pair<String, String>> kombi = HinweisDBUtil
				.getPairKombi(record);
		System.err.println(kombi);
		final Record newRec = tr.transform(kombi);

		System.out.println(newRec);

	}

}
