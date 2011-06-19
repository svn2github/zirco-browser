package org.tint.ui.activities;

import org.tint.R;
import org.tint.adapters.UrlSuggestionCursorAdapter;
import org.tint.adapters.WebViewsImageAdapter;
import org.tint.controllers.BookmarksHistoryController;
import org.tint.controllers.TabsController;
import org.tint.ui.components.CustomWebView;
import org.tint.utils.ApplicationUtils;
import org.tint.utils.Constants;
import org.tint.utils.UrlUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;

/**
 * Tabs activity. Tabs (or open browsing windows) are displayed as a gallery.
 */
public class TabsActivity extends Activity {

	private static final int ACTIVITY_OPEN_HISTORY_BOOKMARKS = 0;
	
	private Gallery mTabsGallery;
	private AutoCompleteTextView mUrl;
	private ImageButton mGo;
	private ImageButton mAddTab;
	private ImageButton mCloseTab;
	private TextView mTabTitle;
	private ImageButton mBack;
	private ImageButton mForward;
	private ImageButton mBookmarks;
		
	private CustomWebView mCurrentWebView = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.PREFERENCES_GENERAL_FULL_SCREEN, false)) {
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        
        }
		
        setContentView(R.layout.tabs_activity);
        
        mUrl = (AutoCompleteTextView) findViewById(R.id.TabsUrlText);
        
        mUrl.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// Select all when focus gained.
                if (hasFocus) {
                	mUrl.setSelection(0, mUrl.getText().length());
                }
			}
		});
        
        mUrl.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					navigateToCurrentUrl();
					return true;
				}
				return false;
			}			
    	});
        
        mUrl.setCompoundDrawablePadding(5);
        
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
					return BookmarksHistoryController.getInstance().getSuggestion(TabsActivity.this, constraint.toString());
				}
				return null;
			}
		});
        
        mUrl.setThreshold(1);
        mUrl.setAdapter(suggestionAdapter);
        
        mGo = (ImageButton) findViewById(R.id.GoBtn);
        
        mGo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				navigateToCurrentUrl();
			}
		});
        
        mTabsGallery = (Gallery) findViewById(R.id.TabsGallery);        

        mTabsGallery.setSpacing(5);
        mTabsGallery.setUnselectedAlpha(0.5f);
        
        mTabsGallery.setOnItemClickListener(new OnItemClickListener() {

        	@Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        		doFinish(position);
            }        	
        });
        
        mTabsGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				
				mCurrentWebView = TabsController.getInstance().getWebViewContainers().get(position).getWebView();
				
				String currentUrl = mCurrentWebView.getUrl();
				
				
				if ((currentUrl != null) &&
					(!currentUrl.equals(UrlUtils.URL_ABOUT_BLANK))) {
					mUrl.setText(currentUrl);
				} else {
					mUrl.setText(null);
				}
				
				mUrl.setCompoundDrawables(ApplicationUtils.getNormalizedFavicon(TabsActivity.this, mCurrentWebView.getFavicon()),
						null,
						null,
						null);
				
				mTabTitle.setText(mCurrentWebView.getTitle());
				
				mBack.setEnabled(mCurrentWebView.canGoBack());
				mForward.setEnabled(mCurrentWebView.canGoForward());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});               
        
        mAddTab = (ImageButton) findViewById(R.id.AddTabBtn);
        mAddTab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				addTab();
			}
		});
        
        mCloseTab = (ImageButton) findViewById(R.id.CloseTabBtn);
        mCloseTab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				removeTab();
			}
		});
        
        mTabTitle = (TextView) findViewById(R.id.TabTitle);
        
        mBack = (ImageButton) findViewById(R.id.BackBtn);
        mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mCurrentWebView != null) {
					mCurrentWebView.goBack();
					int selected = 	mTabsGallery.getSelectedItemPosition();
					doFinish(selected);
				}				
			}
		});
        
        mForward = (ImageButton) findViewById(R.id.ForwardBtn);
        mForward.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mCurrentWebView != null) {
					mCurrentWebView.goForward();
					int selected = 	mTabsGallery.getSelectedItemPosition();
					doFinish(selected);
				}
			}
		});
        
        mBookmarks = (ImageButton) findViewById(R.id.BookmarksBtn);
        mBookmarks.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openBookmarks();
			}
		});
        
        refreshTabsGallery(0);
        
        Bundle extras = getIntent().getExtras();
    	if (extras != null) {        	
    		mTabsGallery.setSelection(extras.getInt(Constants.EXTRA_CURRENT_VIEW_INDEX));        	
        }
	}

	@Override
	public void onBackPressed() {
		doFinish(-1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		super.onActivityResult(requestCode, resultCode, data);
		
		if ((requestCode == ACTIVITY_OPEN_HISTORY_BOOKMARKS) &&
				(resultCode == RESULT_OK)) {
			if (data != null) {
        		Bundle b = data.getExtras();
        		if (b != null) {
        			String url = b.getString(Constants.EXTRA_ID_URL);
        			navigateToUrl(url, b.getBoolean(Constants.EXTRA_ID_NEW_TAB));
        		}
			}
		}
	}	
	
	/**
	 * Close the tabs activity.
	 * @param index The current tab index.
	 */
	private void doFinish(int index) {
		if (index != -1) {
			Intent i = new Intent();
			i.putExtra(Constants.EXTRA_CURRENT_VIEW_INDEX, index);
			setResult(RESULT_OK, i);
		}
		finish();
		overridePendingTransition(R.anim.browser_view_enter, R.anim.tab_view_exit);
	}
	
	/**
	 * Hide the keyboard, if shown.
	 */
	private void hideKeyboard() {
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(mUrl.getWindowToken(), 0);
	}
	
	/**
	 * Navigate to the url in the url text view, using the current webview.
	 */
	private void navigateToCurrentUrl() {
		navigateToUrl(mUrl.getText().toString(), false);
	}
	
	/**
	 * Navigate to the given url.
	 * @param url The url to navigate to.
	 * @param newTab If True, a new tab is open for navigation; Otherwise, the currently selected tab is used.
	 */
	private void navigateToUrl(String url, boolean newTab) {
		hideKeyboard();
		
		if (newTab) {
			addTab();
		}
		
		int selected = 	mTabsGallery.getSelectedItemPosition();
		TabsController.getInstance().getWebViewContainers().get(selected).getWebView().loadUrl(url);
		doFinish(selected);
	}
	
	/**
	 * Open a new tab, positioning it to the right of the current tab.
	 */
	private void addTab() {
		int newIndex = mTabsGallery.getSelectedItemPosition() + 1;
		TabsController.getInstance().addTab(newIndex, UrlUtils.URL_ABOUT_BLANK);
		
		refreshTabsGallery(newIndex);
	}
	
	/**
	 * Close the current tab.
	 */
	private void removeTab() {
		int removedIndex = mTabsGallery.getSelectedItemPosition();
		TabsController.getInstance().removeTab(removedIndex);
		
		removedIndex--;
		refreshTabsGallery(removedIndex);
	}
	
	/**
	 * Refresh the tabs screenshots.
	 * @param indexToShow If > 0, set the current tab to this index.
	 */
	private void refreshTabsGallery(int indexToShow) {		
		mTabsGallery.setAdapter(new WebViewsImageAdapter(this));
		
		if (mTabsGallery.getCount() > 1) {
			mCloseTab.setVisibility(View.VISIBLE);
		} else {
			mCloseTab.setVisibility(View.INVISIBLE);
		}
		
		if (indexToShow > 0) {
			mTabsGallery.setSelection(indexToShow);
		}
	}
	
	/**
	 * Open the bookmarks and history activity.
	 */
	private void openBookmarks() {
		Intent i = new Intent(this, BookmarksHistoryActivity.class);
		startActivityForResult(i, ACTIVITY_OPEN_HISTORY_BOOKMARKS);
	}
}
