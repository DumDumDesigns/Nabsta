package com.spazomatic.nabsta.tasks;

import android.os.AsyncTask;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.db.Track;
import com.spazomatic.nabsta.db.dao.DaoMaster;
import com.spazomatic.nabsta.db.dao.DaoSession;
import com.spazomatic.nabsta.db.dao.TrackDao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by samuelsegal on 5/20/15.
 */
public class LoadTracksTask extends AsyncTask<Long,Void,List<Track>> {
    @Override
    protected List<Track> doInBackground(Long... params) {
        Long songId = params[0];
        DaoMaster daoMaster  = NabstaApplication.getInstance().getDaoMaster();
        DaoSession daoSession = daoMaster.newSession();
        TrackDao trackDao = daoSession.getTrackDao();
        QueryBuilder qb = trackDao.queryBuilder();
        qb.where(TrackDao.Properties.Song_id_fk.eq(songId));

        return qb.list();
    }
}
