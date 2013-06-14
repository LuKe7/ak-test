package com.example.brightcovetest;

import java.util.Formatter;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class VideoController extends RelativeLayout {
	private ImageButton rewindBtn;
	private CheckBox playPauseBtn;
	private SeekBar seekbar;
	private CheckBox captionsBtn;
	private ImageButton ffBtn;
	private CheckBox fullscreenBtn;
	private TextView currentTime;
	private TextView videoLength;
	private ControlledVideo controlledVideo;

	private StringBuilder formatBuilder;
	private Formatter formatter;
	protected boolean seekDragging;

	public VideoController(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public VideoController(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public VideoController(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		// TODO Auto-generated constructor stub
	}

	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.video_controler, this, true);
		setBackgroundResource(R.color.translucent_black);

		formatBuilder = new StringBuilder();
		formatter = new Formatter(formatBuilder, Locale.getDefault());

		initControls();
	}

	private void initControls() {
		rewindBtn = (ImageButton) findViewById(R.id.rewind);
		ffBtn = (ImageButton) findViewById(R.id.fastforward);
		playPauseBtn = (CheckBox) findViewById(R.id.play_pause);
		captionsBtn = (CheckBox) findViewById(R.id.captions_btn);
		fullscreenBtn = (CheckBox) findViewById(R.id.fullscreen_btn);
		seekbar = (SeekBar) findViewById(R.id.seek_bar);
		seekbar.setMax(1000);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				seekDragging = false;
				updateProgress();
				// updatePausePlay();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				seekDragging = true;

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (!fromUser) {
					return;
				}

				if (controlledVideo != null) {
					long duration = controlledVideo.getVideoDuration();
					long newposition = (duration * progress) / 1000L;
					controlledVideo.seekTo((int) newposition);
					if (currentTime != null) currentTime.setText(stringForTime((int) newposition));
				}
			}
		});

		currentTime = (TextView) findViewById(R.id.current_time);
		videoLength = (TextView) findViewById(R.id.video_length);

		rewindBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				seekBy(-5000);
			}
		});
		
		ffBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				seekBy(5000);
			}
		});

		playPauseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (controlledVideo != null) {
					if (playPauseBtn.isChecked()) {
						controlledVideo.play();
					}
					else {
						controlledVideo.pause();
					}
				}
				else {
					playPauseBtn.setChecked(false);
				}
			}
		});

		// playPauseBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// if (controlledVideo != null) {
		// if (isChecked) {
		// controlledVideo.play();
		// } else {
		// controlledVideo.pause();
		// }
		// } else {
		// buttonView.setChecked(false);
		// }
		// }
		// });
		//
		// captionsBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		//
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// if (controlledVideo != null) {
		// boolean isOn = captionsBtn.isChecked();
		// //captionsBtn.setChecked(!isOn);
		// Log.d("", "CONTROLLER: HERE");
		// controlledVideo.toggleCaptions(!isOn);
		//
		// }
		// }
		// });
		// captionsBtn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (controlledVideo != null) {
		// boolean isOn = captionsBtn.isChecked();
		// //captionsBtn.setChecked(!isOn);
		// Log.d("", "CONTROLLER: HERE"+isOn);
		// controlledVideo.toggleCaptions(!isOn);
		//
		// }
		// }
		// });

		captionsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (controlledVideo != null) {
					controlledVideo.toggleCaptions(captionsBtn.isChecked());
				}
				else {
					captionsBtn.setChecked(false);
				}

			}
		});

		fullscreenBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (controlledVideo != null) {
					controlledVideo.toggleFullscreen(fullscreenBtn.isChecked());
				}
				else {
					fullscreenBtn.setChecked(false);
				}

			}
		});

	}

	public interface ControlledVideo {
		public long getVideoDuration();
		public long getCurrentTime();
		public boolean isPlaying();
		public boolean hasNextVid();
		public boolean hasPrevVid();
		public void play();
		public void pause();
		public void stop();
		public void nextVid();
		public void prevVid();
		public void seekTo(long position);
		public void toggleFullscreen(boolean toggleOn);
		public void toggleCaptions(boolean toggleOn);
		public int getBufferPercentage();
	}
	public void seekBy(long seekAmt) {
		if (controlledVideo != null) {
			long currentTime = controlledVideo.getCurrentTime();
			long newposition = currentTime + seekAmt;
			controlledVideo.seekTo(newposition);
		}
	}

	public void registerVideo(ControlledVideo video) {
		this.controlledVideo = video;

		captionsBtn.setChecked(false);
		fullscreenBtn.setChecked(false);
		playPauseBtn.setChecked(video != null ? controlledVideo.isPlaying() : false);
	}

	public void setPlayPauseListener(OnClickListener listener) {
		playPauseBtn.setOnClickListener(listener);
	}

	public void setRewindListener(OnClickListener listener) {
		rewindBtn.setOnClickListener(listener);
	}

	public void setCaptionListener(OnClickListener listener) {
		captionsBtn.setOnClickListener(listener);
	}

	public void setFullscreenListener(OnClickListener listener) {
		fullscreenBtn.setOnClickListener(listener);
	}

	public void onVideoStart() {
		// TODO Auto-generated method stub
		if (controlledVideo != null) {
			playPauseBtn.setChecked(true);
		}

	}

	public void onVideoStop() {
		onVideoPause();
		updateProgress();
	}

	public void onVideoPause() {
		if (controlledVideo != null) {
			playPauseBtn.setChecked(false);
		}
	}

	public void updateProgress() {
		if (controlledVideo != null && !seekDragging) {
			long duration = controlledVideo.getVideoDuration();
			long position = controlledVideo.getCurrentTime();
			if (seekbar != null) {
				if (duration > 0) {
					long pos = 1000L * position / duration;
					seekbar.setProgress((int) pos);
				}
				int percent = controlledVideo.getBufferPercentage();
				seekbar.setSecondaryProgress(percent * 10);
			}
			if (videoLength != null) videoLength.setText(stringForTime((int) duration));
			if (currentTime != null) currentTime.setText(stringForTime((int) position));
		}
	}

	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		formatBuilder.setLength(0);
		if (hours > 0) {
			return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
		}
		else {
			return formatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

}
