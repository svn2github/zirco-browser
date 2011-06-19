package org.tint.ui.components;

import org.tint.utils.Constants;
import org.tint.utils.UrlUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.ZoomDensity;

/**
 * Extension of WebView.
 */
public class CustomWebView extends WebView {
	
	private Context mContext;
	
	private boolean mIsLoading = false;

	/**
	 * Constructor.
	 * @param context The current context.
	 */
	public CustomWebView(Context context) {
		super(context);
		mContext = context;
		initializeOptions();
	}
	
	/**
	 * Constructor.
	 * @param context The current context.
	 * @param attrs The attributes.
	 */
	public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initializeOptions();
	}
	
	/**
	 * Initialize the WebView options to the ones define by user.
	 */
	public void initializeOptions() {
		WebSettings settings = getSettings();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		
		settings.setJavaScriptEnabled(prefs.getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_JAVASCRIPT, true));
		settings.setLoadsImagesAutomatically(prefs.getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_IMAGES, true));
		settings.setSaveFormData(prefs.getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_FORM_DATA, true));
		settings.setSavePassword(prefs.getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_PASSWORDS, true));
		settings.setDefaultZoom(ZoomDensity.valueOf(prefs.getString(Constants.PREFERENCES_BROWSER_DEFAULT_ZOOM_LEVEL, ZoomDensity.MEDIUM.toString())));
		settings.setUserAgentString(prefs.getString(Constants.PREFERENCES_BROWSER_USER_AGENT, Constants.USER_AGENT_DEFAULT));
		
		settings.setPluginState(PluginState.valueOf(prefs.getString(Constants.PREFERENCES_BROWSER_ENABLE_PLUGINS, PluginState.ON_DEMAND.toString())));		
		
		CookieManager.getInstance().setAcceptCookie(prefs.getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_COOKIES, true));
		
		// Technical settings
		//settings.setUseWideViewPort(true);
		settings.setSupportMultipleWindows(true);						
    	setLongClickable(true);
    	setScrollbarFadingEnabled(true);
    	setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    	setDrawingCacheEnabled(true);
    	
    	settings.setAppCacheEnabled(true);
    	settings.setDatabaseEnabled(true);
    	settings.setDomStorageEnabled(true);
	}

	@Override
	public void loadUrl(String url) {
		if (UrlUtils.isUrl(url)) {
			url = UrlUtils.checkUrl(url);
		} else {
			url = UrlUtils.getSearchUrl(mContext, url);			
		}
		
		if (url.equals(UrlUtils.URL_ABOUT_START)) {
			super.loadDataWithBaseURL("file:///android_asset/startpage/",
					UrlUtils.getStartPage((Activity) mContext), "text/html", "UTF-8", UrlUtils.URL_ABOUT_START);
		} else {		
			super.loadUrl(url);
		}
	}
	
	/**
	 * Check if the WebView is currently loading.
	 * @return True if the WebView is currently loading. False otherwise.
	 */
	public boolean isLoading() {
		return mIsLoading;
	}
	
	/**
	 * Set the WebView loading value.
	 * @param value The new value.
	 */
	public void setLoading(boolean value) {
		mIsLoading = value;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		final int action = ev.getAction();
		
		// Enable / disable zoom support in case of multiple pointer, e.g. enable zoom when we have two down pointers, disable with one pointer or when pointer up.
		// We do this to prevent the display of zoom controls, which are not useful and override over the right bubble.
		if ((action == MotionEvent.ACTION_DOWN) ||
				(action == MotionEvent.ACTION_POINTER_DOWN) ||
				(action == MotionEvent.ACTION_POINTER_1_DOWN) ||
				(action == MotionEvent.ACTION_POINTER_2_DOWN) ||
				(action == MotionEvent.ACTION_POINTER_3_DOWN)) {
			if (ev.getPointerCount() > 1) {
				this.getSettings().setBuiltInZoomControls(true);
				this.getSettings().setSupportZoom(true);				
			} else {
				this.getSettings().setBuiltInZoomControls(false);
				this.getSettings().setSupportZoom(false);
			}
		} else if ((action == MotionEvent.ACTION_UP) ||
				(action == MotionEvent.ACTION_POINTER_UP) ||
				(action == MotionEvent.ACTION_POINTER_1_UP) ||
				(action == MotionEvent.ACTION_POINTER_2_UP) ||
				(action == MotionEvent.ACTION_POINTER_3_UP)) {
			this.getSettings().setBuiltInZoomControls(false);
			this.getSettings().setSupportZoom(false);			
		}
		
		return super.onTouchEvent(ev);
	}
	
}
