package com.bancent.receiver;

import org.jivesoftware.smack.XMPPConnection;

import com.bancent.common.TraceLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkReceiver extends BroadcastReceiver
{
    private ConnectivityManager mConnectivityManager = null;
    private NetworkInfo mNetworkInfo = null;

    @Override
    public void onReceive(Context arg0, Intent arg1)
    {
        String action = arg1.getAction();
        
        TraceLog.Print_I("NetworkReceiver: " + action);
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
        {
            TraceLog.Print_I("NetworkReceiver: network state has changed.");
            
            if (mConnectivityManager == null)
            {
                mConnectivityManager = (ConnectivityManager) arg0.getSystemService(Context.CONNECTIVITY_SERVICE);
            }
            
            mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null || mNetworkInfo.isAvailable())
            {
//                XMPPConnection connection = XMPP
            }
            
        }
    }
}
