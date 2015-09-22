package crowdsourceddj.utils;

import crowdsourceddj.utils.GenreUtils;
import static org.junit.Assert.*;

import org.junit.Test;

public class GenreUtilsTest {
	private String[] genreList1 = {"pop","country","rap","pop","pop"};
	private String[] genreList2 = {"rap","rap","pop","rap","pop","pop"};
	private String[] genreList3 = {};
	@Test
	public void testGenreSelect() {
		String genre = GenreUtils.genreSelect(genreList1);
		assertEquals(genre,"pop");
	}

	@Test
	public void testTieBreak() {
		String genre = GenreUtils.genreSelect(genreList2);
		assertEquals(genre,"rap");
	}

	@Test
	public void testEmpty() {
		String genre = GenreUtils.genreSelect(genreList3);
		assertEquals(genre,"");
	}

}
