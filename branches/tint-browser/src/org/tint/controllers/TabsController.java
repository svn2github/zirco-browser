package org.tint.controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.tint.R;
import org.tint.adapters.UrlSuggestionCursorAdapter;
import org.tint.model.WebViewContainer;
import org.tint.ui.activities.MainActivity;
import org.tint.ui.components.CustomWebChromeClient;
import org.tint.ui.components.CustomWebView;
import org.tint.ui.components.CustomWebViewClient;
import org.tint.utils.Constants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;

/**
 * Controller managing tabs.
 * Responsible for tabs creation, selection, deletion.
 */
public final class TabsController {
	
	public static final int TAB_CONTEXT_MENU_OPEN = Menu.FIRST + 10;
	public static final int TAB_CONTEXT_MENU_OPEN_IN_NEW_TAB = Menu.FIRST + 11;
	
	private List<WebViewContainer> mWebViewList;
	
	private MainActivity mMainActivity;
	private ViewFlipper mWebViewsContainer;
	private OnTouchListener mTouchListener;
	private LayoutInflater mInflater = null;
	
	private Method mWebViewSetEmbeddedTitleBar = null;
	
	private OnSharedPreferenceChangeListener mPreferenceChangeListener;
	
	/**
	 * Holder for singleton implementation.
	 */
	private static final class TabsControllerHolder {
		private static final TabsController INSTANCE = new TabsController();
		/**
		 * Private Constructor.
		 */
		private TabsControllerHolder() { }
	}
	
	/**
	 * Get the unique instance of the Controller.
	 * @return The instance of the Controller
	 */
	public static TabsController getInstance() {
		return TabsControllerHolder.INSTANCE;
	}	
	
	/**
	 * Private Constructor.
	 */
	private TabsController() {
		mWebViewList = new ArrayList<WebViewContainer>();		
	}
	
	/**
	 * Event when a preference has changed. Reinitialize all WebViews, to update them with new preferences.
	 */
	private void onPreferencesChanged() {
		for (WebViewContainer view : mWebViewList) {
			view.getWebView().initializeOptions();
		}
	}
	
