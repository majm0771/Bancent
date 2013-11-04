package com.bancent.task;

import org.jivesoftware.smack.XMPPException;

import com.bancent.common.TraceLog;
import com.bancent.manager.XMPPManager;

import android.content.Context;
import android.os.AsyncTask;

public class LoginTask extends AsyncTask<String, Integer, Integer>
{
    private XMPPManager mXmppManager = null;
    
    public LoginTask(Context ctx, XMPPManager mgr)
    {
        mXmppManager = mgr;
    }

    @Override
    protected Integer doInBackground(String... arg0)
    {
        try
        {
            mXmppManager.login();
        }
        catch (Exception e)
        {
            if (e instanceof XMPPException)
            {
                
            }
            else
            {
                TraceLog.Print_E("LoginTask: login failed.");
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result)
    {
    }
}
