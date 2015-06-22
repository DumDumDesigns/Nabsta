package com.spazomatic.nabsta.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.audio.TrackMixer;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.Track;

import java.io.File;
import java.io.IOException;

/**
 * Created by samuelsegal on 5/19/15.
 */
public class MixTracksTask extends AsyncTask<String,Integer,Track> {

    private ProgressBar progressBar;

    public MixTracksTask(ProgressBar progressBar) {
        this.progressBar= progressBar;
    }

    private final static String LOG_TAG = String.format(
            "Nabsta: %s", MixTracksTask.class.getSimpleName());
    @Override
    protected Track doInBackground(String... params) {

        Song song = NabstaApplication.getInstance().getSongInSession();
        Track[] tracks = song.getTracks().toArray(new Track[0]);
        String songName = params[0];
        TrackMixer trackMixer = new TrackMixer(song,tracks);
        Track masterTrack = new Track();
        masterTrack.setName(songName);
        try {
            //testing publish progress feature
            publishProgress(1);
            File masterTrackFile = trackMixer.mixTracks();
            //TODO: define interface callback in trackMixer to share progress
        } catch (IOException e) {
            Log.e(NabstaApplication.LOG_TAG, String.format(
                    "Error Mixing tracks Error Message %s",e.getMessage()),e);
        }

        return masterTrack;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setProgress(0);
    }

    @Override
    protected void onPostExecute(Track track) {
        super.onPostExecute(track);
        progressBar.setProgress(100);
    }

    @Override
    protected void onProgressUpdate(final Integer... values) {
        super.onProgressUpdate(values);
        if(values != null) {
            Log.d(LOG_TAG,String.format("Updating progress %d", values[0]));
            progressBar.setProgress(values[0]);
        }
    }

    private void createTrackFile(Song song,Track defaultTrack) {

        try {
            String songDirName = String.format("%s_%d",song.getName(),song.getId());
            File dir = new File(NabstaApplication.NABSTA_ROOT_DIR,songDirName);

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
                    "Created Default Track %s with image file %s",
                    defaultTrack.getFile_name(), defaultTrack.getBitmap_file_name()));
            Log.d(LOG_TAG,String.format(
                    "Track File %s :readable: %b: writable %b: executable %b",
                    trackFile.getName(),trackFile.canRead(),
                    trackFile.canWrite(),trackFile.canExecute()));

        } catch (IOException e) {
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error Creating Song with Error Message %s",
                    e.getMessage()),e);
        }

    }

}
