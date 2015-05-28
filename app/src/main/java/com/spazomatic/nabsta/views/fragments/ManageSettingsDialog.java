package com.spazomatic.nabsta.views.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samuelsegal on 5/18/15.
 */
public class ManageSettingsDialog extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Map<String,Boolean> choicesMap = new HashMap<>();
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                NabstaApplication.NABSTA_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_manage_settings, null);
        final RadioGroup keepScreenOnRadioGrp = (RadioGroup)dialogView.findViewById(
                R.id.keep_screen_on_radio_grp);
        if(sharedPreferences.contains(NabstaApplication.NABSTA_KEEP_SCREEN_ON)){
            boolean keepScreenOn = sharedPreferences.getBoolean(
                    NabstaApplication.NABSTA_KEEP_SCREEN_ON, true);
            if(keepScreenOn){
                keepScreenOnRadioGrp.check(R.id.keep_screen_on_yes);
            }else{
                keepScreenOnRadioGrp.check(R.id.keep_screen_on_no);
            }
        }

        keepScreenOnRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch(checkedId){
                    case R.id.keep_screen_on_yes:{
                        choicesMap.put(NabstaApplication.NABSTA_KEEP_SCREEN_ON,true);
                        break;
                    }
                    case R.id.keep_screen_on_no:{
                        choicesMap.put(NabstaApplication.NABSTA_KEEP_SCREEN_ON,false);
                        break;
                    }
                }
            }
        });

        builder.setView(dialogView)
                .setPositiveButton(R.string.save_changes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        for(String key : choicesMap.keySet()){
                            editor.putBoolean(key,choicesMap.get(key));
                            editor.commit();
                            if(key.equals(NabstaApplication.NABSTA_KEEP_SCREEN_ON) &&
                                    choicesMap.get(key).equals(true)){
                                getActivity().getWindow().addFlags(
                                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                Log.d(NabstaApplication.LOG_TAG, String.format(
                                        "Keep Screen on: %b", true));
                            }else{
                                getActivity().getWindow().clearFlags(
                                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                Log.d(NabstaApplication.LOG_TAG, String.format(
                                        "Keep Screen on: %b", false));
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Canceled
                    }
                });
        return builder.create();
    }

}
