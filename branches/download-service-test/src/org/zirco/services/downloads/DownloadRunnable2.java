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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.zirco.utils.IOUtils;

/**
 * Background downloader.
 */
public class DownloadRunnable2 implements Runnable {
			
	private static final int BUFFER_SIZE = 4096;
	
	private DownloadService mParent;
	
	private boolean mAborted;
	
	/**
	 * Contructor.
	 * @param parent The parent download service.
	 */
	public DownloadRunnable2(DownloadService parent) {
		mParent = parent;
		mAborted = false;		
	}
	
	public boolean isAborted() {
		return mAborted;
	}
	
	/**
	 * Compute the file name given the url.
	 * @return The file name.
	 */
	private String getFileNameFromUrl() {
		return mParent.getFileName();
	}
	
	/**
	 * Get a file object representation of the file name, in th right folder of the SD card.
	 * @return A file object.
	 */
	private File getFile() {
		
		File downloadFolder = IOUtils.getDownloadFolder();
		
		if (downloadFolder != null) {
			
			return new File(downloadFolder, getFileNameFromUrl());
			
		} else {
			mParent.setErrorMessage("Unable to get download folder from SD Card.");			
			return null;
		}				
	}
	
	@Override
	public void run() {
		File downloadFile = getFile();
		
		if (downloadFile != null) {
			
			if (downloadFile.exists()) {
				downloadFile.delete();
			}
			
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			
			try {
				
				//mParent.onStart();
				
				URL url = new URL(mParent.getUrl());
				URLConnection conn = url.openConnection();
				
				InputStream is = conn.getInputStream();
							
				int size = conn.getContentLength();
				
				double oldCompleted = 0;
				double completed = 0;
				
				bis = new BufferedInputStream(is);
				bos = new BufferedOutputStream(new FileOutputStream(downloadFile));
				
				boolean downLoading = true;
				byte[] buffer = new byte[BUFFER_SIZE];
				int downloaded = 0;
				int read;
				int stepRead = 0;
				
				while ((downLoading) &&
						(!mAborted)) {

					if (size - downloaded < BUFFER_SIZE) {
						buffer = new byte[size - downloaded];
					}

					read = bis.read(buffer);
					
					if (read > 0) {
						bos.write(buffer, 0, read);
						downloaded += read;
						
						completed = ((downloaded * 100f) / size);
						
						stepRead++;
					} else {
						downLoading = false;
					}
					
					// Notify each 5% or more.
					if (oldCompleted + 5 < completed) {
						mParent.onDownloadProgress((int) completed);
						oldCompleted = completed;
					}
				}

			} catch (MalformedURLException mue) {
				mParent.setErrorMessage(mue.getMessage());
			} catch (IOException ioe) {
				mParent.setErrorMessage(ioe.getMessage());
			} finally {
				
				if (bis != null) {
					try {
						bis.close();
					} catch (IOException ioe) {
						mParent.setErrorMessage(ioe.getMessage());
					}
				}
				if (bos != null) {
					try {
						bos.close();
					} catch (IOException ioe) {
						mParent.setErrorMessage(ioe.getMessage());
					}
				}							
			}
		
			if (mAborted) {
				if (downloadFile.exists()) {
					downloadFile.delete();
				}
			}
			
		} 
		
		mParent.onDownloadEnd();
	}
	
	/**
	 * Abort this download.
	 */
	public void abort() {
		mAborted = true;
	}

}
