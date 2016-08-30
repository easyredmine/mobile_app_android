package cz.ackee.androidskeleton.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.base.BaseFragmentActivity;
import cz.ackee.androidskeleton.fragment.EditTaskFragment;
import cz.ackee.androidskeleton.fragment.NewTaskFragment;
import cz.ackee.androidskeleton.fragment.TasksFragment;
import cz.ackee.androidskeleton.fragment.UploadProgressListener;
import cz.ackee.androidskeleton.fragment.UploadWorkerFragment;
import cz.ackee.androidskeleton.fragment.base.BaseFragment;
import cz.ackee.androidskeleton.iface.UploadTaskCallbacks;
import retrofit.RetrofitError;

public class NewTaskActivity extends BaseFragmentActivity implements UploadTaskCallbacks {
    private static final int RC_EDIT_TASK = 1;
    private UploadProgressListener uiFragment;
    private UploadWorkerFragment workerFragment;

    public static void start(Context c) {
        Intent in = new Intent(c, NewTaskActivity.class);
        c.startActivity(in);
    }

    public static void start(Context c, Bundle bundle) {
        Intent in = new Intent(c, NewTaskActivity.class);
        in.putExtras(bundle);
        c.startActivity(in);
    }

    public static void startForResult(BaseFragmentActivity c, Bundle bundle) {
        Intent in = new Intent(c, NewTaskActivity.class);
        in.putExtras(bundle);
        c.startActivityForResult(in, RC_EDIT_TASK);
    }

    @Override
    protected String getFragmentName() {
        return TasksFragment.class.getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        uiFragment = (UploadProgressListener) fm.findFragmentByTag(NewTaskFragment.class.getName());
        workerFragment = (UploadWorkerFragment) fm.findFragmentByTag(UploadWorkerFragment.class.getName());

        // create the fragment and data the first time
        if (uiFragment == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null || extras.getInt(EditTaskFragment.ISSUE_ID, 0) <= 0) {
                uiFragment = NewTaskFragment.newInstance(getIntent().getExtras());
            } else {
                uiFragment = (UploadProgressListener)fm.findFragmentByTag(EditTaskFragment.class.getName());
                if (uiFragment == null) {
                    uiFragment = EditTaskFragment.newInstance(getIntent().getExtras());
                }
            }
        }

        replaceFragment((BaseFragment) uiFragment, false);

        if (workerFragment == null) {
            workerFragment = UploadWorkerFragment.newInstance(null);

            try {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(workerFragment, workerFragment.getClass().getName());
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
            } catch (Exception e) {//java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(String filename, String tag, int percent) {
        uiFragment.onProgressUpdate(filename, tag, percent);
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(String filename, String tag, Object response) {
        uiFragment.onUploadComplete(filename, tag, response);
    }

    @Override
    public void startUpload(File file, String tag, String contentType) {
        workerFragment.startUpload(file, tag, contentType);
    }

    @Override
    public void onError(RetrofitError error, String tag) {
        uiFragment.showError(error, tag);
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .title(R.string.title)
                .content(R.string.content)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        ((UploadProgressListener) getCurrentFragment()).setShouldSend(false);
                        finish();
                    }
                })
                .show();
    }

}