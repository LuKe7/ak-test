package com.example.brightcovetest;

import java.io.Serializable;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.AttributeSet;
import android.util.Log;

import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.view.BrightcoveVideoView;
import com.example.brightcovetest.VideoController.ControlledVideo;

public class BrightcoveWrapper extends BrightcoveVideoView implements OnPreparedListener {

	private OnPreparedListener onPreparedListener;
	private VideoController controller;

	public BrightcoveWrapper(Context context) {
		super(context);
		init();
	}

	public BrightcoveWrapper(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init();
	}

	public BrightcoveWrapper(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
		init();
	}

	
	@Override
	public void onPrepared(MediaPlayer mp) {
		EventEmitter emitter = getEventEmitter();
		if (emitter != null) {
			emitter.emit(EmitEvents.onVideoPrepared.toString());
		}

		if (onPreparedListener != null) {
			onPreparedListener.onPrepared(mp);
		}
	}

	@Override
	public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
		this.onPreparedListener = onPreparedListener;
	}

	private void init() {
		super.setOnPreparedListener(this);
	}

	public void setVideoController(VideoController controller) {
		this.controller = controller;
	}

	@Override
	public void start() {
		super.start();
	}
	
	@Override
	public void setEventEmitter(EventEmitter eventEmitter) {
	    super.setEventEmitter(eventEmitter);
	    if (eventEmitter != null) {
	    	setupEmitter(eventEmitter);
	    }
	}

	private void setupEmitter(EventEmitter eventEmitter) {
		eventEmitter.on(EventType.PROGRESS, new EventListener() {
			@Override
			public void processEvent(Event event) {
				// currentPosition = (Integer) event.properties.get(Event.PLAYHEAD_POSITION);
				
				if(controller != null){
					controller.updateProgress();
				}
			}
		});

		eventEmitter.on(EventType.PAUSE, new EventListener() {
			@Override
			public void processEvent(Event event) {
				// playToggle.setChecked(false);
				if(controller != null){
					controller.onVideoPause();
				}

			}
		});

		eventEmitter.on(EventType.PLAY, new EventListener() {
			@Override
			public void processEvent(Event event) {
				// playToggle.setChecked(true);
				if(controller != null){
					controller.onVideoStart();
				}
			}
		});
		
		eventEmitter.on(EventType.STOP, new EventListener() {
			@Override
			public void processEvent(Event event) {
				// playToggle.setChecked(true);
				if(controller != null){
				controller.onVideoStop();
				}
			}
		});
		
		eventEmitter.on(EmitEvents.completed.toString(), new EventListener() {

			@Override
			public void processEvent(Event arg0) {
				Log.d("", "BC_PLAYER " + EmitEvents.completed.toString());
				controller.onVideoStop();

			}
		});
		

		eventEmitter.once(Event.ERROR, new EventListener() {

			@Override
			public void processEvent(Event event) {
				Serializable errorProperty = event.properties.get("error");
				if (errorProperty != null) {
					Log.d("", "BC_PLAYER: playContentWithBrightcoveId Event.ERROR: " + errorProperty.toString());
					if (errorProperty instanceof IllegalStateException) {
						return;
					}
				}
			}
		});
	}
}
