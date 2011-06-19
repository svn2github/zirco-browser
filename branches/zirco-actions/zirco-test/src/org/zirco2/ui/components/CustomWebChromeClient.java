package org.zirco2.ui.components;

import org.zirco2.R;
import org.zirco2.controllers.TabsController;
import org.zirco2.runnables.HistoryUpdaterRunnable;
import org.zirco2.ui.IWebViewActivity;

import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class CustomWebChromeClient extends WebChromeClient {
	
	private IWebViewActivity mWebViewActivity;
	private ProgressBar mProgressBar;
	
	public CustomWebChromeClient(View view, IWebViewActivity webViewActivity) {
		mWebViewActivity = webViewActivity;
		mProgressBar = (ProgressBar) view.findViewById(R.id.WebViewProgress);
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		mProgressBar.setProgress(newProgress);
		super.onProgressChanged(view, newProgress);
	}
	
	@Override
	public boolean onCreateWindow(WebView view, final boolean dialog, final boolean userGesture, final Message resultMsg) {
		
		WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
		
		int currentWebViewIndex = mWebViewActivity.getCurrentWebViewIndex();
		
		currentWebViewIndex = mWebViewActivity.addTab(currentWebViewIndex + 1, null);
		
		transport.setWebView(TabsController.getInstance().getWebViewContainers().get(currentWebViewIndex).getWebView());
		resultMsg.sendToTarget();
		
		return false;
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		
		new Thread(new HistoryUpdaterRunnable(title, view.getOriginalUrl())).start();
		
		super.onReceivedTitle(view, title);
	}

}
