package com.example.brightcovetest;

import android.media.AudioManager;

import java.util.ArrayList;
import java.util.HashMap;


import android.content.Context;
import android.view.KeyEvent;

/**
 * Created by dx068 on 13-06-19.
 */
public class VolumeManager {
    private AudioManager audioManager;
    private HashMap<Integer, Boolean> isMuteMap = new HashMap<Integer, Boolean>();


    public static interface VolumeObserver {
        public void onVolumeUp();

        public void onVolumeDown();

        public void onVolumeMute(int volumeStream, boolean muteOn);
    }

    private static VolumeManager INSTANCE;

    private VolumeManager() {
        audioManager = (AudioManager) TestApplicaton.getAppContext().getSystemService(Context.AUDIO_SERVICE);
    }

    private ArrayList<VolumeStreamController> controllers = new ArrayList<VolumeStreamController>();

    public synchronized static VolumeManager getInstance() {
        createInstance();

        return INSTANCE;
    }

    public static synchronized void createInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VolumeManager();

        }
    }

    public static synchronized void destroyInstance() {
        if (INSTANCE != null) {
            INSTANCE.destroy();
            INSTANCE = null;

        }

    }

    private synchronized void destroy() {
        unregisterAll();
        removeAllControllers();
    }

    private HashMap<Integer, VolumeObserver> observers = new HashMap<Integer, VolumeObserver>();

    public synchronized void registerObserver(VolumeObserver observer) {
        observers.put(observer.hashCode(), observer);
    }

    public synchronized void unregisterObserver(VolumeObserver observer) {
        observers.remove(observer.hashCode());
    }

    public synchronized void unregisterAll() {
        observers.clear();
    }

    public synchronized void notifyVolumeUp() {
        for (VolumeObserver vo : observers.values()) {
            vo.onVolumeUp();
        }
    }

    public synchronized void notifyVolumeDown() {
        for (VolumeObserver vo : observers.values()) {
            vo.onVolumeDown();
        }
    }

    public synchronized void notifyVolumeMute(int volumeStream, boolean muteOn) {
        for (VolumeObserver vo : observers.values()) {
            vo.onVolumeMute(volumeStream, muteOn);
        }
    }

    public synchronized void addController(VolumeStreamController controller) {
        if (controllers.contains(controller)) {
            controllers.remove(controller);
        }

        controllers.add(controller);
    }

    public synchronized void removeController(VolumeStreamController controller) {
        controllers.remove(controller);
    }

    public synchronized void removeAllControllers() {
        controllers.clear();
    }

    public synchronized boolean onVolumeUpKey(AudioManager manager) {
        if (!controllers.isEmpty()) {
            for (int i = controllers.size() - 1; i >= 0; i--) {
                VolumeStreamController vsc = controllers.get(i);
                if (vsc.overrideSystemControl(KeyEvent.KEYCODE_VOLUME_UP)) {
                    vsc.increaseStreamVolume();
                    notifyVolumeUp();
                    return true;
                }
            }


        }
        return false;

    }

    public synchronized boolean onVolumeDownKey(AudioManager manager) {

        if (!controllers.isEmpty()) {
            for (int i = controllers.size() - 1; i >= 0; i--) {
                VolumeStreamController vsc = controllers.get(i);
                if (vsc.overrideSystemControl(KeyEvent.KEYCODE_VOLUME_DOWN)) {
                    vsc.decreaseStreamVolume();
                    notifyVolumeDown();
                    return true;
                }
            }

        }
        return false;

    }

    public synchronized void adjustStreamVolume(int streamType, int directions, int flags) {
        audioManager.adjustStreamVolume(streamType, directions, flags);
    }

    public synchronized  void setStreamVolume(int streamType, int index, int flags) {
        audioManager.setStreamVolume(streamType, index, flags);
    }

    public synchronized int getStreamMaxVolume(int streamType) {
        return audioManager.getStreamMaxVolume(streamType);
    }

    public synchronized int getStreamVolume(int streamType) {
        return audioManager.getStreamVolume(streamType);
    }

    public synchronized void setStreamMute(int streamType, boolean mute) {
        audioManager.setStreamMute(streamType, mute);
        isMuteMap.put(streamType, mute);
        notifyVolumeMute(AudioManager.STREAM_MUSIC, mute);

    }

    public synchronized boolean isStreamMute(int streamType) {
        return isMuteMap.containsKey(streamType) && isMuteMap.get(streamType);
    }

    public static abstract class VolumeStreamController {

        public abstract boolean overrideSystemControl(int KeyCode);

        public abstract int getStreamId();

        public void increaseStreamVolume() {
            VolumeManager.getInstance().adjustStreamVolume(getStreamId(), AudioManager.ADJUST_RAISE, 0);

        }

        public void decreaseStreamVolume() {
            VolumeManager.getInstance().adjustStreamVolume(getStreamId(), AudioManager.ADJUST_LOWER, 0);


        }

        public void muteStream(boolean muteOn) {

        }

    }


}
