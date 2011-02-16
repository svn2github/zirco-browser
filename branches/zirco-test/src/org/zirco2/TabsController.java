package org.zirco2;

import java.util.ArrayList;
import java.util.List;

public class TabsController {
	
	private List<WebViewContainer> mWebViewContainers;
	
	/**
	 * Holder for singleton implementation.
	 */
	private static final class TabsControllerHolder {
		private static final TabsController INSTANCE = new TabsController();
		/**
		 * Private Constructor.
		 */
		private TabsControllerHolder() { }
	}
	
	/**
	 * Get the unique instance of the Controller.
	 * @return The instance of the Controller
	 */
	public static TabsController getInstance() {
		return TabsControllerHolder.INSTANCE;
	}	
	
	/**
	 * Private Constructor.
	 */
	private TabsController() {
		mWebViewContainers = new ArrayList<WebViewContainer>();
	}
	
	public List<WebViewContainer> getWebViewContainers() {
		return mWebViewContainers;
	}
	
	public int addWebViewContainer(WebViewContainer webViewContainer) {
		mWebViewContainers.add(webViewContainer);
		return mWebViewContainers.indexOf(webViewContainer);
	}

}
