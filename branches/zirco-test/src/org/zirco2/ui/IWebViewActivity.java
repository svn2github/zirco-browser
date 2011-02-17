package org.zirco2.ui;

public interface IWebViewActivity {
	
	int addTab(String url);
	
	int addTab(int tabIndex, String url);
	
	int getCurrentWebViewIndex();

}
