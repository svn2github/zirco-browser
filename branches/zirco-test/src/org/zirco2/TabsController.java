package org.zirco2;

import java.util.ArrayList;
import java.util.List;

import org.zirco2.ui.components.CustomWebChromeClient;
import org.zirco2.ui.components.CustomWebView;
import org.zirco2.ui.components.CustomWebViewClient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

public class TabsController {
	
	private List<WebViewContainer> mWebViewContainers;
	
	private ViewFlipper mWebViewContainer;
	private OnTouchListener mTouchListener;
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
		mWebViewContainers = new ArrayList<WebViewContainer>();
	}
	
	public void initialize(Context context, OnTouchListener touchListener, ViewFlipper webViewContainer) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mWebViewContainer = webViewContainer;
		mTouchListener = touchListener;
	}
	
	public int addTab(int position, String url) {
		RelativeLayout view = (RelativeLayout) mInflater.inflate(R.layout.webview, mWebViewContainer, false);
		
		CustomWebView webView = (CustomWebView) view.findViewById(R.id.webview);
		
		int insertionIndex = addWebViewContainer(position, new WebViewContainer(view, webView));
		
		webView.setWebChromeClient(new CustomWebChromeClient(view));
        webView.setWebViewClient(new CustomWebViewClient(view));        
        webView.setOnTouchListener(mTouchListener);        
        
        webView.loadUrl(url);
        
        if (position >= 0) {
        	mWebViewContainer.addView(view, position);
        } else {
        	mWebViewContainer.addView(view);
        }
        
        return insertionIndex;
	}
	
	public List<WebViewContainer> getWebViewContainers() {
		return mWebViewContainers;
	}
	
	public int addWebViewContainer(int position, WebViewContainer webViewContainer) {
		if (position >= 0) {
			mWebViewContainers.add(position, webViewContainer);
		} else {
			mWebViewContainers.add(webViewContainer);
		}
		
		return mWebViewContainers.indexOf(webViewContainer);
	}

}
