package com.spazomatic.nabsta.tasks;

import android.os.AsyncTask;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.dao.DaoSession;
import com.spazomatic.nabsta.db.dao.SongDao;

/**
 * Created by samuelsegal on 5/20/15.
 */
public class LoadSongTask extends AsyncTask<Long,Void,Song> {
    @Override
    protected Song doInBackground(Long... params) {
        Long songId = params[0];
        DaoSession daoSession  = NabstaApplication.getInstance().getDaoSession();

        SongDao songDao = daoSession.getSongDao();
        Song song = songDao.load(songId);
        songDao.refresh(song);
        return song;
    }
}

