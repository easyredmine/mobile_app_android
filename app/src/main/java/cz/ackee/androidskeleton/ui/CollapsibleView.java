package cz.ackee.androidskeleton.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.utils.HeightSetter;

/**
 * TODO add class description
 * Created by David Bilik[david.bilik@ackee.cz] on {18. 2. 2015}
 */
public class CollapsibleView extends FrameLayout {
    public static final String TAG = CollapsibleView.class.getName();
    private static final long ANIM_ROLLUP_DURATION = 200;
    private static final String PARENT_STATE = "parentState";
    private static final String IS_OPENED = "isOpened";

    int mHiddenHeight;
    @InjectView(R.id.layoutHeader)
    View mLayoutHeader;
    @InjectView(R.id.layoutContent)
    FrameLayout mLayoutContent;
    @InjectView(R.id.txtSectionTitle)
    TextView mTxtSectionTitle;
    @InjectView(R.id.imgArrow)
    View mImgArrow;
    @InjectView(R.id.bottomline)
    View bottomLine;
    private boolean mIsOpened;

    public CollapsibleView(Context context) {

        super(context);
        init(null);

    }

    public CollapsibleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.widget_collapsible, this);

        ButterKnife.inject(this);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CollapsibleItem,
                0, 0);

        try {
            String title = getContext().getString(a.getResourceId(R.styleable.CollapsibleItem_section_title, 0));
            mTxtSectionTitle.setText(title);
        } finally {
            a.recycle();
        }
        mHiddenHeight = -1;

    }

    public CollapsibleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }

    public CollapsibleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void setCollapsibleView(View v) {
        mLayoutContent.removeAllViews();
        mLayoutContent.addView(v, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                if (mHiddenHeight > 1) {
                    mHiddenHeight = mLayoutContent.getHeight();
                }
                Log.d(TAG, "hidden height = " + mHiddenHeight);
                MarginLayoutParams params = (MarginLayoutParams) mLayoutContent.getLayoutParams();
                params.height = 0;
                mLayoutContent.setLayoutParams(params);
                mLayoutHeader.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIsOpened = mLayoutContent.getHeight() == 0;
                        if (mLayoutContent.getHeight() == 0) {
                            animateView(mLayoutContent, mHiddenHeight);
                            mImgArrow.animate().rotation(180).setDuration(ANIM_ROLLUP_DURATION);
                        } else {
                            animateView(mLayoutContent, 0);
                            mImgArrow.animate().rotation(0).setDuration(ANIM_ROLLUP_DURATION);
                        }

                    }
                });
                mImgArrow.setPivotX(mImgArrow.getWidth() / 2);
                mImgArrow.setPivotY(mImgArrow.getHeight() / 2);
                return true;
            }
        });
    }


    private void animateView(View v, int to) {
        animateView(v, to, ANIM_ROLLUP_DURATION);
    }

    private void animateView(View v, int to, long duration) {
        HeightSetter setter = new HeightSetter(v);
        ObjectAnimator animator = ObjectAnimator.ofInt(setter, HeightSetter.HEIGHT_PARAM, to);
        animator.setDuration(duration).start();
    }

    public void setOpened(final boolean opened) {
        mIsOpened = opened;
//        if (mHiddenHeight == 0) {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                if (opened) {
                    animateView(mLayoutContent, mHiddenHeight, 0);
                    mImgArrow.animate().rotation(180).setDuration(0);
                } else {
                    animateView(mLayoutContent, 0, 0);
                    mImgArrow.animate().rotation(0).setDuration(0);
                }
                return false;
            }
        });
//        } else {
//            if (opened) {
//                animateView(mLayoutContent, mHiddenHeight, 0);
//                mImgArrow.animate().rotation(180).setDuration(0);
//            } else {
//                animateView(mLayoutContent, 0, 0);
//                mImgArrow.animate().rotation(0).setDuration(0);
//            }
//        }
    }

    public void hideUnderline(boolean hide) {
        if (hide) {
            mImgArrow.setVisibility(View.GONE);
            bottomLine.setVisibility(View.GONE);
        }
    }

    public boolean isOpened() {
        return mIsOpened;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle args = new Bundle();
        args.putParcelable(PARENT_STATE, super.onSaveInstanceState());
        args.putBoolean(IS_OPENED, mIsOpened);
        return args;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle args = (Bundle) state;
        super.onRestoreInstanceState(args.getParcelable(PARENT_STATE));
        mIsOpened = args.getBoolean(IS_OPENED, false);
    }
}
