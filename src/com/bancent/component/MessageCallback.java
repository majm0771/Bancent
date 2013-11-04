package com.bancent.component;

import android.os.Handler.Callback;
import android.os.Message;

public class MessageCallback implements Callback
{
    @Override
    public boolean handleMessage(Message arg0)
    {
        // TODO Auto-generated method stub
        return MessageProcess(arg0);
    }
    
    protected boolean MessageProcess(Message m)
    {
        return true;
    }
}
