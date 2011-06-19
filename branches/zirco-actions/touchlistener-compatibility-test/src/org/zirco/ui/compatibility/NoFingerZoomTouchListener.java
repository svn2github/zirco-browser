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

import android.view.MotionEvent;
import android.view.View;

public class NoFingerZoomTouchListener extends BaseTouchListener {

	public NoFingerZoomTouchListener(ZircoMain activity) {
		super(activity);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		hideKeyboard();
		
		final int action = event.getAction();
		
		switch (action) {
        case MotionEvent.ACTION_DOWN: {
                
                mGestureMode = GestureMode.SWIP;
                
                // store the X value when the user's finger was pressed down
                mDownXValue = event.getX();
                mDownDateValue = System.currentTimeMillis();
                
                if (mDownDateValue - mLastDownTimeForDoubleTap < 250) {
                        zoomIn();
                        mLastDownTimeForDoubleTap = -1;
                } else {
                        mLastDownTimeForDoubleTap = mDownDateValue;
                }
                
                break;
        }

        case MotionEvent.ACTION_UP: {
                
                if (mGestureMode == GestureMode.SWIP) {
                
                        // Get the X value when the user released his/her finger
                        float currentX = event.getX();
                        long timeDelta = System.currentTimeMillis() - mDownDateValue;

                        if (timeDelta <= FLIP_TIME_THRESHOLD) {
                                if (currentWebViewHasMultipleTabs()) {
                                        // going backwards: pushing stuff to the right
                                        if (currentX > (mDownXValue + FLIP_PIXEL_THRESHOLD)) {                                          

                                                showPreviousTab();
                                                return false;
                                        }

                                        // going forwards: pushing stuff to the left
                                        if (currentX < (mDownXValue - FLIP_PIXEL_THRESHOLD)) {                                  

                                                showNextTab();
                                                return false;
                                        }
                                }
                        }
                }
                break;
        }
                
        default: break;
        }

		return false;
	}	

}
