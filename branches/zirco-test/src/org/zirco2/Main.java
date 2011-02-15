package org.zirco2;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Main extends Activity implements OnTouchListener {
	
	private GestureDetector mGestureDetector;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mGestureDetector = new GestureDetector(this, new GestureListener(this));
        
        WebView webView = (WebView) findViewById(R.id.webview);
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
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}
}