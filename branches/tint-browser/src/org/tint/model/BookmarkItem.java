package org.tint.model;

public class BookmarkItem {
	
	private String mTitle;
	private String mUrl;
	
	public BookmarkItem(String title, String url) {
		mTitle = title;
		mUrl = url;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public String getUrl() {
		return mUrl;
	}

}
