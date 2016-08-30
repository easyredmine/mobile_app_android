package cz.ackee.androidskeleton.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.model.Issue;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {15. 6. 2015}
 **/
public class IssueErrorDialog extends DialogFragment {
    public static final String TAG = IssueErrorDialog.class.getName();
    private static final String BODY_KEY = "body";

    public static IssueErrorDialog newInstance(String body) {
        Bundle args = new Bundle();
        args.putString(BODY_KEY, body);
        IssueErrorDialog isd = new IssueErrorDialog();
        isd.setArguments(args);
        return isd;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.error);
        builder.setMessage(getArguments().getString(BODY_KEY));
        builder.setPositiveButton(R.string.validation_ok, null);
        return builder.create();
    }
}
