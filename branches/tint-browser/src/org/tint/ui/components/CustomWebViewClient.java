package org.tint.ui.components;

import org.tint.R;
import org.tint.ui.IWebViewActivity;
import org.tint.utils.ApplicationUtils;
import org.tint.utils.UrlUtils;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Custom WebViewClient implementation.
 */
public class CustomWebViewClient extends WebViewClient {
	
	private IWebViewActivity mWebViewActivity;
	
	/**
	 * Constructor.
	 * @param webViewActivity The parent IWebViewActivity.
	 */
	public CustomWebViewClient(IWebViewActivity webViewActivity) {
		mWebViewActivity = webViewActivity;
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		
		// Some magic here: when performing WebView.loadDataWithBaseURL, the url is "file:///android_asset/startpage,
		// whereas when the doing a "previous" or "next", the url is "about:start", and we need to perform the
		// loadDataWithBaseURL here, otherwise it won't load.
		if (url.equals(UrlUtils.URL_ABOUT_START)) {
			view.loadDataWithBaseURL("file:///android_asset/startpage/",
					UrlUtils.getStartPage(mWebViewActivity.getActivity()), "text/html", "UTF-8", UrlUtils.URL_ABOUT_START);
		}
		
		((CustomWebView) view).setLoading(true);		
		mWebViewActivity.onPageStarted(view, url);				
				
		super.onPageStarted(view, url, favicon);
	}
	
	@Override
	public void onPageFinished(WebView view, String url) {
		
		((CustomWebView) view).setLoading(false);
		mWebViewActivity.onPageFinished(view);				

		super.onPageFinished(view, url);
	}

	@Override
	public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {		
		super.onReceivedSslError(view, handler, error);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(view.getResources().getString(R.string.Commons_SslWarningsHeader));
		sb.append("\n\n");
		
		if (error.hasError(SslError.SSL_UNTRUSTED)) {
			sb.append(" - ");
			sb.append(view.getResources().getString(R.string.Commons_SslUntrusted));
			sb.append("\n");
		}
		
		if (error.hasError(SslError.SSL_IDMISMATCH)) {
			sb.append(" - ");
			sb.append(view.getResources().getString(R.string.Commons_SslIDMismatch));
			sb.append("\n");
		}
		
		if (error.hasError(SslError.SSL_EXPIRED)) {
			sb.append(" - ");
			sb.append(view.getResources().getString(R.string.Commons_SslExpired));
			sb.append("\n");
		}
		
		if (error.hasError(SslError.SSL_NOTYETVALID)) {
			sb.append(" - ");
			sb.append(view.getResources().getString(R.string.Commons_SslNotYetValid));
			sb.append("\n");
		}
		
		ApplicationUtils.showContinueCancelDialog(view.getContext(),
				android.R.drawable.ic_dialog_info,
				view.getResources().getString(R.string.Commons_SslWarning),
				sb.toString(),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						handler.proceed();
					}

				},
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						handler.cancel();
					}
		});
	}
	
}
