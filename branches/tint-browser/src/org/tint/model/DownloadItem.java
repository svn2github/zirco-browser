package org.tint.model;

/**
 * Represent a download.
 */
public class DownloadItem {
	
	private long mId;
	private String mUrl;
	private String mDestinationFileName;
	
	/**
	 * Constructor.
	 * @param url The url to download.
	 * @param destinationFileName The destination filename (without path).
	 */
	public DownloadItem(String url, String destinationFileName) {
		mId = -1;
		mUrl = url;
		mDestinationFileName = destinationFileName;
	}
	
	/**
	 * Get the download id.
	 * @return The download id.
	 */
	public long getId() {
		return mId;
	}
	
	/**
	 * Set the download id.
	 * @param value The download id.
	 */
	public void setId(long value) {
		mId = value;
	}
	
	/**
	 * Get the download url.
	 * @return The download url.
	 */
	public String getUrl() {
		return mUrl;
	}
	
	/**
	 * Get the destination filename (without path).
	 * @return The destination filename.
	 */
	public String getDestinationFileName() {
		return mDestinationFileName;
	}

}
