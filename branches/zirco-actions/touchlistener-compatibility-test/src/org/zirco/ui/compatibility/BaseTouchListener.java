/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 J. Devauchelle and contributors.
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

package org.zirco.ui.compatibility;

import org.zirco.ui.activities.ZircoMain;

import android.os.Build;
import android.view.View.OnTouchListener;

public abstract class BaseTouchListener implements OnTouchListener {
	
	/**
	 * Gesture mode.
	 */
	protected enum GestureMode {
		SWIP,
		ZOOM
	}
	
	protected static final int FLIP_PIXEL_THRESHOLD = 200;
	protected static final int FLIP_TIME_THRESHOLD = 400;
	
	protected long mDownDateValue;
	protected float mDownXValue;
	
	protected long mLastDownTimeForDoubleTap = -1;
	
	protected GestureMode mGestureMode;
	
	private ZircoMain mMainActivity;
	
	public BaseTouchListener(ZircoMain activity) {
		mMainActivity = activity;
	}
	
	protected boolean currentWebViewHasMultipleTabs() {
		return mMainActivity.currentWebViewHasMultipleTabs();
	}
	
	protected void hideKeyboard() {
		mMainActivity.hideKeyboardNow();
	}
	
	protected void zoomIn() {
		mMainActivity.zoomIn();
	}
	
	protected void zoomOut() {
		mMainActivity.zoomOut();
	}
	
	protected void showPreviousTab() {
		mMainActivity.showPreviousTab();
	}
	
	protected void showNextTab() {
		mMainActivity.showNextTab();
	}
	
	public static BaseTouchListener newTouchListenerInstance(ZircoMain activity) {
		final int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
		
		if (sdkVersion <= Build.VERSION_CODES.ECLAIR) {
			return new NoFingerZoomTouchListener(activity);
		} else {
			return new FingerZoomTouchListener(activity);
		}
	}

}
