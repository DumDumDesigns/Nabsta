package com.spazomatic.nabsta.views.actionBar;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.views.fragments.ManageProjectsDialog;
import com.spazomatic.nabsta.views.fragments.ManageSettingsDialog;
import com.spazomatic.nabsta.views.fragments.NewProjectDialog;

/**
 * Created by samuelsegal on 5/18/15.
 */
public class NabstaActionProvider extends ActionProvider {

    private Context context;
    private MenuItem newProjectMenuItem;
    private MenuItem manageProjectMenuItem;
    private MenuItem settingsItem;
    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public NabstaActionProvider(Context context) {
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
        //TODO: Add Icons per action like X for delete
        //MenuInflater menuInflater = ((Activity)((ContextThemeWrapper)context)
        //       .getBaseContext()).getMenuInflater();
        //menuInflater.inflate(R.menu.menu_project,subMenu);
        newProjectMenuItem = subMenu.add(R.string.new_project);
        newProjectMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                createProject();
                return true;
            }
        });
        newProjectMenuItem.setIcon(R.drawable.ic_launcher);

        manageProjectMenuItem = subMenu.add(R.string.manage_projects);
        manageProjectMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                manageProjects();
                return true;
            }
        });

        settingsItem = subMenu.add(R.string.action_settings);
        settingsItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                manageSettings();
                return true;
            }
        });
    }



    @Override
    public boolean onPerformDefaultAction() {
        return super.onPerformDefaultAction();
    }


    private void manageProjects() {
        Activity activity = (Activity)((ContextThemeWrapper)context).getBaseContext();
        ManageProjectsDialog manageProjectsDialog = new ManageProjectsDialog();
        manageProjectsDialog.show(activity.getFragmentManager(),"ManageProjectsDialog");
    }

    private void createProject() {
        Activity activity = (Activity)((ContextThemeWrapper)context).getBaseContext();
        DialogFragment newProjectDialog = new NewProjectDialog();
        newProjectDialog.show(activity.getFragmentManager(),"NewProjectDialog");
    }
    private void manageSettings() {
        Activity activity = (Activity)((ContextThemeWrapper)context).getBaseContext();
        DialogFragment manageSettingsDialog = new ManageSettingsDialog();
        manageSettingsDialog.show(activity.getFragmentManager(),"ManageSettingsDialog");
    }
    @Override
    public boolean hasSubMenu() {
        return true;
    }
}
