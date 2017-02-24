package com.aevobits.games.crazyeights;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aevobits.games.crazyeights.screen.GameMultiplayerScreen;
import com.aevobits.games.crazyeights.screen.GameScreen;
import com.aevobits.games.crazyeights.service.PlayServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.GameHelper;

import java.util.ArrayList;
import java.util.List;


public class AndroidLauncher extends AndroidApplication implements PlayServices {

	protected AdView adView;
	protected View gameView;
	private ActionResolverAndroid actionResolverAndroid;
	private GameHelper gameHelper;
	private RelativeLayout layout;
	private AdView admobView;
	private InterstitialAd interstitialAd;
	private AndroidApplicationConfiguration config;
	private CrazyEightsGame crazyEightsGame;
	private GameMultiplayerScreen gameMultiplayerScreen;

	private static final String AD_UNIT_ID_BANNER = "ca-app-pub-3940256099942544/6300978111";
	private static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712";
	public static final String TAG = "AndroidLauncher";

	// My participant ID in the currently active game
	private String mMyId = null;

	private static final int GAMEHELPER_REQUEST_ID = 1;

	/**
	 * The key for invitation
	 */
	private static final String INVITATION_ARG_KEY = "InvitationKey";

	/**
	 * The Request Id for the waiting room
	 */
	private static final int WAITING_ROOM_REQUEST_ID = 37;

	/**
	 * The Request Id for the invite room
	 */
	private static final int INVITE_ROOM_REQUEST_ID = 38;

	/**
	 * The Request Id for the invite inbox
	 */
	private static final int INVITE_INBOX_REQUEST_ID = 39;

	/**
	 * The min number of auto match players for his game
	 */
	private static final int MIN_AUTOMATCH_PLAYERS = 1;

	/**
	 * The max number of auto match players for his game
	 */
	private static final int MAX_AUTOMATCH_PLAYERS = 1;

	/**
	 * The min number of invited players for his game
	 */
	private static final int MIN_INVITE_PLAYERS = 1;

	/**
	 * The max number of invited players for his game
	 */
	private static final int MAX_INVITE_PLAYERS = 3;

	/**
	 * The min number to launch the game
	 */
	private static final int WAITING_ROOM_MIN_PLAYERS = 1;

	private static final int REQUEST_CAPTURE = 777;

