package org.zirco2.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class ApplicationUtils {
	
	private static int mFaviconSize = -1;
	
	/**
	 * Get the required size of the favicon, depending on current screen density.
	 * @param activity The current activity.
	 * @return The size of the favicon, in pixels.
	 */
	public static int getFaviconSize(Activity activity) {
		if (mFaviconSize == -1) {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			switch (metrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW: mFaviconSize = 12; break;
			case DisplayMetrics.DENSITY_MEDIUM: mFaviconSize = 24; break;
			case DisplayMetrics.DENSITY_HIGH: mFaviconSize = 32; break;
			default: mFaviconSize = 24;
			}
		}
		
		return mFaviconSize;
	}

}
