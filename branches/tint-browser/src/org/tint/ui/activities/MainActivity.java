package org.tint.ui.activities;

import java.util.Random;

import org.tint.R;
import org.tint.adapters.UrlSuggestionCursorAdapter;
import org.tint.controllers.BookmarksHistoryController;
import org.tint.controllers.TabsController;
import org.tint.model.DownloadItem;
import org.tint.runnables.HideToolbarsRunnable;
import org.tint.ui.IWebViewActivity;
import org.tint.ui.activities.preferences.PreferencesActivity;
import org.tint.ui.components.CustomWebView;
import org.tint.utils.AnimationUtils;
import org.tint.utils.ApplicationUtils;
import org.tint.utils.Constants;
import org.tint.utils.UrlUtils;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebIconDatabase;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;

/**
 * Main application activity.
 */
public class MainActivity extends Activity implements OnTouchListener, IWebViewActivity {	

	public static int ACTIVITY_SHOW_TABS = 0;
	public static int ACTIVITY_SHOW_BOOKMARKS_HISTORY = 1;
	
	private static final int MENU_ADD_BOOKMARK = Menu.FIRST;
	private static final int MENU_OPEN_HISTORY_BOOKMARKS = Menu.FIRST + 1;
	private static final int MENU_OPEN_TABS_ACTIVITY = Menu.FIRST + 2;
	private static final int MENU_SHARE_PAGE = Menu.FIRST + 3;
	private static final int MENU_OPEN_PREFERENCES_ACTIVITY = Menu.FIRST + 4;
	
	private LinearLayout mTopBar;
	private LinearLayout mBottomBar;
	
	private ImageView mPreviousTabView;
	private ImageView mNextTabView;
	
	private ImageView mBubbleRightView;
	private ImageView mBubbleLeftView;
	
	private ProgressBar mProgressBar;
	
	private ImageButton mHomeButton;
	private AutoCompleteTextView mUrlEditText;
	private ImageButton mGoButton;
	
	private ImageButton mBackButton;	
	private ImageButton mRemoveTabButton;
	private ImageButton mBookmarksButton;
	private ImageButton mAddTabButton;
	private ImageButton mForwardButton;
	
	private GestureDetector mGestureDetector;
	private ViewFlipper mWebViewFlipper;
	
	private Drawable mCircularProgress;
	
	//private ScaleGestureDetector mScaleGestureDetector;	
	//private float mScaleFactor = 1.f;
	
	private boolean mUrlBarVisible;
	
	private HideToolbarsRunnable mHideToolbarsRunnable;
	
	private int mCurrentViewIndex = -1;
	private CustomWebView mCurrentWebView = null;
	
