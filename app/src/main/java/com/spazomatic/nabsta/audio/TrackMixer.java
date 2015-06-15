package com.spazomatic.nabsta.audio;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.Track;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by samuelsegal on 6/10/15.
 */
public class TrackMixer {
    private final static String LOG_TAG = String.format(
            "Nabsta: %s: ", TrackMixer.class.getSimpleName());
    private Song song;
    private Track[] tracks;
    private static final int FREQUENCY = 44100;
    private static final int MIN_BUFF_SIZE= AudioTrack.getMinBufferSize(
            FREQUENCY, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);


    public TrackMixer(Song song, Track[] tracks){
        this.song = song;
        this.tracks = tracks;
    }

    public File mixTracks() throws IOException{

        byte[][] tracksBytes = new byte[tracks.length][];

        int longestTrack = 0;
        for(int i = 0; i < tracks.length; i++){
            tracksBytes[i] = convertStreamToByteArray(tracks[i].getFile_name());
            if(tracksBytes[i].length > longestTrack){
                longestTrack = tracksBytes[i].length;
            }
        }

        byte[] output = new byte[longestTrack];

        for(int i=0; i < output.length; i++){
            float mixed = 0;
            for(int z = 0; z < tracksBytes.length; z++){
                mixed += tracksBytes[z].length > i ? tracksBytes[z][i] / 128.0f : 0.0f;
            }

            // reduce volume
            mixed *= 0.8;
            if (mixed > 1.0f) mixed = 1.0f;
            if (mixed < -1.0f) mixed = -1.0f;

            byte outputSample = (byte)(mixed * 128.0f);
            output[i] = outputSample;

        }
        Log.d(LOG_TAG, String.format("Creating Master track in dir %s", song.getDir_name()));
        File masterTrack = new File(song.getDir_name(),"master.wav");
        if(masterTrack.exists()){
            masterTrack.delete();
        }
        FileOutputStream fos = new FileOutputStream(masterTrack);

        WaveHeader mwh = new WaveHeader( WaveHeader.FORMAT_PCM, (short) 1,
                FREQUENCY, (short) 16, output.length);
        mwh.write(fos);
        fos.write(output);
        fos.flush();
        fos.close();
        Log.d(LOG_TAG, String.format("Created Master track %s", masterTrack.getAbsolutePath()));
        return masterTrack;
    }

    private byte[] convertStreamToByteArray(String musacFile) {
        byte [] soundBytes = null;
        ByteArrayOutputStream bos = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try {
            bos = new ByteArrayOutputStream();
            fis = new FileInputStream(musacFile);
            bis = new BufferedInputStream(fis);
            byte[] buffer = new byte[MIN_BUFF_SIZE/2];

            while(bis.read(buffer) != - 1){
                bos.write(buffer);
            }
            soundBytes = bos.toByteArray();

        } catch (IOException e) {
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error reading file %s", musacFile),e);
        }finally{
            try {
                if(bis != null) {
                    bis.close();
                }
                if(fis != null) {
                    fis.close();
                }
                if(bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                Log.e(NabstaApplication.LOG_TAG,String.format(
                        "Error Closing Stream with message: %s",
                        e.getMessage()),e);
            }
        }

        return soundBytes;
    }

}
