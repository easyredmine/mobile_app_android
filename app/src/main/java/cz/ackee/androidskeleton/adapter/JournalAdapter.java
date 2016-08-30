package cz.ackee.androidskeleton.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.base.BaseFragmentActivity;
import cz.ackee.androidskeleton.model.Journal;
import cz.ackee.androidskeleton.utils.TimeUtils;

/**
 * Adapter for journal of issue
 * Created by Petr Schneider[petr.schneider@ackee.cz] on {24. 3. 2015}
 */
public class JournalAdapter extends ArrayAdapter<Journal> {
    public static final String TAG = JournalAdapter.class.getName();

    List<Journal> mData = new ArrayList<>();

    public JournalAdapter(Context context) {
        super(context, 0);
    }

    public JournalAdapter(Context context, List<Journal> journals) {
        super(context, 0);
        this.mData = journals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_task_detail_journal, parent, false);
        }
        Journal j = getItem(position);
        if (j.getNotes() == null || j.getNotes().isEmpty()){
            convertView.setVisibility(View.GONE);
        }else{
            convertView.setVisibility(View.VISIBLE);
        }
        TextView txtAuthor = (TextView) convertView.findViewById(R.id.txtauthor);
        TextView txtNote = (TextView) convertView.findViewById(R.id.txtnote);
        String preAuthor = getContext().getString(R.string.prefix_author);
        String author = j.getUser().getName();
        String postAuthor = TimeUtils.getTimeFormatted(j.getCreatedOn(), TimeUtils.ATOM_FORMAT, TimeUtils.COMMENT_TIME_FORMAT);
        Spannable span = new SpannableString(preAuthor + " " + author + " " + postAuthor);
        span.setSpan(new ForegroundColorSpan(((BaseFragmentActivity) getContext()).isEasyRedmine() ? getContext().getResources().getColor(R.color.er_primary) : getContext().getResources().getColor(R.color.r_primary)), preAuthor.length(), (preAuthor + author + 1).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtAuthor.setText(span);
        txtNote.setText(j.getNotes());
        return convertView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Journal getItem(int position) {
        return mData.get(position);
    }


    @Override
    public void clear() {
        super.clear();
        mData.clear();
    }
}
