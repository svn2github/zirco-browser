package org.tint.ui.components;

import org.tint.R;
import org.tint.controllers.TabsController;
import org.tint.runnables.FaviconUpdaterRunnable;
import org.tint.runnables.HistoryUpdaterRunnable;
import org.tint.ui.IWebViewActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Custom WebChromeClient implementation.
 */
public class CustomWebChromeClient extends WebChromeClient {
	
	private Activity mMainActivity;
	private IWebViewActivity mWebViewActivity;
	
	/**
	 * Constructor.
	 * @param activity The parent activity.
	 * @param webViewActivity The IWebView activity.
	 */
	public CustomWebChromeClient(Activity activity, IWebViewActivity webViewActivity) {
		mMainActivity = activity;
		mWebViewActivity = webViewActivity;
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		mWebViewActivity.onPageProgress(newProgress);
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
		
		new Thread(new HistoryUpdaterRunnable(mMainActivity, title, view.getUrl(), view.getOriginalUrl())).start();
		
		super.onReceivedTitle(view, title);
	}

	@Override
	public void onReceivedIcon(WebView view, Bitmap icon) {
		
		new Thread(new FaviconUpdaterRunnable(mMainActivity, view.getUrl(), view.getOriginalUrl(), icon)).start();
		
		super.onReceivedIcon(view, icon);
	}

	@Override
	public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
		new AlertDialog.Builder(mMainActivity)
		.setTitle(R.string.Commons_JavaScriptAlert)
		.setMessage(message)
		.setPositiveButton(android.R.string.ok,
				new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				result.confirm();
			}
		})
		.setCancelable(false)
		.create()
		.show();

		return true;
	}
	
	@Override
	public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
		new AlertDialog.Builder(mMainActivity)
		.setTitle(R.string.Commons_JavaScriptConfirm)
		.setMessage(message)
		.setPositiveButton(android.R.string.ok, 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				result.confirm();
			}
		})
		.setNegativeButton(android.R.string.cancel, 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				result.cancel();
			}
		})
		.create()
		.show();

		return true;
	}

	@Override
	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
		final LayoutInflater factory = LayoutInflater.from(mMainActivity);
        final View v = factory.inflate(R.layout.javascript_prompt_dialog, null);
        ((TextView) v.findViewById(R.id.JavaScriptPromptMessage)).setText(message);
        ((EditText) v.findViewById(R.id.JavaScriptPromptInput)).setText(defaultValue);

        new AlertDialog.Builder(mMainActivity)
            .setTitle(R.string.Commons_JavaScriptPrompt)
            .setView(v)
            .setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = ((EditText) v.findViewById(R.id.JavaScriptPromptInput)).getText()
                                    .toString();
                            result.confirm(value);
                        }
                    })
            .setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            result.cancel();
                        }
                    })
            .setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            result.cancel();
                        }
                    })
            .show();
        
        return true;
	}
	
}
