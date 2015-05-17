package com.spazomatic.nabsta.receivers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.spazomatic.nabsta.MainActivity;
import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;

public class BatteryLevelReceiver extends BroadcastReceiver {

    public BatteryLevelReceiver() {
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        int level = intent.getIntExtra("level", 0);
        Log.i(NabstaApplication.LOG_TAG, String.format(
                "Intent Action: %s : Battery level: %d",intent.getAction(),level));
        if (level < 10) {
            if(NabstaApplication.isActivityVisible()) {
                new AlertDialog.Builder(context)
                        //TODO.setIcon(R.drawable.alert_dialog_icon)
                        .setTitle(R.string.toast_battery_low)
                        .setPositiveButton(R.string.shut_down, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (context instanceof MainActivity) {
                                    Log.i(NabstaApplication.LOG_TAG, String.format(
                                            "MainActivity %s Shutting Down...",
                                            context.toString()));
                                    ((MainActivity) context).finish();
                                } else if (context instanceof Activity) {
                                    Log.i(NabstaApplication.LOG_TAG, String.format(
                                            "Activity %s Shutting Down...",context.toString()));
                                    ((Activity) context).finish();
                                }
                            }
                        })
                        .setNegativeButton(R.string.keep_open, null)
                        .create().show();
            }else{
                //App is not in foreground, though check is not necessary am checking for now anyhow
            }
        }
    }


}
