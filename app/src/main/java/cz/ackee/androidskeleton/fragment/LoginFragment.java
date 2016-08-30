package cz.ackee.androidskeleton.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.afollestad.materialdialogs.MaterialDialog;
import com.material.widget.FloatingEditText;


import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.ackee.androidskeleton.Args;
import cz.ackee.androidskeleton.BuildConfig;
import cz.ackee.androidskeleton.Configuration;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.activity.MainActivity;
import cz.ackee.androidskeleton.activity.WebViewActivity;
import cz.ackee.androidskeleton.fragment.base.BaseFragment;
import cz.ackee.androidskeleton.fragment.dialog.IssueErrorDialog;
import cz.ackee.androidskeleton.model.CMRHash;
import cz.ackee.androidskeleton.model.request.CreateCMRRequest;
import cz.ackee.androidskeleton.model.response.IssueResponse;
import cz.ackee.androidskeleton.model.response.ProjectsResponse;
import cz.ackee.androidskeleton.rest.RestServiceGenerator;
import cz.ackee.androidskeleton.rest.RestServiceGeneratorCollectInfo;
import cz.ackee.androidskeleton.utils.AccountType;
import cz.ackee.androidskeleton.utils.Storage;
import cz.ackee.androidskeleton.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * TODO add class description Created by David Bilik[david.bilik@ackee.cz] on {19. 2. 2015}
 */
public class LoginFragment extends BaseFragment {
    public static final String TAG = LoginFragment.class.getName();
    private static final java.lang.String CURRENT_VIEW_KEY = "currentView";
    private static final String ACCOUNT_TYPE = "account_type";

    @InjectView(R.id.vFlipper)
    ViewFlipper vFlipper;

    @InjectView(R.id.editName)
    FloatingEditText mEditName;
    @InjectView(R.id.editUrl)
    FloatingEditText mEditUrl;
    @InjectView(R.id.editPassword)
    FloatingEditText mEditPassword;

    @InjectView(R.id.ReditName)
    FloatingEditText mEditNameRedmine;
    @InjectView(R.id.ReditUrl)
    FloatingEditText mEditUrlRedmine;
    @InjectView(R.id.ReditPassword)
    FloatingEditText mEditPasswordRedmine;
    @InjectView(R.id.ReditPhone)
    FloatingEditText mEditPhoneRedmine;
    @InjectView(R.id.ReditEmail)
    FloatingEditText mEditEmailRedmine;
    private boolean mOnPause;

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    protected void initAB() {

    }

    @Override
    public String getGAName() {
        return null;
    }

