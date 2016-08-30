package cz.ackee.androidskeleton.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

import cz.ackee.androidskeleton.Args;
import cz.ackee.androidskeleton.R;
import cz.ackee.androidskeleton.fragment.base.BaseFragment;

/**
 * Webview fragment that supports custom title and url
 * Created by David Bilik[david.bilik@ackee.cz] on {29. 1. 2015}
 */
public class WebviewFragment extends BaseFragment {

    public static final String TAG = WebviewFragment.class.getName();
    private static final String URL_KEY = "url";
    private static final String HEADER_KEY_KEY = "headerKey";
    private static final String HEADER_VALUE_KEY = "headerValue";
    private static final String TITLE_KEY = "title";

    public static Bundle getBundle(String url, String title) {
        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        args.putString(TITLE_KEY, title);
        return args;
    }

    public static WebviewFragment newInstance(String url, String title) {
        WebviewFragment fragment = new WebviewFragment();
        fragment.setArguments(getBundle(url, title));
        return fragment;
    }

    @Override
    protected String getTitle() {
        return getArguments().getString(Args.ARG_EASY_REDMINE_REGISTRATION_TITLE);
    }

    @Override
    protected void initAB() {
        baseSettingsAB();
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_webview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WebView webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.loadUrl(getArguments().getString(Args.ARG_EASY_REDMINE_REGISTRATION_URL));
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (getActivity() == null) {
                    return;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (getActivity() == null) {
                    return;
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (getActivity() == null) {
                    return;
                }
            }


        });

    }

    private void reloadUrl() {
        WebView webView = (WebView) getView().findViewById(R.id.webView);
        webView.reload();
    }

}
