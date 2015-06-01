package com.spazomatic.nabsta;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.spazomatic.nabsta.db.DataBaseOpenHelper;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.dao.DaoMaster;
import com.spazomatic.nabsta.db.dao.DaoSession;
import com.spazomatic.nabsta.tasks.CreateSongTask;
import com.spazomatic.nabsta.tasks.LoadSongsTask;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by samuelsegal on 4/20/15.
 */
public class NabstaApplication extends Application{

    public static final String LOG_TAG = "Nabsta";
    public static final String NABSTA_SHARED_PREFERENCES = "NABSTA_SHARED_PREFERENCES";
    public static final String NABSTA_CURRENT_PROJECT_ID = "NABSTA_CURRENT_PROJECT_ID";
    public static final String NABSTA_KEEP_SCREEN_ON = "NABSTA_KEEP_SCREEN_ON";
    public static File NABSTA_ROOT_DIR;
    public static String [] ALL_TRACKS;

    private static NabstaApplication nabstaApplicationInstance;
    private static boolean activityVisible;
    private static Song songInSession;

    private DaoSession daoSession;
    public static NabstaApplication getInstance(){
        return nabstaApplicationInstance;
    }
    @Override
    public void onCreate() {
        nabstaApplicationInstance = this;
        Log.d(LOG_TAG, "Nabsta starting...");
        if(Environment.getExternalStorageDirectory().exists() &&
                Environment.getExternalStorageDirectory().canWrite()) {
            NABSTA_ROOT_DIR = new File(Environment.getExternalStorageDirectory(), "Nabsta");
        }else if(getApplicationContext().getFilesDir().exists() &&
                getApplicationContext().getFilesDir().canWrite()){
            NABSTA_ROOT_DIR = new File(getApplicationContext().getFilesDir(), "Nabsta");
        }else{
            //TODO: shutdown with nice message explaining nowhere to write file to. Also need to check disk space
        }
        NABSTA_ROOT_DIR.setExecutable(true);
        NABSTA_ROOT_DIR.setReadable(true);
        NABSTA_ROOT_DIR.setWritable(true);
        if (!NABSTA_ROOT_DIR.exists()) {
            Log.d(LOG_TAG, String.format("Creating root dir: %s",NABSTA_ROOT_DIR.getName()));
            NABSTA_ROOT_DIR.mkdirs();
        }

        setupDataBase();
        LoadSongsTask loadSongsTask = new LoadSongsTask();
        loadSongsTask.execute();
        List<Song> songs = null;
        try {
            songs = loadSongsTask.get();
            if(songs == null | songs.isEmpty()){
                Song exampleSong = createSong("Example Project","Example Artist");
                setSongInSession(exampleSong);
                SharedPreferences sharedPreferences = getSharedPreferences(NabstaApplication.NABSTA_SHARED_PREFERENCES,
                        Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(NabstaApplication.NABSTA_CURRENT_PROJECT_ID,exampleSong.getId());
                editor.commit();

            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(LOG_TAG,"Error loading songs",e);
        }
    }
    private Song createSong(String songNameValue, String artistNameValue) {
        try {
            CreateSongTask createSongTask = new CreateSongTask();
            String [] params = {songNameValue,artistNameValue};
            createSongTask.execute(params);
            Song song = createSongTask.get();
            return song;
        }catch(Exception e){
            Log.e(NabstaApplication.LOG_TAG,"Error Saving to Database",e);
        }
        return null;
    }
    private void setupDataBase() {
        DataBaseOpenHelper helper = DataBaseOpenHelper.getInstance(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        if(daoSession == null){
            setupDataBase();
        }
        daoSession.clear();
        return daoSession;
    }

    public static Song getSongInSession() {
        return songInSession;
    }

    public static void setSongInSession(Song songInSession) {
        NabstaApplication.songInSession = songInSession;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }
}
