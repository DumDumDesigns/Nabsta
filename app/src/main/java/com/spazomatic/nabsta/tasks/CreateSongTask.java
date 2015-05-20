package com.spazomatic.nabsta.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.db.Artist;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.Track;
import com.spazomatic.nabsta.db.dao.DaoSession;

import java.io.File;
import java.io.IOException;

/**
 * Created by samuelsegal on 5/19/15.
 */
public class CreateSongTask extends AsyncTask<String,Void,Song> {
    @Override
    protected Song doInBackground(String... params) {

        DaoSession daoSession = NabstaApplication.getInstance().getDaoSession();
        Artist artist = new Artist();
        artist.setName(params[1]);
        daoSession.getArtistDao().insert(artist);


        Song song = new Song();
        song.setName(params[0]);
        song.setArtist(artist);
        song.setDir_name("TEMP_FOR_NOT_NULL");
        daoSession.getSongDao().insert(song);

        Track defaultTrack = new Track();
        defaultTrack.setName("Track 1");
        defaultTrack.setSong_id_fk(song.getId());
        defaultTrack.setArtist_id_fk(artist.getId());
        daoSession.getTrackDao().insert(defaultTrack);

        createSongDirTrackFile(song,defaultTrack);

        daoSession.getSongDao().update(song);
        daoSession.getTrackDao().update(defaultTrack);

        Log.d(NabstaApplication.LOG_TAG,String.format(
                "Song %s created with track %s", song.getName(),song.getTrack()));
        return song;
    }

    private void createSongDirTrackFile(Song song,Track defaultTrack) {

        try {
            String songDirName = String.format("%s_%d",song.getName(),song.getId());
            File dir = new File(NabstaApplication.NABSTA_ROOT_DIR,songDirName);

            dir.mkdirs();
            song.setDir_name(dir.getAbsolutePath());

            File trackFile = new File(dir, String.format("%s_%s",
                    defaultTrack.getName(),defaultTrack.getId()));
            trackFile.createNewFile();
            defaultTrack.setFile_name(trackFile.getAbsolutePath());
            Log.d(NabstaApplication.LOG_TAG,String.format(
                    "Created Default Track %s", defaultTrack.getFile_name()));
        } catch (IOException e) {
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error Creating Song with Error Message %s",
                    e.getMessage()),e);
        }

    }

}
