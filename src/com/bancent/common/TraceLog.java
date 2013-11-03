package com.bancent.common;


import com.bancent.common.Constant.LogType;

import android.content.Context;
import android.util.Log;

public class TraceLog
{
    private static boolean bInit           = false;    
    private static LogThread mLogger = null;

    public static void Init(Context ctx)
    {
        if (!bInit)
        {
            bInit = true;

            int total = Thread.activeCount();     
            Thread[] totalThread = new Thread[total];     
            int length = Thread.enumerate(totalThread);   
            
            for(int i=0; i < length; i++)     
            {
                 if ("LogThread".equalsIgnoreCase(totalThread[i].getName()))
                 {
                     mLogger = (LogThread) totalThread[i];
                     break;
                 }
            }
            
            if (mLogger == null || !mLogger.isAlive())
            {
                mLogger = new LogThread(ctx);
                mLogger.setName("LogThread");
                mLogger.start();
            }
            
            Print_I("Log init ok.");
        }
    }

    public static void Print_I(String msg)
    {        
        if (Profile.EnableLog)
        {
            mLogger.AddTraceLog(LogType.Type_Info, GetPrefix() + msg);
        }
    }

    public static void Print_D(String msg)
    {
        if (Profile.EnableLog)
        {
            mLogger.AddTraceLog(LogType.Type_Debug, GetPrefix() + msg);
        }
    }
    
    public static void Print_W(String msg)
    {
        if (Profile.EnableLog)
        {
            mLogger.AddTraceLog(LogType.Type_Warning, GetPrefix() + msg);
        }
    }
    
    public static void Print_E(String msg)
    {
        if (Profile.EnableLog)
        {
            mLogger.AddTraceLog(LogType.Type_Error, GetPrefix() + msg);
        }
    }

    public static void Print_P(String msg)
    {
        if (Profile.EnableLog)
        {
            mLogger.AddTraceLog(LogType.Type_Debug, GetPrefix() + GetPrefix() + msg + " -- " + System.currentTimeMillis());
        }
    }

    private static String GetPrefix()
    {
        StringBuffer prefix = new StringBuffer();

        try
        {
            String file_Name = Thread.currentThread().getStackTrace()[4]
                    .getFileName();
            int line_Num = Thread.currentThread().getStackTrace()[4]
                    .getLineNumber();

            if (null != file_Name && file_Name.length() > 0)
            {
                prefix.append("(");
                prefix.append(file_Name.substring(0,
                        file_Name.lastIndexOf(".java")));
                prefix.append(": ");
                prefix.append(line_Num);
                prefix.append(")> ");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(Constant.TAG, e.toString());
        }

        return prefix.toString();
    }
}
