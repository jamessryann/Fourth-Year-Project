package crowdsourceddj.utils;

import java.util.Arrays;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

/**
 * @author James
 *
 */
public class GenreUtils {

	/**
	 * Determines the most popular genre out of an array of strings (genres)
	 * that represent user votes. Ties are broken by choosing the genre that was
	 * voted on first.
	 * 
	 * @param genreList
	 * @return genre
	 */
	public static String genreSelect(String[] genreList) {
		try {
			String genre = "";
			Multiset<String> multiset = HashMultiset.create(Arrays
					.asList(genreList));
			ImmutableMultiset<String> immumset = Multisets
					.copyHighestCountFirst(multiset);
			genre = immumset.toArray()[0].toString();
			return genre;
		} catch (ArrayIndexOutOfBoundsException e) {
			String genre = "";
			return genre;
		}
	}
}
