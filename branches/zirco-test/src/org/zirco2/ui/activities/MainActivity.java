package org.zirco2.ui.activities;

import org.zirco2.R;
import org.zirco2.TabsController;
import org.zirco2.WebViewContainer;
import org.zirco2.ui.components.CustomWebView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

public class MainActivity extends Activity implements OnTouchListener {
	
	public static int ACTIVITY_SHOW_TABS = 0;
	
	private GestureDetector mGestureDetector;
	//private FrameLayout mWebViewContainer;
	private ViewFlipper mWebViewContainer;
	private LayoutInflater mInflater = null;
	
	private int mCurrentViewIndex = -1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main_activity);
        
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mGestureDetector = new GestureDetector(this, new GestureListener());
        
        //mWebViewContainer = (FrameLayout) findViewById(R.id.WebWiewContainer);
        mWebViewContainer = (ViewFlipper) findViewById(R.id.WebWiewContainer);
        
        
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
		
		CustomWebView webView = (CustomWebView) view.findViewById(R.id.webview);
		
		mCurrentViewIndex = TabsController.getInstance().addWebViewContainer(new WebViewContainer(view, webView));
		
		webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());        
        webView.setOnTouchListener(this);        
        
        webView.loadUrl(url);
        
        mWebViewContainer.addView(view);
        
        showTab(mCurrentViewIndex);
	}
	
	private void showTab(int tabIndex) {
		mCurrentViewIndex = tabIndex;
		//View view = TabsController.getInstance().getWebViews().get(tabIndex).getView();
		//mWebViewContainer.bringChildToFront(view);
		//view.requestFocus();
		mWebViewContainer.setDisplayedChild(mCurrentViewIndex);
		
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
        			showTab(position);        			
        		}
			}
		}
	}
	
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Intent i = new Intent(MainActivity.this, TabsActivity.class);
			i.putExtra("CURRENT_VIEW_INDEX", mCurrentViewIndex);
			
			MainActivity.this.startActivityForResult(i, MainActivity.ACTIVITY_SHOW_TABS);
			MainActivity.this.overridePendingTransition(R.anim.tab_view_enter, R.anim.browser_view_exit);
			
			return true;
		}
		
	}
	
}