package org.tint.utils;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Utility class to manage common Animation objects.
 */
public class AnimationUtils {

	private static final int ANIMATION_DURATION = 150;
	
	private static Animation mTopBarShowAnimation = null;
	private static Animation mTopBarHideAnimation = null;
	
	private static Animation mBottomBarShowAnimation = null;
	private static Animation mBottomBarHideAnimation = null;		
	
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
			
			mTopBarShowAnimation.setDuration(ANIMATION_DURATION);
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
			
			mTopBarHideAnimation.setDuration(ANIMATION_DURATION);
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
			
			mBottomBarShowAnimation.setDuration(ANIMATION_DURATION);
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
			
			mBottomBarHideAnimation.setDuration(ANIMATION_DURATION);
		}
		
		return mBottomBarHideAnimation;
	}
}
