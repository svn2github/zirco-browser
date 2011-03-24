package org.tint.model;

public class DownloadItem {
	
	private long mId;
	private String mUrl;
	private String mDestinationFileName;
	
	public DownloadItem(String url, String destinationFileName) {
		mId = -1;
		mUrl = url;
		mDestinationFileName = destinationFileName;
	}
	
	public long getId() {
		return mId;
	}
	
	public void setId(long value) {
		mId = value;
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public String getDestinationFileName() {
		return mDestinationFileName;
	}

}
