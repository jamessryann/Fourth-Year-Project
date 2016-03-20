package crowdsourceddj.clientConnection;

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.echonest.api.v4.DynamicPlaylistSession;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.AddTrackToPlaylistRequest;
import com.wrapper.spotify.methods.PlaylistCreationRequest;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.Playlist;

import crowdsourceddj.genreradio.DynamicPlaylister;
import crowdsourceddj.utils.GenreUtils;

public class VotingServerConnector {
	private static String[] genreList;
	private static String currentGenre;
	private static String username;
	private static String playlistId;
	private static int songCount;
	private static Song lastSong;
	private static DynamicPlaylistSession session;

	public VotingServerConnector(final int portNum) throws IOException {
		// Spotify Set-up
		final String clientId = "ded9a884dd72482c95d93c88246dac5b";
		final String clientSecret = "91044c2cf9a747f38d204037313c09b8";
		final String redirectURI = "http://localhost:8888/callback";

		final Api api = Api.builder().clientId(clientId)
				.clientSecret(clientSecret).redirectURI(redirectURI).build();

		/* Set the necessary scopes that the application will need from the user */
		final List<String> scopes = Arrays.asList("playlist-modify-public");

		/* Set a state. This is used to prevent cross site request forgeries. */
		final String state = "someExpectedStateString";

		System.out.println("Please enter spotify user name:");
		Scanner scanner = new Scanner(System.in);
		/* So songs are added to correct user's account */
		username = scanner.nextLine();

		String authorizeURL = api.createAuthorizeURL(scopes, state);
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().browse(URI.create(authorizeURL));
		} else {
			System.out
					.println("Please follow the following link to get an access token");
			System.out.println(authorizeURL);
		}
		System.out.println("Please enter the access code received.");
		/* Application details necessary to get an access token */
		String code = scanner.nextLine();
		scanner.close();

		/*
		 * Make a token request. Asynchronous requests are made with the
		 * .getAsync method and synchronous requests are made with the .get
		 * method. This holds for all type of requests.
		 */
		final SettableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = api
				.authorizationCodeGrant(code).build().getAsync();

		/* Add callbacks to handle success and failure */
		Futures.addCallback(authorizationCodeCredentialsFuture,
				new FutureCallback<AuthorizationCodeCredentials>() {
					@Override
					public void onSuccess(
							AuthorizationCodeCredentials authorizationCodeCredentials) {
						System.out.println("Success!");
						/* The tokens were retrieved successfully! */
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
						api.setAccessToken(authorizationCodeCredentials
								.getAccessToken());
						api.setRefreshToken(authorizationCodeCredentials
								.getRefreshToken());

						playlistId = "";
						String playlistCreationDate = new SimpleDateFormat(
								"dd/MM/yyyy-HH:mm:ss").format(new Date());
						final PlaylistCreationRequest prequest = api
								.createPlaylist(username,
										"CSDj:" + playlistCreationDate)
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
						api.setRefreshToken(authorizationCodeCredentials
								.getRefreshToken());
						// Start-Up Echonest API
						// Listen for votes
						try {
							ServerSocket host = new ServerSocket(portNum);
							String previousGenre;
							boolean awaitingConnection = true;
							while (awaitingConnection) {
								previousGenre = currentGenre;
								System.out.println("Awaiting Connection");
								new VotingServerThread(host.accept()).start();
								TimeUnit.SECONDS.sleep(3);
								System.out.println("Accepted");
								currentGenre = GenreUtils
										.genreSelect(GenreUtils.getGenres());
								System.out.println("Current Genre is: " + currentGenre + "!");
								if (currentGenre != null
										&& !currentGenre.isEmpty()) {
									if (songCount == 0) {
										DynamicPlaylister
												.addGenre(currentGenre);
										session = DynamicPlaylister
												.createPlaylistSession();

										com.echonest.api.v4.Playlist playlist = DynamicPlaylister
												.createPlaylist(session);
										List<String> tracksToAdd = DynamicPlaylister
												.getSpotifyTrackIds(playlist);

										AddTrackToPlaylistRequest request = api
												.addTracksToPlaylist(username,
														playlistId, tracksToAdd)
												.position(songCount).build();
										try {
											request.get();
										} catch (Exception e) {
											System.out
													.println("Something went wrong adding the track! 1"
															+ e.getMessage());
										}
										songCount += 1;
										api.setRefreshToken(authorizationCodeCredentials
												.getRefreshToken());
									} else if (!currentGenre
											.equalsIgnoreCase(previousGenre)) {
										session.delete();
										DynamicPlaylister.resetParams();
										DynamicPlaylister
												.addGenre(currentGenre);
										session = DynamicPlaylister
												.createPlaylistSession();
										com.echonest.api.v4.Playlist playlist = DynamicPlaylister
												.createPlaylist(session);
										List<String> tracksToAdd = DynamicPlaylister
												.getSpotifyTrackIds(playlist);

										AddTrackToPlaylistRequest request = api
												.addTracksToPlaylist(username,
														playlistId, tracksToAdd)
												.position(songCount).build();
										try {
											request.get();
										} catch (Exception e) {
											System.out
													.println("Something went wrong adding the track! 2"
															+ e.getMessage());
										}
										songCount += 1;
										api.setRefreshToken(authorizationCodeCredentials
												.getRefreshToken());
									} else {
										com.echonest.api.v4.Playlist playlist = DynamicPlaylister
												.nextSong(session);
										List<String> tracksToAdd = DynamicPlaylister
												.getSpotifyTrackIds(playlist);

										AddTrackToPlaylistRequest request = api
												.addTracksToPlaylist(username,
														playlistId, tracksToAdd)
												.position(songCount).build();
										try {
											request.get();
										} catch (Exception e) {
											System.out
													.println("Something went wrong adding the track! 3"
															+ e.getMessage());
										}
										songCount += 1;
										api.setRefreshToken(authorizationCodeCredentials
												.getRefreshToken());
									}
								}
							}
							host.close();
						} catch (IOException | EchoNestException | InterruptedException e) {
							e.printStackTrace();
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

	public static void main(String[] args) {
		try {
			VotingServerConnector vs = new VotingServerConnector(4545);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
