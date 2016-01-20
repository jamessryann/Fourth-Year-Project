package crowdsourceddj.utils;

import java.util.ArrayList;
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
	
	private static ArrayList<String> genres = new ArrayList<String>();

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
			for(Object item : immumset.toArray())
			{
				System.out.println(item.toString());
			}
			genre = immumset.toArray()[0].toString();
			
			return genre;
		} catch (ArrayIndexOutOfBoundsException e) {
			String genre = "";
			return genre;
		}
	}
	
	public static void addGenre(String genre)
	{
		genres.add(genre);
	}
	
	public static String[] getGenres()
	{
		return genres.toArray(new String[genres.size()]);
	}
}
