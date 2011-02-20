package org.tint.controllers;

import java.util.ArrayList;
import java.util.List;

import org.tint.R;
import org.tint.ui.IWebViewActivity;
import org.tint.ui.components.CustomWebChromeClient;
import org.tint.ui.components.CustomWebView;
import org.tint.ui.components.CustomWebViewClient;
import org.tint.utils.Constants;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

public class TabsController {
	
	public static final int TAB_CONTEXT_MENU_OPEN = Menu.FIRST + 10;
	public static final int TAB_CONTEXT_MENU_OPEN_IN_NEW_TAB = Menu.FIRST + 11;
	
	private List<WebViewContainer> mWebViewList;
	
	private ViewFlipper mWebViewsContainer;
	private OnTouchListener mTouchListener;
	private IWebViewActivity mWebViewActivity;
	private LayoutInflater mInflater = null;
	
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
	
	public void initialize(Context context, OnTouchListener touchListener, IWebViewActivity webViewActivity, ViewFlipper webViewContainer) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mWebViewsContainer = webViewContainer;
		mTouchListener = touchListener;
		mWebViewActivity = webViewActivity;
	}
	
	public int addTab(int position, String url) {
		RelativeLayout view = (RelativeLayout) mInflater.inflate(R.layout.webview, mWebViewsContainer, false);
		
		CustomWebView webView = (CustomWebView) view.findViewById(R.id.webview);
		
		int insertionIndex = addWebViewContainer(position, new WebViewContainer(view, webView));
		
		webView.setWebChromeClient(new CustomWebChromeClient(view, mWebViewActivity));
        webView.setWebViewClient(new CustomWebViewClient(view));        
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
	
	public void removeTab(int index) {
		mWebViewList.remove(index);
		mWebViewsContainer.removeViewAt(index);
	}
	
	public List<WebViewContainer> getWebViewContainers() {
		return mWebViewList;
	}
	
	public int addWebViewContainer(int position, WebViewContainer webViewContainer) {
		if (position >= 0) {
			mWebViewList.add(position, webViewContainer);
		} else {
			mWebViewList.add(webViewContainer);
		}
		
		return mWebViewList.indexOf(webViewContainer);
	}

}
