package cz.ackee.androidskeleton.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class HeightSetter {

    public static final String HEIGHT_PARAM = "height";
    View viewToAnimate;

    public HeightSetter(View v) {
        viewToAnimate = v;
    }

    public int getHeight() {
        return viewToAnimate.getHeight();
    }

    public void setHeight(int height) {
        ViewGroup.MarginLayoutParams params = (LinearLayout.LayoutParams) viewToAnimate.getLayoutParams();
        params.height = height;
        viewToAnimate.setLayoutParams(params);
    }
}