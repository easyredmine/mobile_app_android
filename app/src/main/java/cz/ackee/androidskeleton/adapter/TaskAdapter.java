package cz.ackee.androidskeleton.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.base.BaseFragmentActivity;
import cz.ackee.androidskeleton.event.IssueDeletedEvent;
import cz.ackee.androidskeleton.model.Issue;
import cz.ackee.androidskeleton.utils.TimeUtils;
import de.greenrobot.event.EventBus;

/**
 * Adapter for list of issues
 * Created by David Bilik[david.bilik@ackee.cz] on {18. 2. 2015}
 */
public class TaskAdapter extends ArrayAdapter<Issue> {
    public static final String TAG = TaskAdapter.class.getName();

    List<Issue> mData = new ArrayList<>();

    public TaskAdapter(Context context) {
        super(context, 0);
        EventBus.getDefault().register(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_task, parent, false);
        }
        TextView txtSubject = (TextView) convertView.findViewById(R.id.txtTaskSubject);
        TextView txtAssignee = (TextView) convertView.findViewById(R.id.txtAssignee);
        TextView txtDueDate = (TextView) convertView.findViewById(R.id.txtDueDate);
        TextView txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
        TextView txtPriority = (TextView) convertView.findViewById(R.id.txtPriority);
        ImageView favorite = (ImageView) convertView.findViewById(R.id.favorite);
        Issue i = getItem(position);

        txtPriority.setText(i.getPriority().getName());
        txtPriority.setTextColor(getContext().getResources().getColor(isEasyRedmine() ? R.color.er_primary : R.color.r_primary));
        txtStatus.setText(i.getDoneRatio() + "%");
        txtSubject.setText(i.getSubject());
        if (i.getAssignedTo() != null) {
            txtAssignee.setText(i.getAssignedTo().getName());
        } else {
            txtAssignee.setText(getContext().getResources().getString(R.string.no_value));
        }

        final SpannableStringBuilder sb = new SpannableStringBuilder(getContext().getString(R.string.task_list_item_due_date) + " " + (TextUtils.isEmpty(i.getDueDate()) ? getContext().getResources().getString(R.string.no_value) : TimeUtils.getTimeFormatted(i.getDueDate(), TimeUtils.ATOM_FORMAT_DATE, TimeUtils.DUE_DATE)));
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.RED);
        if (TimeUtils.expired(i.getDueDate())) {
            sb.setSpan(fcs, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        txtDueDate.setText(sb);

        if (i.isFavorited()) {
            favorite.setVisibility(View.VISIBLE);
            favorite.setImageResource(R.drawable.ic_star_full);
        } else {
            if (isEasyRedmine()) {
                favorite.setVisibility(View.VISIBLE);
                favorite.setImageResource(R.drawable.ic_star);
            } else {
                favorite.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private boolean isEasyRedmine() {
        return ((BaseFragmentActivity) getContext()).isEasyRedmine();
    }

    public void appendData(List<Issue> data) {
        mData.addAll(data);
//        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Issue getItem(int position) {
        return mData.get(position);
    }


    @Override
    public void clear() {
        super.clear();
        mData.clear();
    }

    public void onEventMainThread(IssueDeletedEvent event) {
        for (int i = 0; i < getCount(); i++) {
            if (mData.get(i).getId() == event.getIssueId()) {
                mData.remove(i);
                break;
            }
        }
        notifyDataSetChanged();
    }
}
