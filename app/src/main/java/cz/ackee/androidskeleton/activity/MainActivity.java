package cz.ackee.androidskeleton.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;


import com.afollestad.materialdialogs.MaterialDialog;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.base.BaseMenuActivity;
import cz.ackee.androidskeleton.fragment.NewTaskFragment;
import cz.ackee.androidskeleton.fragment.TasksFragment;
import cz.ackee.androidskeleton.model.ProjectDownloadedEvent;
import cz.ackee.androidskeleton.model.ProjectDownloadingEvent;
import cz.ackee.androidskeleton.service.ProjectsUpdateService;
import cz.ackee.androidskeleton.utils.Utils;
import de.greenrobot.event.EventBus;

/**
 * Main activity Created by david.bilik@ackee.cz on 26. 6. 2014.
 */
public class MainActivity extends BaseMenuActivity {
    @Override
    protected String getFragmentName() {
        return TasksFragment.class.getName();
    }

    private NewTaskFragment dataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            startService(new Intent(this, ProjectsUpdateService.class));
        }
        if (Utils.getTempIssueHashFile().exists()) {
//            new AlertDialog.Builder(this)
//                    .setTitle(R.string.app_dialog_work_in_progress_title)
//                    .setMessage(R.string.app_dialog_work_in_progress_title)
//                    .setPositiveButton(R.string.app_dialog_work_in_progress_btn_show, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Bundle args = new Bundle();
//                            args.putBoolean(NewEditTaskFragment.IS_SAVED_KEY, true);
//                            NewTaskActivity.start(MainActivity.this, args);
//                        }
//                    })
//                    .setNegativeButton(R.string.app_dialog_work_in_progress_btn_delete, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Utils.getTempIssueHashFile().delete();
//                        }
//                    }).show();
            new MaterialDialog.Builder(this)
                    .content(R.string.app_dialog_work_in_progress)
                    .title(R.string.app_dialog_work_in_progress_title)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            Bundle args = new Bundle();
                            args.putBoolean(NewTaskFragment.IS_SAVED_KEY, true);
                            NewTaskActivity.start(MainActivity.this, args);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            Utils.getTempIssueHashFile().delete();
                        }
                    }).positiveText(R.string.app_dialog_work_in_progress_btn_show)
                    .negativeText(R.string.app_dialog_work_in_progress_btn_delete)
                    .build()
                    .show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static class ProjectDownloadingDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            setCancelable(false);
            return new MaterialDialog.Builder(getActivity()).progress(true, 0).cancelable(false).content(R.string.app_dialog_synchronizing_projects).build();
        }

    }


    public void onEventMainThread(ProjectDownloadedEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        DialogFragment df = (DialogFragment) getSupportFragmentManager().findFragmentByTag(ProjectDownloadingDialogFragment.class.getName());
        if (df != null) {
            df.dismissAllowingStateLoss();
        }
    }

    public void onEventMainThread(ProjectDownloadingEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        if (!event.isHasSomeProjects()) {
            new ProjectDownloadingDialogFragment().show(getSupportFragmentManager(), ProjectDownloadingDialogFragment.class.getName());
        }
    }
}
