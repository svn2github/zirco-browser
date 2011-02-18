package org.zirco2.controllers;

import java.util.ArrayList;
import java.util.List;

import org.zirco2.R;
import org.zirco2.ui.IWebViewActivity;
import org.zirco2.ui.components.CustomWebChromeClient;
import org.zirco2.ui.components.CustomWebView;
import org.zirco2.ui.components.CustomWebViewClient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

public class TabsController {
	
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
