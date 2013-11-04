package com.bancent.task;

import com.bancent.common.Constant.XMPPType;
import com.bancent.common.MessageDeclare.XMPPMessage;
import com.bancent.common.Constant;
import com.bancent.common.RetCode;
import com.bancent.common.TraceLog;
import com.bancent.component.IXMPPCallback;
import com.bancent.manager.XMPPManager;

import android.os.AsyncTask;
import android.os.Bundle;

public class LoginTask extends AsyncTask<Integer, Integer, Integer>
{
    private XMPPManager mXmppManager = null;
    private IXMPPCallback mCallback = null;
    private Bundle mResultSet = new Bundle();
    
    public LoginTask(XMPPManager mgr, IXMPPCallback cb)
    {
        mXmppManager = mgr;
        mCallback = cb;
    }

    @Override
    protected Integer doInBackground(Integer... arg0)
    {
        int ret = RetCode.RC_FAILED;
        
        switch (arg0[0])
        {
            case XMPPType.OP_LOGIN:
                LoginToServer();
                break;
            case XMPPType.OP_REGISTER:
                
                break;

            default:
                break;
        }
        return ret;
    }
    
    @Override
    protected void onProgressUpdate(Integer... values)
    {
        if (mCallback == null)
        {
            TraceLog.Print_E("LoginTask: no callback");
            return;
        }
        
        mCallback.onResult(values[0], null);
    }

    @Override
    protected void onPostExecute(Integer result)
    {
        mCallback.onResult(result, mResultSet);
    }
    
    private void LoginToServer()
    {
            publishProgress(XMPPMessage.MSG_LOGIN_START);
            mResultSet.putInt(Constant.RESULT, mXmppManager.login());
            publishProgress(XMPPMessage.MSG_LOGIN_END);
    }
}
