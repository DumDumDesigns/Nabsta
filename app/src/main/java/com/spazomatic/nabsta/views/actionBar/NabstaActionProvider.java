package com.spazomatic.nabsta.views.actionBar;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.spazomatic.nabsta.NabstaApplication;
import com.spazomatic.nabsta.R;
import com.spazomatic.nabsta.views.fragments.ManageProjectsDialog;
import com.spazomatic.nabsta.views.fragments.NewProjectDialog;

/**
 * Created by samuelsegal on 5/18/15.
 */
public class NabstaActionProvider extends ActionProvider
        implements MenuItem.OnMenuItemClickListener{

    private Context context;
    private MenuItem newProjectMenuItem;
    private MenuItem manageProjectMenuItem;
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
        newProjectMenuItem.setOnMenuItemClickListener(this);
        newProjectMenuItem.setIcon(R.drawable.ic_launcher);

        manageProjectMenuItem = subMenu.add(R.string.manage_projects);
        manageProjectMenuItem.setOnMenuItemClickListener(this);

    }

    @Override
    public boolean onPerformDefaultAction() {
        return super.onPerformDefaultAction();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final String itemTitle = item.getTitle().toString();
        Log.d(NabstaApplication.LOG_TAG,String.format("Item Title: %s",itemTitle));
        if(itemTitle.equals(newProjectMenuItem.getTitle())) {
            createProject();
        }else if(itemTitle.equals(manageProjectMenuItem.getTitle())){
            manageProjects();
        }
        return true;
    }

    private void manageProjects() {
        Activity activity = (Activity)((ContextThemeWrapper)context).getBaseContext();
        ManageProjectsDialog manageProjectsDialog = new ManageProjectsDialog();
        manageProjectsDialog.show(activity.getFragmentManager(),"NewProjectDialog");
    }

    private void createProject() {
        Activity activity = (Activity)((ContextThemeWrapper)context).getBaseContext();
        DialogFragment newProjectDialog = new NewProjectDialog();
        newProjectDialog.show(activity.getFragmentManager(),"NewProjectDialog");
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }
}
