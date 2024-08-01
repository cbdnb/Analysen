package busse;

import java.io.FileNotFoundException;
import java.util.HashSet;

import de.dnb.basics.applicationComponents.FileUtils;

public class Dateivergleich {

	public static void main(String[] args) throws FileNotFoundException {
		HashSet<String> set1 = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();

		String path =
				"//DNBF-NSC1-P01/DNB-Gesamt/"
						+ "Abt_2/005_Referate/Z_Arbeitsordner/Busse/"
						+ "Abgleich_Korpora_Kurznotationen/";

		FileUtils.readFileIntoCollection(path + "Inhvz_610_IT_Liste.txt", set1);
		FileUtils.readFileIntoCollection(path + "Np_610_IT_liste.txt", set2);

		set1.retainAll(set2); // Schnittmenge

		System.out.println(set1);

	}

}
