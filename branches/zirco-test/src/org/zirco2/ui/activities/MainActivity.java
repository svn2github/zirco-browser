package org.zirco2.ui.activities;

import org.zirco2.R;
import org.zirco2.TabsController;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.webkit.WebIconDatabase;
import android.widget.ViewFlipper;

public class MainActivity extends Activity implements OnTouchListener {	

	public static int ACTIVITY_SHOW_TABS = 0;
	
	private GestureDetector mGestureDetector;
	private ViewFlipper mWebViewContainer;
	
	//private ScaleGestureDetector mScaleGestureDetector;	
	//private float mScaleFactor = 1.f;
	
	private int mCurrentViewIndex = -1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main_activity);
        
        mGestureDetector = new GestureDetector(this, new GestureListener());
        //mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureListener());
        
        mWebViewContainer = (ViewFlipper) findViewById(R.id.WebWiewContainer);
        
        TabsController.getInstance().initialize(this, this, mWebViewContainer);
        
        initializeWebIconDatabase();
        
        addTab("http://fr.m.wikipedia.org/");
        addTab("http://www.google.com/");
    }

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
    
    @Override
	protected void onDestroy() {
    	WebIconDatabase.getInstance().close();
		super.onDestroy();
	}
    
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
		/*
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		} else {
			return mScaleGestureDetector.onTouchEvent(event);			
		}
		*/
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if ((requestCode == ACTIVITY_SHOW_TABS) &&
				(resultCode == RESULT_OK)) {
			if (data != null) {
        		Bundle b = data.getExtras();
        		if (b != null) {
        			int position = b.getInt("TAB_INDEX");
        			showTab(position);        			
        		}
			}
		}
	}
	
	/**
     * Initialize the Web icons database.
     */
    private void initializeWebIconDatabase() {
        
    	final WebIconDatabase db = WebIconDatabase.getInstance();
    	db.open(getDir("icons", 0).getPath());   
    }
	
	private void addTab(String url) {
		/*
		RelativeLayout view = (RelativeLayout) mInflater.inflate(R.layout.webview, mWebViewContainer, false);
		
		CustomWebView webView = (CustomWebView) view.findViewById(R.id.webview);
		
		mCurrentViewIndex = TabsController.getInstance().addWebViewContainer(new WebViewContainer(view, webView));
		
		webView.setWebChromeClient(new CustomWebChromeClient(view));
        webView.setWebViewClient(new CustomWebViewClient(view));        
        webView.setOnTouchListener(this);        
        
        webView.loadUrl(url);
        
        mWebViewContainer.addView(view);
        */
		mCurrentViewIndex = TabsController.getInstance().addTab(-1, url);
        
        showTab(mCurrentViewIndex);
	}
	
	private void showTab(int tabIndex) {
		mCurrentViewIndex = tabIndex;
		mWebViewContainer.setDisplayedChild(mCurrentViewIndex);
		
	}
	
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Intent i = new Intent(MainActivity.this, TabsActivity.class);
			i.putExtra("CURRENT_VIEW_INDEX", mCurrentViewIndex);
			
			MainActivity.this.startActivityForResult(i, MainActivity.ACTIVITY_SHOW_TABS);
			MainActivity.this.overridePendingTransition(R.anim.tab_view_enter, R.anim.browser_view_exit);
			
			return true;
		}
	}	
	
	/*
	private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {			
			mScaleFactor *= detector.getScaleFactor();
	        
	        // Don't let the object get too small or too large.
	        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

			
			CustomWebView currentWebView = TabsController.getInstance().getWebViewContainers().get(mCurrentViewIndex).getWebView();
			currentWebView.setNextDrawZoomFactor(mScaleFactor, detector.getFocusX(), detector.getFocusY());
			currentWebView.invalidate();
			
			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();
	        
	        // Don't let the object get too small or too large.
	        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

			
			CustomWebView currentWebView = TabsController.getInstance().getWebViewContainers().get(mCurrentViewIndex).getWebView();
			currentWebView.setNextDrawZoomFactor(mScaleFactor, detector.getFocusX(), detector.getFocusY());
			currentWebView.invalidate();
			
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();
	        
	        // Don't let the object get too small or too large.
	        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

			
			CustomWebView currentWebView = TabsController.getInstance().getWebViewContainers().get(mCurrentViewIndex).getWebView();
			currentWebView.setNextDrawZoomFactor(mScaleFactor, detector.getFocusX(), detector.getFocusY());
			currentWebView.invalidate();
			
		}
	}
	*/
	
}