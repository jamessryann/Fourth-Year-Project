package crowdsourceddj.sandbox;

import com.echonest.api.v4.DynamicPlaylistParams;
import com.echonest.api.v4.DynamicPlaylistSession;
import com.echonest.api.v4.DynamicPlaylistSteerParams;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Playlist;
import com.echonest.api.v4.PlaylistParams;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.Track;

import crowdsourceddj.utils.GenreUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author James
 */
public class GenreRadio {
	public static void addGenre(String genre) {
		GenreUtils.addGenre(genre);
	}

	public static void main(String[] args) throws EchoNestException,
			IOException {
		EchoNestAPI en = new EchoNestAPI("FXUOGXLAT1LT9BJ4M");
		for (String genre : en.listGenres()) {
			System.out.println(genre);
		}
		Song lastSong = null;
		en.setTraceSends(false);

		DynamicPlaylistParams params = new DynamicPlaylistParams();
		params.setType(PlaylistParams.PlaylistType.GENRE_RADIO);
		params.addGenre("rap");
		params.setMinEnergy(.5f);
		params.setMinDanceability(.75f);
		params.setArtistMinFamiliarity(.7f);
		params.includeAudioSummary();
		DynamicPlaylistSession session = en.createDynamicPlaylist(params);

		boolean done = false;
		while (!done) {
			String keys = "nsfd+-";

			System.out.println();
			System.out
					.print("(n)ext (s)kip (f)av (d)one (+)faster (-)slower ->");

			int cv;
			do {
				cv = System.in.read();
			} while (keys.indexOf(cv) < 0);

			char c = (char) cv;

			// System.out.println("c " + c + " " + cv);

			if (c == 'd') {
				done = true;
			}

			if (c == 'f') {
				session.feedback(
						DynamicPlaylistSession.FeedbackType.favorite_song,
						"last");
			}

			if (c == 's') {
				session.feedback(DynamicPlaylistSession.FeedbackType.skip_song,
						"last");
			}

			if (c == 'n') {
				Playlist playlist = session.next();
				System.out.println(playlist.getSongs().size());
				for (Song song : playlist.getSongs()) {
					System.out.println(song.getTitle());
					System.out.println(song.getArtistName());
					System.out.printf("Dance: %f\n", song.getDanceability());
					System.out.printf("Energy: %f\n", song.getEnergy());
					System.out.printf("Tempo: %f\n", song.getTempo());
					System.out.printf("ID:  %s\n", song.getID());
					System.out.printf("Artist Familiarity: %f\n",
							song.getArtistFamiliarity());
					addGenre("rock");
					String[] genres = GenreUtils.getGenres();
					for (String g : genres) {
						System.out.println(g);
					}

					List<String> spotifyIds = new ArrayList<String>();
					Track track = song.getTrack("spotify-WW");
					System.out.println(track.getForeignID() + " "
							+ song.getTitle() + " by " + song.getArtistName());
					Pattern p = Pattern.compile("spotify(-.+?):");
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
			}

			if (c == '+') {
				if (lastSong != null) {
					DynamicPlaylistSteerParams steerParams = new DynamicPlaylistSteerParams();
					steerParams.addTargetValue(
							DynamicPlaylistSteerParams.SteeringParameter.tempo,
							(float) lastSong.getTempo() * 1.2f);
					System.out.println("steer " + steerParams);
					session.steer(steerParams);

				}
			}

			if (c == '-') {
				if (lastSong != null) {
					DynamicPlaylistSteerParams steerParams = new DynamicPlaylistSteerParams();
					steerParams.addTargetValue(
							DynamicPlaylistSteerParams.SteeringParameter.tempo,
							(float) lastSong.getTempo() * .8f);
					System.out.println("steer " + steerParams);

					session.steer(steerParams);
				}
			}
		}
	}
}