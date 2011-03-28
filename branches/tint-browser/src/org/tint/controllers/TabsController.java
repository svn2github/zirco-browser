package org.tint.controllers;

import java.util.ArrayList;
import java.util.List;

import org.tint.R;
import org.tint.model.DownloadItem;
import org.tint.model.WebViewContainer;
import org.tint.ui.IWebViewActivity;
import org.tint.ui.components.CustomWebChromeClient;
import org.tint.ui.components.CustomWebView;
import org.tint.ui.components.CustomWebViewClient;
import org.tint.utils.Constants;
import org.tint.utils.IOUtils;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

/**
 * Controller managing tabs.
 * Responsible for tabs creation, selection, deletion.
 */
public final class TabsController {
	
	public static final int CONTEXT_MENU_OPEN = Menu.FIRST + 10;
	public static final int CONTEXT_MENU_OPEN_IN_NEW_TAB = Menu.FIRST + 11;
	public static final int CONTEXT_MENU_DOWNLOAD = Menu.FIRST + 12;
	public static final int CONTEXT_MENU_COPY = Menu.FIRST + 13;
	public static final int CONTEXT_MENU_SEND_MAIL = Menu.FIRST + 14;
	
	private List<WebViewContainer> mWebViewList;
	
	private List<DownloadItem> mDownloadsList;
	
	private DownloadManager mDownloadManager;	
	
	private Activity mMainActivity;
	private ViewFlipper mWebViewsContainer;
	private OnTouchListener mTouchListener;
	private IWebViewActivity mWebViewActivity;
	private LayoutInflater mInflater = null;
	
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
		mDownloadsList = new ArrayList<DownloadItem>();		
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
	 * @param webViewActivity 
	 * @param webViewContainer The main ViewFlipper, containing all the WebView.
	 */
	public void initialize(Activity activity, OnTouchListener touchListener, IWebViewActivity webViewActivity, ViewFlipper webViewContainer) {
		mMainActivity = activity;		
		mWebViewsContainer = webViewContainer;
		mTouchListener = touchListener;
		mWebViewActivity = webViewActivity;
		
		mInflater = (LayoutInflater) mMainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mPreferenceChangeListener = new OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				onPreferencesChanged();
			}			
		};
		
		PreferenceManager.getDefaultSharedPreferences(mMainActivity).registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
		
		mDownloadManager = (DownloadManager) mMainActivity.getSystemService(Context.DOWNLOAD_SERVICE);
	}
	
	/**
	 * Retrieve a DownloadItem by its id.
	 * @param id The DownloadItem id.
	 * @return The DownloadItem, or null if not found.
	 */
	public DownloadItem getDownloadItemById(long id) {
		for (DownloadItem item : mDownloadsList) {
			if (item.getId() == id) {
				return item;
			}
		}
		
		return null;
	}
	
	/**
	 * Remove a DownloadItem from the download list.
	 * @param item The DownloadItem to remove.
	 */
	public void removeDownloadItem(DownloadItem item) {
		mDownloadsList.remove(item);
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
		
		int insertionIndex = addWebViewContainer(position, new WebViewContainer(view, webView));
		
		webView.setWebChromeClient(new CustomWebChromeClient(mMainActivity, mWebViewActivity));
        webView.setWebViewClient(new CustomWebViewClient(mWebViewActivity));        
        webView.setOnTouchListener(mTouchListener);
        
        webView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				HitTestResult result = ((WebView) v).getHitTestResult();
				
				int resultType = result.getType();
				if ((resultType == HitTestResult.ANCHOR_TYPE) ||						
						(resultType == HitTestResult.SRC_ANCHOR_TYPE)) {
					
					Intent i = new Intent();
					i.putExtra(Constants.EXTRA_ID_URL, result.getExtra());
					
					MenuItem item = menu.add(0, CONTEXT_MENU_OPEN, 0, R.string.ContextMenu_Open);
					item.setIntent(i);
	
					item = menu.add(0, CONTEXT_MENU_OPEN_IN_NEW_TAB, 0, R.string.ContextMenu_OpenInNewTab);					
					item.setIntent(i);
					
					item = menu.add(0, CONTEXT_MENU_COPY, 0, R.string.ContextMenu_CopyLinkUrl);					
					item.setIntent(i);
				
					menu.setHeaderTitle(result.getExtra());		
					
				} else if ((resultType == HitTestResult.IMAGE_TYPE) ||
						(resultType == HitTestResult.IMAGE_ANCHOR_TYPE) || 
						(resultType == HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
					
					Intent i = new Intent();
					i.putExtra(Constants.EXTRA_ID_URL, result.getExtra());
					
					MenuItem item = menu.add(0, CONTEXT_MENU_OPEN, 0, R.string.ContextMenu_ViewImage);
					item.setIntent(i);
					
					item = menu.add(0, CONTEXT_MENU_OPEN_IN_NEW_TAB, 0, R.string.ContextMenu_ViewImageInNewTab);					
					item.setIntent(i);
					
					item = menu.add(0, CONTEXT_MENU_COPY, 0, R.string.ContextMenu_CopyImageUrl);					
					item.setIntent(i);
					
					item = menu.add(0, CONTEXT_MENU_DOWNLOAD, 0, R.string.ContextMenu_DownloadImage);					
					item.setIntent(i);
					
					menu.setHeaderTitle(result.getExtra());
					
				}  else if (resultType == HitTestResult.EMAIL_TYPE) {
					
					Intent sendMail = new Intent(Intent.ACTION_VIEW, Uri.parse(WebView.SCHEME_MAILTO + result.getExtra()));
					
					MenuItem item = menu.add(0, CONTEXT_MENU_SEND_MAIL, 0, R.string.ContextMenu_SendEmail);					
					item.setIntent(sendMail);
					
					Intent i = new Intent();
					i.putExtra(Constants.EXTRA_ID_URL, result.getExtra());
					
					item = menu.add(0, CONTEXT_MENU_COPY, 0, R.string.ContextMenu_CopyEmailUrl);					
					item.setIntent(i);
					
					menu.setHeaderTitle(result.getExtra());
				}
			}
		});
        
        webView.setDownloadListener(new DownloadListener() {
			
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {				
				doDownload(url);
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
	 * Launch a download through the DownloadManager.
	 * @param url The url of the file to download.
	 */
	public void doDownload(String url) {
		DownloadItem item = new DownloadItem(url, url.substring(url.lastIndexOf("/") + 1));
		
		IOUtils.createDownloadFolderIfRequired();
		IOUtils.deleteFileInDownloadFolderIfPresent(item.getDestinationFileName());

		Uri uriUrl = Uri.parse(url);
		Request request = new Request(uriUrl);
		request.setTitle(String.format(mMainActivity.getString(R.string.Commons_Downloading), item.getDestinationFileName()));
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, item.getDestinationFileName());
		long id = mDownloadManager.enqueue(request);
		
		item.setId(id);
		
		mDownloadsList.add(item);
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