	/**
	 * Initialize the Controller.
	 * @param activity The main activity.
	 * @param touchListener The TouchListener to be set on each created WebView.
	 * @param webViewContainer The main ViewFlipper, containing all the WebView.
	 */
	public void initialize(MainActivity activity, OnTouchListener touchListener, ViewFlipper webViewContainer) {
		mMainActivity = activity;		
		mWebViewsContainer = webViewContainer;
		mTouchListener = touchListener;
		
		mInflater = (LayoutInflater) mMainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mPreferenceChangeListener = new OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				onPreferencesChanged();
			}			
		};
		
		PreferenceManager.getDefaultSharedPreferences(mMainActivity).registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
		
		try {
			
			mWebViewSetEmbeddedTitleBar = WebView.class.getMethod("setEmbeddedTitleBar", new Class[] { View.class });
			
		} catch (SecurityException e) {
			mWebViewSetEmbeddedTitleBar = null;
			Log.e("TabsController: Unable to get setEmbeddedTitleBar method: SecurityException.", e.getMessage());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			mWebViewSetEmbeddedTitleBar = null;
			Log.e("TabsController: Unable to get setEmbeddedTitleBar method: NoSuchMethodException.", e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Call the WebView method setEmbeddedTitleBar throught reflection.
	 * @param webView The WebView.
	 * @param view The method parameter.
	 */
	private void callSetEmbeddedTitleBar(WebView webView, View view) {
		try {
			
			mWebViewSetEmbeddedTitleBar.invoke(webView, view);
			
		} catch (IllegalArgumentException e) {
			Log.e("TabsController: Unable to call setEmbeddedTitleBar method: IllegalArgumentException.", e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.e("TabsController: Unable to call setEmbeddedTitleBar method: IllegalAccessException.", e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Log.e("TabsController: Unable to call setEmbeddedTitleBar method: InvocationTargetException.", e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a new tab at the given position, and navigate to the given url.
	 * @param position The position to insert the tab.
	 * @param url The url to navigate to.
	 * @return The new tab index.
	 */
	public int addTab(int position, String url) {
		RelativeLayout view = (RelativeLayout) mInflater.inflate(R.layout.webview, mWebViewsContainer, false);		
		final CustomWebView webView = (CustomWebView) view.findViewById(R.id.webview);
				
		View titleBar = mInflater.inflate(R.layout.title_bar, view, false);
		callSetEmbeddedTitleBar(webView, titleBar);
		
		final AutoCompleteTextView urlView = (AutoCompleteTextView) titleBar.findViewById(R.id.UrlText);
		
		int insertionIndex = addWebViewContainer(position, new WebViewContainer(view, webView));
		
		webView.setWebChromeClient(new CustomWebChromeClient(mMainActivity, view));
        webView.setWebViewClient(new CustomWebViewClient(mMainActivity, view));        
        webView.setOnTouchListener(mTouchListener);
        
        webView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				HitTestResult result = ((WebView) v).getHitTestResult();
				
				int resultType = result.getType();
				if ((resultType == HitTestResult.ANCHOR_TYPE) ||
						(resultType == HitTestResult.IMAGE_ANCHOR_TYPE) ||
						(resultType == HitTestResult.SRC_ANCHOR_TYPE) ||
						(resultType == HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
					
					Intent i = new Intent();
					i.putExtra(Constants.EXTRA_ID_URL, result.getExtra());
					
					MenuItem item = menu.add(0, TAB_CONTEXT_MENU_OPEN, 0, R.string.ContextMenu_Open);
					item.setIntent(i);
	
					item = menu.add(0, TAB_CONTEXT_MENU_OPEN_IN_NEW_TAB, 0, R.string.ContextMenu_OpenInNewTab);					
					item.setIntent(i);
					
//					item = menu.add(0, CONTEXT_MENU_COPY, 0, R.string.Main_MenuCopyLinkUrl);					
//					item.setIntent(i);
//					
//					item = menu.add(0, CONTEXT_MENU_DOWNLOAD, 0, R.string.Main_MenuDownload);					
//					item.setIntent(i);										
				
					menu.setHeaderTitle(result.getExtra());					
				}
			}
		});
        
        urlView.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// Select all when focus gained.
                if (hasFocus) {
                	urlView.setSelection(0, urlView.getText().length());
                }
			}
		});
        
        urlView.setCompoundDrawablePadding(5);
        
        String[] from = new String[] { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL };
        int[] to = new int[] {R.id.AutocompleteTitle, R.id.AutocompleteUrl};
        
        UrlSuggestionCursorAdapter suggestionAdapter = new UrlSuggestionCursorAdapter(mMainActivity, R.layout.url_autocomplete_line, null, from, to);
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
					return BookmarksHistoryController.getInstance().getSuggestion(mMainActivity, constraint.toString());
				}
				return null;
			}
		});
        
        urlView.setThreshold(1);
        urlView.setAdapter(suggestionAdapter);
        
        ImageButton goBtn = (ImageButton) titleBar.findViewById(R.id.GoBtn);
        
        goBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				webView.loadUrl(urlView.getText().toString());
			}
		});
        
        if ((url != null) &&
        		(url.length() > 0)) {
        	webView.loadUrl(url);
        }        
        
        if (position >= 0) {
        	mWebViewsContainer.addView(view, position);
        } else {
        	mWebViewsContainer.addView(view);
        }
        
        return insertionIndex;
	}
	
	/**
	 * Remove the tab at the given index.
	 * @param index The index of the tab to remove.
	 */
	public void removeTab(int index) {
		mWebViewList.remove(index);
		mWebViewsContainer.removeViewAt(index);
	}
	
	/**
	 * Get the list of WebViewContainer, e.g. the association of a WebView and its parent layout.
	 * @return The list of WebViewContainer.
	 * @see WebViewContainer
	 */
	public List<WebViewContainer> getWebViewContainers() {
		return mWebViewList;
	}
	
	/**
	 * Clear the form data on all existants WebView.
	 */
	public void clearFormData() {
		for (WebViewContainer view : mWebViewList) {
			view.getWebView().clearFormData();
		}
	}
	
	/**
	 * Clear the cache.
	 */
	public void clearCache() {
		if (!mWebViewList.isEmpty()) {
			// Clear cache only need to be done on one WebView. See http://developer.android.com/reference/android/webkit/WebView.html#clearCache%28boolean%29
			mWebViewList.get(0).getWebView().clearCache(true);
		}
	}

	/**
	 * Add the given WebViewContainer at the given position.
	 * @param position The insertion position. Can be < 0. If so, the insertion will be at the end of the list.
	 * @param webViewContainer The WebViewContainer to add.
	 * @return The index of the insertion.
	 * @see WebViewContainer
	 */
	private int addWebViewContainer(int position, WebViewContainer webViewContainer) {
		if (position >= 0) {
			mWebViewList.add(position, webViewContainer);
		} else {
			mWebViewList.add(webViewContainer);
		}
		
		return mWebViewList.indexOf(webViewContainer);
	}
	
}
