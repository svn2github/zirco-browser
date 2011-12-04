/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 - 2011 J. Devauchelle and contributors.
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

package org.zirco.ui.components;

import java.util.ArrayList;
import java.util.List;

import org.zirco.R;
import org.zirco.ui.activities.MainActivity;
import org.zirco.utils.Constants;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.widget.RelativeLayout;

public class CustomPagerAdapter extends PagerAdapter {
	
	private MainActivity mParentActivity;
	private CustomViewPager mViewPager;
	private LayoutInflater mInflater;
	
	private List<Tab> mTabs;

	public CustomPagerAdapter(MainActivity parentActivity, CustomViewPager viewPager) {
		super();
		mParentActivity = parentActivity;
		mViewPager = viewPager;
		mInflater = (LayoutInflater) mParentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mTabs = new ArrayList<Tab>();
	}
	
	public int addPage(int position) {
		RelativeLayout view = (RelativeLayout) mInflater.inflate(R.layout.webview, mViewPager, false);
		CustomWebView webView = (CustomWebView) view.findViewById(R.id.webview);	
		
		webView = initializeWebView(webView);
		
		Tab tab = new Tab(view, webView);
		
		if (position == -1) {
			mTabs.add(tab);
		} else {
			mTabs.add(position, tab);
		}
		
		int index = mTabs.indexOf(tab);			
		
		notifyDataSetChanged();
		
		return index;
	}
	
	public void removePage(int index) {
		if (mViewPager.getChildCount() > 1) {
			int currentIndex = mParentActivity.getCurrentDisplayedViewIndex();
			
			if (index == currentIndex) {
				if (currentIndex > 0) {
					currentIndex--;
					mViewPager.setCurrentItem(currentIndex);
				}
			}
			
			Tab currentTab = mTabs.get(index);
			
			mViewPager.removeView(currentTab.getParentView());
			mTabs.remove(index);
			
			notifyDataSetChanged();
		}
	}
	
	@Override
	public Object instantiateItem(View collection, int position) {
		View view = mTabs.get(position).getParentView();
		((ViewPager) collection).addView(view, 0);
		
		return view;
	}
	
	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}
	
	@Override
	public int getCount() {
		return mTabs.size();
	}
	
	@Override
	public int getItemPosition(Object object) {
		// Seems to be required as a workaround.
	    return POSITION_NONE;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}
	
	@Override
	public void finishUpdate(View arg0) { }

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) { }

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) { }
	
	public CustomWebView getWebViewAtIndex(int index) {
		Tab tab = mTabs.get(index);
		
		if (tab != null) {
			return tab.getWebView();
		} else {
			return null;
		}
	}
	
	private CustomWebView initializeWebView(CustomWebView webView) {
		
		webView.setOnTouchListener(mParentActivity);
		
		webView.setWebViewClient(new CustomWebViewClient(mParentActivity));
		webView.setWebChromeClient(new CustomWebChromeClient(mParentActivity, webView));
		
		webView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				HitTestResult result = ((WebView) v).getHitTestResult();
				
				int resultType = result.getType();
				if ((resultType == HitTestResult.ANCHOR_TYPE) ||
						(resultType == HitTestResult.IMAGE_ANCHOR_TYPE) ||
						(resultType == HitTestResult.SRC_ANCHOR_TYPE) ||
						(resultType == HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
					
					Intent i = new Intent();
					i.putExtra(Constants.EXTRA_ID_URL, result.getExtra());
					
					MenuItem item = menu.add(0, MainActivity.CONTEXT_MENU_OPEN, 0, R.string.Main_MenuOpen);
					item.setIntent(i);
	
					item = menu.add(0, MainActivity.CONTEXT_MENU_OPEN_IN_NEW_TAB, 0, R.string.Main_MenuOpenNewTab);					
					item.setIntent(i);
					
					item = menu.add(0, MainActivity.CONTEXT_MENU_COPY, 0, R.string.Main_MenuCopyLinkUrl);					
					item.setIntent(i);
					
					item = menu.add(0, MainActivity.CONTEXT_MENU_DOWNLOAD, 0, R.string.Main_MenuDownload);					
					item.setIntent(i);
					
					item = menu.add(0, MainActivity.CONTEXT_MENU_SHARE, 0, R.string.Main_MenuShareLinkUrl);					
					item.setIntent(i);
				
					menu.setHeaderTitle(result.getExtra());					
				} else if (resultType == HitTestResult.IMAGE_TYPE) {
					Intent i = new Intent();
					i.putExtra(Constants.EXTRA_ID_URL, result.getExtra());
					
					MenuItem item = menu.add(0, MainActivity.CONTEXT_MENU_OPEN, 0, R.string.Main_MenuViewImage);					
					item.setIntent(i);
					
					item = menu.add(0, MainActivity.CONTEXT_MENU_COPY, 0, R.string.Main_MenuCopyImageUrl);					
					item.setIntent(i);
					
					item = menu.add(0, MainActivity.CONTEXT_MENU_DOWNLOAD, 0, R.string.Main_MenuDownloadImage);					
					item.setIntent(i);	
					
					item = menu.add(0, MainActivity.CONTEXT_MENU_SHARE, 0, R.string.Main_MenuShareImageUrl);					
					item.setIntent(i);
					
					menu.setHeaderTitle(result.getExtra());
					
				} else if (resultType == HitTestResult.EMAIL_TYPE) {
					
					Intent sendMail = new Intent(Intent.ACTION_VIEW, Uri.parse(WebView.SCHEME_MAILTO + result.getExtra()));
					
					MenuItem item = menu.add(0, MainActivity.CONTEXT_MENU_SEND_MAIL, 0, R.string.Main_MenuSendEmail);					
					item.setIntent(sendMail);										
					
					Intent i = new Intent();
					i.putExtra(Constants.EXTRA_ID_URL, result.getExtra());
					
					item = menu.add(0, MainActivity.CONTEXT_MENU_COPY, 0, R.string.Main_MenuCopyEmailUrl);					
					item.setIntent(i);		
					
					item = menu.add(0, MainActivity.CONTEXT_MENU_SHARE, 0, R.string.Main_MenuShareEmailUrl);					
					item.setIntent(i);
					
					menu.setHeaderTitle(result.getExtra());
				}
			}    		
    	});
		
		webView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				mParentActivity.doDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength);
			}
    		
    	});
		
		return webView;
	}
	
	private class Tab {
		private View mParentView;
		private CustomWebView mWebView;
		
		public Tab(View parentView, CustomWebView webView) {
			mParentView = parentView;
			mWebView = webView;
		}
		
		public View getParentView() {
			return mParentView;
		}
		
		public CustomWebView getWebView() {
			return mWebView;
		}
	}

}
