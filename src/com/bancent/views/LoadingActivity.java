package com.bancent.views;

import com.bancent.GlobalApplication;
import com.bancent.R;
import com.bancent.common.Constant;
import com.bancent.common.TraceLog;
import com.bancent.common.XMPPConfig;
import com.bancent.common.Profile.LoginKeySet;
import com.bancent.extend.ISupport;
import com.bancent.extend.MessageCallback;
import com.bancent.service.XMPPService;
import com.bancent.service.XMPPService.XmppSvcBinder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

public class LoadingActivity extends BaseView implements OnClickListener, ISupport
{
    private GlobalApplication mApp = null;
    
    //controls
    private FrameLayout mHeader = null;
    private Button mRegisterButton = null;
    private Button mLoginButton = null;
    private EditText mNameInput = null;
    private EditText mPWDInput = null;
    
    private Handler mHandler = new Handler(new LoginProcesser());
    private XMPPService mService = null;
    private XMPPConfig mLoginConfig = null;
    private SharedPreferences mPreferences = null;
    
    private ServiceConnection mConnection = new ServiceConnection()
    {
        
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            // TODO Auto-generated method stub
            mService = null;
        }
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            // TODO Auto-generated method stub
            mService = ((XmppSvcBinder)service).GetService();
            mService.SetUIHandler(mHandler);
            
            try
            {
                mService.XmppLogin(mLoginConfig);
            }
            catch (Exception e)
            {
                TraceLog.Print_E("LoadingActivity: service login error.");
                e.printStackTrace();
            }
        }
    };

    @Override
    protected boolean InitParam()
    {
        mApp = GetGlobalApplication();
        mApp.RegisterActivity(this);
        
        if (mPreferences == null)
        {
            mPreferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        }
        
        if (mLoginConfig == null)
        {
            mLoginConfig = new XMPPConfig();
        }
        
        InitLoginConfig();
        return true;
    }

    @Override
    protected boolean InitControls()
    {
        mHeader = (FrameLayout) findViewById(R.id.view_header);
        mRegisterButton = (Button) findViewById(R.id.btn_register);
        mLoginButton = (Button) findViewById(R.id.btn_login);
        mNameInput = (EditText) findViewById(R.id.et_id);
        mPWDInput = (EditText) findViewById(R.id.et_pwd);
        
        
        if (mHeader != null)
        {
            mHeader.setVisibility(View.GONE);
        }
        
        if (mRegisterButton != null)
        {
            mRegisterButton.setOnClickListener(this);
        }
        
        if (mLoginButton != null)
        {
            mLoginButton.setOnClickListener(this);
        }
        
        if (mNameInput != null)
        {
            mNameInput.setText("james");
        }
        
        if (mNameInput != null)
        {
            mPWDInput.setText("123456");
        }
        return true;
    }

    @Override
    protected void SetViewLayout()
    {
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_loading);
    }

    @Override
    protected void DoActivityFinish()
    {
        // TODO Auto-generated method stub
        mApp.UnregisterActivity(this);
    }
    
    private void InitLoginConfig()
    {
        mLoginConfig.SetHostIP(
                mPreferences.getString(LoginKeySet.KEY_HOST_NAME, 
                        getResources().getString(R.string.default_xmpp_host_name)));
        mLoginConfig.SetHostPort(
                mPreferences.getInt(LoginKeySet.KEY_HOST_PORT, 
                        getResources().getInteger(R.integer.default_xmpp_host_port)));
        mLoginConfig.SetServiceName(
                mPreferences.getString(LoginKeySet.KEY_SERVICE_NAME, 
                        getResources().getString(R.string.default_xmpp_service_name)));
        mLoginConfig.SetLoginName(mNameInput.getText().toString());
        mLoginConfig.SetLoginPWD(mPWDInput.getText().toString());
    }
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_register:
                TraceLog.Print_I("LoadingActivity: register clicked");
                DoRegister();
                break;
            case R.id.btn_login:
                TraceLog.Print_I("LoadingActivity: login clicked");
                DoLogin();
                break;

            default:
                TraceLog.Print_I("LoadingActivity: unknown clicked");
                break;
        }
    }
    
    private void DoRegister()
    {
        
    }
    
    private void DoLogin()
    {
        Intent i = new Intent();
        i.setClass(this, XMPPService.class);
        bindService(i, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public GlobalApplication GetGlobalApplication()
    {
        // TODO Auto-generated method stub
        if (mApp == null)
        {
            mApp = (GlobalApplication) getApplication();
        }
        return mApp;
    }

    @Override
    public boolean CheckNetworkState()
    {
        // TODO Auto-generated method stub
        if (mApp == null)
        {
            mApp = GetGlobalApplication();
        }
        
        return mApp.GetNetworkManager().IsWifiEnable();
    }

    @Override
    public boolean CheckLoginState()
    {
        // TODO Auto-generated method stub
        return false;
    }

    private class LoginProcesser extends MessageCallback
    {
        @Override
        protected boolean MessageProcess(Message m)
        {
            return true;
        }    
    }
}
