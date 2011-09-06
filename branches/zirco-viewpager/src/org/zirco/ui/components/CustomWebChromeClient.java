/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 - 2011 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.zirco.ui.components;

import org.zirco.R;
import org.zirco.ui.activities.MainActivity;
import org.zirco.ui.runnables.FaviconUpdaterRunnable;
import org.zirco.ui.runnables.HistoryUpdater;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

public class CustomWebChromeClient extends WebChromeClient {
	
	private MainActivity mParentActivity;
	private CustomWebView mWebView;
	
	public CustomWebChromeClient(MainActivity parentActivity, CustomWebView webView) {
		mParentActivity = parentActivity;
		mWebView = webView;
	}
	
	// This is an undocumented method, it _is_ used, whatever Eclipse may think :)
	// Used to show a file chooser dialog.
	public void openFileChooser(ValueCallback<Uri> uploadMsg) {
		mParentActivity.setUploadMessage(uploadMsg);
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("*/*");
		mParentActivity.startActivityForResult(
				Intent.createChooser(i, mParentActivity.getString(R.string.Main_FileChooserPrompt)),
				MainActivity.OPEN_FILE_CHOOSER_ACTIVITY);
	}
	
	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		((CustomWebView) view).setProgress(newProgress);
		mParentActivity.setLoadProgress(mWebView, mWebView.getProgress());
	}
	
	@Override
	public void onReceivedIcon(WebView view, Bitmap icon) {
		new Thread(new FaviconUpdaterRunnable(mParentActivity, view.getUrl(), view.getOriginalUrl(), icon)).start();
		
		mParentActivity.updateFavIcon();
		
		super.onReceivedIcon(view, icon);
	}

	@Override
	public boolean onCreateWindow(WebView view, final boolean dialog, final boolean userGesture, final Message resultMsg) {
		
		WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
		
		WebView newWebView = mParentActivity.addTab();		
		
		transport.setWebView(newWebView);
		resultMsg.sendToTarget();
		
		return false;
	}
	
	@Override
	public void onReceivedTitle(WebView view, String title) {
		mParentActivity.setTitle(String.format(mParentActivity.getResources().getString(R.string.ApplicationNameUrl), title)); 
		
		startHistoryUpdaterRunnable(title, mWebView.getUrl(), mWebView.getOriginalUrl());
		
		super.onReceivedTitle(view, title);
	}

	@Override
	public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
		new AlertDialog.Builder(mParentActivity)
		.setTitle(R.string.Commons_JavaScriptDialog)
		.setMessage(message)
		.setPositiveButton(android.R.string.ok,
				new AlertDialog.OnClickListener()
		{
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
		new AlertDialog.Builder(mParentActivity)
		.setTitle(R.string.Commons_JavaScriptDialog)
		.setMessage(message)
		.setPositiveButton(android.R.string.ok, 
				new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) {
				result.confirm();
			}
		})
		.setNegativeButton(android.R.string.cancel, 
				new DialogInterface.OnClickListener() 
		{
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
		
		final LayoutInflater factory = LayoutInflater.from(mParentActivity);
        final View v = factory.inflate(R.layout.javascriptpromptdialog, null);
        ((TextView) v.findViewById(R.id.JavaScriptPromptMessage)).setText(message);
        ((EditText) v.findViewById(R.id.JavaScriptPromptInput)).setText(defaultValue);

        new AlertDialog.Builder(mParentActivity)
            .setTitle(R.string.Commons_JavaScriptDialog)
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
	
	/**
     * Start a runnable to update history.
     * @param title The page title.
     * @param url The page url.
     */
    private void startHistoryUpdaterRunnable(String title, String url, String originalUrl) {
    	if ((url != null) &&
    			(url.length() > 0)) {
    		new Thread(new HistoryUpdater(mParentActivity, title, url, originalUrl)).start();
    	}
    }

}
