package com.oneflow.analytics.customwidgets;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.oneflow.analytics.utils.OFHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class OFCustomeWebView extends WebView {

    String tag = this.getClass().getName();
    Context context = null;
    private boolean isLoading = false;
    private boolean mError = false;
    private boolean isBack = false;
    int counter = 0;
    String appCachePath;

    private WebChromeClient.CustomViewCallback customViewCallback;
    View mCustomView;

    public OFCustomeWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        SystemClock.sleep(500); // time in milliseconds

        OFHelper.v(tag, "1Flow at webView");




        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        this.getSettings().setJavaScriptEnabled(true);

        this.setWebChromeClient(new MyWebChromeClient());
        this.getSettings().setBuiltInZoomControls(false);
        this.getSettings().setSupportZoom(false);
        this.getSettings().setLoadWithOverviewMode(true);
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        this.getSettings().setAllowFileAccess(false);
        this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.getSettings().setLoadsImagesAutomatically(true);

        this.setWebViewClient(new ABWebViewClient());
    }

    public OFCustomeWebView(Context context) {
        super(context);
        this.context = context;

        if (Build.VERSION.SDK_INT >= 11) {
            this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            try {
                Method setLayerTypeMethod = this.getClass().getMethod(
                        "setLayerType",
                        int.class, WebView.class);
                setLayerTypeMethod.invoke(this, LAYER_TYPE_HARDWARE, null);
            } catch (NoSuchMethodException e) {
                // Older OS, no HW acceleration anyway
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                OFHelper.e(tag,e.getMessage());
            }
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        SystemClock.sleep(500); // time in milliseconds

        OFHelper.v(tag, "1Flow at ABVwebView");

        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setJavaScriptEnabled(true);
        this.setWebChromeClient(new MyWebChromeClient());
        this.getSettings().setBuiltInZoomControls(false);
        this.getSettings().setSupportZoom(false);

        this.getSettings().setDatabaseEnabled(true);
        this.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        this.getSettings().setAllowFileAccess(true);
        this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.getSettings().setLoadsImagesAutomatically(true);

        this.setWebViewClient(new ABWebViewClient());
    }

    private class ABWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {


            OFHelper.e(tag, "1Flow onReceivedError error isBack[" + isBack + "][" + failingUrl + "]ABWebView.this.getSettings().getCacheMode()[" + OFCustomeWebView.this.getSettings().getCacheMode() + "][" + errorCode + "][" + description + "]");


            if (errorCode == ERROR_HOST_LOOKUP) {
                mError = true;
                if (!OFHelper.isConnected(context)) {
                    OFHelper.v(this.getClass().getName(), "1Flow Network issue");
                }

            }
            if (errorCode == ERROR_CONNECT) {
                view.stopLoading();
                if (failingUrl.endsWith(".com/")) {
                    AlertDialog ald = new AlertDialog.Builder(context)
                            .create();
                    ald.setMessage("Internet connection is not available. Please check.");
                    ald.setButton("OK", (dialog, which) -> {
                        dialog.cancel();
                        ((Activity) OFCustomeWebView.this.context).finish();

                    });

                    ald.show();
                } else {
                    OFHelper.v(this.getClass().getName(), "1Flow Network issue");
                }
            }
            if (errorCode == ERROR_TIMEOUT) {
                view.stopLoading();
                OFHelper.e(this.getClass().getName(), "1Flow Network issue");
            }

        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (mError) {
                OFHelper.v(this.getClass().getName(), "Network issue");
                view.stopLoading();
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            handler.proceed("hbx", "hbx");

        }

        @Override
        public void onLoadResource(WebView view, String url) {
            if (mError) {
                view.stopLoading();
                OFHelper.v(this.getClass().getName(), "Network issue");
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.setDownloadListener((url1, userAgent, contentDisposition, mimetype, contentlength) -> {

                final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri source = Uri.parse(url1);

                // Make a new request pointing to the mp3 url
                DownloadManager.Request request = new DownloadManager.Request(source);
                manager.enqueue(request);
            });
            if (url.contains("subscribe") || url.contains("emailArticlePrompt") || url.contains("m.facebook.com") || url.contains("plus.google.com") || url.contains("www.linkedin.com") || url.contains("twitter.com") || url.contains("license.icopyright.net")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                OFCustomeWebView.this.context.startActivity(browserIntent);
            }
            else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            counter++;
        }
    }

    public boolean inCustomView() {
        return (mCustomView != null);
    }

    CountDownTimer cdt = new CountDownTimer(10000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            // onTick
        }

        @Override
        public void onFinish() {
            // onFinish
        }
    };

    class MyWebChromeClient extends WebChromeClient {

        private View mVideoProgressView;

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if(newProgress>=100){
                view.setVisibility(VISIBLE);
            }
        }

        @Override
        public void getVisitedHistory(ValueCallback<String[]> callback) {
            // history
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {

            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;

            customViewCallback = callback;
        }

        @Override
        public View getVideoLoadingProgressView() {

            return mVideoProgressView;
        }

        @Override
        public void onHideCustomView() {
            //To change body of overridden methods use File | Settings | File Templates.
            if (mCustomView == null)
                return;


            customViewCallback.onCustomViewHidden();

            mCustomView = null;
        }
    }

    final class DemoJavaScriptInterface {
        DemoJavaScriptInterface() {
            // also you can call javascript functions here.
        }
    }

    public boolean getLoadingStatus() {
        return isLoading;
    }

    @Override
    public void goBack() {
        super.goBack();

        isBack = true;
    }
}
