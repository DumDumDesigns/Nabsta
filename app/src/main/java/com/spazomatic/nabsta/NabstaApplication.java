package com.spazomatic.nabsta;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.spazomatic.nabsta.db.DataBaseOpenHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by samuelsegal on 4/20/15.
 */
public class NabstaApplication extends Application{
    public static final String LOG_TAG = "Nabsta";
    public static File NABSTA_ROOT_DIR;
    public static String [] ALL_TRACKS;
    //private DAOSession daoSession;
    private static NabstaApplication nabstaApplicationInstance;
    public static NabstaApplication getInstance(){
        return nabstaApplicationInstance;
    }
    @Override
    public void onCreate() {
        nabstaApplicationInstance = this;
        Log.d(LOG_TAG, "Nabsta starting...");
        if(Environment.getExternalStorageDirectory().exists() && Environment.getExternalStorageDirectory().canWrite()) {
            NABSTA_ROOT_DIR = new File(Environment.getExternalStorageDirectory(), "Nabsta");
        }else if(getApplicationContext().getFilesDir().exists() && getApplicationContext().getFilesDir().canWrite()){
            NABSTA_ROOT_DIR = new File(getApplicationContext().getFilesDir(), "Nabsta");
        }else{
            //TODO: shutdown with nice message explaining nowhere to write file to. Also need to check disk space
        }
        NABSTA_ROOT_DIR.setExecutable(true);
        NABSTA_ROOT_DIR.setReadable(true);
        NABSTA_ROOT_DIR.setWritable(true);
        if (!NABSTA_ROOT_DIR.exists()) {
            Log.d(LOG_TAG, "Creating root dir: " + NABSTA_ROOT_DIR.getName());
            NABSTA_ROOT_DIR.mkdirs();
        }

        //TODO: TEMP SOLUTION WAITING FOR BEST IDEA to handle the common multiple tracks every project will need.
        // FOR NOW get all tracks or create track1,track2, track3, etc.., if first launch of app.
        String[] tracks = {"track1.3gp", "track2.3gp", "track3.3gp"};
        ALL_TRACKS = new String[tracks.length];
        int i = 0;
        for(String fileName : tracks) {
            String playBackFileName = NabstaApplication.NABSTA_ROOT_DIR.getAbsolutePath() + "/" + fileName;
            Log.d(NabstaApplication.LOG_TAG, "Got playBackFileName attr: " + playBackFileName);

            File f = new File(NabstaApplication.NABSTA_ROOT_DIR.getAbsolutePath(), fileName);
            f.setExecutable(true);
            f.setReadable(true);
            f.setWritable(true);
            if (!f.exists()) {
                Log.d(NabstaApplication.LOG_TAG, "Creating new playback file: " + f.getName());
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    Log.e(NabstaApplication.LOG_TAG, "Error Creating File: " + f + " Error Message: " + e.getMessage());
                }
            } else {
                Log.d(NabstaApplication.LOG_TAG, "Playback file exists: " + f.getName());
            }
            Log.d(LOG_TAG, "Adding track " + playBackFileName + " to ALL_TRACKS.");
            ALL_TRACKS[i++] = playBackFileName;
        }
        
        //setupDataBase();

    }

    private void setupDataBase() {
        DataBaseOpenHelper helper = DataBaseOpenHelper.getInstance(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        //DaoMaster daoMaster = new DaoMaster(db);
       // daoSession = daoMaster.newSession();
       // List<LiquorType> liquorTypes = daoSession.getLiquorTypeDao().loadAll();
       // Log.d(LOG_TAG,"Number of liquorTypes:" + liquorTypes.size());
    }
}
