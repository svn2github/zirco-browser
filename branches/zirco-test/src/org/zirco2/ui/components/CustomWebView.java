package org.zirco2.ui.components;

import org.zirco2.utils.UrlUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.ZoomDensity;

public class CustomWebView extends WebView {

	public CustomWebView(Context context) {
		super(context);
		initializeOptions();
	}
	
	public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeOptions();
	}
	
	private void initializeOptions() {
		WebSettings settings = getSettings();
		
		settings.setJavaScriptEnabled(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setSaveFormData(true);
		settings.setSavePassword(true);
		settings.setDefaultZoom(ZoomDensity.MEDIUM);
		settings.setSupportZoom(true);
		
		CookieManager.getInstance().setAcceptCookie(true);
		
		// Technical settings
		settings.setSupportMultipleWindows(true);						
    	setLongClickable(true);
    	setScrollbarFadingEnabled(true);
    	setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	}

	@Override
	public void loadUrl(String url) {
		url = UrlUtils.checkUrl(url);
		super.loadUrl(url);
	}
	
}
