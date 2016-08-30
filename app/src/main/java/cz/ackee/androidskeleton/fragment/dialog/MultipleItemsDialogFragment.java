package cz.ackee.androidskeleton.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import cz.ackee.androidskeleton.R;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {12. 6. 2015}
 **/
public class MultipleItemsDialogFragment extends DialogFragment {
    public static final String TAG = MultipleItemsDialogFragment.class.getName();
    private static final String ITEMS_KEY = "items";
    private static final String TITLE_KEY = "title";
    private static final String CHECKED_ITEMS_KEY = "checked_items";
    public DialogInterface.OnMultiChoiceClickListener mOnMultiChoiceClickListener;
    public DialogInterface.OnClickListener mOnDoneListener;

    public static MultipleItemsDialogFragment newInstance(String title, ArrayList<String> items, boolean [] checkedItems) {
        Bundle args = new Bundle();
        args.putStringArrayList(ITEMS_KEY, items);
        args.putString(TITLE_KEY, title);
        args.putBooleanArray(CHECKED_ITEMS_KEY, checkedItems);
        MultipleItemsDialogFragment fragment = new MultipleItemsDialogFragment();
        fragment.setArguments(args);
        return fragment;


    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString(TITLE_KEY));
        ArrayList<String> items = getArguments().getStringArrayList(ITEMS_KEY);
        String[] itemsArray = new String[items.size()];
        items.toArray(itemsArray);
        builder.setMultiChoiceItems(itemsArray, getArguments().getBooleanArray(CHECKED_ITEMS_KEY), mOnMultiChoiceClickListener);
        builder.setPositiveButton(R.string.dialog_multiselect_done, mOnDoneListener);
        return builder.create();
    }
}
