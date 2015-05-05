package com.spazomatic.nabsta.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by samuelsegal on 5/5/15.
 */
public class PlayMediaService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
