package com.spazomatic.nabsta.views.actionBar;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ActionProvider;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.db.Image;
import com.spazomatic.nabsta.db.Song;
import com.spazomatic.nabsta.views.fragments.OpenProjectDialog;
import com.spazomatic.nabsta.tasks.LoadSongsTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by samuelsegal on 5/18/15.
 */
public class SongsActionProvider extends ActionProvider
        implements MenuItem.OnMenuItemClickListener{

    private Context context;
    public static final String SONG_ID = "songId";
    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public SongsActionProvider(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();

        LoadSongsTask loadSongsTask = new LoadSongsTask();
        loadSongsTask.execute();
        List<Song> songs = null;
        try {
            songs = loadSongsTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(NabstaApplication.LOG_TAG,String.format(
                    "Error loading songs with Error Message: %s", e.getMessage()), e);
        }
        if(songs != null) {
            for (final Song song : songs) {
                MenuItem songItem = subMenu.add(song.getName());
                if (song.getImage() != null) {
                    Image songImage = song.getImage();
                    String fileName = songImage.getFile_name();
                    File imageFile = new File(fileName);
                    try {
                        InputStream fileInputStream = new FileInputStream(imageFile);
                        Drawable songDrawable = Drawable.createFromStream(fileInputStream, imageFile.getAbsolutePath());
                        songItem.setIcon(songDrawable);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    songItem.setIcon(R.drawable.ic_launcher);
                }
                songItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        openSong(song);
                        return true;
                    }
                });
            }
        }
    }

    private void openSong(Song song) {

        Activity activity = (Activity)((ContextThemeWrapper)context).getBaseContext();
        DialogFragment openSongDialog = OpenProjectDialog.newInstance(song.getId());
        openSongDialog.show(activity.getFragmentManager(), "OpenSongDialog");
    }

    @Override
    public boolean onPerformDefaultAction() {
        return super.onPerformDefaultAction();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        //TODO: implement view All Songs Activity
        return true;
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }


}
