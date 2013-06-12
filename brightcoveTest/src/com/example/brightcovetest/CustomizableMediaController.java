package com.example.brightcovetest;

import java.util.Formatter;
import java.util.Locale;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class CustomizableMediaController extends MediaController {
	private static final int DEFAULT_TIMEOUT = 3000;

	private class Messages {
		private static final int FADE_OUT = 1;
		private static final int SHOW_PROGRESS = 2;
	}

	private ProgressBar mProgress;
	private TextView mEndTime, mCurrentTime;
	private ImageButton mPauseButton;
	private ImageButton mFfwdButton;
	private ImageButton mRewButton;
	private ImageButton mNextButton;
	private ImageButton mPrevButton;

	private MediaPlayerControl mPlayer;
	private View.OnClickListener mNextListener, mPrevListener;

	private boolean mDragging;
	private boolean mUseFastForward;
	private boolean mListenersSet;
	private boolean mUseCustomLayout;

	StringBuilder mFormatBuilder;
	Formatter mFormatter;

	public CustomizableMediaController(Context context, boolean useFastForward) {
		super(context, useFastForward);
		mUseFastForward = useFastForward;
	}

	public CustomizableMediaController(Context context) {
		super(context);
	}

	public CustomizableMediaController(Context context, boolean useFastForward, boolean useCustomLayout) {
		this(context, useFastForward);
		mUseCustomLayout = useCustomLayout;
	}

	public void setAnchorView(View view) {
		super.setAnchorView(view);

		if (useCustomLayout()) {
			FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			removeAllViews();
			View v = makeControllerView();
			addView(v, frameParams);
		}
	};

	private View makeControllerView() {
		View controller;
		int layoutId = getControllerLayoutID();

		LayoutInflater inflate = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		controller = inflate.inflate(layoutId, null);

		initControllerView(controller);
		return controller;
	}

	private boolean useCustomLayout() {
		return mUseCustomLayout && getControllerLayoutID() != NO_ID;
	}

	protected int getControllerLayoutID() {
		return R.layout.media_controller_layout;
		// return NO_ID;
	}

	protected int getPlayPauseButtonID() {
		return R.id.pause_play_btn;
	}

	protected int getFFwdButtonID() {
		return R.id.ffwd_btn;
	}

	protected int getRewButtonID() {
		return R.id.rew_btn;
	}

	protected int getNextButtonID() {
		return R.id.next_btn;
	}

	protected int getPrevButtonID() {
		return R.id.prev_btn;
	}

	protected int getProgressBarID() {
		return R.id.mediacontroller_progress;
	}

	protected int getEndTimeID() {
		return R.id.end_time;
	}

	protected int getCurrentTimeID() {
		return R.id.current_time;
	}

	private void initControllerView(View v) {
		mPauseButton = (ImageButton) v.findViewById(getPlayPauseButtonID());
		if (mPauseButton != null) {
			mPauseButton.requestFocus();
			mPauseButton.setOnClickListener(mPauseListener);
		}

		mFfwdButton = (ImageButton) v.findViewById(getFFwdButtonID());
		if (mFfwdButton != null) {
			mFfwdButton.setOnClickListener(mFfwdListener);
			mFfwdButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
		}

		mRewButton = (ImageButton) v.findViewById(getRewButtonID());
		if (mRewButton != null) {
			mRewButton.setOnClickListener(mRewListener);
			mRewButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
		}

		mNextButton = (ImageButton) v.findViewById(getNextButtonID());
		if (mNextButton != null && !mListenersSet) {
			mNextButton.setVisibility(View.GONE);
		}

		mPrevButton = (ImageButton) v.findViewById(getPrevButtonID());
		if (mPrevButton != null && !mListenersSet) {
			mPrevButton.setVisibility(View.GONE);
		}

		mProgress = (ProgressBar) v.findViewById(getProgressBarID());
		if (mProgress != null) {
			if (mProgress instanceof SeekBar) {
				SeekBar seeker = (SeekBar) mProgress;
				seeker.setOnSeekBarChangeListener(mSeekListener);
			}
			mProgress.setMax(1000);
		}

		mEndTime = (TextView) v.findViewById(getEndTimeID());
		mCurrentTime = (TextView) v.findViewById(getCurrentTimeID());
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

		installPrevNextListeners();
	}

	@Override
	public void setPrevNextListeners(OnClickListener next, OnClickListener prev) {
		if (useCustomLayout()) {
			mNextListener = next;
			mPrevListener = prev;
			mListenersSet = true;

			installPrevNextListeners();

			if (mNextButton != null) {
				mNextButton.setVisibility(View.VISIBLE);
			}
			if (mPrevButton != null) {
				mPrevButton.setVisibility(View.VISIBLE);
			}
		}
		else {
			super.setPrevNextListeners(next, prev);
		}
	}

	@Override
	public void show(int timeout) {
		if (useCustomLayout()) {

			if (!isShowing() && mPlayer != null) {
				setProgress();
				if (mPauseButton != null) {
					mPauseButton.requestFocus();
				}
				disableUnsupportedButtons();
				updatePausePlay();
			}

			mHandler.sendEmptyMessage(Messages.SHOW_PROGRESS);
			Message msg = mHandler.obtainMessage(Messages.FADE_OUT);
			if (timeout != 0) {
				mHandler.removeMessages(Messages.FADE_OUT);
				mHandler.sendMessageDelayed(msg, timeout);
			}
		}

		super.show(timeout);
	}

	@Override
	public void hide() {
		if (useCustomLayout()) {
			if (mPlayer == null) {
				return;
			}

			if (isShowing()) {
				mHandler.removeMessages(Messages.SHOW_PROGRESS);
			}
		}

		super.hide();
	}

	private void disableUnsupportedButtons() {
		try {
			if (mPauseButton != null && !mPlayer.canPause()) {
				mPauseButton.setEnabled(false);
			}
			if (mRewButton != null && !mPlayer.canSeekBackward()) {
				mRewButton.setEnabled(false);
			}
			if (mFfwdButton != null && !mPlayer.canSeekForward()) {
				mFfwdButton.setEnabled(false);
			}
		}
		catch (IncompatibleClassChangeError ex) {
			// We were given an old version of the interface, that doesn't have
			// the canPause/canSeekXYZ methods. This is OK, it just means we
			// assume the media can be paused and seeked, and so we don't disable
			// the buttons.
		}
	}

	@Override
	public void setMediaPlayer(MediaPlayerControl player) {
		super.setMediaPlayer(player);

		if (useCustomLayout()) {
			mPlayer = player;
			updatePausePlay();
		}
	}

	private void updatePausePlay() {
		if (mPauseButton == null) {
			return;
		}
		if (mPlayer.isPlaying()) {
			mPauseButton.setImageResource(android.R.drawable.ic_media_pause); // Pause image
		}
		else {
			mPauseButton.setImageResource(android.R.drawable.ic_media_play); // Play image
		}
	}

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			show(3600000);

			mDragging = true;
			mHandler.removeMessages(Messages.SHOW_PROGRESS);
		}

		public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
			if (!fromuser) {
				return;
			}

			long duration = mPlayer.getDuration();
			long newposition = (duration * progress) / 1000L;
			mPlayer.seekTo((int) newposition);
			if (mCurrentTime != null) mCurrentTime.setText(stringForTime((int) newposition));
		}

		public void onStopTrackingTouch(SeekBar bar) {
			mDragging = false;
			setProgress();
			updatePausePlay();
			show(DEFAULT_TIMEOUT);

			mHandler.sendEmptyMessage(Messages.SHOW_PROGRESS);
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int pos;
			switch (msg.what) {
				case Messages.FADE_OUT :
					hide();
					break;
				case Messages.SHOW_PROGRESS :
					pos = setProgress();
					if (!mDragging && isShowing() && mPlayer.isPlaying()) {
						msg = obtainMessage(Messages.SHOW_PROGRESS);
						sendMessageDelayed(msg, 1000 - (pos % 1000));
					}
					break;
			}
		}
	};

	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
		}
		else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	private void seekMedia(int seekAmt) {
		if (mPlayer != null) {
			int pos = mPlayer.getCurrentPosition();
			pos += seekAmt;
			mPlayer.seekTo(pos);
			setProgress();

			show(DEFAULT_TIMEOUT);
		}
	}

	private View.OnClickListener mRewListener = new View.OnClickListener() {
		public void onClick(View v) {
			seekMedia(-5000);
		}
	};

	private View.OnClickListener mFfwdListener = new View.OnClickListener() {
		public void onClick(View v) {
			seekMedia(15000);
		}
	};

	private int setProgress() {
		if (mPlayer == null || mDragging) {
			return 0;
		}
		int position = mPlayer.getCurrentPosition();
		int duration = mPlayer.getDuration();
		if (mProgress != null) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 1000L * position / duration;
				mProgress.setProgress((int) pos);
			}
			int percent = mPlayer.getBufferPercentage();
			mProgress.setSecondaryProgress(percent * 10);
		}

		if (mEndTime != null) mEndTime.setText(stringForTime(duration));
		if (mCurrentTime != null) mCurrentTime.setText(stringForTime(position));

		return position;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (useCustomLayout()) {
			int keyCode = event.getKeyCode();
			final boolean uniqueDown = event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN;

			if (uniqueDown) {
				switch (keyCode) {
					case KeyEvent.KEYCODE_HEADSETHOOK :
					case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE :
					case KeyEvent.KEYCODE_SPACE :
						if (mPauseButton != null) {
							mPauseButton.requestFocus();
						}
						break;

					case KeyEvent.KEYCODE_MEDIA_PLAY :
						if (mPlayer != null && !mPlayer.isPlaying()) {
							updatePausePlay();
						}
						break;

					case KeyEvent.KEYCODE_MEDIA_STOP :
					case KeyEvent.KEYCODE_MEDIA_PAUSE :
						if (mPlayer != null && mPlayer.isPlaying()) {
							updatePausePlay();
						}
						break;
				}
			}
		}

		return super.dispatchKeyEvent(event);
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (useCustomLayout()) {
			if (mPauseButton != null) {
				mPauseButton.setEnabled(enabled);
			}
			if (mFfwdButton != null) {
				mFfwdButton.setEnabled(enabled);
			}
			if (mRewButton != null) {
				mRewButton.setEnabled(enabled);
			}
			if (mNextButton != null) {
				mNextButton.setEnabled(enabled && mNextListener != null);
			}
			if (mPrevButton != null) {
				mPrevButton.setEnabled(enabled && mPrevListener != null);
			}
			if (mProgress != null) {
				mProgress.setEnabled(enabled);
			}
			disableUnsupportedButtons();
		}

		super.setEnabled(enabled);
	}

	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		public void onClick(View v) {
			doPauseResume();
			show(DEFAULT_TIMEOUT);
		}
	};

	private void doPauseResume() {
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
		}
		else {
			mPlayer.start();
		}
		updatePausePlay();
	}

	private void installPrevNextListeners() {
		if (mNextButton != null) {
			mNextButton.setOnClickListener(mNextListener);
			mNextButton.setEnabled(mNextListener != null);
		}

		if (mPrevButton != null) {
			mPrevButton.setOnClickListener(mPrevListener);
			mPrevButton.setEnabled(mPrevListener != null);
		}
	}
}
