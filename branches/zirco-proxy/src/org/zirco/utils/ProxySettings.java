package org.zirco.utils;

import android.content.Context;
import org.apache.http.HttpHost;



import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class for setting WebKit proxy used by Android WebView
 *
 */
public class ProxySettings {

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
