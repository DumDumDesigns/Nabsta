package com.spazomatic.nabsta.tasks;

import android.os.AsyncTask;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.Track;
import com.spazomatic.nabsta.db.dao.DaoSession;
import com.spazomatic.nabsta.db.dao.TrackDao;

/**
 * Created by samuelsegal on 6/6/15.
 */
public class DeleteTracksTask extends AsyncTask<Track,Void,Song> {
    @Override
    protected Song doInBackground(Track... params) {
        DaoSession daoSession = NabstaApplication.getInstance().getDaoSession();
        TrackDao trackDao = daoSession.getTrackDao();
        trackDao.deleteInTx(params);
        //TODO:Delete TrackFiles, think of redo / undo before implementation
        Song song = NabstaApplication.getInstance().getSongInSession();
        song = daoSession.getSongDao().load(song.getId());
        song.refresh();
        return song;
    }
}
