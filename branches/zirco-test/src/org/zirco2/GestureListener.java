package org.zirco2;

import android.app.Activity;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
	
	private Activity mActivity;
	
	public GestureListener(Activity context) {
		mActivity = context;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Intent i = new Intent(mActivity, GalleryActivity.class);		
		mActivity.startActivityForResult(i, Main.ACTIVITY_SHOW_TABS);
		mActivity.overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	}

}
