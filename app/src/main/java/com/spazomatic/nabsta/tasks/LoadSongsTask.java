package com.spazomatic.nabsta.tasks;

import android.os.AsyncTask;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.dao.DaoSession;
import com.spazomatic.nabsta.db.dao.SongDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelsegal on 5/19/15.
 */
public class LoadSongsTask extends AsyncTask<Long,Void,List<Song>> {
    @Override
    protected List<Song> doInBackground(Long... params) {

        DaoSession daoSession = NabstaApplication.getInstance().getDaoSession();
        SongDao songDao = daoSession.getSongDao();
        if(params != null && params.length > 0){
            Song song = songDao.load(params[0]);
            List<Song> songs = new ArrayList<>();
            songs.add(song);
            return songs;
        }
        return songDao.loadAll();
    }
}