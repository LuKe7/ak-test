package com.example.brightcovetest;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.Video;
import com.example.brightcovetest.VideoController.ControlledVideo;
import com.example.brightcovetest.VolumeManager.VolumeObserver;
import com.example.brightcovetest.SpeechUtils.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class MainActivity extends Activity {
    private static final String CATALOG_ID = "jskS1rEtQHy9exQKoc14IcMq8v5x2gCP6yaB7d0hraRtO__6HUuxMg..";
    private static final String VIDEO_ID_1 = "1520880903001";

    private static final String MUTE_ON_FLAG = "muteIsOn";
    private static final String FULLSCREEN_FLAG = "fullScreenOn";

    private static final int SIGN_IN_REQUEST_CODE = 21;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 22;

    private boolean isFullScreen = false;
    private MediaController controller;
    private Catalog catalog;
    private BrightcoveWrapper brightcoveVideoView;
    private Coordinate originalCoordinate;
    private LinearLayout.LayoutParams originalParams;

    private ProgressBar loadingSpinner;
    private DrawerLayout drawerLayout;
    private TextView counterDisplay;
    private View videoContainer;
    private ActionBarDrawerToggle drawerToggle;
    private ListView sideMenu;
    private TextView selectedTv;
    private String actionBarTitle;
    private OptionsAdapter adapter;
    private Option[] menuOption = new Option[7];
    private VolumeObserver observer;
    private Button mbtSpeak;


    private static class MenuOptionIds {
        private static final int TRAILER_OPT_ID = 0;
        private static final int SPACE_OPT_ID = 1;
        private static final int VOLUME_SLIDER = 2;
        private static final int MUTE = 3;
        private static final int VOICE = 4;
        private static final int SIGNIN = 5;
        private static final int MOVEABLE_SHAPES= 6;
    }

    public MainActivity() {
        menuOption[0] = new Option("Trailer", 0, MenuOptionIds.TRAILER_OPT_ID);
        menuOption[1] = new Option("Space", 0, MenuOptionIds.SPACE_OPT_ID);
        menuOption[2] = new Option("Volume", 1, MenuOptionIds.VOLUME_SLIDER);
        menuOption[3] = new Option("Mute", 0, MenuOptionIds.MUTE) {

            @Override
            public String getName() {
                if (VolumeManager.getInstance().isStreamMute(AudioManager.STREAM_MUSIC)) {
                    return "Unmute";
                } else {
                    return super.getName();
                }
            }
        };
        menuOption[4] = new Option("Voice Recognition", 0, MenuOptionIds.VOICE);
        menuOption[5] = new Option("Sign In", 0, MenuOptionIds.SIGNIN);
        menuOption [6] = new Option ("Moveable Objects", 0, MenuOptionIds.MOVEABLE_SHAPES);

    }


    ArrayList<Boolean> clipList = new ArrayList<Boolean>();
    Queue<ViewGroup.LayoutParams> paramList = new LinkedList<ViewGroup.LayoutParams>();
    private VideoController vc;

    private void updateParentForFullscreen(View myView) {
        if (myView.getParent() == myView.getRootView()) {
            myView.getParent().recomputeViewAttributes(myView);
        } else {
            if (myView.getParent() instanceof View) {
                View parent = (View) myView.getParent();
                paramList.add(((View) myView.getParent()).getLayoutParams());

                updateParentForFullscreen(parent);

                ViewGroup.LayoutParams params = getlayout(((View) parent).getLayoutParams());
                ((View) myView.getParent()).setLayoutParams(params);
            }

        }
    }

    private static class Coordinate {
        public float x;
        public float y;

        public Coordinate(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    /*
     * not includedDrawerLayout.LayoutParams, SlidingPaneLayout.LayoutParams
     */
    private ViewGroup.LayoutParams getlayout(ViewGroup.LayoutParams originalParams) {

        if (originalParams instanceof LinearLayout.LayoutParams) {
            return new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        } else if (originalParams instanceof RelativeLayout.LayoutParams) {
            return new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        } else if (originalParams instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            return params;
        } else if (originalParams instanceof GridLayout.LayoutParams) {
            return new GridLayout.LayoutParams(new ViewGroup.MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        } else if (originalParams instanceof android.support.v4.widget.DrawerLayout.LayoutParams) {
            return new android.support.v4.widget.DrawerLayout.LayoutParams(LayoutParams.MATCH_PARENT, android.support.v4.widget.DrawerLayout.LayoutParams.MATCH_PARENT);
        } else if (originalParams instanceof ViewGroup.MarginLayoutParams) {
            return new ViewGroup.MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        } else if (originalParams instanceof RadioGroup.LayoutParams) {
            return new RadioGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        } else if (originalParams instanceof TableLayout.LayoutParams) {
            return new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        } else if (originalParams instanceof TableRow.LayoutParams) {
            return new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        } else if (originalParams instanceof ActionBar.LayoutParams) {
            return new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        } else if (originalParams instanceof AbsListView.LayoutParams) {
            return new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        } else if (originalParams instanceof WindowManager.LayoutParams) {
            return new WindowManager.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        } else if (originalParams instanceof ViewPager.LayoutParams) {
            return new ViewPager.LayoutParams();
        } else if (originalParams instanceof Gallery.LayoutParams) {
            return new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        } else if (originalParams instanceof AbsoluteLayout.LayoutParams) {
            return new AbsoluteLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0, 0);
        } else {
            return new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
    }

    private void restoreLayout(View myView) {
        if (myView.getParent() == myView.getRootView()) {
            myView.getParent().recomputeViewAttributes(myView);
        } else {
            if (myView.getParent() instanceof View) {
                ViewGroup.LayoutParams params = paramList.poll();
                ((View) myView.getParent()).setLayoutParams(params);
                restoreLayout((View) myView.getParent());
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkVoiceRecognition();
        VolumeManager.createInstance();

        setContentView(R.layout.activity_main);
        sideMenu = (ListView) findViewById(R.id.sideMenu);

        adapter = new OptionsAdapter(this, menuOption);
        observer = new VolumeObserver() {
            @Override
            public void onVolumeUp() {
            }

            @Override
            public void onVolumeDown() {
            }

            @Override
            public void onVolumeMute(int volumeStream, boolean muteOn) {
                if (volumeStream == AudioManager.STREAM_MUSIC) {
                    OptionsAdapter menuAdapter = (OptionsAdapter) sideMenu.getAdapter();
                    if (menuAdapter != null) {
                        menuAdapter.notifyDataSetChanged();
                    }
                }
            }
        };

        VolumeManager.getInstance().registerObserver(observer);


        sideMenu.setAdapter(adapter);
        sideMenu.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int optionId = ((OptionsAdapter) parent.getAdapter()).getItem(position).getId();

                switch (optionId) {
                    case MenuOptionIds.TRAILER_OPT_ID:
                        if (sideMenu.isItemChecked(position)) {
                            brightcoveVideoView.clear();
                            playContent("http://media.w3.org/2010/05/sintel/trailer.mp4");
                            sideMenu.setItemChecked(position, true);
                            actionBarTitle = menuOption[position].getName();
                            getActionBar().setTitle(actionBarTitle);
                        }
                        drawerLayout.closeDrawer(sideMenu);
                        break;

                    case MenuOptionIds.SPACE_OPT_ID:
                        if (sideMenu.isItemChecked(position)) {
                            brightcoveVideoView.clear();
                            playCatalogVideo(CATALOG_ID, VIDEO_ID_1);
                            sideMenu.setItemChecked(position, true);
                            actionBarTitle = menuOption[position].getName();
                            getActionBar().setTitle(actionBarTitle);
                        }
                        drawerLayout.closeDrawer(sideMenu);
                        break;

                    case MenuOptionIds.MUTE:
                        selectedTv = (TextView) sideMenu.getChildAt(position);


                        toggleMute();
                        break;
                    case MenuOptionIds.VOICE:
                        speak(view);
                        break;
                    case MenuOptionIds.SIGNIN:
                        brightcoveVideoView.pause();
                        selectedTv = (TextView) sideMenu.getChildAt(position);
                        if (selectedTv.getText().equals("Sign Out")) {
                            LoginManager.signOut();
                            Toast.makeText(view.getContext(), "Signed Out", Toast.LENGTH_SHORT).show();

                            toggleSigninOut();
//                            adapter.newOptionsList(menuOption);
//                            sideMenu.setAdapter(adapter);
                        } else {
                            //selectedTv.setText("Sign Out");
                            Intent intent = new Intent(MainActivity.this, SigninActivity.class);
                            MainActivity.this.startActivityForResult(intent, SIGN_IN_REQUEST_CODE);
                        }
                        break;
                    case MenuOptionIds.MOVEABLE_SHAPES:
                        Intent intent = new Intent(MainActivity.this, ShapeActivity.class);
                        MainActivity.this.startActivity(intent);

                    default:
                        break;
                }
            }
        }

        );

        drawerLayout = (DrawerLayout)

                findViewById(R.id.drawer_layout);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        getActionBar().setHomeButtonEnabled(true);

        drawerToggle = new

                ActionBarDrawerToggle(this, /* host Activity */
                        drawerLayout, /* DrawerLayout object */
                        R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                        R.string.accessibiility_open_settings, /* "open drawer" description for accessibility */
                        R.string.accessibility_close_settings /* "close drawer" description for accessibility */
                ) {
                    public void onDrawerClosed(View view) {
                        // getActionBar().setTitle(getTitle());
                        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                        getActionBar().setTitle(actionBarTitle);
                    }

                    public void onDrawerOpened(View drawerView) {
                        getActionBar().setTitle("Options");
                        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                    }
                };
        drawerLayout.setDrawerListener(drawerToggle);

        brightcoveVideoView = (BrightcoveWrapper) findViewById(R.id.video_view);

        initController(brightcoveVideoView, brightcoveVideoView);
        // brightcoveVideoView.setMediaController(controller);

        loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        hideLoadingSpinner();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(FULLSCREEN_FLAG)) {

                isFullScreen = savedInstanceState.getBoolean(FULLSCREEN_FLAG);
            }
        }

        // playCatalogVideo(CATALOG_ID, VIDEO_ID_1);
        playContent("http://media.w3.org/2010/05/sintel/trailer.mp4");

        videoContainer = findViewById(R.id.video_container);
        videoContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (vc != null) {
                    if (vc.getVisibility() == View.VISIBLE) {
                        vc.setVisibility(View.INVISIBLE);
                    } else {
                        vc.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        initVideoController();
        brightcoveVideoView.setVideoController(vc);

        if (isFullScreen) {
            setFullScreen(true);
        }


    }

    @Override
    public void finish() {
        VolumeManager.destroyInstance();
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case SIGN_IN_REQUEST_CODE:

                if (resultCode == RESULT_OK && selectedTv != null) {
                    Log.d("", "SIGNIN setting textView " + selectedTv);
                    toggleSigninOut();
//                adapter.newOptionsList(menuOption);
//                sideMenu.setAdapter(adapter);

                }
                break;

            case VOICE_RECOGNITION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    onSpeechInput(textMatchList);

                } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                    showToastMessage("Audio Error");
                } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                    showToastMessage("Client Error");
                } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                    showToastMessage("Network Error");
                } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                    showToastMessage("No Match");
                } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                    showToastMessage("Server Error");
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }//onActivityResult

    protected void onSpeechInput(ArrayList<String> textMatchList) {
        if (!textMatchList.isEmpty()) {
            String firstMatch = textMatchList.get(0);
            SpeechCmd command = SpeechCmd.getSpeechCmd(firstMatch);
            switch (command) {
                case SEARCH:
                    String searchQuery = textMatchList.get(0);
                    searchQuery = searchQuery.replace("search", "");
                    Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                    search.putExtra(SearchManager.QUERY, searchQuery);
                    startActivity(search);
                    break;

                case TOGGLE_MUTE:
                    toggleMute();
                    break;

                case VOLUME_UP:
                    VolumeManager.getInstance().adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
                    VolumeManager.getInstance().notifyVolumeUp();
                    break;

                case VOLUME_DOWN:
                    VolumeManager.getInstance().adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
                    VolumeManager.getInstance().notifyVolumeDown();
                    break;

                case LANDSCAPE:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    break;

                case PORTRAIT:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;

                case SCREEN_DEFAULT:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                    break;

                default:
                    showToastMessage("Unknown command: " + firstMatch);
                    break;


            }

        }
    }


    void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(FULLSCREEN_FLAG, isFullScreen);
    }

    private void initVideoController() {
        vc = (VideoController) findViewById(R.id.our_video_view);
        vc.registerVideo(new ControlledVideo() {

            @Override
            public void toggleFullscreen(boolean toggleOn) {
                if (toggleOn) {
                    Log.d("", "COORDINATES: fullscreen");

                    setFullScreen(true);

                } else {
                    setFullScreen(false);
                }
            }

            @Override
            public void toggleCaptions(boolean toggleOn) {
                Log.d("", "CONTROLLER: captions on = " + toggleOn);
            }

            @Override
            public void stop() {
                Log.d("", "CONTROLLER: stop");
                if (brightcoveVideoView != null) {
                    brightcoveVideoView.stopPlayback();
                }
            }

            @Override
            public void seekTo(long position) {
                Log.d("", "CONTROLLER: seekTo " + position);
                if (brightcoveVideoView != null) {
                    brightcoveVideoView.seekTo((int) position);
                }
            }

            @Override
            public void prevVid() {
                Log.d("", "CONTROLLER: prevVid");
            }

            @Override
            public void play() {
                Log.d("", "CONTROLLER: play");
                if (brightcoveVideoView != null) {
                    brightcoveVideoView.start();
                }
            }

            @Override
            public void pause() {
                Log.d("", "CONTROLLER: pause");
                if (brightcoveVideoView != null) {
                    brightcoveVideoView.pause();
                }
            }

            @Override
            public void nextVid() {
                Log.d("", "CONTROLLER: nextVid");
            }

            @Override
            public boolean isPlaying() {
                if (brightcoveVideoView != null) {
                    return brightcoveVideoView.isPlaying();
                }

                return false;
            }

            @Override
            public boolean hasPrevVid() {
                return false;
            }

            @Override
            public boolean hasNextVid() {
                return false;
            }

            @Override
            public long getVideoDuration() {
                if (brightcoveVideoView != null) {
                    return brightcoveVideoView.getDuration();
                }
                return 0;
            }

            @Override
            public long getCurrentTime() {
                if (brightcoveVideoView != null) {
                    return brightcoveVideoView.getCurrentPosition();
                }
                return 0;
            }

            @Override
            public int getBufferPercentage() {
                if (brightcoveVideoView != null) {
                    return brightcoveVideoView.getBufferPercentage();
                }
                return 0;
            }
        });
    }

    private void toggleSigninOut() {
        if (menuOption[5].getName().equals("Sign In")) {
            menuOption[5].setName("Sign Out");
        } else {
            menuOption[5].setName("Sign In");
        }
        ((OptionsAdapter) sideMenu.getAdapter()).notifyDataSetChanged();
    }

    private void toggleMute() {
        VolumeManager.getInstance().setStreamMute(AudioManager.STREAM_MUSIC, !VolumeManager.getInstance().isStreamMute(AudioManager.STREAM_MUSIC));
        OptionsAdapter adapter = (OptionsAdapter) sideMenu.getAdapter();

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            // case R.id.action_websearch:
            // // create intent to perform web search for this planet
            // Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            // intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            // // catch event that there's no activity to handle intent
            // if (intent.resolveActivity(getPackageManager()) != null) {
            // startActivity(intent);
            // } else {
            // Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            // }
            // return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void initController(View anchorView, MediaPlayerControl mpc) {
        controller = new CustomizableMediaController(this, true, false);
        controller.setPrevNextListeners(null, null);
        controller.setAnchorView(anchorView);
        controller.setMediaPlayer(mpc);
    }

    private void hideLoadingSpinner() {
        Log.d("", "BC_PLAYER: hiding loading spinner");
        if (loadingSpinner != null) {
            loadingSpinner.setVisibility(View.GONE);
        }
    }

    public void showLoadingSpinner() {
        Log.d("", "BC_PLAYER: showing loading spinner");
        if (loadingSpinner != null) {
            loadingSpinner.setVisibility(View.VISIBLE);
        }
    }

    public void playContent(String contentUrl) {
        // controller.show();

        // showLoadingSpinner();

        // brightcoveVideoView.setVideoPath(contentUrl);
        brightcoveVideoView.add(Video.createVideo(contentUrl));

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

    private void setupEventEmitter() {
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();

        eventEmitter.once(EmitEvents.didSetVideo.toString(), new EventListener() {

            @Override
            public void processEvent(Event event) {
                Log.d("", "BC_PLAYER: " + EmitEvents.didSetVideo.toString());
//                brightcoveVideoView.start();
            }
        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    public synchronized void setFullScreen(boolean fullscreenOn) {
        if (videoContainer != null) {
            if (fullscreenOn) {
                isFullScreen = true;
                originalParams = (android.widget.LinearLayout.LayoutParams) videoContainer.getLayoutParams();

                updateParentForFullscreen(videoContainer);

                originalCoordinate = new Coordinate(videoContainer.getX(), videoContainer.getY());
                videoContainer.setX(0);
                videoContainer.setY(0);

                ViewGroup.LayoutParams params = getlayout(originalParams);

                videoContainer.setLayoutParams(params);
                vc.setVisibility(View.INVISIBLE);

                findViewById(android.R.id.content).invalidate();
            } else {
                if (originalCoordinate != null) {
                    isFullScreen = false;
                    videoContainer.setX(originalCoordinate.x);
                    videoContainer.setY(originalCoordinate.y);

                    videoContainer.setLayoutParams(originalParams);

                    restoreLayout(videoContainer);
                    vc.setVisibility(View.VISIBLE);

                }
            }
        }
    }


    public void checkVoiceRecognition() {
        // Check if voice recognition is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            mbtSpeak.setEnabled(false);
            mbtSpeak.setText("Voice recognizer not present");
            Toast.makeText(this, "Voice recognizer not present",
                    Toast.LENGTH_SHORT).show();

        }
    }

    public void speak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                .getPackage().getName());

        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a Command");

        // Given an hint to the recognizer about what the user is going to say
        //There are two form of language model available
        //1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
        //2.LANGUAGE_MODEL_FREE_FORM  : If not sure about the words or phrases and its domain.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);


        //int noOfMatches = Integer.parseInt(msTextMatches.getSelectedItem().toString());
        // Specify how many results you want to receive. The results will be
        // sorted where the first result is the one with higher confidence.
        // intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);
        //Start the Voice recognizer activity for the result.
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = false;
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                result = VolumeManager.getInstance().onVolumeUpKey(audioManager);

                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                result = VolumeManager.getInstance().onVolumeDownKey(audioManager);
                break;
//            case KeyEvent.KEYCODE_VOLUME_MUTE:
//                VolumeObserverManager.getInstance().notifyVolumeMute();
//                break;
        }

        if (false) {
            result = super.onKeyDown(keyCode, event);
        }

        return result;
    }
}
