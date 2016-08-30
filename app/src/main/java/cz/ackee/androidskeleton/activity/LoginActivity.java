package cz.ackee.androidskeleton.activity;

import android.content.Intent;
import android.os.Bundle;

import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.base.BaseFragmentActivity;
import cz.ackee.androidskeleton.event.SelectProject;
import cz.ackee.androidskeleton.fragment.LoginFragment;
import cz.ackee.androidskeleton.utils.Storage;
import cz.ackee.androidskeleton.utils.Utils;

/**
 * Activity that handles login related screens
 * Created by David Bilik[david.bilik@ackee.cz] on {17. 2. 2015}
 */
public class LoginActivity extends BaseFragmentActivity {
    public static final String TAG = LoginActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Storage.isLogged()) {
            Utils.hideIme(getContentView());
            finish();
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0,0);
        }
    }

    @Override
    protected String getFragmentName() {
        return LoginFragment.class.getName();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_ordinary_no_toolbar;
    }

    public void onEvent(SelectProject event) {

    }
}
