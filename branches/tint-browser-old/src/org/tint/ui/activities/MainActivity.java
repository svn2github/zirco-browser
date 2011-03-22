package org.tint.ui.activities;

import org.tint.R;
import org.tint.controllers.TabsController;
import org.tint.ui.activities.preferences.PreferencesActivity;
import org.tint.ui.components.CustomWebView;
import org.tint.utils.Constants;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.webkit.WebIconDatabase;
import android.widget.ViewFlipper;

/**
 * Main application activity.
 */
public class MainActivity extends Activity implements OnTouchListener {	

	public static int ACTIVITY_SHOW_TABS = 0;
	public static int ACTIVITY_SHOW_BOOKMARKS_HISTORY = 1;
	
	private static final int MENU_ADD_BOOKMARK = Menu.FIRST;
	private static final int MENU_OPEN_HISTORY_BOOKMARKS = Menu.FIRST + 1;
	private static final int MENU_OPEN_TABS_ACTIVITY = Menu.FIRST + 2;
	private static final int MENU_OPEN_PREFERENCES_ACTIVITY = Menu.FIRST + 3;
	
	private GestureDetector mGestureDetector;
	private ViewFlipper mWebViewContainer;
	
	//private ScaleGestureDetector mScaleGestureDetector;	
	//private float mScaleFactor = 1.f;
	
	private int mCurrentViewIndex = -1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Constants.initializeConstantsFromResources(this);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.PREFERENCES_GENERAL_FULL_SCREEN, false)) {
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        
        }
        
        setContentView(R.layout.main_activity);
        
        mGestureDetector = new GestureDetector(this, new GestureListener());
        //mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureListener());
        
        mWebViewContainer = (ViewFlipper) findViewById(R.id.WebWiewContainer);
        
        TabsController.getInstance().initialize(this, this, mWebViewContainer);
        
        initializeWebIconDatabase();
        
        addTab("about:blank");                
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		
		MenuItem item;
		
		item = menu.add(0, MENU_ADD_BOOKMARK, 0, R.string.MainActivity_MenuAddBookmark);
		item.setIcon(R.drawable.ic_menu_add_bookmark);
		
		item = menu.add(0, MENU_OPEN_HISTORY_BOOKMARKS, 0, R.string.MainActivity_MenuShowBookmarks);
		item.setIcon(R.drawable.ic_menu_bookmarks);
		
		item = menu.add(0, MENU_OPEN_TABS_ACTIVITY, 0, R.string.MainActivity_MenuShowTabs);
		item.setIcon(R.drawable.ic_menu_tabs);
		
		item = menu.add(0, MENU_OPEN_PREFERENCES_ACTIVITY, 0, R.string.MainActivity_MenuShowPreferences);
		item.setIcon(R.drawable.ic_menu_preferences);
		
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case MENU_ADD_BOOKMARK:
			CustomWebView currentWebView = TabsController.getInstance().getWebViewContainers().get(mCurrentViewIndex).getWebView();
			i = new Intent(this, EditBookmarkActivity.class);
			i.putExtra(Constants.EXTRA_ID_BOOKMARK_ID, (long) -1);
			i.putExtra(Constants.EXTRA_ID_BOOKMARK_TITLE, currentWebView.getTitle());
			i.putExtra(Constants.EXTRA_ID_BOOKMARK_URL, currentWebView.getUrl());
			startActivity(i);
			return true;
		case MENU_OPEN_HISTORY_BOOKMARKS:
			i = new Intent(this, BookmarksHistoryActivity.class);
			startActivityForResult(i, ACTIVITY_SHOW_BOOKMARKS_HISTORY);			
			return true;
		case MENU_OPEN_TABS_ACTIVITY:
			openTabsActivity();
			return true;
		case MENU_OPEN_PREFERENCES_ACTIVITY:
			i = new Intent(this, PreferencesActivity.class);
			startActivity(i);
			return true;
		default: return super.onMenuItemSelected(featureId, item); 
		}		
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
		} else if ((requestCode == ACTIVITY_SHOW_BOOKMARKS_HISTORY) &&
				(resultCode == RESULT_OK)) {
			if (data != null) {
				Bundle b = data.getExtras();
				if (b != null) {
					if (b.getBoolean(Constants.EXTRA_ID_NEW_TAB)) {
						addTab(b.getString(Constants.EXTRA_ID_URL));
					} else {
						navigateToUrl(b.getString(Constants.EXTRA_ID_URL));
					}					
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
	    
    /**
	 * Open a new tab and navigate to the given url.
	 * @param url The url to navigate to.
	 * @return The index of the new tab.
	 */
	public int addTab(String url) {
		return addTab(-1, url);
	}
    
	/**
	 * Open a new tab and navigate to the given url, at the specified position.
	 * @param tabIndex The index to insert the new tab.
	 * @param url The url to navigate to.
	 * @return The index of the new tab.
	 */
	public int addTab(int tabIndex, String url) {
    	mCurrentViewIndex = TabsController.getInstance().addTab(tabIndex, url);        
        showTab(mCurrentViewIndex);
        return mCurrentViewIndex;
    }
	
	/**
	 * Get the current tab index.
	 * @return The current tab index.
	 */
	public int getCurrentWebViewIndex() {		
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
	
    /**
     * Open the TabsActivity activity.
     */
    private void openTabsActivity() {
    	Intent i = new Intent(MainActivity.this, TabsActivity.class);
		i.putExtra(Constants.EXTRA_CURRENT_VIEW_INDEX, mCurrentViewIndex);
		
		startActivityForResult(i, MainActivity.ACTIVITY_SHOW_TABS);
		overridePendingTransition(R.anim.tab_view_enter, R.anim.browser_view_exit);
    }
    
    /**
     * Show a tab given its index.
     * @param tabIndex The tab's index to show.
     */
	private void showTab(int tabIndex) {
		mCurrentViewIndex = tabIndex;
		mWebViewContainer.setDisplayedChild(mCurrentViewIndex);		
	}
	
	/**
	 * Navigate to the given url using the current WebView.
	 * @param url The url.
	 */
	private void navigateToUrl(String url) {
		CustomWebView webView = TabsController.getInstance().getWebViewContainers().get(mCurrentViewIndex).getWebView();
		webView.loadUrl(url);
	}
	
	/**
	 * Gesture listener implementation.
	 */
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			openTabsActivity();
			
			// Should be better to return true here, but it breaks on Cyanogen 7RC1. Test with next releases.
			return false;
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
