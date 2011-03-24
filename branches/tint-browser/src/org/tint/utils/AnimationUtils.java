package org.tint.utils;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Utility class to manage common Animation objects.
 */
public class AnimationUtils {

	private static final int BARS_ANIMATION_DURATION = 150;
	private static final int FLIPPER_ANIMATION_DURATION = 350;
	
	private static Animation mTopBarShowAnimation = null;
	private static Animation mTopBarHideAnimation = null;
	
	private static Animation mBottomBarShowAnimation = null;
	private static Animation mBottomBarHideAnimation = null;	
	
	private static Animation mInFromRightAnimation = null;
	private static Animation mOutToLeftAnimation = null;
	
	private static Animation mInFromLeftAnimation = null;
	private static Animation mOutToRightAnimation = null;
	
	/**
	 * Get the show animation of the top bar.
	 * @return The animation.
	 */
	public static Animation getTopBarShowAnimation() {
		if (mTopBarShowAnimation == null) {
			mTopBarShowAnimation = new TranslateAnimation(
        			Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
        			Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        	);
			
			mTopBarShowAnimation.setDuration(BARS_ANIMATION_DURATION);
		}
		
		return mTopBarShowAnimation;
	}
	
	/**
	 * Get the hide animation of the top bar.
	 * @return The animation.
	 */
	public static Animation getTopBarHideAnimation() {
		if (mTopBarHideAnimation == null) {
			mTopBarHideAnimation = new TranslateAnimation(
        			Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
        			Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f
        	);
			
			mTopBarHideAnimation.setDuration(BARS_ANIMATION_DURATION);
		}
		
		return mTopBarHideAnimation;
	}
	
	/**
	 * Get the show animation of the bottom bar.
	 * @return The animation.
	 */
	public static Animation getBottomBarShowAnimation() {
		if (mBottomBarShowAnimation == null) {
			mBottomBarShowAnimation = new TranslateAnimation(
        			Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
        			Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        	);
			
			mBottomBarShowAnimation.setDuration(BARS_ANIMATION_DURATION);
		}
		
		return mBottomBarShowAnimation;
	}

	/**
	 * Get the hide animation of the bottom bar.
	 * @return The animation.
	 */
	public static Animation getBottomBarHideAnimation() {
		if (mBottomBarHideAnimation == null) {
			mBottomBarHideAnimation = new TranslateAnimation(
        			Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
        			Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f
        	);
			
			mBottomBarHideAnimation.setDuration(BARS_ANIMATION_DURATION);
		}
		
		return mBottomBarHideAnimation;
	}
	
	/**
	 * Get the "in" animation from right for the ViewFlipper.
	 * @return The animation.
	 */
	public static Animation getInFromRightAnimation() {
		if (mInFromRightAnimation == null) {
			mInFromRightAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT,
					0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);

			mInFromRightAnimation.setDuration(FLIPPER_ANIMATION_DURATION);
			mInFromRightAnimation.setInterpolator(new AccelerateInterpolator());
		}
		
		return mInFromRightAnimation;
	}
	
	/**
	 * Get the "out" animation to left for the ViewFlipper.
	 * @return The animation.
	 */
	public static Animation getOutToLeftAnimation() {
		if (mOutToLeftAnimation == null) {
			mOutToLeftAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, -1.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f);

			mOutToLeftAnimation.setDuration(FLIPPER_ANIMATION_DURATION);
			mOutToLeftAnimation.setInterpolator(new AccelerateInterpolator());
		}
		
		return mOutToLeftAnimation;
	}
	
	/**
	 * Get the "in" animation from left for the ViewFlipper.
	 * @return The animation.
	 */
	public static Animation getInFromLeftAnimation() {
		if (mInFromLeftAnimation == null) {
			mInFromLeftAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f);

			mInFromLeftAnimation.setDuration(FLIPPER_ANIMATION_DURATION);
			mInFromLeftAnimation.setInterpolator(new AccelerateInterpolator());
		}
		
		return mInFromLeftAnimation;
	}
	
	/**
	 * Get the "out" animation to right for the ViewFlipper.
	 * @return The animation.
	 */
	public static Animation getOutToRightAnimation() {
		if (mOutToRightAnimation == null) {
			mOutToRightAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, +1.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f);

			mOutToRightAnimation.setDuration(FLIPPER_ANIMATION_DURATION);
			mOutToRightAnimation.setInterpolator(new AccelerateInterpolator());
		}
		
		return mOutToRightAnimation;
	}
}
