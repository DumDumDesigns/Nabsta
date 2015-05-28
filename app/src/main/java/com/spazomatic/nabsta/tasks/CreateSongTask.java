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

    private final static String LOG_TAG = String.format(
            "Nabsta: %s", CreateSongTask.class.getSimpleName());

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
        defaultTrack.setName("Track1");
        defaultTrack.setSong_id_fk(song.getId());
        defaultTrack.setArtist_id_fk(artist.getId());
        daoSession.getTrackDao().insert(defaultTrack);

        createSongDirTrackFile(song, defaultTrack);

        daoSession.getSongDao().update(song);
        daoSession.getTrackDao().update(defaultTrack);
        daoSession.refresh(song);
        daoSession.refresh(defaultTrack);
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

            String trackImageFileName = String.format(
                    "%s%s%s%s",dir.getAbsolutePath(),"/trackView_",defaultTrack.getId(),".jpg");
            defaultTrack.setBitmap_file_name(trackImageFileName);

            File trackFile = new File(dir, String.format("%s_%d.PCM",
                    defaultTrack.getName(),defaultTrack.getId()));

            trackFile.createNewFile();
            trackFile.setReadable(true);
            trackFile.setWritable(true);
            trackFile.setExecutable(true);
            defaultTrack.setFile_name(trackFile.getAbsolutePath());
            Log.d(LOG_TAG, String.format(
                    "Created Default Track:%n %s with image file:%n %s",
                    defaultTrack.getFile_name(), defaultTrack.getBitmap_file_name()));
            Log.d(LOG_TAG,String.format(
                    "Track File:%n %s :readable: %b: writable %b: executable %b",
                    trackFile.getName(),trackFile.canRead(),
                    trackFile.canWrite(),trackFile.canExecute()));

        } catch (IOException e) {
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error Creating Song with Error Message %s",
                    e.getMessage()),e);
        }

    }

}
