package org.tint.utils;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Environment;

/**
 * Helpers from read/write to SD card.
 */
public class IOUtils {
	
	private static final String APPLICATION_FOLDER = "tint-browser";
	private static final String BOOKMARKS_EXPORT_FOLDER = "bookmarks-exports";
	
	/**
	 * Create the download folder if it does not exists.
	 */
	public static void createDownloadFolderIfRequired() {
		File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		if (!root.exists()) {
			root.mkdirs();
		}
	}
	
	/**
	 * Delete the file with the given filename from the download folder if it exists.
	 * @param fileName The filename.
	 */
	public static void deleteFileInDownloadFolderIfPresent(String fileName) {
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
		if (file.exists()) {
			file.delete();
		}
	}
	
	/**
	 * Get the application folder on the SD Card. Create it if not present.
	 * @return The application folder.
	 */
	public static File getApplicationFolder() {
		File root = Environment.getExternalStorageDirectory();
		if (root.canWrite()) {
			
			File folder = new File(root, APPLICATION_FOLDER);			
			if (!folder.exists()) {
				folder.mkdir();
			}
			
			return folder;
			
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
	 * Get the list of xml files in the bookmark export folder.
	 * @return The list of xml files in the bookmark export folder.
	 */
	public static List<String> getExportedBookmarksFileList() {
		List<String> result = new ArrayList<String>();
		
		File folder = getBookmarksExportFolder();		
		
		if (folder != null) {
			
			FileFilter filter = new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					if ((pathname.isFile()) &&
							(pathname.getPath().endsWith(".xml"))) {
						return true;
					}
					return false;
				}
			};
			
			File[] files = folder.listFiles(filter);
			
			for (File file : files) {
				result.add(file.getName());
			}
			
		}
		
		Collections.sort(result, new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {				
				return arg1.compareTo(arg0);
			}    		
    	});
		
		return result;
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
