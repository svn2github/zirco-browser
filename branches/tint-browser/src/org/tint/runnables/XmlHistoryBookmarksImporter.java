package org.tint.runnables;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.tint.R;
import org.tint.controllers.BookmarksHistoryController;
import org.tint.utils.ApplicationUtils;
import org.tint.utils.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class XmlHistoryBookmarksImporter implements Runnable {	
	
	private Activity mActivity;
	private String mFileName;
	
	private ProgressDialog mProgressDialog;
	
	private String mErrorMessage = null;
	
	public XmlHistoryBookmarksImporter(Activity activity, String fileName, ProgressDialog progressDialog) {
		mActivity = activity;
		mFileName = fileName;
		mProgressDialog = progressDialog;
	}
	
	/**
	 * Get the content of a node, why Android does not include Node.getTextContent() ?
	 * @param node The node.
	 * @return The node content.
	 */
	private String getNodeContent(Node node) {
		StringBuffer buffer = new StringBuffer();
		NodeList childList = node.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
		    Node child = childList.item(i);
		    if (child.getNodeType() != Node.TEXT_NODE) {
		        continue; // skip non-text nodes
		    }
		    buffer.append(child.getNodeValue());
		}

		return buffer.toString(); 
	}
	
	@Override
	public void run() {
		
		File file = new File(IOUtils.getBookmarksExportFolder(), mFileName);
		
		if ((file != null) &&
				(file.exists()) &&
				(file.canRead())) {
			
			try {
			
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();				
				DocumentBuilder builder;

				builder = factory.newDocumentBuilder();

				Document document = builder.parse(file);
				
				Element root = document.getDocumentElement();
				
				if ((root != null) &&
						(root.getNodeName().equals("itemlist"))) {
					
					NodeList itemsList = root.getElementsByTagName("item");
					
					Node item;
					NodeList record;
					Node dataItem;
					
					for (int i = 0; i < itemsList.getLength(); i++) {
						
						item = itemsList.item(i);
						
						if (item != null) {
							record = item.getChildNodes();
							
							String title = null;
							String url = null;
							int visits = 0;
							long date = -1;
							long created = -1;
							int bookmark = 0;
							
							for (int j = 0; j < record.getLength(); j++) {
								dataItem = record.item(j);																
								
								if ((dataItem != null) &&
										(dataItem.getNodeName() != null)) {
									
									if (dataItem.getNodeName().equals("title")) {
										title = URLDecoder.decode(getNodeContent(dataItem));										
									} else if (dataItem.getNodeName().equals("url")) {
										url = URLDecoder.decode(getNodeContent(dataItem));
									} else if (dataItem.getNodeName().equals("visits")) {
										try {
											visits = Integer.parseInt(getNodeContent(dataItem));
										} catch (Exception e) {
											visits = 0;
										}
									} else if (dataItem.getNodeName().equals("date")) {
										try {
											date = Long.parseLong(getNodeContent(dataItem));
										} catch (Exception e) {
											date = -1;
										}
									} else if (dataItem.getNodeName().equals("created")) {
										try {
											created = Long.parseLong(getNodeContent(dataItem));
										} catch (Exception e) {
											created = -1;
										}
									} else if (dataItem.getNodeName().equals("bookmark")) {
										try {
											bookmark = Integer.parseInt(getNodeContent(dataItem));
										} catch (Exception e) {
											bookmark = 0;
										}
									}																									
								}																								
							}
							
							BookmarksHistoryController.getInstance().insertRawRecord(mActivity, title, url, visits, date, created, bookmark);
						}
					}
					
				}
			
			} catch (ParserConfigurationException e) {
				Log.w("Bookmark import failed", e.getMessage());
				mErrorMessage = e.toString();
			} catch (SAXException e) {
				Log.w("Bookmark import failed", e.getMessage());
				mErrorMessage = e.toString();
			} catch (IOException e) {
				Log.w("Bookmark import failed", e.getMessage());
				mErrorMessage = e.toString();
			}			
		}
		
		mHandler.sendEmptyMessage(0);
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
			
			if (mErrorMessage != null) {
				ApplicationUtils.showOkDialog(mActivity,
						android.R.drawable.ic_dialog_alert,
						mActivity.getResources().getString(R.string.Commons_HistoryBookmarksImportSDCardFailedTitle),
						String.format(mActivity.getResources().getString(R.string.Commons_HistoryBookmarksFailedMessage), mErrorMessage));
				
			}
		}
	};

}
