package cz.ackee.androidskeleton.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.model.IdNameEntity;

/**
 * Adapter for assaginees autocompletetextview
 *
 * @author Michal Kučera [michal.kucera@ackee.cz]
 * @since 19/11/15
 **/
public class AssigneesAdapter extends HRArrayAdapter<IdNameEntity> implements Searchable {

    public static final int GROUP_ID = -123;
    public static final int ITEM_SECTION = 0;
    public static final int ITEM_VALUE = 1;

    public AssigneesAdapter(Context context, List<IdNameEntity> assignee) {
        super(context, 0, assignee);
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == ITEM_VALUE;
    }


    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getId() != null && getItem(position).getId() == GROUP_ID) {
            return ITEM_SECTION;
        }
        return ITEM_VALUE;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == ITEM_SECTION) {
            return getSectionView(position, parent);
        }
        return getValueView(position, parent);
    }

    private View getSectionView(int position, ViewGroup parent) {
        View convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_assignee_section, parent, false);

        TextView txt = (TextView) convertView.findViewById(android.R.id.text1);
        txt.setText(getItem(position).getName());
        txt.setTypeface(Typeface.DEFAULT_BOLD);
        return convertView;
    }

    private View getValueView(int position, ViewGroup parent) {
        View convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_1, parent, false);

        TextView txt = (TextView) convertView.findViewById(android.R.id.text1);

        String text = getItem(position).getName();
        if (TextUtils.isEmpty(mFilterConstraint)) {
            txt.setText(text);
        } else {
            SpannableString spannable = new SpannableString(text);
            String testText = toNoPalatals(text).toLowerCase();
            String filter =  toNoPalatals(String.valueOf(mFilterConstraint)).toLowerCase();
            int start = testText.indexOf(filter);
            if (start >= 0) {
                spannable.setSpan(new StyleSpan(Typeface.BOLD), start, start + mFilterConstraint.length(), 0);
            }

            txt.setText(spannable);
        }
        return convertView;
    }

    @Override
    public void searchString(@NonNull CharSequence string) {
//        mSearchString = String.valueOf(string).toLowerCase();
    }
}