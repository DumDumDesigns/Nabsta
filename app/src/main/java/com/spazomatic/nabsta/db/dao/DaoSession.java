package com.spazomatic.nabsta.db.dao;

import android.database.sqlite.SQLiteDatabase;

import com.spazomatic.nabsta.db.Artist;
import com.spazomatic.nabsta.db.Image;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.db.Track;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig imageDaoConfig;
    private final DaoConfig artistDaoConfig;
    private final DaoConfig trackDaoConfig;
    private final DaoConfig songDaoConfig;

    private final ImageDao imageDao;
    private final ArtistDao artistDao;
    private final TrackDao trackDao;
    private final SongDao songDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        imageDaoConfig = daoConfigMap.get(ImageDao.class).clone();
        imageDaoConfig.initIdentityScope(type);

        artistDaoConfig = daoConfigMap.get(ArtistDao.class).clone();
        artistDaoConfig.initIdentityScope(type);

        trackDaoConfig = daoConfigMap.get(TrackDao.class).clone();
        trackDaoConfig.initIdentityScope(type);

        songDaoConfig = daoConfigMap.get(SongDao.class).clone();
        songDaoConfig.initIdentityScope(type);

        imageDao = new ImageDao(imageDaoConfig, this);
        artistDao = new ArtistDao(artistDaoConfig, this);
        trackDao = new TrackDao(trackDaoConfig, this);
        songDao = new SongDao(songDaoConfig, this);

        registerDao(Image.class, imageDao);
        registerDao(Artist.class, artistDao);
        registerDao(Track.class, trackDao);
        registerDao(Song.class, songDao);
    }
    
    public void clear() {
        imageDaoConfig.getIdentityScope().clear();
        artistDaoConfig.getIdentityScope().clear();
        trackDaoConfig.getIdentityScope().clear();
        songDaoConfig.getIdentityScope().clear();
    }

    public ImageDao getImageDao() {
        return imageDao;
    }

    public ArtistDao getArtistDao() {
        return artistDao;
    }

    public TrackDao getTrackDao() {
        return trackDao;
    }

    public SongDao getSongDao() {
        return songDao;
    }

}
