package org.zirco2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class GalleryActivity extends Activity {
	
	private Gallery mGallery;
	private AutoCompleteTextView mUrl;
	private ImageButton mGo;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
        setContentView(R.layout.gallery);
        
        mUrl = (AutoCompleteTextView) findViewById(R.id.UrlText);
        
        mUrl.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// Select all when focus gained.
                if (hasFocus) {
                	mUrl.setSelection(0, mUrl.getText().length());
                }
			}
		});
        
        mGo = (ImageButton) findViewById(R.id.GoBtn);
        
        mGo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int selected = 	mGallery.getSelectedItemPosition();
				TabsController.getInstance().getWebViews().get(selected).getWebView().loadUrl(mUrl.getText().toString());
				doFinish(selected);
			}
		});
        
        mGallery = (Gallery) findViewById(R.id.gallery);
        mGallery.setAdapter(new ImageAdapter(this));

        mGallery.setSpacing(5);
        mGallery.setUnselectedAlpha(0.5f);
        
        mGallery.setOnItemClickListener(new OnItemClickListener() {

        	@Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        		doFinish(position);
            }
        	
        });
        
        mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				
				mUrl.setText(TabsController.getInstance().getWebViews().get(position).getWebView().getUrl());
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub				
			}
		});               
        
        Bundle extras = getIntent().getExtras();
    	if (extras != null) {        	
    		mGallery.setSelection(extras.getInt("CURRENT_VIEW_INDEX"));        	
        }
	}

	@Override
	public void onBackPressed() {
		doFinish(-1);
	}

	private void doFinish(int index) {
		if (index != -1) {
			Intent i = new Intent();
			i.putExtra("TAB_INDEX", index);
			setResult(RESULT_OK, i);
		}
		finish();
		overridePendingTransition(R.anim.browser_view_enter, R.anim.tab_view_exit);
	}
	
}
