package com.example.brightcovetest;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.SeekBar;

import com.example.brightcovetest.VolumeManager.VolumeObserver;
import com.example.brightcovetest.VolumeManager.VolumeStreamController;

/**
 * Created by dx068 on 13-06-19.
 */
public class VolumeSlider extends SeekBar {
    private OnSeekBarChangeListener seekBarChangeListener;

    private VolumeStreamController musicController = new VolumeManager.VolumeStreamController() {
        @Override
        public boolean overrideSystemControl(int keyCode) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                if (VolumeSlider.this.isEnabled()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int getStreamId() {
            return AudioManager.STREAM_MUSIC;
        }
    };

    private VolumeObserver observer = new VolumeManager.VolumeObserver() {
        @Override
        public void onVolumeUp() {
            setProgress(VolumeManager.getInstance().getStreamVolume(AudioManager.STREAM_MUSIC));

        }

        @Override
        public void onVolumeDown() {
            setProgress(VolumeManager.getInstance().getStreamVolume(AudioManager.STREAM_MUSIC));
        }

        @Override
        public void onVolumeMute(int volumeStream, boolean muteOn) {
            if (volumeStream == AudioManager.STREAM_MUSIC) {
                setEnabled(!muteOn);
            }
        }
    };

    public VolumeSlider(Context context) {
        super(context);
        init(context);

    }

    public VolumeSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VolumeSlider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

//    @Override
//    public synchronized void setProgress(int progress) {
//       // if(isEnabled()){
//          super.setProgress(progress);
       // }
//        else
//            setProgress(VolumeManager.getInstance().getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    //}

    private void init(Context context) {
        setMax(VolumeManager.getInstance().getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        setEnabled(!VolumeManager.getInstance().isStreamMute(AudioManager.STREAM_MUSIC));


        setProgress(VolumeManager.getInstance().getStreamVolume(AudioManager.STREAM_MUSIC));


        super.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBarChangeListener != null) {
                    seekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (seekBarChangeListener != null) {
                    seekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    VolumeManager.getInstance().setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }

                if (seekBarChangeListener != null) {
                    seekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
                }
            }
        });

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();


            setProgress(VolumeManager.getInstance().getStreamVolume(AudioManager.STREAM_MUSIC));

        VolumeManager.getInstance().registerObserver(observer);
        VolumeManager.getInstance().addController(musicController);
        VolumeManager.getInstance().addController(new VolumeManager.VolumeStreamController() {
            @Override
            public boolean overrideSystemControl(int keyCode) {
                return false;
            }

            @Override
            public int getStreamId() {
                return AudioManager.STREAM_ALARM;
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        VolumeManager.getInstance().unregisterObserver(observer);
        VolumeManager.getInstance().removeController(musicController);

    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        seekBarChangeListener = l;
    }

}
