package com.bancent.manager;

import com.bancent.common.TraceLog;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastManager
{
    private static BroadcastManager mInstance = null;
    private Context mContext = null;
    private LocalBroadcastManager mLocalBroadcastManager = null;
    
    private BroadcastManager()
    {}
    
    public static BroadcastManager GetInstance()
    {
        if (mInstance == null)
        {
            mInstance = new BroadcastManager();
        }
        return mInstance;
    }
    
    public void Init(Context ctx)
    {
        TraceLog.Print_I("BroadcastManager: do init.");
        mContext = ctx;
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
    }
    
    public void SendBroadcast(Intent i)
    {
        if (i == null)
        {
            TraceLog.Print_W("BroadcastManager: empty intent to send.");
            return;
        }
        
        mLocalBroadcastManager.sendBroadcast(i);
    }
}
