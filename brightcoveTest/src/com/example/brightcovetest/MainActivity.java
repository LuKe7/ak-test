package com.example.brightcovetest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;


import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveVideoView;

public class MainActivity extends Activity {
	private static final String CATALOG_ID = "jskS1rEtQHy9exQKoc14IcMq8v5x2gCP6yaB7d0hraRtO__6HUuxMg..";
	private static final String VIDEO_ID_1 = "1520880903001";
	
	private MediaController controller;
	private Catalog catalog;
	private BrightcoveVideoView brightcoveVideoView;

	private ProgressBar loadingProgressBar;
	private TextView counterDisplay;

	@SuppressLint("NewApi")
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.video_view);
		
		initController(brightcoveVideoView, brightcoveVideoView);
		brightcoveVideoView.setMediaController(controller);

		brightcoveVideoView.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				hideLoadingSpinner();
			}
		});

		playCatalogVideo(CATALOG_ID, VIDEO_ID_1);
		
		// videoView.add(Video.createVideo("http://media.w3.org/2010/05/sintel/trailer.mp4"));
		/*
		 * String[] msgArray = {"didChangeList", "didSelectSource", "pause", "play", "prebufferNextVideo", "readyToPlay", "seekTo", "selectSource", "setSource", "setVideo", "stop", "willChangeVideo",
		 * "bufferedUpdate", "completed", "didSeekTo", "didSetSource", "pause", "didPlay", "progress", "sourceNotPlayable", "stop", "videoSizeKnown", "willChangeVideo"};
		 * 
		 * for (String msg : msgArray) { listenfor(brightcoveVideoView, msg); } brightcoveVideoView.getEventEmitter().once("didSetVideo", new EventListener() {
		 * 
		 * @Override public void processEvent(Event event) { brightcoveVideoView.start(); } }); controller = new MediaController(this);
		 * 
		 * brightcoveVideoView.setMediaController(controller);
		 * 
		 * controller.setMediaPlayer(brightcoveVideoView); controller.setAnchorView(brightcoveVideoView); catalog = new Catalog("jskS1rEtQHy9exQKoc14IcMq8v5x2gCP6yaB7d0hraRtO__6HUuxMg..");
		 * 
		 * catalog.findVideoByID("1520880903001", new VideoListener() {
		 * 
		 * @Override public void onVideo(Video video) { brightcoveVideoView.add(video); brightcoveVideoView.add(video); }
		 * 
		 * @Override public void onError(String error) { throw new RuntimeException(error); } });
		 */

	}

	private void initController(View anchorView, MediaPlayerControl mpc) {
		controller = new CustomizableMediaController(this, true, false);
		controller.setPrevNextListeners(null, null);
		controller.setAnchorView(anchorView);
		controller.setMediaPlayer(mpc);
	}

	private void hideLoadingSpinner() {
		Log.d("", "BC_PLAYER: hiding loading spinner");
		if (loadingProgressBar != null) {
			loadingProgressBar.setVisibility(View.GONE);
		}
	}

	public void showLoadingSpinner() {
		Log.d("", "BC_PLAYER: showing loading spinner");
		if (loadingProgressBar != null) {
			loadingProgressBar.setVisibility(View.VISIBLE);
		}
	}

	public void playContent(String contentUrl) {
		controller.show();
		showLoadingSpinner();
		
		brightcoveVideoView.setVideoPath(contentUrl);
		brightcoveVideoView.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.d("", "BC_PLAYER: video finished playing");
			}
		});
		
		setupEventEmitter();
	}
	
	private void playCatalogVideo(String catalogId, String videoId) {
		setupEventEmitter();

		catalog = new Catalog(catalogId);
		catalog.findVideoByID(videoId, new VideoListener() {
			
			@Override
			public void onError(String error) {
				throw new RuntimeException(error);
			}
			
			@Override
			public void onVideo(Video video) {
				brightcoveVideoView.add(video);
			}
		});
	}
	
	private enum EmitEvents {
		didSetVideo
	}
	
	private void setupEventEmitter() {
		EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
		
		eventEmitter.once(EmitEvents.didSetVideo.toString(), new EventListener() {
			
			@Override
			public void processEvent(Event event) {
				Log.d("", "BC_PLAYER: " + EmitEvents.didSetVideo.toString());
				brightcoveVideoView.start();
			}
		});
		
		eventEmitter.once(Event.ERROR, new EventListener() {
			
			@Override
			public void processEvent(Event event) {
				Log.d("", "BC_PLAYER: playContentWithBrightcoveId Event.ERROR: " + event.toString());
				if (event.properties.get("error") != null && event.properties.get("error") instanceof IllegalStateException) {
					return;
				}
				
			}
		});
	}
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void listenfor(BrightcoveVideoView view, final String msg) {
		if (msg.equals("progress")) {
			view.getEventEmitter().once(msg, new EventListener() {

				@Override
				public void processEvent(Event event) {
					Log.d("", "BC_EVENT: " + msg);
				}
			});
		}
		else {
			view.getEventEmitter().on(msg, new EventListener() {

				@Override
				public void processEvent(Event event) {
					Log.d("", "BC_EVENT: " + msg);
				}
			});
		}
	}
}
