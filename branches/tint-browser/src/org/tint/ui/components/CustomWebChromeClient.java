package org.tint.ui.components;

import org.tint.R;
import org.tint.controllers.TabsController;
import org.tint.runnables.HistoryUpdaterRunnable;
import org.tint.ui.IWebViewActivity;

import android.app.Activity;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class CustomWebChromeClient extends WebChromeClient {
	
	private Activity mMainActivity;
	private IWebViewActivity mWebViewActivity;
	private ProgressBar mProgressBar;
	
	public CustomWebChromeClient(Activity activity, View view, IWebViewActivity webViewActivity) {
		mMainActivity = activity;
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
		
		new Thread(new HistoryUpdaterRunnable(mMainActivity, title, view.getOriginalUrl())).start();
		
		super.onReceivedTitle(view, title);
	}

}
