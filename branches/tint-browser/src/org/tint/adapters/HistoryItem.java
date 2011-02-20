package org.tint.adapters;

/**
 * Represent an history element.
 */
public class HistoryItem {

	private long mId;
	private String mTitle;
	private String mUrl;

	/**
	 * Constructor.
	 * @param id The element id.
	 * @param title The title.
	 * @param url The url.
	 */
	public HistoryItem(long id, String title, String url) {
		mId = id;
		mTitle = title;
		mUrl = url;
	}

	/**
	 * Get the id.
	 * @return The id.
	 */
	public long getId() {
		return mId;
	}

	/**
	 * Get the title.
	 * @return The title.
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * Get the url.
	 * @return The url.
	 */
	public String getUrl() {
		return mUrl;
	}
	
}
