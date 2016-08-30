package cz.ackee.androidskeleton.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.event.SelectProject;
import cz.ackee.androidskeleton.model.Project;
import de.greenrobot.event.EventBus;
import me.grantland.widget.AutofitHelper;

/**
 * Adapter for project search
 * <p/>
 * Created by Petr Schneider[petr.schneider@ackee.cz] on {13. 3. 2015}
 */
public class SearchProjectAdapter extends ArrayAdapter<Project> {
    public static final String TAG = SearchProjectAdapter.class.getName();

    List<Project> mData = new ArrayList<>();

    public SearchProjectAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_project_search, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        final Project p = getItem(position);
        holder.txtTitle.setText(p.getShowName());
//        AutofitHelper.create(txtTitle);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SelectProject(p.getId()));
            }

        });
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.title)
        TextView txtTitle;

        public ViewHolder(View v) {
            ButterKnife.inject(this, v);
        }
    }


    public void appendData(List<Project> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Project getItem(int position) {
        return mData.get(position);
    }

    @Override
    public void clear() {
        super.clear();
        mData.clear();
    }
}
