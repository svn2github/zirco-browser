package org.zirco2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class Main extends Activity implements OnTouchListener {
	
	public static int ACTIVITY_SHOW_TABS = 0;
	
	private GestureDetector mGestureDetector;
	private FrameLayout mWebViewContainer;
	private LayoutInflater mInflater = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mGestureDetector = new GestureDetector(this, new GestureListener(this));
        
        mWebViewContainer = (FrameLayout) findViewById(R.id.WebWiewContainer);
        
        addTab("http://fr.m.wikipedia.org/");
        addTab("http://www.google.com/");
        
        /*
        WebView webView = (WebView) findViewById(R.id.webview);
        
        TabsController.getInstance().addWebView(webView);
        
        webView.loadUrl("http://fr.m.wikipedia.org/");
        
        webView.setLongClickable(true);
        
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        
        webView.setOnTouchListener(this);        
        
        webView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(Main.this, "OnLongClickListenerOnLink", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		*/		
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}
	
	private void addTab(String url) {
		RelativeLayout view = (RelativeLayout) mInflater.inflate(R.layout.webview, mWebViewContainer, false);
		
		WebView webView = (WebView) view.findViewById(R.id.webview);
		
		TabsController.getInstance().addWebViewContainer(new WebViewContainer(view, webView));
		
		webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());        
        webView.setOnTouchListener(this);
        
        webView.loadUrl(url);
        
        mWebViewContainer.addView(view);
        mWebViewContainer.bringChildToFront(view);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if ((requestCode == ACTIVITY_SHOW_TABS) &&
				(resultCode == RESULT_OK)) {
			if (data != null) {
        		Bundle b = data.getExtras();
        		if (b != null) {
        			int position = b.getInt("TAB_INDEX");
        			Log.d("Position:", Integer.toString(position));
        			mWebViewContainer.bringChildToFront(TabsController.getInstance().getWebViews().get(position).getView());
        		}
			}
		}
	}
}