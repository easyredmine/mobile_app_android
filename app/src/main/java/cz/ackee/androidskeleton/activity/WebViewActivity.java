package cz.ackee.androidskeleton.activity;



import android.content.Context;
import android.content.Intent;

import cz.ackee.androidskeleton.activity.base.BaseFragmentActivity;
import cz.ackee.androidskeleton.fragment.WebviewFragment;

/**
 * Created by petr.schneider@ackee.cz on 12.3.2015.
 */
public class WebViewActivity extends BaseFragmentActivity {

    public static void startActivity(Context ctx, String fragmentName) {
        Intent intent = new Intent(ctx, BaseFragmentActivity.class).putExtra(BaseFragmentActivity.EXTRA_FRAGMENT_NAME, fragmentName);
        ctx.startActivity(intent);
    }

    @Override
    protected String getFragmentName() {
        return WebviewFragment.class.getName();
    }

}
