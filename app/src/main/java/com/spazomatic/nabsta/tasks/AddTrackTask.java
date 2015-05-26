package com.spazomatic.nabsta.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.Track;
import com.spazomatic.nabsta.db.dao.DaoMaster;
import com.spazomatic.nabsta.db.dao.DaoSession;

import java.io.File;
import java.io.IOException;

/**
 * Created by samuelsegal on 5/19/15.
 */
public class AddTrackTask extends AsyncTask<Long,Void,Track> {
    @Override
    protected Track doInBackground(Long... params) {
        Long songId = params[0];
        DaoMaster daoMaster  = NabstaApplication.getInstance().getDaoMaster();
        DaoSession daoSession = daoMaster.newSession();

        Track track = new Track();
        track.setSong_id_fk(songId);
        track.setName("Track");
        daoSession.getTrackDao().insert(track);

        Song song = daoSession.getSongDao().load(songId);
        createTrackFile(song,track);
        track.update();
        song.update();

        return track;
    }

    private void createTrackFile(Song song,Track defaultTrack) {

        try {
            String songDirName = String.format("%s_%d",song.getName(),song.getId());
            File dir = new File(NabstaApplication.NABSTA_ROOT_DIR,songDirName);



            File trackFile = new File(dir, String.format("%s_%d.PCM",
                    defaultTrack.getName(),defaultTrack.getId()));

            trackFile.createNewFile();
            trackFile.setReadable(true);
            trackFile.setWritable(true);
            trackFile.setExecutable(true);
            defaultTrack.setFile_name(trackFile.getAbsolutePath());
            Log.d(NabstaApplication.LOG_TAG, String.format(
                    "Created Default Track %s", defaultTrack.getFile_name()));
            Log.d(NabstaApplication.LOG_TAG,String.format(
                    "File %s :readable: %b: writable %b: executable %b",
                    trackFile.getName(),trackFile.canRead(),
                    trackFile.canWrite(),trackFile.canExecute()));

        } catch (IOException e) {
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error Creating Song with Error Message %s",
                    e.getMessage()),e);
        }

    }

}
