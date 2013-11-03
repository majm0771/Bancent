package com.bancent.manager;

import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;

public class XMPPManager
{
    private Context mContext = null;
    private static XMPPManager mInstance = null;
    private XMPPConnection mConnection = null;
    
    private XMPPManager()
    {}

    public static XMPPManager GetInstance()
    {
        if (mInstance == null)
        {
            mInstance = new XMPPManager();
        }
        return mInstance;
    }
    
    public void Init(Context ctx)
    {
        mContext = ctx;
    }
}
