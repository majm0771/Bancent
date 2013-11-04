package com.bancent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import com.bancent.common.TraceLog;
import com.bancent.manager.BroadcastManager;
import com.bancent.manager.NetworkManager;
import com.bancent.manager.XMPPManager;
import com.bancent.service.XMPPService;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

public class GlobalApplication extends Application
{
    private BroadcastManager mBroadcastManager = null;
    private NetworkManager mNetworkManager = null;
    private XMPPManager mXmppManager = null;
    
    private HashMap<Activity, Boolean> mActivityMap = new HashMap<Activity, Boolean>(0);
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        
        InitManagers();
        InitServices();
    }
    
    public void InitManagers()
    {
        TraceLog.Print_I("GlobalApplication: init broadcast manager");
        if (mBroadcastManager == null)
        {
            mBroadcastManager = BroadcastManager.GetInstance();
        }
        mBroadcastManager.Init(getApplicationContext());

        TraceLog.Print_I("GlobalApplication: init network manager");
        if (mNetworkManager == null)
        {
            mNetworkManager = NetworkManager.GetInstance();
        }
        mNetworkManager.Init(getApplicationContext());

        TraceLog.Print_I("GlobalApplication: init XMPP manager");
        if (mXmppManager == null)
        {
            mXmppManager = XMPPManager.GetInstance();
        }
        mXmppManager.Init(getApplicationContext());
        
        TraceLog.Print_I("GlobalApplication: init service manager");
    }
    
    public BroadcastManager GetBroadcastManager()
    {
        return mBroadcastManager;
    }
    
    public NetworkManager GetNetworkManager()
    {
        return mNetworkManager;
    }
    
    public XMPPManager GetXMPPManager()
    {
        return mXmppManager;
    }
    
    private void InitServices()
    {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), XMPPService.class);
        startService(intent);
    }
    
    private void UninitServices()
    {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), XMPPService.class);
        stopService(intent);
    }
    
    private void OnApplicationExit()
    {
        UninitServices();
        
        //stop all activities
        
        //kill process
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    
    public void RegisterActivity(Activity activity)
    {
        mActivityMap.put(activity, true);
    }
    
    public void UnregisterActivity(Activity activity)
    {
        mActivityMap.put(activity, false);
    }
}
