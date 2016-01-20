package crowdsourceddj.sandbox;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.AddTrackToPlaylistRequest;
import com.wrapper.spotify.methods.AlbumRequest;
import com.wrapper.spotify.methods.PlaylistCreationRequest;
import com.wrapper.spotify.models.Album;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.Playlist;

public class SpotifyPlaylist {
	public static void main(String[] args) {
		Api api = Api.DEFAULT_API;
		// Create a request object for the type of request you want to make
//		AlbumRequest request = api.getAlbum("5wYgsQRpjIoYHGdGvpbsBf").build();
//		// Retrieve an album
//		try {
//			Album album = request.get();
//
//			// Print the genres of the album
//			String titles = album.getName();
//			System.out.println(titles);
//
//		} catch (Exception e) {
//			System.out.println("Could not get albums.");
//		}

		final String clientId = "ded9a884dd72482c95d93c88246dac5b";
		final String clientSecret = "91044c2cf9a747f38d204037313c09b8";
		final String redirectURI = "http://localhost:8888/callback";

		final Api api2 = Api.builder().clientId(clientId)
				.clientSecret(clientSecret).redirectURI(redirectURI).build();

		/* Set the necessary scopes that the application will need from the user */
		final List<String> scopes = Arrays.asList("playlist-modify-public");

		/* Set a state. This is used to prevent cross site request forgeries. */
		final String state = "someExpectedStateString";

		String authorizeURL = api2.createAuthorizeURL(scopes, state);
		System.out.println("Please follow the following link to get an access token");
		System.out.println(authorizeURL);
		System.out.println("Please enter the access code received.");
		Scanner scanner = new Scanner(System.in);
		/* Application details necessary to get an access token */
		String code = scanner.nextLine();

		/*
		 * Make a token request. Asynchronous requests are made with the
		 * .getAsync method and synchronous requests are made with the .get
		 * method. This holds for all type of requests.
		 */
		final SettableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = api2
				.authorizationCodeGrant(code).build().getAsync();

		/* Add callbacks to handle success and failure */
		Futures.addCallback(authorizationCodeCredentialsFuture,
				new FutureCallback<AuthorizationCodeCredentials>() {
					@Override
					public void onSuccess(
							AuthorizationCodeCredentials authorizationCodeCredentials) {
						/* The tokens were retrieved successfully! */
						String playlistId = "";
						System.out
								.println("Successfully retrieved an access token! "
										+ authorizationCodeCredentials
												.getAccessToken());
						System.out.println("The access token expires in "
								+ authorizationCodeCredentials.getExpiresIn()
								+ " seconds");
						System.out
								.println("Luckily, I can refresh it using this refresh token! "
										+ authorizationCodeCredentials
												.getRefreshToken());

						/*
						 * Set the access token and refresh token so that they
						 * are used whenever needed
						 */
						api2.setAccessToken(authorizationCodeCredentials
								.getAccessToken());
						api2.setRefreshToken(authorizationCodeCredentials
								.getRefreshToken());

						final PlaylistCreationRequest prequest = api2
								.createPlaylist("jamessryann", "test5")
								.publicAccess(true).build();
						try {
							final Playlist playlist = prequest.get();
							playlistId = playlist.getId();
							System.out
									.println("You just created this playlist!");
							System.out.println("Its title is "
									+ playlist.getName());
							System.out.println("Its ID is " + playlistId);
						} catch (Exception e) {
							System.out.println("Something went wrong!"
									+ e.getMessage());
						}

						api2.setRefreshToken(authorizationCodeCredentials
								.getRefreshToken());
						final List<String> tracksToAdd = Arrays.asList(
								"spotify:track:4BYGxv4rxSNcTgT3DsFB9o",
								"spotify:track:0BG2iE6McPhmAEKIhfqy1X");

						// Index starts at 0
						final int insertIndex = 0;

						final AddTrackToPlaylistRequest request = api2
								.addTracksToPlaylist("jamessryann", playlistId,
										tracksToAdd).position(insertIndex)
								.build();

						try {
							request.get();
						} catch (Exception e) {
							System.out.println("Something went wrong!"
									+ e.getMessage());
						}
					}

					@Override
					public void onFailure(Throwable throwable) {
						/*
						 * Let's say that the client id is invalid, or the code
						 * has been used more than once, the request will fail.
						 * Why it fails is written in the throwable's message.
						 */

					}
				});
	}
}
