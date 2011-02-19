package org.zirco2.ui.activities;

import org.zirco2.R;
import org.zirco2.adapters.BookmarksHistoryAdapter;
import org.zirco2.controllers.TabsController;
import org.zirco2.ui.IWebViewActivity;
import org.zirco2.ui.components.CustomWebView;
import org.zirco2.utils.Constants;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.webkit.WebIconDatabase;
import android.widget.ViewFlipper;

public class MainActivity extends Activity implements OnTouchListener, IWebViewActivity {	

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
        
        BookmarksHistoryAdapter.getInstance().initialize(this);
        TabsController.getInstance().initialize(this, this, this, mWebViewContainer);
        
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
        			int position = b.getInt(Constants.EXTRA_CURRENT_VIEW_INDEX);
        			showTab(position);        			
        		}
			}
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		CustomWebView webView = TabsController.getInstance().getWebViewContainers().get(mCurrentViewIndex).getWebView();
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (webView.canGoBack()) {
				webView.goBack();				
			} else {
				this.moveTaskToBack(true);
			}
			return true;
		
		default: return super.onKeyUp(keyCode, event);
		}
	}
	
	/**
     * Initialize the Web icons database.
     */
    private void initializeWebIconDatabase() {
        
    	final WebIconDatabase db = WebIconDatabase.getInstance();
    	db.open(getDir("icons", 0).getPath());   
    }
	    
    @Override
	public int addTab(String url) {
		return addTab(-1, url);
	}
    
    @Override
	public int addTab(int tabIndex, String url) {
    	mCurrentViewIndex = TabsController.getInstance().addTab(tabIndex, url);        
        showTab(mCurrentViewIndex);
        return mCurrentViewIndex;
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
		
		Bundle b = item.getIntent().getExtras();
		
		switch(item.getItemId()) {
		case TabsController.TAB_CONTEXT_MENU_OPEN:
			if (b != null) {
				navigateToUrl(b.getString(Constants.EXTRA_ID_URL));
			}			
			return true;
			
		case TabsController.TAB_CONTEXT_MENU_OPEN_IN_NEW_TAB:
			if (b != null) {
				addTab(mCurrentViewIndex + 1, b.getString(Constants.EXTRA_ID_URL));
			}			
			return true;
				
		default: return super.onContextItemSelected(item);
		}		
	}
	
	private void showTab(int tabIndex) {
		mCurrentViewIndex = tabIndex;
		mWebViewContainer.setDisplayedChild(mCurrentViewIndex);
		
	}
	
	private void navigateToUrl(String url) {
		CustomWebView webView = TabsController.getInstance().getWebViewContainers().get(mCurrentViewIndex).getWebView();
		webView.loadUrl(url);
	}
	
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Intent i = new Intent(MainActivity.this, TabsActivity.class);
			i.putExtra(Constants.EXTRA_CURRENT_VIEW_INDEX, mCurrentViewIndex);
			
			MainActivity.this.startActivityForResult(i, MainActivity.ACTIVITY_SHOW_TABS);
			MainActivity.this.overridePendingTransition(R.anim.tab_view_enter, R.anim.browser_view_exit);
			
			// Should be better to return true here, but it breaks on Cyanogen 7RC1. Test with next releases.
			return false;
		}
	}

	@Override
	public int getCurrentWebViewIndex() {		
		return mCurrentViewIndex;
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