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

package org.zirco.services.downloads;

import java.util.Random;

import org.zirco.R;
import org.zirco.ui.activities.DownloadsListActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Represent a download item.
 */
public class DownloadItem2 {
	
	private DownloadService mParent;
	private Context mContext;
	
	private String mUrl;
	private String mFileName;
	
	private int mProgress;
	
	private String mErrorMessage;
	
	private DownloadRunnable2 mRunnable;
	
	private boolean mIsFinished;
	private boolean mIsAborted;
	
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private int mNotificationId;
	
	private boolean mUseNotification;
	
	/**
	 * Constructor.
	 * @param context The current context.
	 * @param url The download url.
	 */
	public DownloadItem2(DownloadService parent, String url, boolean useNotification) {
		
		mParent = parent;
		mContext = mParent;
		
		mUseNotification = useNotification;
		
		mUrl = url;
		mFileName = mUrl.substring(mUrl.lastIndexOf("/") + 1);
		
		mProgress = 0;
	
		mRunnable = null;
		mErrorMessage = null;
		
		mIsFinished = false;
		mIsAborted = false;
		
		Random r = new Random();
		mNotificationId = r.nextInt();
		mNotification = null;
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	/**
	 * Gets the download url.
	 * @return The download url.
	 */
	public String getUrl() {
		return mUrl;
	}
	
	/**
	 * Gets the filename on disk.
	 * @return The filename on disk.
	 */
	public String getFileName() {
		return mFileName;
	}
	
	/**
	 * Gets the download progress.
	 * @return The download progress.
	 */
	public int getProgress() {
		return mProgress;
	}
	
	/**
	 * Set the current error message for this download.
	 * @param errorMessage The error message.
	 */
	public void setErrorMessage(String errorMessage) {
		mErrorMessage = errorMessage;
	}
	
	/**
	 * Gets the error message for this download.
	 * @return The error message.
	 */
	public String getErrorMessage() {
		return mErrorMessage;
	}
	
	/**
	 * Trigger a start download event.
	 */
	public void onStart() {
		if (mUseNotification) {
			createAndUpdateNotification();
		}
	}
	
	/**
	 * Set this item is download finished state. Trigger a finished download event.
	 */
	public void onFinished() {
		mProgress = 100;
		mRunnable = null;
		
		mIsFinished = true;
		
		mParent.onDownloadEnd();
		
		if (mUseNotification) {
			updateNotificationOnEnd();
		}
	}
	
	/**
	 * Set the current progress. Trigger a progress download event.
	 * @param progress The current progress.
	 */
	public void onProgress(int progress) {
		mProgress = progress;
		
		mParent.onDownloadProgress(mProgress);
		
		if (mUseNotification) {
			createAndUpdateNotification();
		}
	}
	
	/**
	 * Start the current download.
	 */
	public void startDownload() {
		if (mRunnable != null) {
			mRunnable.abort();
		}
		//mRunnable = new DownloadRunnable2(this);
		new Thread(mRunnable).start();
	}
	
	/**
	 * Abort the current download.
	 */
	public void abortDownload() {
		if (mRunnable != null) {
			mRunnable.abort();
		}
		mIsAborted = true;
	}
	
	/**
	 * Check if the download is finished.
	 * @return True if the download is finished.
	 */
	public boolean isFinished() {
		return mIsFinished;
	}
	
	/**
	 * Check if the download is aborted.
	 * @return True if the download is aborted.
	 */
	public boolean isAborted() {
		return mIsAborted;
	}
	
	/**
	 * Create the download notification.
	 */
	private void createNotification() {
		mNotification = new Notification(R.drawable.download_anim, mFileName, System.currentTimeMillis());		
		
		mNotification.flags = mNotification.flags | Notification.FLAG_NO_CLEAR;
		
		RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.download_notification);
		contentView.setTextViewText(R.id.DownloadFileNameText, mFileName);
		contentView.setTextViewText(R.id.DownloadUrlText, mUrl);		
		contentView.setTextViewText(R.id.DownloadProgressText, String.format("%s%%", 0));
		contentView.setProgressBar(R.id.DownloadProgress, 100, 0, false);
		
		mNotification.contentView = contentView;
		
		Intent notificationIntent = new Intent(mContext.getApplicationContext(), DownloadsListActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext.getApplicationContext(), 0, notificationIntent, 0);
		mNotification.contentIntent = contentIntent;
		
		mNotificationManager.notify(mNotificationId, mNotification);
	}

	/**
	 * Update the download notification.
	 */
	private void updateNotification() {
		mNotification.contentView.setProgressBar(R.id.DownloadProgress, 100, mProgress, false);
		mNotification.contentView.setTextViewText(R.id.DownloadProgressText, String.format("%s%%", mProgress));
		mNotificationManager.notify(mNotificationId, mNotification);
	}
	
	/**
	 * Update the download notification at the end of download.
	 */
	private void updateNotificationOnEnd() {
		if (mNotification != null) {
			mNotificationManager.cancel(mNotificationId);
			
			String message;
			if (mIsAborted) {
				message = mContext.getString(R.string.DownloadNotification_DownloadCanceled);
			} else {
				message = mContext.getString(R.string.DownloadNotification_DownloadComplete);
			}
			
			mNotification = new Notification(R.drawable.stat_sys_download, mContext.getString(R.string.DownloadNotification_DownloadComplete), System.currentTimeMillis());
			
			Intent notificationIntent = new Intent(mContext.getApplicationContext(), DownloadsListActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(mContext.getApplicationContext(), 0, notificationIntent, 0);
			
			mNotification.setLatestEventInfo(mContext.getApplicationContext(), mFileName, message, contentIntent);
			
			mNotificationManager.notify(mNotificationId, mNotification);
		}
	}
	
	/**
	 * Update the download notification. Create it if it does not exists.
	 */
	private void createAndUpdateNotification() {
		if (mNotification == null) {
			createNotification();
		}
		updateNotification();
	}

}
