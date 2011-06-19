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
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class DownloadService extends Service {

	private DownloadServiceBinder mBinder;
	
	private String mFileName;
	private String mUrl;
	private String mErrorMessage = null;
	
	private int mProgress = 0;
	
	private DownloadRunnable2 mRunnable = null;
	
	private Notification mNotification = null;
	private int mNotificationId;
	private NotificationManager mNotificationManager;
	
	private Random mRandom;
	
	@Override
	public void onCreate() {
		mBinder = new DownloadServiceBinder(this);
		
		mRandom = new Random();
		mNotificationId = mRandom.nextInt();
		
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onDestroy() {
		mBinder = null;
		
		Log.d("DownloadService", "In onDestroy()");
		// TODO: clear all notifications.
		super.onDestroy();		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public String getFileName() {
		return mFileName;
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public void setErrorMessage(String message) {
		mErrorMessage = message;
	}

	public void onDownloadProgress(int progress) {
		mProgress = progress;
		
		mNotification.contentView.setProgressBar(R.id.DownloadProgress, 100, mProgress, false);
		mNotification.contentView.setTextViewText(R.id.DownloadProgressText, String.format("%s%%", mProgress));
		mNotificationManager.notify(mNotificationId, mNotification);
	}
	
	public void onDownloadEnd() {
		stopService(new Intent(this, DownloadService.class));
		
		mNotificationId = mRandom.nextInt();
		
		String message;
		if (mRunnable.isAborted()) {
			message = getString(R.string.DownloadNotification_DownloadCanceled);
		} else {
			message = getString(R.string.DownloadNotification_DownloadComplete);
		}
		
		mNotification = new Notification(R.drawable.stat_sys_download, getString(R.string.DownloadNotification_DownloadComplete), System.currentTimeMillis());
		
		Intent notificationIntent = new Intent(getApplicationContext(), DownloadsListActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
		
		mNotification.setLatestEventInfo(getApplicationContext(), mFileName, message, contentIntent);
		
		mNotificationManager.notify(mNotificationId, mNotification);
	}
	
	private void startDownloadThread() {
		if (mRunnable != null) {
			mRunnable.abort();
		}
		mRunnable = new DownloadRunnable2(this);
		new Thread(mRunnable).start();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		mUrl = intent.getStringExtra("url");
		mFileName = mUrl.substring(mUrl.lastIndexOf("/") + 1);
		
		startDownloadThread();
		
		mNotification = new Notification(R.drawable.download_anim, mFileName, System.currentTimeMillis());
		this.
		
		mNotification.flags = mNotification.flags | Notification.FLAG_NO_CLEAR;
		
		RemoteViews contentView = new RemoteViews(this.getPackageName(), R.layout.download_notification);
		contentView.setTextViewText(R.id.DownloadFileNameText, mFileName);
		contentView.setTextViewText(R.id.DownloadUrlText, mUrl);		
		contentView.setTextViewText(R.id.DownloadProgressText, String.format("%s%%", 0));
		contentView.setProgressBar(R.id.DownloadProgress, 100, 0, false);
		
		mNotification.contentView = contentView;
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, DownloadsListActivity.class), 0);

		mNotification.contentIntent = contentIntent;

		startForeground(mNotificationId, mNotification);
		
		return START_STICKY;
	}

}
