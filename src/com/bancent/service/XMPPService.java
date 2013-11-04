package com.bancent.service;

import com.bancent.GlobalApplication;
import com.bancent.common.Constant.XMPPType;
import com.bancent.common.TraceLog;
import com.bancent.common.XMPPConfig;
import com.bancent.component.ISupport;
import com.bancent.component.IXMPPCallback;
import com.bancent.manager.XMPPManager;
import com.bancent.task.LoginTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class XMPPService extends Service implements ISupport
{
    private GlobalApplication mApp = null;
    private XmppSvcBinder binder = new XmppSvcBinder();
    private Handler mUIHandler = null;
    private XMPPManager mXmppManager = null;
    private ServiceCallback mCallback = new ServiceCallback();
    
    private class ServiceCallback implements IXMPPCallback
    {

        @Override
        public void onResult(int type, Bundle data)
        {
            Message m = mUIHandler.obtainMessage(type);
            m.setData(data);
            m.sendToTarget();
        }
    }
    
    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        mXmppManager = mApp.GetXMPPManager();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }
    
    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    
    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return binder;
    }

    public class XmppSvcBinder extends Binder
    {
        public XMPPService GetService()
        {
            return XMPPService.this;
        }
    }
    
    public void SetUIHandler(Handler h)
    {
        mUIHandler = h;
    }
    
    public void XmppLogin(XMPPConfig config)
    {
        mXmppManager.InitBeforeLogin(config);
        LoginpProcedure();
    }
    
    private void LoginpProcedure()
    {
        LoginTask task = new LoginTask(mXmppManager, mCallback);
        task.execute(XMPPType.OP_LOGIN);
    }

    private void PublishStatusToUI(int what)
    {
        PublishStatusToUI(what, 0, 0, null);
    }
    
    private void PublishStatusToUI(int what, int arg1, int arg2, Object obj)
    {
        if (mUIHandler == null)
        {
            TraceLog.Print_E("XMPPService: ui handler nullpointer.");
            return;
        }
        
        Message m = mUIHandler.obtainMessage(what, arg1, arg2, obj);
        m.sendToTarget();
    }

    @Override
    public GlobalApplication GetGlobalApplication()
    {
        if (mApp == null)
        {
            mApp = (GlobalApplication)getApplication();
        }
        return mApp;
    }

    @Override
    public boolean CheckNetworkState()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean CheckLoginState()
    {
        // TODO Auto-generated method stub
        return false;
    }
}
