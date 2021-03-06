package com.spazomatic.nabsta;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.Track;
import com.spazomatic.nabsta.receivers.BatteryLevelReceiver;
import com.spazomatic.nabsta.utils.SharedPrefUtil;
import com.spazomatic.nabsta.views.actionBar.CurrentSongActionProvider;
import com.spazomatic.nabsta.views.fragments.DeleteTracksDialog;
import com.spazomatic.nabsta.views.fragments.MixMasterTrackDialog;
import com.spazomatic.nabsta.views.fragments.NewProjectDialog;
import com.spazomatic.nabsta.views.fragments.OpenProjectDialog;
import com.spazomatic.nabsta.views.fragments.Studio;

public class MainActivity extends ActionBarActivity implements
        NewProjectDialog.OnNewSongListener,
        OpenProjectDialog.OnOpenSongListener,
        CurrentSongActionProvider.OnAddTrackListener,
        DeleteTracksDialog.OnDeleteTracksListener,
        MixMasterTrackDialog.OnMasterTrackCreatedListener {

    private BatteryLevelReceiver batteryLevelReceiver;
    private IntentFilter batteryChanged;
    private Menu nabstaMenu;
    private Studio studioFragment;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        checkPrefs();
        batteryLevelReceiver = new BatteryLevelReceiver();
        batteryChanged = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    }

    private void checkPrefs() {
        if(SharedPrefUtil.containsKey(this, NabstaApplication.NABSTA_KEEP_SCREEN_ON)){
            if(SharedPrefUtil.getBooleanValue(this,NabstaApplication.NABSTA_KEEP_SCREEN_ON,false)) {
                Log.d(NabstaApplication.LOG_TAG,String.format("Keep Screen on: %b",true));
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }else{
                Log.d(NabstaApplication.LOG_TAG,String.format("Keep Screen on: %b",false));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(NabstaApplication.LOG_TAG, "MainActivity onCreateOptions");
        nabstaMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, nabstaMenu);
        if(NabstaApplication.getInstance().getSongInSession() != null) {
            MenuItem currentSongMenuItem = nabstaMenu.findItem(R.id.action_current_song);
            currentSongMenuItem.setTitle(
                    NabstaApplication.getInstance().getSongInSession().getName());
        }
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasWindowFocus() && studioFragment == null){
            Song currentSong = NabstaApplication.getInstance().getSongInSession();
            if(currentSong != null){
                openSong(currentSong);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(NabstaApplication.LOG_TAG, "MainActivity onResume called...");
        studioFragment = null;
        NabstaApplication.activityResumed();
        //TODO Think of best solution for battery monitoring when has most all features developed.
        Log.d(NabstaApplication.LOG_TAG, "Register Batter receiver dynamically...");
        registerReceiver(batteryLevelReceiver, batteryChanged);
    }

    @Override
    protected void onPause() {
        super.onPause();
        studioFragment = null;
        Log.d(NabstaApplication.LOG_TAG, "MainActivity onPause called...");
        NabstaApplication.activityPaused();
        Log.d(NabstaApplication.LOG_TAG, "UnRegister Batter receiver dynamically...");
        unregisterReceiver(batteryLevelReceiver);
    }

    @Override
    public void onNewSong(Song song) {
        openSong(song);
    }

    @Override
    public void onOpenSong(Song song) {
        openSong(song);
    }
    private void openSong(Song song){
        NabstaApplication.getInstance().setSongInSession(song);
        if(nabstaMenu != null) {
            MenuItem currentSongMenuItem = nabstaMenu.findItem(R.id.action_current_song);
            currentSongMenuItem.setTitle(song.getName());
        }
        Log.d(NabstaApplication.LOG_TAG, String.format(
                "Opening Studio with song %s", song.getName()));
        FragmentManager fragmentManager = getSupportFragmentManager();
        studioFragment = null;
        studioFragment = (Studio)fragmentManager.findFragmentById(R.id.studioFragment);
        studioFragment.setProject(song);
    }

    @Override
    public void onAddTrack(Song song) {
        //TODO: Do not reopen whole song, should just add track view
        openSong(song);
    }

    @Override
    public void onDeleteTracks(Song song) {
        openSong(song);
    }

    @Override
    public void onMasterTrackCreated(Track masterTrack) {
        //TODO: Add Master Track to UI
    }
}