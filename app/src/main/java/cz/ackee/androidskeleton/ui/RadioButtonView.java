package cz.ackee.androidskeleton.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.material.widget.RadioButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.ackee.androidskeleton.R;

/**
 * TODO add class description
 * Created by Petr Schneider[petr.schneider@ackee.cz] on {16.3. 2015}
 */
public class RadioButtonView extends RelativeLayout {
    public static final String TAG = RadioButtonView.class.getName();

    @InjectView(R.id.filterRadioButton)
    RadioButton mRadioButton;
    @InjectView(R.id.txtFilterName)
    TextView mTxtFilterName;
    @InjectView(R.id.separator)
    View separator;


    int mQueryId;
    String mQueryString;

    // position in layout
    Integer mPosition;
    public Object mLastSelected;

    public RadioButtonView(Context context) {
        super(context);
        init(null);
    }

    public RadioButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.widget_radiobutton, this);

        ButterKnife.inject(this);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RadioButtonItem,
                0, 0);

        try {
            int arg = a.getResourceId(R.styleable.RadioButtonItem_filter_name, 0);
            if (arg != 0) {
                String title = getContext().getString(arg);
                mTxtFilterName.setText(title);
            }
            arg = a.getResourceId(R.styleable.RadioButtonItem_filter_value, 0);
            if (arg != 0) {
                mQueryString = getContext().getString(arg);
            }
        } finally {
            a.recycle();
        }
        mPosition = 0;
//        setBackgroundResource(R.drawable.selector);
    }

    public RadioButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }

    public RadioButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void setTitle(String name) {
        mTxtFilterName.setText(name);
    }

    public void setQueryId(int id) {
        mQueryString = "query_id=" + id;
    }

    public String getQuery() {
        return mQueryString;
    }

    public void setPosition(Integer position) {
        this.mPosition = position;
    }

    public Integer getPosition() {
        return mPosition;
    }

    public void setChecked(boolean b) {

        mRadioButton.setChecked(b);
        mRadioButton.invalidate();
    }

    public void setCheckedForced(boolean checked) {
        mRadioButton.setChecked(checked);
    }

    public void setLast() {
        separator.setVisibility(View.GONE);
    }

    public String getTitle() {
        return mTxtFilterName.getText().toString();
    }

    public void setClickListener(OnClickListener listener) {
        mTxtFilterName.setOnClickListener(listener);
    }
}
