package org.tint.runnables;

import org.tint.ui.activities.MainActivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * A runnable to hide tool bars after the given delay.
 */
public class HideToolbarsRunnable implements Runnable {
	
	private static final String TAG = "HideToolbarsRunnable";
	
	private MainActivity mParent;
	private boolean mDisabled;
	private int mDelay;
	
	/**
	 * Constructor.
	 * @param parent The parent tool bar container.
	 * @param delay The delay before hiding, in milliseconds.
	 */
	public HideToolbarsRunnable(MainActivity parent, int delay) {
		mParent = parent;
		mDisabled = false;
		mDelay = delay;
	}
	
	private Handler mHandler = new Handler() {				
		
		public void handleMessage(Message msg) {
			if ((mParent != null) &&
					(!mDisabled)) {
				mParent.hideToolbars();
			}
		}
	};
	
	/**
	 * Disable this runnable.
	 */
	public void setDisabled() {
		mDisabled = true;
	}
	
	@Override
	public void run() {
		try {
			
			Thread.sleep(mDelay);
			
			mHandler.sendEmptyMessage(0);
			
		} catch (InterruptedException e) {
			Log.w(TAG, "Exception in thread: " + e.getMessage());
			
			mHandler.sendEmptyMessage(0);
		}
	}

}
