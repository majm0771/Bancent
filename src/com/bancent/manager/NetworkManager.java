package com.bancent.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager
{
    private static NetworkManager mInstance = null;
    private Context mContext = null;
    
    private NetworkManager()
    {}
    
    public static NetworkManager GetInstance()
    {
        if (mInstance == null)
        {
            mInstance = new NetworkManager();
        }
        return mInstance;
    }
    
    public void Init(Context ctx)
    {
        mContext = ctx;
    }
    
    public boolean IsWifiEnable()
    {
        boolean ret = false;
        
        if (mContext == null)
        {
            return ret;
        }
        
        ConnectivityManager manager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null)
        {
            NetworkInfo network = manager.getActiveNetworkInfo();
            if (network != null && network.isConnectedOrConnecting()) 
            {
                ret = true;
            }
        }
        return ret;
    }
    
    public boolean EnableWifi()
    {
        boolean ret = false;
        return ret;
    }
}
