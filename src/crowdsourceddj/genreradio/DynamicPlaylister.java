package crowdsourceddj.genreradio;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.echonest.api.v4.DynamicPlaylistParams;
import com.echonest.api.v4.DynamicPlaylistSession;
import com.echonest.api.v4.DynamicPlaylistSteerParams;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Playlist;
import com.echonest.api.v4.PlaylistParams;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.Track;

public class DynamicPlaylister {

	private static DynamicPlaylistParams params = new DynamicPlaylistParams();
	
	private static Pattern p = Pattern.compile("spotify(-.+?):");

	public static DynamicPlaylistSession createPlaylistSession()
			throws EchoNestException {
		EchoNestAPI en = new EchoNestAPI("FXUOGXLAT1LT9BJ4M");
		params.setType(PlaylistParams.PlaylistType.GENRE_RADIO);
		params.setMinEnergy(.5f);
		params.setMinDanceability(.75f);
		params.setArtistMinFamiliarity(.7f);
		params.includeAudioSummary();
		DynamicPlaylistSession session = en.createDynamicPlaylist(params);
		return session;
	}

	public static void resetParams() {
		params = new DynamicPlaylistParams();
	}

	public static void addGenre(String genre) {
		params.addGenre(genre);
	}

	public static void setMinEnergy(float f) {
		params.setMinEnergy(f);
	}

	public static void setMinArtistFamiliarity(float f) {
		params.setArtistMinFamiliarity(f);
	}

	public static void setMinDanceability(float f) {
		params.setMinDanceability(f);
	}

	public static Playlist createPlaylist(DynamicPlaylistSession session) {
		Playlist playlist = null;
		try {
			playlist = session.next();
		} catch (EchoNestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return playlist;
	}
	
	public static Playlist nextSong(DynamicPlaylistSession session)
	{
		Playlist playlist = null;
		try {
			playlist = session.next();
		} catch (EchoNestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return playlist;
	}
	
	public static List<String> getSpotifyTrackIds(Playlist playlist)
			throws EchoNestException {
		List<String> spotifyIds = new ArrayList<String>();
		for (Song song : playlist.getSongs()) {
			Track track = song.getTrack("spotify-WW");
			System.out.println(track.getForeignID() + " " + song.getTitle()
					+ " by " + song.getArtistName());
			Matcher m = p.matcher(track.getForeignID());
			if (m.find()) {
				System.out.println(m.group(1));
				spotifyIds.add(track.getForeignID().replaceAll(
						"(-.+?):", ":"));
			} else {
				spotifyIds.add(track.getForeignID());
			}
			for (String s : spotifyIds) {
				System.out.println(s);
			}
		}
		return spotifyIds;
	}

}
