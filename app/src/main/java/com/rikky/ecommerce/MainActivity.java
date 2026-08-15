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

    // Broad universal selectors - not dependent on Flipkart's obfuscated class names
    private static final String INJECTED_CSS =
        "body, html { background-color: #0D0D0D !important; } " +
        "div[class*='_1AtVbE'], div[class*='_2kHMtA'], div[class*='_3ZKMKM'] { background-color: #0D0D0D !important; } " +
        "a { color: #FFD700 !important; } " +
        "div, section, header, footer, nav { background-color: transparent; } " +
        "img { opacity: 0.95; } " +
        "input, textarea, select { background-color: #1A1A1A !important; color: #FFFFFF !important; border-color: #FFD700 !important; } " +
        ".rikky-topbar { position: fixed; top: 0; left: 0; width: 100%; height: 48px; " +
        "background-color: #0D0D0D !important; color: #FFD700 !important; z-index: 999999; " +
        "display: flex; align-items: center; justify-content: center; font-size: 18px; " +
        "font-weight: bold; font-family: sans-serif; letter-spacing: 1px; }";

    private static final String HIDE_ORIGINAL_LOGO_JS =
        "(function() {" +
        "var imgs = document.querySelectorAll('img');" +
        "imgs.forEach(function(img) {" +
        "  var src = img.src ? img.src.toLowerCase() : '';" +
        "  var alt = img.alt ? img.alt.toLowerCase() : '';" +
        "  if (src.indexOf('flipkart') !== -1 || alt.indexOf('flipkart') !== -1) {" +
        "    img.style.visibility = 'hidden';" +
        "  }" +
        "});" +
        "})()";

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
                injectTopBar(view);
                hideOriginalLogo(view);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress == 100 ? android.view.View.GONE : android.view.View.VISIBLE);
                if (newProgress == 100) {
                    injectCSS(view);
                    injectTopBar(view);
                    hideOriginalLogo(view);
                }
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

    private void injectTopBar(WebView view) {
        String js = "javascript:(function() {" +
                "if (document.getElementById('rikky-topbar-id')) return;" +
                "var bar = document.createElement('div');" +
                "bar.id = 'rikky-topbar-id';" +
                "bar.className = 'rikky-topbar';" +
                "bar.innerText = 'RikkY E-commerce';" +
                "document.body.insertBefore(bar, document.body.firstChild);" +
                "document.body.style.paddingTop = '48px';" +
                "})()";
        view.evaluateJavascript(js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {}
        });
    }

    private void hideOriginalLogo(WebView view) {
        view.evaluateJavascript("javascript:" + HIDE_ORIGINAL_LOGO_JS, new ValueCallback<String>() {
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
