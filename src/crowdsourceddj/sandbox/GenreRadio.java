package crowdsourceddj.sandbox;

import com.echonest.api.v4.DynamicPlaylistParams;
import com.echonest.api.v4.DynamicPlaylistSession;
import com.echonest.api.v4.DynamicPlaylistSteerParams;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Playlist;
import com.echonest.api.v4.PlaylistParams;
import com.echonest.api.v4.Song;
import java.io.IOException;

/**
 *
 * @author James
 */
public class GenreRadio {

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
		params.addGenre("trap music");
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
					lastSong = song;
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