package cz.ackee.androidskeleton.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cz.ackee.androidskeleton.model.IdNameEntity;

/**
 * TODO add class description
 *
 * @author Michal Kučera [michal.kucera@ackee.cz]
 * @since 24/11/15
 **/
public class VersionsAdapter extends ArrayAdapter<IdNameEntity> {
    public static final String TAG = VersionsAdapter.class.getName();


    public VersionsAdapter(Context context, List<IdNameEntity> versions) {
        super(context, 0, versions);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        TextView txtView = (TextView) view.findViewById(android.R.id.text1);
        txtView.setText(getItem(position).getName());

        return view;
    }

}
