/**
 *
 */
package mengel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;

import de.dnb.basics.Constants;
import de.dnb.basics.collections.Multimap;
import de.dnb.basics.collections.TreeMultimap;
import de.dnb.basics.marc.MarcIterator;
import de.dnb.basics.marc.MarcSubfieldComparator;
import de.dnb.basics.marc.MarcUtils;

/**
 * @author baumann
 *
 */
public class TagsUndUnterfelderDDC {

	public static Comparator<Character> myCharComparator = new MarcSubfieldComparator();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final MarcIterator iterator = MarcIterator
				.getFromFile(Constants.DDC_XML);
		final Multimap<String, Character> tag2sub = new TreeMultimap<>();
		iterator.forEach(rec ->
		{
			if (!MarcUtils.isClassification(rec))
				return;
			rec.getVariableFields().forEach(field ->
			{
				if (field instanceof ControlField) {
					final String tag = field.getTag();
					tag2sub.add(tag);
				}
				if (field instanceof DataField) {
					final DataField dataField = (DataField) field;
					final String tag = dataField.getTag();
					dataField.getSubfields().forEach(subf ->
					{
						final char ind = subf.getCode();
						tag2sub.add(tag, ind);
					});
				}
			});
		});

		tag2sub.forEach(tag ->
		{
			System.out.println(tag);

			final ArrayList<Character> orderedList = new ArrayList<>(
					tag2sub.getNullSafe(tag));
			Collections.sort(orderedList, myCharComparator);
			orderedList.forEach(c -> System.out.println("\t" + c));

		});

	}

}
