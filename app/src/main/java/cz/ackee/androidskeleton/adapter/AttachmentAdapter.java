package cz.ackee.androidskeleton.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.model.Upload;

/**
 * Adapter for list of attachements
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on 26.4.2015.
 */
public class AttachmentAdapter extends ArrayAdapter<Upload> {
    public static final String TAG = TaskAdapter.class.getName();

    List<Upload> mData = new ArrayList<>();

    public AttachmentAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_attachment, parent, false);
        }
        TextView txtTitle = (TextView) convertView.findViewById(R.id.attachmentTitle);
        ImageView remove = (ImageView) convertView.findViewById(R.id.remove);

        Upload upload = getItem(position);

        txtTitle.setText(upload.filename);

        return convertView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Upload getItem(int position) {
        return mData.get(position);
    }

    public void appendData(List<Upload> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void append(Upload data) {
        mData.add(data);
    }

    public void setData(List<Upload> uploads) {
        mData = uploads;
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        super.clear();
        mData.clear();
    }
}
