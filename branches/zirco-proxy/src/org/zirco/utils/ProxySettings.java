package org.zirco.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;



import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class for setting WebKit proxy used by Android WebView
 *
 */
public class ProxySettings 
{
    public static String SystemProxyAddress;
    public static String SystemProxyPort;
    
    
    
    public static boolean isSystemProxyReachable(Context ctx)
    {
        return true;
    }
    
	public static boolean testSystemProxy(Context ctx)
	{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		ContentResolver cr = ctx.getContentResolver();
		String proxyString = Settings.Secure.getString(cr,Settings.Secure.HTTP_PROXY);
		 
		if (proxyString != null && proxyString != "" && proxyString.contains(":"))
		{      
		        String proxyAddress = proxyString.split(":")[0];
		        int proxyPort = Integer.parseInt(proxyString.split(":")[1]);
		        HttpHost proxy = new HttpHost(proxyAddress,proxyPort);
		        // And when you have it, it's child's play to execute an HTTP request
		        // passing through the proxy:
		        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		                       
		
		try {
			HttpGet request = new HttpGet("http://www.google.com");
			HttpResponse response = httpclient.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	
	public static boolean setSystemProxy(Context ctx)
	{
		return setProxy(ctx,"10.130.193.63",1973);
	}

    /**
     * Override WebKit Proxy settings
     *
     * @param ctx Android ApplicationContext
     * @param host
     * @param port
     * @return  true if Proxy was successfully set
     */
    public static boolean setProxy(Context ctx, String host, int port) {
        boolean ret = false;
        try 
        {
            Object requestQueueObject = getRequestQueue(ctx);
            if (requestQueueObject != null) 
            {
                //Create Proxy config object and set it into request Q
                HttpHost httpHost = new HttpHost(host, port, "http");
                setDeclaredField(requestQueueObject, "mProxyHost", httpHost);
                //Log.d("Webkit Setted Proxy to: " + host + ":" + port);
                
                
                
                ret = true;
            }
        } 
        catch (Exception e) 
        {
            //Logger.Exception("Webkit Set Proxy: ",e);
        }
        return ret;
    }

    public static void resetProxy(Context ctx) throws Exception 
    {
        Object requestQueueObject = getRequestQueue(ctx);
        if (requestQueueObject != null) 
        {
            setDeclaredField(requestQueueObject, "mProxyHost", null);
        }
    }

    public static Object GetNetworkInstance(Context ctx) throws ClassNotFoundException
    {
        Class networkClass = Class.forName("android.webkit.Network");
        return networkClass;
    }
    
    public static Object getRequestQueue(Context ctx) throws Exception 
    {
        Object ret = null;
        Object networkClass = GetNetworkInstance(ctx);
        if (networkClass != null) 
        {
            Object networkObj = invokeMethod(networkClass, "getInstance", new Object[]{ctx}, Context.class);
            if (networkObj != null) 
            {
                ret = getDeclaredField(networkObj, "mRequestQueue");
            }
        }
        return ret;
    }

    private static Object getDeclaredField(Object obj, String name)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException 
    {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    private static void setDeclaredField(Object obj, String name, Object value)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException 
    {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, value);
    }

    private static Object invokeMethod(Object object, String methodName, Object[] params, Class... types) throws Exception 
    {
        Object out = null;
        Class c = object instanceof Class ? (Class) object : object.getClass();
        
        if (types != null) 
        {
            Method method = c.getMethod(methodName, types);
            out = method.invoke(object, params);
        } 
        else 
        {
            Method method = c.getMethod(methodName);
            out = method.invoke(object);
        }
        return out;
    }
}