	/**
	 * The Current room
	 */
	private Room mRoom;


	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionResolverAndroid = new ActionResolverAndroid(this);
		config = new AndroidApplicationConfiguration();
		// Do the stuff that initialize() would do for you
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		layout = new RelativeLayout(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(params);

		admobView = createAdView();
		View gameView = createGameView(config);

		layout.addView(gameView);
		layout.addView(admobView);

		setContentView(layout);
		startAdvertising(admobView);

		//if (gameHelper == null) {
			gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
			gameHelper.enableDebugLog(true);
		//}

		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener()
		{
			@Override
			public void onSignInFailed(){
				Gdx.app.log(TAG, "onSignInFailed.");
			}

			@Override
			public void onSignInSucceeded(){
				Gdx.app.log(TAG, "onSignInSucceeded.");
			}


		};

		gameHelper.setup(gameHelperListener);

		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				Toast.makeText(getApplicationContext(), "Finished Loading Interstitial", Toast.LENGTH_SHORT).show();
			}
			@Override
			public void onAdClosed() {
				if (crazyEightsGame.getScreen() instanceof GameScreen){
					startGame();
				}else {
					startMultiplayerGame(null);
				}
				Toast.makeText(getApplicationContext(), "Closed Interstitial", Toast.LENGTH_SHORT).show();
			}
		});
		showOrLoadInterstital();
	}

	private AdView createAdView() {
		adView = new AdView(this);
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId(AD_UNIT_ID_BANNER);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		adView.setLayoutParams(params);
		adView.setBackgroundColor(Color.BLACK);
		return adView;
	}

	private View createGameView(AndroidApplicationConfiguration cfg) {
		crazyEightsGame = new CrazyEightsGame(actionResolverAndroid, this, false);
		gameView = initializeForView(crazyEightsGame, cfg);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		gameView.setLayoutParams(params);
		return gameView;
	}

	private void startAdvertising(AdView adView) {
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice("043E555F988D1CEBF136C59FD0DD2C9B")
				//.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.build();
		adView.loadAd(adRequest);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == WAITING_ROOM_REQUEST_ID) {
			if (resultCode == Activity.RESULT_OK) {
				// We get the current room
				mRoom = data.getParcelableExtra(Multiplayer.EXTRA_ROOM);
				printRoomData("onActivityResult ", mRoom);
				printRoomParticipants(mRoom);
				String creatorId = mRoom.getParticipantIds().get(0);
				if (creatorId.equals(mMyId)){
					startMultiplayerGame(null);
				}
			}
		} else if (requestCode == INVITE_ROOM_REQUEST_ID) {
			if (resultCode == Activity.RESULT_OK) {
				// We get the invited players
				final ArrayList<String> invitedPlayers = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
				// We get information related to the auto match player
				Bundle autoMatchCriteria = null;
				int minAutoMatchPlayers =
						data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
				int maxAutoMatchPlayers =
						data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
				if (minAutoMatchPlayers > 0) {
					autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
							minAutoMatchPlayers, maxAutoMatchPlayers, 0);
				} else {
					autoMatchCriteria = null;
				}
				// We create now the Room with the participants to invite
				RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(mRoomUpdateListener)
						.setRoomStatusUpdateListener(mRoomStatusUpdateListener)
						.setMessageReceivedListener(mMessageReceivedListener);
				// We add the participants
				roomConfigBuilder.addPlayersToInvite(invitedPlayers);
				// We set the auto match criteria if any
				if (autoMatchCriteria != null) {
					roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
				}
				// We create the room
				Games.RealTimeMultiplayer.create(gameHelper.getApiClient(), roomConfigBuilder.build());
				// Here we have to keep the screen on because if the screen go off during the
				// handshake the game ends
				//getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			}
		} else if (requestCode == INVITE_INBOX_REQUEST_ID) {
			if (resultCode == Activity.RESULT_OK) {
				// get the selected invitation
				Bundle extras = data.getExtras();
				Invitation invitation = extras.getParcelable(Multiplayer.EXTRA_INVITATION);
				RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(mRoomUpdateListener)
						.setRoomStatusUpdateListener(mRoomStatusUpdateListener)
						.setMessageReceivedListener(mMessageReceivedListener);
				// We add the information related to the invitation to accept
				roomConfigBuilder.setInvitationIdToAccept(invitation.getInvitationId());
				// We accept the invitation
				Games.RealTimeMultiplayer.join(gameHelper.getApiClient(), roomConfigBuilder.build());
				// We keep the screen on
				//getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			}
		}else {
			//if (requestCode == ){
			gameHelper.onActivityResult(requestCode, resultCode, data);
			//}
		}
	}

	@Override
	public void signIn()
	{
		try
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		}
		catch (Exception e)
		{
			Gdx.app.log(TAG, "Log in failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void signOut()
	{
		try
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					gameHelper.signOut();
				}
			});
		}
		catch (Exception e)
		{
			Gdx.app.log(TAG, "Log out failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void rateGame()
	{
		String str = "Your PlayStore Link";
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
	}

	@Override
	public void unlockAchievement()
	{
		//Games.Achievements.unlock(gameHelper.getApiClient(),getString(R.string.achievement_dum_dum));
	}

	@Override
	public void submitScore(int highScore)
	{
		if (isSignedIn() == true)
		{
			//Games.Leaderboards.submitScore(gameHelper.getApiClient(),getString(R.string.leaderboard_highest), highScore);
		}
	}

	@Override
	public void showAchievement()
	{
		if (isSignedIn() == true)
		{
			//startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient(),
			//		getString(R.string.achievement_dum_dum)), requestCode);
		}
		else
		{
			signIn();
		}
	}

	@Override
	public void showScore()
	{
		if (isSignedIn() == true)
		{
			//startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
			//		getString(R.string.leaderboard_highest)), requestCode);
		}
		else
		{
			signIn();
		}
	}

	@Override
	public boolean isSignedIn()
	{
		return gameHelper.isSignedIn();
	}

	/**
	 * This is the interface implementation that manages the Room lifecycle
	 */
	private final RoomUpdateListener mRoomUpdateListener = new RoomUpdateListener() {
		@Override
		public void onRoomCreated(int statusCode, Room room) {
			if (!statusCodeManaged(statusCode)) {
				Gdx.app.log(TAG,"onRoomCreated ");
				mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(gameHelper.getApiClient()));
				printRoomData("onRoomCreated ", room);
				printRoomParticipants(room);
				Log.i(AndroidLauncher.TAG, "" + room);
				// We launch the waiting room
				launchWaitingRoom(room);
			}
		}

		@Override
		public void onJoinedRoom(int statusCode, Room room) {
			if (!statusCodeManaged(statusCode)) {
				Gdx.app.log(TAG,"onJoinedRoom ");
				printRoomData("onJoinedRoom ", room);
			}
		}

		@Override
		public void onLeftRoom(int statusCode, String roomId) {
			if (!statusCodeManaged(statusCode)) {
				Gdx.app.log(TAG,"onLeftRoom " + roomId);
				Log.i(AndroidLauncher.TAG, "roomId: " + roomId);
			}
		}

		@Override
		public void onRoomConnected(int statusCode, Room room) {
			if (!statusCodeManaged(statusCode)) {
				Gdx.app.log(TAG,"onRoomConnected ");
				printRoomData("onRoomConnected ", room);
			}
		}
	};

	/**
	 * The interface we implement to manage the different status of the Room
	 */
	private final RoomStatusUpdateListener mRoomStatusUpdateListener = new RoomStatusUpdateListener() {
		@Override
		public void onRoomConnecting(Room room) {
			Gdx.app.log(TAG,"onRoomConnecting ");
			printRoomData("onRoomConnected ", room);
		}

		@Override
		public void onRoomAutoMatching(Room room) {
			Gdx.app.log(TAG,"onRoomAutoMatching ");
			printRoomData("onRoomAutoMatching ", room);
		}

		@Override
		public void onPeerInvitedToRoom(Room room, List<String> participantIds) {
			Gdx.app.log(TAG,"onPeerInvitedToRoom ");
			printRoomData("onPeerInvitedToRoom ", room);
			printRoomParticipants(room, participantIds);
		}

		@Override
		public void onPeerDeclined(Room room, List<String> participantIds) {
			if (room!= null){
				Games.RealTimeMultiplayer.leave(gameHelper.getApiClient(), mRoomUpdateListener, room.getRoomId());
				//activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				Gdx.app.log(TAG,"onPeerDeclined ");
				printRoomData("onPeerDeclined ", room);
				printRoomParticipants(room, participantIds);
			}
		}

		@Override
		public void onPeerJoined(Room room, List<String> participantIds) {
			Gdx.app.log(TAG,"onPeerJoined ");
			printRoomData("onPeerJoined ", room);
			printRoomParticipants(room, participantIds);
		}

		@Override
		public void onPeerLeft(Room room, List<String> participantIds) {
			if (room!= null){
				Games.RealTimeMultiplayer.leave(gameHelper.getApiClient(), mRoomUpdateListener, room.getRoomId());
				//activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				Gdx.app.log(TAG,"onPeerLeft ");
				printRoomData("onPeerLeft ", room);
				printRoomParticipants(room, participantIds);
			}
		}

		@Override
		public void onConnectedToRoom(Room room) {
			printRoomData("onPeerLeft ", room);
		}

		@Override
		public void onDisconnectedFromRoom(Room room) {
			Gdx.app.log(TAG,"onDisconnectedFromRoom ");
			printRoomData("onDisconnectedFromRoom ", room);
			Games.RealTimeMultiplayer.leave(gameHelper.getApiClient(), mRoomUpdateListener, room.getRoomId());
			// clear the flag that keeps the screen on
			//activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

		@Override
		public void onPeersConnected(Room room, List<String> participantIds) {
			Gdx.app.log(TAG,"onPeersConnected ");
			printRoomData("onPeersConnected ", room);
			printRoomParticipants(room, participantIds);
		}

		@Override
		public void onPeersDisconnected(Room room, List<String> participantIds) {
			if (room!= null){
				Games.RealTimeMultiplayer.leave(gameHelper.getApiClient(), mRoomUpdateListener, room.getRoomId());
				//activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				Gdx.app.log(TAG,"onPeersDisconnected ");
				printRoomData("onPeersDisconnected ", room);
				printRoomParticipants(room, participantIds);
			}
		}

		@Override
		public void onP2PConnected(String participantId) {
			Gdx.app.log(TAG,"onP2PConnected " + participantId);
			Log.i(AndroidLauncher.TAG, "participantId: " + participantId);
		}

		@Override
		public void onP2PDisconnected(String participantId) {
			Gdx.app.log(TAG,"onP2PDisconnected " + participantId);
			Log.i(AndroidLauncher.TAG, "participantId: " + participantId);
		}
	};

	/**
	 * Implementation of the ReliableMessageSentCallback interface to be notified about the
	 * message sending status
	 */
	private final RealTimeMultiplayer.ReliableMessageSentCallback mReliableMessageSentCallback =
			new RealTimeMultiplayer.ReliableMessageSentCallback() {
				@Override
				public void onRealTimeMessageSent(final int statusCode, final int tokenId,
												  final String recipientParticipantId) {
					// We manage the different type of status
					String logMessage = null;
					switch (statusCode) {
						case GamesStatusCodes.STATUS_REAL_TIME_MESSAGE_SEND_FAILED:
							logMessage = "Message sending failed to player " + recipientParticipantId;
							break;
						case GamesStatusCodes.STATUS_REAL_TIME_ROOM_NOT_JOINED:
							logMessage = "Player" + recipientParticipantId + " has not joined the room";
							break;
						case GamesStatusCodes.STATUS_OK:
						default:
							logMessage = "Message send successfully to " + recipientParticipantId
									+ " with token " + tokenId;
					}
					Gdx.app.log(TAG, logMessage);
				}
			};

	/**
	 * RealTimeMessageReceivedListener implementation for the messages
	 */
	private final RealTimeMessageReceivedListener mMessageReceivedListener = new RealTimeMessageReceivedListener() {
		@Override
		public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
			// We define if the message is reliable or not
			final boolean isReliable = realTimeMessage.isReliable();
			// We get the data
			final String message = new String(realTimeMessage.getMessageData());
			// The sender participant
			final String participantId = realTimeMessage.getSenderParticipantId();

			if (message.substring(0,1).equals("*")){
				startMultiplayerGame(message);
			}else {
				crazyEightsGame.updateMessage(message);
			}
			if (isReliable) {
				Gdx.app.log(TAG,"Receiver message: " + message + " as reliable message from:" + participantId);
			} else {
				Gdx.app.log(TAG,"Receiver message: " + message + " as unreliable message from:" + participantId);
			}
		}
	};

	@Override
	public void quickStartGame(BaseGame game) {
		crazyEightsGame = (CrazyEightsGame) game;
		// We initialize the mask for the user
		final int exclusiveBitMask = 0;
		// We create the Bundle with the auto match criteria
		Bundle autoCriteria = RoomConfig.createAutoMatchCriteria(MIN_AUTOMATCH_PLAYERS, MAX_AUTOMATCH_PLAYERS, 0);
		// We create the Room configuration using the previous bundle. We register the
		// RoomUpdateListener implementation
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(mRoomUpdateListener);
		// We set the auto criteria
		roomConfigBuilder.setAutoMatchCriteria(autoCriteria);
		// We register the listener for the different status for the Room
		roomConfigBuilder.setRoomStatusUpdateListener(mRoomStatusUpdateListener);
		// We register the listener for the different messages sent
		roomConfigBuilder.setMessageReceivedListener(mMessageReceivedListener);
		// We create the room
		Games.RealTimeMultiplayer.create(gameHelper.getApiClient(), roomConfigBuilder.build());
		// Here we have to keep the screen on because if the screen go off during the
		// handshake the game ends
		//activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/**
	 * Utility method that manages status code
	 *
	 * @param statusCode The status code
	 * @return False if everything is ok and true otherwise
	 */
	private boolean statusCodeManaged(final int statusCode) {
		switch (statusCode) {
			case GamesStatusCodes.STATUS_OK:
				return false;
			case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
				Gdx.app.log(TAG,"Reconnect is required");
				return true;
			case GamesStatusCodes.STATUS_REAL_TIME_CONNECTION_FAILED:
				Gdx.app.log(TAG,"Connection failed");
				return true;
			case GamesStatusCodes.STATUS_MULTIPLAYER_DISABLED:
				Gdx.app.log(TAG,"The Multiplayer status is disabled");
				return true;
			case GamesStatusCodes.STATUS_INTERNAL_ERROR:
				Gdx.app.log(TAG,"Internal error");
				return true;

		}
		return false;
	}

	/**
	 * Utility method that prints data for the room
	 *
	 * @return The
	 */
	private void printRoomData(final String methodName, final Room room) {
		if (room != null) {
			Gdx.app.log(TAG, methodName + " Room Id:" + room.getRoomId());
		} else {
			Gdx.app.log(TAG, "No Room!");
		}
	}

	/**
	 * Utility class to print participants of a room
	 *
	 * @param room The room
	 */
	private void printRoomParticipants(final Room room) {
		if (room == null) {
			Gdx.app.log(TAG, "No participants!");
			return;
		}
		final List<String> participantIds = room.getParticipantIds();
		if (participantIds != null) {
			for (String participantId : participantIds) {
				final String participantName = room.getParticipant(participantId).getDisplayName();
				Gdx.app.log(TAG, "Participant: " + participantName);
			}
		}
	}

	/**
	 * Utility class to print participants of a room
	 *
	 * @param room           The room
	 * @param participantIds The participants ids for the room
	 */
	private void printRoomParticipants(final Room room, final List<String> participantIds) {
		if (participantIds != null) {
			for (String participantId : participantIds) {
				final String participantName = room.getParticipant(participantId).getDisplayName();
				Gdx.app.log(TAG, "Participant: " + participantName);
			}
		}
	}

	/**
	 * Utility method to launch waiting room
	 *
	 * @param room The room we're waiting for
	 */
	private void launchWaitingRoom(final Room room) {
		Intent waitingIntent = Games.RealTimeMultiplayer.getWaitingRoomIntent(gameHelper.getApiClient(), room, WAITING_ROOM_MIN_PLAYERS);
		startActivityForResult(waitingIntent, WAITING_ROOM_REQUEST_ID);
	}

	/**
	 * This utility method sends the message with a reliable mechanism
	 *
	 * @param messageToSend The message to send
	 */
	public void sendReliableMessage(final String messageToSend) {
		if (mRoom != null) {
			// We get the List of participants
			final List<Participant> participants = mRoom.getParticipants();
			// We get the message as array of bytes
			byte[] message = messageToSend.getBytes();
			// We send the message to all the participants but me
			final String myId = mRoom.getParticipantId(Games.Players.getCurrentPlayerId(gameHelper.getApiClient()));
			for (Participant p : participants) {
				// If the participants is not me
				final String participantId = p.getParticipantId();
				if (!myId.equals(participantId)) {
					// We send the message in reliable way
					Games.RealTimeMultiplayer.sendReliableMessage(gameHelper.getApiClient(), mReliableMessageSentCallback,
							message, mRoom.getRoomId(), participantId);
				}
			}
		} else {
			Gdx.app.log(TAG,"No room connected");
		}
	}

	private void startMultiplayerGame(final String messageReceived){
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				crazyEightsGame.multiplayerGameScreen(messageReceived);
			}
		});
	}

	private void startGame(){
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				crazyEightsGame.gameScreen();
			}
		});
	}

	public void recordVideo(){
		this.runOnUiThread(new Runnable(){
			public void run(){
				startActivityForResult(Games.Videos.getCaptureOverlayIntent(gameHelper.getApiClient()), REQUEST_CAPTURE);
			}
		});
	}

	@Override
	public void showOrLoadInterstital() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					if (interstitialAd.isLoaded()) {
						interstitialAd.show();
						Toast.makeText(getApplicationContext(), "Showing Interstitial", Toast.LENGTH_SHORT).show();
					}
					else {
						AdRequest interstitialRequest = new AdRequest.Builder().build();
						interstitialAd.loadAd(interstitialRequest);
						Toast.makeText(getApplicationContext(), "Loading Interstitial", Toast.LENGTH_SHORT).show();
					}
				}
			});
		} catch (Exception e) {
		}
	}

}
