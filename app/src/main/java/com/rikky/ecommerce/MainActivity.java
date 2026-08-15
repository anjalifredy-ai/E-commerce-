package com.rikky.ecommerce;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    private static final String FLIPKART_URL = "https://www.flipkart.com";

    // Injected CSS: RikkY dark theme + gold accent overlay on Flipkart's UI
    private static final String INJECTED_CSS =
        "body, html { background-color: #0D0D0D !important; } " +
        "header, ._1MR4o0, ._3NxV1s, ._2eVfvL { background: #0D0D0D !important; } " +
        "._1YokD2, ._3Mn1Gg, ._2kHMtA { background-color: #1A1A1A !important; } " +
        "a, ._2whKao { color: #FFD700 !important; } " +
        "._1Y1rWl, ._30jeq3 { color: #FFD700 !important; } " +
        "input, textarea { background-color: #1A1A1A !important; color: #FFFFFF !important; } " +
        "footer { background-color: #0D0D0D !important; }";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setAllowFileAccess(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setUserAgentString(settings.getUserAgentString() + " RikkyEcommerceApp");

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectCSS(view);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress == 100 ? android.view.View.GONE : android.view.View.VISIBLE);
            }
        });

        webView.loadUrl(FLIPKART_URL);
    }

    private void injectCSS(WebView view) {
        String js = "javascript:(function() {" +
                "var style = document.createElement('style');" +
                "style.type = 'text/css';" +
                "style.innerHTML = '" + INJECTED_CSS.replace("'", "\\'") + "';" +
                "document.head.appendChild(style);" +
                "})()";
        view.evaluateJavascript(js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {}
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
