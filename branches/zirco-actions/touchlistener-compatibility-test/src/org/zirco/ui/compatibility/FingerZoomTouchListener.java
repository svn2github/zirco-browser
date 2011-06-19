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

import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

public class FingerZoomTouchListener extends BaseTouchListener {

	private float mOldDistance;
	
	public FingerZoomTouchListener(ZircoMain activity) {
		super(activity);		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		hideKeyboard();
		
		final int action = event.getAction();
		
		// Get the action that was done on this touch event
		//switch (event.getAction()) {
		switch (action & MotionEvent.ACTION_MASK) {
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
		
		case MotionEvent.ACTION_POINTER_DOWN: {
			
			mOldDistance = computeSpacing(event);
			
			if (mOldDistance > 10f) {
				mGestureMode = GestureMode.ZOOM;
			}
			
			break;
		}				
		
		case MotionEvent.ACTION_MOVE: {
			
			if (mGestureMode == GestureMode.ZOOM) {
			
				float newDist = computeSpacing(event);
				
				if (newDist > 10f) {
					
					float scale = newDist / mOldDistance;
					
					if (scale > 1) {
						
						if (scale > 1.3f) {
						
							zoomIn();							
							mOldDistance = newDist;
						}
						
					} else {
						
						if (scale < 0.8f) {
						
							zoomOut();
							mOldDistance = newDist;
						}
					}					
				}
			}		
			break;
		}
		default: break;
		}

        // if you return false, these actions will not be recorded
        return false;
	}
	
	/**
	 * Compute the distance between points of a motion event.
	 * @param event The event.
	 * @return The distance between the two points.
	 */
	private float computeSpacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

}
