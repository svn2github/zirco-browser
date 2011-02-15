package org.zirco2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GalleryActivity extends Activity {	

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        
        Gallery g = (Gallery) findViewById(R.id.gallery);
        g.setAdapter(new ImageAdapter(this));

        g.setOnItemClickListener(new OnItemClickListener() {

        	@Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(GalleryActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
        		doFinish();
            }


        });
	}

	@Override
	public void onBackPressed() {
		doFinish();
	}

	private void doFinish() {
		finish();
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	}
	
}
