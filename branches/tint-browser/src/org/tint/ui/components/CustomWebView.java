package org.tint.ui.components;

import org.tint.utils.Constants;
import org.tint.utils.UrlUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
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
	
	/*
	private static final DrawFilter sZoomFilter = new PaintFlagsDrawFilter(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG, Paint.LINEAR_TEXT_FLAG);
	
	private boolean mUpdateZoomOnNextDraw = false;
	private float mNextZoomFactor;
	private float mNextX;
	private float mNextY;
	
	private int mDefaultWidth = 0;
	private int mDefaultHeight = 0;
    private int mWidth = 100;
    private int mHeight = 100;
    */    
	
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
		
		//mDefaultWidth = mWidth = Math.round(getContext().getResources().getDisplayMetrics().widthPixels);
        //mDefaultHeight = mHeight = Math.round(getContext().getResources().getDisplayMetrics().heightPixels);
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
        
        //mDefaultWidth = mWidth = Math.round(getContext().getResources().getDisplayMetrics().widthPixels);
        //mDefaultHeight = mHeight = Math.round(getContext().getResources().getDisplayMetrics().heightPixels);
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
		
		if (Build.VERSION.SDK_INT <= 7) {
			settings.setPluginsEnabled(prefs.getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_PLUGINS_ECLAIR, true));
		} else {
			settings.setPluginState(PluginState.valueOf(prefs.getString(Constants.PREFERENCES_BROWSER_ENABLE_PLUGINS, PluginState.ON_DEMAND.toString())));
		}		
		
		CookieManager.getInstance().setAcceptCookie(prefs.getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_COOKIES, true));
		
		// Technical settings
		settings.setSupportZoom(true);
		settings.setSupportMultipleWindows(true);						
    	setLongClickable(true);
    	setScrollbarFadingEnabled(true);
    	setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    	setDrawingCacheEnabled(true);
	}

	@Override
	public void loadUrl(String url) {
		if (UrlUtils.isUrl(url)) {
			url = UrlUtils.checkUrl(url);
		} else {
			url = UrlUtils.getSearchUrl(mContext, url);
		}
		super.loadUrl(url);
	}
	
	public boolean isLoading() {
		return mIsLoading;
	}
	
	public void setLoading(boolean value) {
		mIsLoading = value;
	}
	
	/*
	@Override
	protected void onDraw(Canvas canvas) {
		if (mUpdateZoomOnNextDraw) {
			Log.d("CustomWebView", "onDraw: " + mNextZoomFactor);
			
			canvas.setDrawFilter(sZoomFilter);
			canvas.scale(mNextZoomFactor, mNextZoomFactor);
			canvas.translate(mNextX, mNextY);

			mWidth = Math.round(mDefaultWidth * mNextZoomFactor);
            mHeight = Math.round(mDefaultHeight * mNextZoomFactor);

			int newWidth = (int) (this.getWidth() * mNextZoomFactor);
			int newHeight = (int) (this.getHeight() * mNextZoomFactor);
			
			canvas.setViewport(mWidth, mHeight);

			mUpdateZoomOnNextDraw = false;
		}
		super.onDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	public void setNextDrawZoomFactor(float scaleFactor, float nextX, float nextY) {
		mUpdateZoomOnNextDraw = true;
		mNextZoomFactor = scaleFactor;
		mNextX = nextX;
		mNextY = nextY;
	}
		
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = mWidth;
        } else {
            // Measure the text
            result = (int) (mWidth) + getPaddingLeft()
                    + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = mHeight;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = (int) (mHeight) + getPaddingTop()
                    + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
	*/
	
}