	private BroadcastReceiver mDownloadsReceiver = new BroadcastReceiver() {			
		@Override
		public void onReceive(Context context, Intent intent) {
			onReceivedDownloadNotification(context, intent);
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Constants.initializeConstantsFromResources(this);
        
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.PREFERENCES_GENERAL_HIDE_TITLE_BARS, true)) {
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.PREFERENCES_GENERAL_FULL_SCREEN, false)) {
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        
        }
        
        setContentView(R.layout.main_activity);
        
        mCircularProgress = getResources().getDrawable(R.drawable.spinner);
        
        mHideToolbarsRunnable = null;
        
        buildUI();
        
        TabsController.getInstance().initialize(this, this, this, mWebViewFlipper);
        
        initializeWebIconDatabase();
        
        String startUrl;
        Intent i = getIntent();
        if (i.getData() != null) {
        	startUrl = i.getDataString();
        } else {
        	startUrl = getHomeUrl();
        }
        
        addTab(startUrl, false);
        
        startToolbarsHideRunnable();
    }
    
    /**
     * Create the MainActivity UI.
     */
    private void buildUI() {
    	mGestureDetector = new GestureDetector(this, new GestureListener());
        //mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureListener());
        
        mWebViewFlipper = (ViewFlipper) findViewById(R.id.WebWiewContainer);
        
        mTopBar = (LinearLayout) findViewById(R.id.TopBarLayout);    	
    	mTopBar.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// Dummy event to steel it from the WebView, in case of clicking between the buttons.				
			}
		});    	
    	
    	mBottomBar = (LinearLayout) findViewById(R.id.BottomBarLayout);    	
    	mBottomBar.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// Dummy event to steel it from the WebView, in case of clicking between the buttons.				
			}
		});
    	
    	mBubbleRightView = (ImageView) findViewById(R.id.BubbleRightView);
    	mBubbleRightView.setOnClickListener(new View.OnClickListener() {
    		@Override
			public void onClick(View v) {
				setToolbarsVisibility(true);				
			}
		});    	
    	mBubbleRightView.setVisibility(View.GONE);
    	
    	mBubbleLeftView = (ImageView) findViewById(R.id.BubbleLeftView);
    	mBubbleLeftView.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				setToolbarsVisibility(true);
			}
		});
    	mBubbleLeftView.setVisibility(View.GONE);
    	
    	mPreviousTabView = (ImageView) findViewById(R.id.PreviousTabView);
    	mPreviousTabView.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				showTab(mCurrentViewIndex - 1, true, true);
			}
		});
    	mPreviousTabView.setVisibility(View.GONE);
    	
    	mNextTabView = (ImageView) findViewById(R.id.NextTabView);
    	mNextTabView.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				showTab(mCurrentViewIndex + 1, true, true);
			}
		});
    	mNextTabView.setVisibility(View.GONE);
    	    	
    	String[] from = new String[] { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL };
        int[] to = new int[] {R.id.AutocompleteTitle, R.id.AutocompleteUrl};
        
        UrlSuggestionCursorAdapter suggestionAdapter = new UrlSuggestionCursorAdapter(this, R.layout.url_autocomplete_line, null, from, to);
        suggestionAdapter.setCursorToStringConverter(new CursorToStringConverter() {
			
			@Override
			public CharSequence convertToString(Cursor cursor) {
				String aColumnString = cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.URL));
                return aColumnString;
			}
		});
        
        suggestionAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			
			@Override
			public Cursor runQuery(CharSequence constraint) {
				if ((constraint != null) &&
						(constraint.length() > 0)) {
					return BookmarksHistoryController.getInstance().getSuggestion(MainActivity.this, constraint.toString());
				}
				return null;
			}
		});
    	
    	mUrlEditText = (AutoCompleteTextView) findViewById(R.id.UrlText);
    	mUrlEditText.setThreshold(1);
    	mUrlEditText.setAdapter(suggestionAdapter);    	
    	
    	mUrlEditText.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					navigateToCurrentUrl();
					return true;
				}
				return false;
			}
    		
    	});
    	
    	mUrlEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

    		@Override
    		public void onFocusChange(View v, boolean hasFocus) {
    			// Select all when focus gained.
    			if (hasFocus) {
    				mUrlEditText.setSelection(0, mUrlEditText.getText().length());
    			}
    		}
    	});
    	
    	mUrlEditText.setCompoundDrawablePadding(5);
    	
    	mUrlBarVisible = true;
    	
    	mProgressBar = (ProgressBar) findViewById(R.id.WebViewProgress);
    	mProgressBar.setMax(100);
    	
    	mHomeButton = (ImageButton) findViewById(R.id.HomeBtn);
    	mHomeButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				navigateToHome();
			}
		});
    	
    	mGoButton = (ImageButton) findViewById(R.id.GoBtn);
    	mGoButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (mCurrentWebView.isLoading()) {
					mCurrentWebView.stopLoading();
				} else {
					navigateToCurrentUrl();
				}
			}
		});
    	
    	mRemoveTabButton = (ImageButton) findViewById(R.id.RemoveTabBtn);
    	mRemoveTabButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				removeCurrentTab();
			}
		});
    	
    	mBookmarksButton = (ImageButton) findViewById(R.id.BookmarksBtn);
    	mBookmarksButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {            	
            	Intent i = new Intent(MainActivity.this, BookmarksHistoryActivity.class);
            	startActivityForResult(i, ACTIVITY_SHOW_BOOKMARKS_HISTORY);
            }          
        });
    	
    	mAddTabButton = (ImageButton) findViewById(R.id.NewTabBtn);
    	mAddTabButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addTab(mCurrentViewIndex + 1, getHomeUrl(), true);
			}
		});
    	
    	mBackButton = (ImageButton) findViewById(R.id.BackBtn);
    	mBackButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (mCurrentWebView.canGoBack()) {
					mCurrentWebView.goBack();
				}
			}
		});
    	
    	mForwardButton = (ImageButton) findViewById(R.id.ForwardBtn);
    	mForwardButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (mCurrentWebView.canGoForward()) {
					mCurrentWebView.goForward();
				}
			}
		});
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	
    	IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        
        registerReceiver(mDownloadsReceiver, filter);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mDownloadsReceiver);
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
		
		item = menu.add(0, MENU_SHARE_PAGE, 0, R.string.MainActivity_MenuSharePage);
		item.setIcon(android.R.drawable.ic_menu_share);
		
		item = menu.add(0, MENU_OPEN_PREFERENCES_ACTIVITY, 0, R.string.MainActivity_MenuShowPreferences);
		item.setIcon(android.R.drawable.ic_menu_preferences);
		
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case MENU_ADD_BOOKMARK:			
			i = new Intent(this, EditBookmarkActivity.class);
			i.putExtra(Constants.EXTRA_ID_BOOKMARK_ID, (long) -1);
			i.putExtra(Constants.EXTRA_ID_BOOKMARK_TITLE, mCurrentWebView.getTitle());
			i.putExtra(Constants.EXTRA_ID_BOOKMARK_URL, mCurrentWebView.getUrl());
			startActivity(i);
			return true;
		case MENU_OPEN_HISTORY_BOOKMARKS:
			i = new Intent(this, BookmarksHistoryActivity.class);
			startActivityForResult(i, ACTIVITY_SHOW_BOOKMARKS_HISTORY);
			return true;
		case MENU_OPEN_TABS_ACTIVITY:
			openTabsActivity();
			return true;
		case MENU_SHARE_PAGE:
			sharePage(mCurrentWebView.getTitle(), mCurrentWebView.getUrl());
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
		
		hideKeyboard(false);
		
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
        			showTab(position, false, false);        			
        		}
			}
		} else if ((requestCode == ACTIVITY_SHOW_BOOKMARKS_HISTORY) &&
				(resultCode == RESULT_OK)) {
			if (data != null) {
				Bundle b = data.getExtras();
				if (b != null) {
					if (b.getBoolean(Constants.EXTRA_ID_NEW_TAB)) {
						addTab(b.getString(Constants.EXTRA_ID_URL), false);
					} else {
						navigateToUrl(b.getString(Constants.EXTRA_ID_URL));
					}					
				}
			}
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {					
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (mCurrentWebView.canGoBack()) {
				mCurrentWebView.goBack();				
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
    public Activity getActivity() {
    	return this;
    }
    
    @Override
	public int addTab(String url, boolean useAnimation) {
		return addTab(-1, url, useAnimation);
	}
    
    @Override
	public int addTab(int tabIndex, String url, boolean useAnimation) {   	
    	mCurrentViewIndex = TabsController.getInstance().addTab(tabIndex, url);        
        showTab(mCurrentViewIndex, useAnimation, false);
        
        return mCurrentViewIndex;
    }
    
    @Override
	public int getCurrentWebViewIndex() {		
		return mCurrentViewIndex;
	}
    
    @Override
    public void onPageStarted(WebView webView, String url) { 
    	if (mCurrentWebView == webView) {
    		mUrlEditText.setText(url);
    		setToolbarsVisibility(true);
    		
    		setTitle(R.string.Commons_ApplicationName);    		
    		updateStopGoButton();
    		updateUrlEditIcons();
    		updateBackForwardButtons();
    	}
    }
    
    @Override
    public void onPageFinished(WebView webView) {
    	if (mCurrentWebView == webView) {
    		if (mUrlBarVisible) {
    			startToolbarsHideRunnable();    			
    		}
    		mUrlEditText.setText(webView.getUrl());
    		
    		updateTitle(mCurrentWebView.getTitle());
    		updateUrlEditIcons();
    		updateStopGoButton();
    		updateBackForwardButtons();
    		mProgressBar.setProgress(100);
    	}
    }
    
    @Override
    public void onPageProgress(WebView webView, int newProgress) {
    	if (mCurrentWebView == webView) {
    		mProgressBar.setProgress(newProgress);
    	}
    }
    
    @Override
    public void onReceivedFavicon(WebView webView, Bitmap favicon) {
    	if (mCurrentWebView == webView) {
    		mUrlEditText.setCompoundDrawablesWithIntrinsicBounds(ApplicationUtils.getNormalizedFavicon(this, favicon),
    				null,
    				mUrlEditText.getCompoundDrawables()[2],
    				null);
    	}
    }
    
    @Override
    public void onReceivedTitle(WebView webView, String title) {
    	if (mCurrentWebView == webView) {
    		updateTitle(title);
    	}
    }        
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
		
		Bundle b = item.getIntent().getExtras();
		
		switch(item.getItemId()) {
		case TabsController.CONTEXT_MENU_OPEN:
			if (b != null) {
				navigateToUrl(b.getString(Constants.EXTRA_ID_URL));
			}			
			return true;
			
		case TabsController.CONTEXT_MENU_OPEN_IN_NEW_TAB:
			if (b != null) {
				addTab(mCurrentViewIndex + 1, b.getString(Constants.EXTRA_ID_URL), true);
			}			
			return true;
				
		case TabsController.CONTEXT_MENU_COPY:
			if (b != null) {
				ApplicationUtils.copyTextToClipboard(this, b.getString(Constants.EXTRA_ID_URL), getString(R.string.Commons_UrlCopyToastMessage));
			}
			return true;
		
		case TabsController.CONTEXT_MENU_DOWNLOAD:
			if (b != null) {
				TabsController.getInstance().doDownload(b.getString(Constants.EXTRA_ID_URL));
			}
			return true;
		
		case TabsController.CONTEXT_MENU_SHARE:
			if (b != null) {
				sharePage("", b.getString(Constants.EXTRA_ID_URL));
			}
			return true;
			
		default: return super.onContextItemSelected(item);
		}		
	}
    
    private void sharePage(String title, String url) {
    	Intent shareIntent = new Intent(Intent.ACTION_SEND);
    	shareIntent.setType("text/plain");
    	shareIntent.putExtra(Intent.EXTRA_TEXT, url);
    	shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
    	try {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.MainActivity_ShareChooserTitle)));
        } catch(android.content.ActivityNotFoundException ex) {
            // if no app handles it, do nothing
        }
    }
    
    private void showNotification(String notificationTitle, String title, String message) {
    	Notification notification =  new Notification(android.R.drawable.stat_sys_download_done, notificationTitle, System.currentTimeMillis());
    	Intent notificationIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
    	PendingIntent contentIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, notificationIntent, 0);
    	
    	notification.setLatestEventInfo(this, title, message, contentIntent);
    	
    	((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(new Random().nextInt(), notification);
    }
	
    /**
     * Process a download notification.
     * @param context The notification context.
     * @param intent The notification intent.
     */
    private void onReceivedDownloadNotification(Context context, Intent intent) {

		if (intent.getAction().compareTo(DownloadManager.ACTION_DOWNLOAD_COMPLETE) == 0) {
			
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			
			DownloadItem item = TabsController.getInstance().getDownloadItemById(id);
			
			if (item != null) {
				// This is one of our downloads.
				final DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
				Query query = new Query();
				query.setFilterById(id);
				Cursor cursor = downloadManager.query(query);
				
				if (cursor.moveToFirst()) {
					int localUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
					int reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
					int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
					
					int status = cursor.getInt(statusIndex);
					
					if (status == DownloadManager.STATUS_SUCCESSFUL) {
						
						String localUri = cursor.getString(localUriIndex);
						Toast.makeText(context, String.format(getString(R.string.Commons_DownloadComplete), localUri), Toast.LENGTH_SHORT).show();
						TabsController.getInstance().removeDownloadItem(item);
						
						showNotification(getString(R.string.Commons_DownloadComplete), item.getDestinationFileName(), getString(R.string.Commons_DownloadComplete));
						
					} else if (status == DownloadManager.STATUS_FAILED) {
						
						int reason = cursor.getInt(reasonIndex);
						
						String message;
						switch (reason) {
						case DownloadManager.ERROR_FILE_ERROR:
						case DownloadManager.ERROR_DEVICE_NOT_FOUND:					
						case DownloadManager.ERROR_INSUFFICIENT_SPACE:
							message = getString(R.string.Commons_DownloadErrorDisk);
							break;
						case DownloadManager.ERROR_HTTP_DATA_ERROR:
						case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
							message = getString(R.string.Commons_DownloadErrorHttp);
							break;
						case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
							message = getString(R.string.Commons_DownloadErrorRedirection);
							break;
						default:
							message = getString(R.string.Commons_DownloadErrorUnknown);
							break;
						}
						
						Toast.makeText(context, String.format(getString(R.string.Commons_DownloadFailedWithErrorMessage), message), Toast.LENGTH_SHORT).show();
						TabsController.getInstance().removeDownloadItem(item);
						
						showNotification(getString(R.string.Commons_DownloadFailed), item.getDestinationFileName(), String.format(getString(R.string.Commons_DownloadFailedWithErrorMessage), message));
					}
				}												
			}
			
		} else if (intent.getAction().compareTo(DownloadManager.ACTION_NOTIFICATION_CLICKED) == 0) {			
			Intent i = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
			startActivity(i);						
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
     * @param useAnimation If true, the switch will use animations.
     */
	private void showTab(int tabIndex, boolean useAnimation, boolean resetToolbarsRunnable) {
		if (useAnimation) {
			if (tabIndex < mCurrentViewIndex) {
				mWebViewFlipper.setInAnimation(AnimationUtils.getInFromLeftAnimation());
				mWebViewFlipper.setOutAnimation(AnimationUtils.getOutToRightAnimation());
			} else if (tabIndex > mCurrentViewIndex) {				
				mWebViewFlipper.setInAnimation(AnimationUtils.getInFromRightAnimation());
				mWebViewFlipper.setOutAnimation(AnimationUtils.getOutToLeftAnimation());
			} else {
				mWebViewFlipper.setInAnimation(null);
				mWebViewFlipper.setOutAnimation(null);
			}
		} else {
			mWebViewFlipper.setInAnimation(null);
			mWebViewFlipper.setOutAnimation(null);
		}
		
		mCurrentViewIndex = tabIndex;
		mWebViewFlipper.setDisplayedChild(mCurrentViewIndex);
		mCurrentWebView = TabsController.getInstance().getWebViewContainers().get(mCurrentViewIndex).getWebView();		
		
		if (resetToolbarsRunnable) {
			startToolbarsHideRunnable();
		}
		
		updateBars();
		updatePreviousNextTabViewsVisibility();
		
		mUrlEditText.clearFocus();
	}
	
	private String getHomeUrl() {
		return PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREFERENCES_GENERAL_HOME_PAGE, UrlUtils.URL_ABOUT_START);
	}
	
	/**
	 * Navigate to the user home page.
	 */
	private void navigateToHome() {
		navigateToUrl(getHomeUrl());
	}
	
	/**
     * Navigate to the url given in the url edit text.
     */
    private void navigateToCurrentUrl() {
    	navigateToUrl(mUrlEditText.getText().toString());    	
    }
	
	/**
	 * Navigate to the given url using the current WebView.
	 * @param url The url.
	 */
	private void navigateToUrl(String url) {
		mUrlEditText.clearFocus();
		hideKeyboard(true);
		mCurrentWebView.loadUrl(url);
	}
	
	/**
     * Hide the keyboard.
     * @param delayedHideToolbars If True, will start a runnable to delay tool bars hiding. If False, tool bars are hidden immediatly.
     */
    private void hideKeyboard(boolean delayedHideToolbars) {
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(mUrlEditText.getWindowToken(), 0);
    	
    	if (mUrlBarVisible) {
    		if (delayedHideToolbars) {
    			startToolbarsHideRunnable();
    		} else {
    			setToolbarsVisibility(false);
    		}
    	}
    }
    
    /**
	 * Hide the tool bars.
	 */
	public void hideToolbars() {
		if (mUrlBarVisible) {			
			if (!mUrlEditText.hasFocus()) {
				
				if (!mCurrentWebView.isLoading()) {
					setToolbarsVisibility(false);
				}
			}
		}
		mHideToolbarsRunnable = null;
	}
	
	/**
     * Start a runnable to hide the tool bars after a user-defined delay.
     */
    private void startToolbarsHideRunnable() {
    	    	    	
    	if (mHideToolbarsRunnable != null) {
    		mHideToolbarsRunnable.setDisabled();
    	}
    	
    	int delay = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREFERENCES_GENERAL_BARS_DURATION, "3000"));
    	if (delay <= 0) {
    		delay = 3000;
    	}
    	
    	mHideToolbarsRunnable = new HideToolbarsRunnable(this, delay);    	
    	new Thread(mHideToolbarsRunnable).start();
    }
	
	/**
     * Change the tool bars visibility.
     * @param visible If True, the tool bars will be shown.
     */
    private void setToolbarsVisibility(boolean visible) {
    	    	
    	if (visible) {
        	
    		// No need to perform this if already shown, may cause flickering (due to animations) in case of web page redirections.
    		if (!mUrlBarVisible) {
    			boolean showPreviousTabView = mCurrentViewIndex > 0;
    			boolean showNextTabView = mCurrentViewIndex < TabsController.getInstance().getWebViewContainers().size() - 1;
    			
    			mTopBar.startAnimation(AnimationUtils.getTopBarShowAnimation());
    			mBottomBar.startAnimation(AnimationUtils.getBottomBarShowAnimation());
    			
    			if (showPreviousTabView) {
    				mPreviousTabView.startAnimation(AnimationUtils.getPreviousTabViewShowAnimation());
    			}
    			
    			if (showNextTabView) {
    				mNextTabView.startAnimation(AnimationUtils.getNextTabViewShowAnimation());
    			}
    			
    			mTopBar.setVisibility(View.VISIBLE);
    			mBottomBar.setVisibility(View.VISIBLE);

    			if (showPreviousTabView) {
    				mPreviousTabView.setVisibility(View.VISIBLE);
    			}
    			
    			if (showNextTabView) {
    				mNextTabView.setVisibility(View.VISIBLE);
    			}
    			
    			mBubbleRightView.setVisibility(View.GONE);
    			mBubbleLeftView.setVisibility(View.GONE);
    		}
    		
    		startToolbarsHideRunnable();
    		
    		mUrlBarVisible = true;    		    		
    		
    	} else {        	
        	
    		if (mUrlBarVisible) {
    			boolean showPreviousTabView = mCurrentViewIndex > 0;
    			boolean showNextTabView = mCurrentViewIndex < TabsController.getInstance().getWebViewContainers().size() - 1;
    			
    			mTopBar.startAnimation(AnimationUtils.getTopBarHideAnimation());
    			mBottomBar.startAnimation(AnimationUtils.getBottomBarHideAnimation());

    			if (showPreviousTabView) {
    				mPreviousTabView.startAnimation(AnimationUtils.getPreviousTabViewHideAnimation());
    			}
    			
    			if (showNextTabView) {
    				mNextTabView.startAnimation(AnimationUtils.getNextTabViewHideAnimation());
    			}
    			
    			mTopBar.setVisibility(View.GONE);
    			mBottomBar.setVisibility(View.GONE);
    			
    			if (showPreviousTabView) {
    				mPreviousTabView.setVisibility(View.GONE);
    			}
    			
    			if (showNextTabView) {
    				mNextTabView.setVisibility(View.GONE);
    			}

    			String bubblePosition = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREFERENCES_GENERAL_BUBBLE_POSITION, "right");

    			if (bubblePosition.equals("right")) {
    				mBubbleRightView.setVisibility(View.VISIBLE);
    				mBubbleLeftView.setVisibility(View.GONE);
    			} else if (bubblePosition.equals("left")) {
    				mBubbleRightView.setVisibility(View.GONE);
    				mBubbleLeftView.setVisibility(View.VISIBLE);
    			} else if (bubblePosition.equals("both")) {
    				mBubbleRightView.setVisibility(View.VISIBLE);
    				mBubbleLeftView.setVisibility(View.VISIBLE);
    			} else {
    				mBubbleRightView.setVisibility(View.VISIBLE);
    				mBubbleLeftView.setVisibility(View.GONE);
    			}
    		}
			
			mUrlBarVisible = false;
    	}
    }
    
    /**
     * Update the url bar icons (favicon and progress animation).
     */
    private void updateUrlEditIcons() {
    	
    	if (mCurrentWebView.isLoading()) {    		
    		mUrlEditText.setCompoundDrawablesWithIntrinsicBounds(ApplicationUtils.getNormalizedFavicon(this, mCurrentWebView.getFavicon()),
    				null,
    				mCircularProgress.getCurrent(),
    				null);    		
    		((AnimationDrawable) mCircularProgress).start();
    	} else {
    		mUrlEditText.setCompoundDrawablesWithIntrinsicBounds(ApplicationUtils.getNormalizedFavicon(this, mCurrentWebView.getFavicon()),
    				null,
    				null,
    				null);    		
    		((AnimationDrawable) mCircularProgress).stop();
    	}
    }
    
    /**
     * Update the icon of the Stop/Go button.
     */
    private void updateStopGoButton() {
    	if (mCurrentWebView.isLoading()) {
    		mGoButton.setImageResource(R.drawable.ic_btn_stop);
    	} else {
    		mGoButton.setImageResource(R.drawable.ic_btn_go);
    	}
    }
    
    /**
     * Update the Back/Forward buttons enabled state.
     */
    private void updateBackForwardButtons() {
    	mBackButton.setEnabled(mCurrentWebView.canGoBack());
    	mForwardButton.setEnabled(mCurrentWebView.canGoForward());
    }
    
    private void updateTitle(String title) {
    	if ((title != null) &&
				(title.length() > 0)) {
			setTitle(title);
		} else {
			setTitle(R.string.Commons_ApplicationName);
		}
    }
    
    private void updatePreviousNextTabViewsVisibility() {
    	if (mUrlBarVisible) {
    		if (mCurrentViewIndex > 0) {
    			mPreviousTabView.setVisibility(View.VISIBLE);
    		} else {
    			mPreviousTabView.setVisibility(View.GONE);
    		}

    		if (mCurrentViewIndex < TabsController.getInstance().getWebViewContainers().size() - 1) {
    			mNextTabView.setVisibility(View.VISIBLE);
    		} else {
    			mNextTabView.setVisibility(View.GONE);
    		}
    	} else {
    		mPreviousTabView.setVisibility(View.GONE);
    		mNextTabView.setVisibility(View.GONE);
    	}
    }
    
    /**
     * Update the bars element (url, favivon, button states...) with the current WebView.
     */
    private void updateBars() {
    	if (mCurrentWebView.isLoading()) {
    		setToolbarsVisibility(true);
    	}
    	
    	mUrlEditText.setText(mCurrentWebView.getUrl());
    	
    	updateTitle(mCurrentWebView.getTitle());
    	
    	updateUrlEditIcons();
    	updateStopGoButton();
    	updateBackForwardButtons();
    	
    	mRemoveTabButton.setEnabled(TabsController.getInstance().getWebViewContainers().size() > 1);    	    	
    }
	
    /**
     * Remove the current tab.
     */
    private void removeCurrentTab() {
    	TabsController.getInstance().removeTab(mCurrentViewIndex);
    	
    	mCurrentViewIndex--;
    	if (mCurrentViewIndex < 0) {
    		mCurrentViewIndex = 0;
    	}
    	
    	showTab(mCurrentViewIndex, true, false);
    }
    
	/**
	 * Gesture listener implementation.
	 */
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			hideKeyboard(false);
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
