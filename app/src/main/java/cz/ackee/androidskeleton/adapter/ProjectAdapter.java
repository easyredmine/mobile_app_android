package cz.ackee.androidskeleton.adapter;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.model.Project;

/**
 * Adapter class managing projects
 * Created by Jan Stanek[jan.stanek@ackee.cz] on {21. 4. 2015}
 */
public class ProjectAdapter extends HRArrayAdapter<Project> implements Searchable {

    private List<Project> projects;

    public ProjectAdapter(FragmentActivity activity, List<Project> data) {
        super(activity, 0, data);
        projects = data;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner_project, parent, false);
        }

        String text = getItem(position).toString();
        TextView textView = ((TextView) convertView.findViewById(android.R.id.text1));

        if (TextUtils.isEmpty(mFilterConstraint)) {
            textView.setText(text);
        } else {
            SpannableString spannable = new SpannableString(text);

            String testText = toNoPalatals(text).toLowerCase();
            String filter =  toNoPalatals(String.valueOf(mFilterConstraint)).toLowerCase();
            int start = testText.indexOf(filter);
            if (start >= 0) {
                spannable.setSpan(new StyleSpan(Typeface.BOLD), start, start+mFilterConstraint.length(), 0);
            }

            textView.setText(spannable);
        }

        convertView.setTag(getItem(position));

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }

    /**
     * Return position of project by its id
     *
     * @param id
     * @return position of project in arrayadapter or -1 if this doesn't contain project
     */
    public int getPositionByProjectId(int id) {
        int count = getCount();
        for (int i = 0; i < count; ++i) {
            if (projects.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void searchString(@NonNull CharSequence string) {
//        mSearchString = String.valueOf(string).toLowerCase();
    }


}
