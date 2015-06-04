package com.spazomatic.nabsta;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.receivers.BatteryLevelReceiver;
import com.spazomatic.nabsta.views.actionBar.CurrentSongActionProvider;
import com.spazomatic.nabsta.views.actionBar.SongsActionProvider;
import com.spazomatic.nabsta.views.fragments.NewProjectDialog;
import com.spazomatic.nabsta.views.fragments.OpenProjectDialog;
import com.spazomatic.nabsta.views.fragments.Studio;

public class MainActivity extends ActionBarActivity implements
        NewProjectDialog.OnNewSongListener,
        OpenProjectDialog.OnOpenSongListener,
        CurrentSongActionProvider.OnAddTrackListener{


    private CharSequence title;
    private BatteryLevelReceiver batteryLevelReceiver;
    private IntentFilter batteryChanged;
    private SharedPreferences sharedPreferences;
    private Menu nabstaMenu;
    private Bundle studioBundle;
    Fragment studioFragment;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.d(NabstaApplication.LOG_TAG, "mainActivity onCreate called");
        //AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        //Hardware buttons setting to adjust the media
        setContentView(R.layout.activity_main);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        sharedPreferences = getSharedPreferences(NabstaApplication.NABSTA_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        if(sharedPreferences.contains(NabstaApplication.NABSTA_KEEP_SCREEN_ON)){
            if(sharedPreferences.getBoolean(NabstaApplication.NABSTA_KEEP_SCREEN_ON,true)) {
                Log.d(NabstaApplication.LOG_TAG,String.format("Keep Screen on: %b",true));
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }else{
                Log.d(NabstaApplication.LOG_TAG,String.format("Keep Screen on: %b",false));
            }
        }
        title = getTitle();
        batteryLevelReceiver = new BatteryLevelReceiver();
        batteryChanged = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(NabstaApplication.LOG_TAG, "MainActivity onStart called...");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(NabstaApplication.LOG_TAG, "windowFocusChanged");
        //TODO: Fix this hack: For some reason on app start this is only method that keeps all
        // tracks isShown true and trackView is not null during updateVisualizer
        if(hasFocus) {
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
        NabstaApplication.activityResumed();
        //TODO Think of best solution for battery monitoring when has most all features developed.
        Log.d(NabstaApplication.LOG_TAG, "Register Batter receiver dynamically...");
        registerReceiver(batteryLevelReceiver, batteryChanged);

        //TODO: Fix this hack: similar to onWindoFocusChangedHack, this is keeps tracks alive
        //when screen turns off.

        Song currentSong = NabstaApplication.getInstance().getSongInSession();
        if(currentSong != null){
            openSong(currentSong);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(NabstaApplication.LOG_TAG, "MainActivity onPause called...");
        NabstaApplication.activityPaused();
        Log.d(NabstaApplication.LOG_TAG, "UnRegister Batter receiver dynamically...");
        unregisterReceiver(batteryLevelReceiver);
    }

    @Override
    public void onNewSong(Song song) {
        saveSongToSharedPreferences(song);
        openSong(song);
    }


    private void saveSongToSharedPreferences(Song song){
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(NabstaApplication.NABSTA_CURRENT_PROJECT_ID, song.getId());
        editor.commit();
    }
    @Override
    public void onOpenSong(Song song) {
        saveSongToSharedPreferences(song);
        openSong(song);
    }
    private void openSong(Song song){
        Log.d(NabstaApplication.LOG_TAG, String.format(
                "----------Opening Project %s------------",
                song.getName()));
        NabstaApplication.getInstance().setSongInSession(song);
        if(nabstaMenu != null) {
            MenuItem currentSongMenuItem = nabstaMenu.findItem(R.id.action_current_song);
            currentSongMenuItem.setTitle(song.getName());
        }
        Log.d(NabstaApplication.LOG_TAG,String.format(
                "Opening Studio with song id %d",song.getId()));
        studioBundle = new Bundle();
        studioBundle.putString(Studio.SONG_NAME, song.getName());
        studioBundle.putLong(SongsActionProvider.SONG_ID, song.getId());
        studioFragment = Studio.newInstance(song.getName(), song.getId());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, studioFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onAddTrack(Song song) {
        //TODO: Do not reopen whole song, should just add track view
        openSong(song);
    }
}