package com.bancent.common;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class Utils
{
    public static File GetSDCardRoot(Context ctx)
    {
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //�ж�sd���Ƿ���� 
        
        if (sdCardExist) 
        { 
            return Environment.getExternalStorageDirectory();
        } 
        return null;
    }
    
    public static int dip2px(Context context, float dipValue)
    { 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(dipValue * scale + 0.5f); 
    } 
    
    public static int px2dip(Context context, float pxValue)
    { 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(pxValue / scale + 0.5f); 
    } 
}
