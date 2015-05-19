package com.spazomatic.nabsta.tasks;

import android.os.AsyncTask;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.db.Artist;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.dao.DaoSession;

/**
 * Created by samuelsegal on 5/19/15.
 */
public class CreateSongTask extends AsyncTask<String,Void,Song> {
    @Override
    protected Song doInBackground(String... params) {
        DaoSession daoSession = NabstaApplication.getInstance().getDaoSession();
        Artist artist = new Artist();
        artist.setName(params[1]);
        long artistId = daoSession.getArtistDao().insert(artist);
        artist.setId(artistId);

        Song song = new Song();
        song.setName(params[0]);
        song.setArtist(artist);
        long songId = daoSession.getSongDao().insert(song);
        song = daoSession.getSongDao().load(songId);
        return song;
    }
}