    @Override
    public int getPositionInMenu() {
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        getArguments().putInt(CURRENT_VIEW_KEY, getArguments().getInt(ACCOUNT_TYPE, AccountType.EASY_REDMINE.value));

        if (getFragmentActivity().isEasyRedmine() != null) {
            view.findViewById(R.id.vSwitchRedmine).setVisibility(View.GONE);
            view.findViewById(R.id.vSwitchEasyRedmine).setVisibility(View.GONE);
            getArguments().putInt(CURRENT_VIEW_KEY, Storage.getAccountType());
        }

        if (Storage.getURL() != null && !Storage.getURL().isEmpty()) {
            mEditUrl.setText(Storage.getURL());
            mEditUrlRedmine.setText(Storage.getURL());
        }

        if (getArguments().getInt(CURRENT_VIEW_KEY) == 1) {
            getFragmentActivity().tintManager.setStatusBarTintColor(getResources().getColor(R.color.r_primary_dark));
        } else {
            getFragmentActivity().tintManager.setStatusBarTintColor(getResources().getColor(R.color.er_primary_dark));
        }

        vFlipper.setDisplayedChild(getArguments().getInt(CURRENT_VIEW_KEY));
        mEditPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                    if (validateER()) {
                        loginEasyRedmine();
                    }
                    return true;
                }
                return false;
            }
        });

        mEditEmailRedmine.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                    if (validate()) {
                        loginRedmine();
                    }
                    return true;
                }
                return false;
            }
        });

        view.findViewById(R.id.vCreateFreeTrial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString(Args.ARG_EASY_REDMINE_REGISTRATION_URL,
                        Configuration.URL_EASYREDMINE_REGISTRATION);
                args.putString(Args.ARG_EASY_REDMINE_REGISTRATION_TITLE, getString(
                        R.string.create_account_page_title));
                WebViewActivity.startActivity(getFragmentActivity(), WebviewFragment.class.getName(), args);
                //getFragmentActivity().replaceFragment(WebviewFragment.newInstance(Configuration
                // .URL_EASYREDMINE_REGISTRATION, getString(R.string.create_account_page_title)));
            }
        });

        view.findViewById(R.id.vSwitchRedmine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentActivity().tintManager.setStatusBarTintColor(getResources().getColor(
                        R.color.r_primary_dark));
                vFlipper.setDisplayedChild(1);
                getArguments().putInt(CURRENT_VIEW_KEY, 1);


            }
        });

        view.findViewById(R.id.vSwitchEasyRedmine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentActivity().tintManager.setStatusBarTintColor(getResources().getColor(
                        R.color.er_primary_dark));
                vFlipper.setDisplayedChild(0);
                getArguments().putInt(CURRENT_VIEW_KEY, 0);
            }
        });

        view.findViewById(R.id.btnLoginEasyRedmine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateER()) {
                    loginEasyRedmine();
                }
            }
        });

        view.findViewById(R.id.btnLoginRedmine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    loginRedmine();
                }
            }
        });
    }

    private void loginEasyRedmine() {
        getView().findViewById(R.id.btnLoginEasyRedmine).setEnabled(false);

        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).progress(true, 0).content(R.string.login_dialog_logging).cancelable(false).show();
        Log.d("t", "Save URL " + mEditUrl.getText().toString());
        Storage.storeCredentials(Base64.encodeToString(
                (mEditName.getText().toString() + ":" + mEditPassword.getText().toString()).getBytes(),
                Base64.NO_WRAP));
        Storage.storeURL(mEditUrl.getText().toString());
        RestServiceGenerator.createApiDescription().pingServer(new Callback<ProjectsResponse>() {
            @Override
            public void success(ProjectsResponse projectsResponse, Response response) {
                if (getActivity() == null || getView() == null || mOnPause) {
                    return;
                }
                getView().findViewById(R.id.btnLoginEasyRedmine).setEnabled(true);

                dialog.dismiss();
                Storage.setLogged(true);
                Storage.storeName(mEditName.getText().toString());
                Storage.setAccountType(AccountType.EASY_REDMINE);
                setDefaultFilter();

                getActivity().finish();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }

            @Override
            public void failure(RetrofitError error) {
                if (getActivity() == null || getView() == null || getActivity().isFinishing()) {
                    return;
                }
                getView().findViewById(R.id.btnLoginEasyRedmine).setEnabled(true);

                dialog.dismiss();
                handleError(error);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOnPause = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mOnPause = false;
    }

    private void loginRedmine() {
        getView().findViewById(R.id.btnLoginRedmine).setEnabled(false);
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).progress(true, 0).content(R.string.login_dialog_logging).cancelable(false).widgetColorRes(R.color.r_primary).show();

        Log.d("t", "Save URL " + mEditUrlRedmine.getText().toString());
        Storage.storeCredentials(Base64.encodeToString(
                (mEditNameRedmine.getText().toString() + ":" + mEditPasswordRedmine.getText().toString())
                        .getBytes(), Base64.NO_WRAP));
        Storage.storeURL(mEditUrlRedmine.getText().toString());
        RestServiceGenerator.createApiDescription().pingServer(new Callback<ProjectsResponse>() {
            @Override
            public void success(ProjectsResponse projectsResponse, Response response) {
                if (getActivity() == null || getView() == null || mOnPause) {
                    return;
                }

                dialog.dismiss();
                getView().findViewById(R.id.btnLoginRedmine).setEnabled(true);

                Storage.setLogged(true);
                Storage.storeName(mEditNameRedmine.getText().toString());
                Storage.setAccountType(AccountType.REDMINE);
                setDefaultFilter();
                getActivity().finish();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }

            @Override
            public void failure(RetrofitError error) {
                if (getActivity() == null || getView() == null) {
                    return;
                }
                getView().findViewById(R.id.btnLoginRedmine).setEnabled(true);
                dialog.dismiss();
                handleError(error);
            }
        });

        //Sending additional information about phone and email (optional)
        if (Patterns.EMAIL_ADDRESS.matcher(mEditEmailRedmine.getText().toString()).matches() || !TextUtils.isEmpty(mEditPhoneRedmine.getText())) {
            RestServiceGeneratorCollectInfo.getApiServiceCollectInfo()
                    .sendInformation(createCMRRequest(mEditEmailRedmine.getText().toString(), mEditPhoneRedmine.getText().toString()), new Callback<IssueResponse>() {
                        @Override
                        public void success(IssueResponse issueResponse, Response response) {
                            //Dont care about result - it is optional
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            //Dont care about result - it is optional
                        }
                    });
        }
    }

    private void setDefaultFilter() {
        Storage.setFilter(getString(R.string.filter_value_assigned_to_me));
        Storage.setFilterName(getString(R.string.filter_assigned_to_me));
    }

    private CreateCMRRequest createCMRRequest(String email, String phone) {
        return new CreateCMRRequest(new CMRHash("Mobile App contact", email, phone));
    }

    private void handleError(RetrofitError error) {
        if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
            if (error.getCause() instanceof UnknownHostException ||
                    error.getCause() instanceof MalformedURLException) {
                IssueErrorDialog.newInstance(getString(R.string.unknown_host)).show(getChildFragmentManager(), IssueErrorDialog.class.getName());
            } else {
                IssueErrorDialog.newInstance(getString(R.string.errorConnection)).show(getChildFragmentManager(), IssueErrorDialog.class.getName());

            }
            return;
        }

        if (error.getResponse().getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            IssueErrorDialog.newInstance(getString(R.string.bad_credentials)).show(getChildFragmentManager(), IssueErrorDialog.class.getName());
        } else {
            Utils.handleError(getChildFragmentManager(), error);
        }
    }

    private boolean validate() {
        mEditUrlRedmine.setValidateResult(true, null);
        mEditNameRedmine.setValidateResult(true, null);
        mEditPasswordRedmine.setValidateResult(true, null);
        if (mEditUrlRedmine.getText().toString().isEmpty()) {
            mEditUrlRedmine.setValidateResult(false, getString(R.string.login_error_empty_url));
            return false;
        }
        return true;
    }

    private boolean validateER() {
        mEditUrl.setValidateResult(true, null);
        mEditName.setValidateResult(true, null);
        mEditPassword.setValidateResult(true, null);
        if (mEditUrl.getText().toString().isEmpty()) {
            mEditUrl.setValidateResult(false, getString(R.string.login_error_empty_url));
            return false;
        }
        return true;
    }

    public static Bundle getBundle(int oldAccType) {
        Bundle b = new Bundle();
        b.putInt(ACCOUNT_TYPE, oldAccType);
        return b;
    }
}
