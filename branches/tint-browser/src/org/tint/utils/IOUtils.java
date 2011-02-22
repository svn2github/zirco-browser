package org.tint.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Environment;

/**
 * Helpers from read/write to SD card.
 */
public class IOUtils {
	
	private static final String APPLICATION_FOLDER_1 = "Android";
	private static final String APPLICATION_FOLDER_2 = "data";
	private static final String APPLICATION_FOLDER_3 = "org.tint";
	private static final String BOOKMARKS_EXPORT_FOLDER = "bookmarks-exports";
	
	/**
	 * Get the application folder on the SD Card. Create it if not present.
	 * @return The application folder.
	 */
	public static File getApplicationFolder() {
		File root = Environment.getExternalStorageDirectory();
		if (root.canWrite()) {
			
			File folder1 = new File(root, APPLICATION_FOLDER_1);			
			if (!folder1.exists()) {
				folder1.mkdir();
			}
			
			File folder2 = new File(folder1, APPLICATION_FOLDER_2);
			if (!folder2.exists()) {
				folder2.mkdir();
			}
			
			File folder3 = new File(folder2, APPLICATION_FOLDER_3);
			if (!folder3.exists()) {
				folder3.mkdir();
			}
			
			return folder3;
			
		} else {
			return null;
		}
	}
	
	/**
	 * Get the application folder for bookmarks export. Create it if not present.
	 * @return The application folder for bookmarks export.
	 */
	public static File getBookmarksExportFolder() {
		File root = getApplicationFolder();
		
		if (root != null) {
			
			File folder = new File(root, BOOKMARKS_EXPORT_FOLDER);
			
			if (!folder.exists()) {
				folder.mkdir();
			}
			
			return folder;
			
		} else {
			return null;
		}
	}
	
	/**
	 * Get a string representation of the current date / time in a format suitable for a file name.
	 * @return A string representation of the current date / time.
	 */
	public static String getNowForFileName() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		
		return sdf.format(c.getTime());
	}

}
