package org.tint.ui.components;

import org.tint.R;
import org.tint.utils.ApplicationUtils;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

/**
 * Custom WebViewClient implementation.
 */
public class CustomWebViewClient extends WebViewClient {
	
	private AutoCompleteTextView mUrl;
	private ProgressBar mProgressBar;
	private Drawable mCircularProgress;
	
	/**
	 * Constructor.
	 * @param activity The current activity.
	 * @param view The parent view.
	 */
	public CustomWebViewClient(Activity activity, View view) {
		mUrl = (AutoCompleteTextView) view.findViewById(R.id.UrlText);
		mProgressBar = (ProgressBar) view.findViewById(R.id.WebViewProgress);
		
		mCircularProgress = activity.getResources().getDrawable(R.drawable.spinner);
		
		//mProgressBar.setVisibility(View.GONE);
		mProgressBar.setMax(100);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		mProgressBar.setProgress(100);
		
		mUrl.setCompoundDrawables(mUrl.getCompoundDrawables()[0], null, null, null);
		((AnimationDrawable) mCircularProgress).stop();
		
		//mProgressBar.setVisibility(View.GONE);
		super.onPageFinished(view, url);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		mUrl.setText(url);
		mProgressBar.setProgress(0);
		
		mUrl.setCompoundDrawables(mUrl.getCompoundDrawables()[0], null, mCircularProgress, null);
		((AnimationDrawable) mCircularProgress).start();
		
		//mProgressBar.setVisibility(View.VISIBLE);		
		super.onPageStarted(view, url, favicon);
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
