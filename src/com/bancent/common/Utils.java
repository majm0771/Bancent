package com.bancent.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

public class Utils
{
    public static String GetIPFromHost(String name)
    {
        InetAddress address = null;
        try
        {
            address = InetAddress.getByName(name);
        }
        catch (UnknownHostException e) 
        {
            e.printStackTrace();
            return null;
        }
        return address.getHostAddress().toString();
    }
    
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
    
    // 将byte[]转换成InputStream  
    public static InputStream Byte2InputStream(byte[] b)
    {  
        ByteArrayInputStream bais = new ByteArrayInputStream(b);  
        return bais;  
    }  
  
    // 将InputStream转换成byte[]  
    public static byte[] InputStream2Bytes(InputStream is)
    {  
        String str = "";  
        byte[] readByte = new byte[1024];  
        int readCount = -1;  
        
        try
        {  
            while ((readCount = is.read(readByte, 0, 1024)) != -1)
            {  
                str += new String(readByte).trim();  
            }  
            return str.getBytes();  
        }
        catch (Exception e)
        {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    // 将Bitmap转换成InputStream  
    public static InputStream Bitmap2InputStream(Bitmap bm)
    {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
        InputStream is = new ByteArrayInputStream(baos.toByteArray());  
        return is;  
    }  
  
    // 将Bitmap转换成InputStream  
    public static InputStream Bitmap2InputStream(Bitmap bm, int quality)
    {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);  
        InputStream is = new ByteArrayInputStream(baos.toByteArray());  
        return is;  
    }  
  
    // 将InputStream转换成Bitmap  
    public static Bitmap InputStream2Bitmap(InputStream is)
    {  
        return BitmapFactory.decodeStream(is);  
    }  
  
    // Drawable转换成InputStream  
    public static InputStream Drawable2InputStream(Drawable d)
    {  
        Bitmap bitmap = drawable2Bitmap(d);  
        return Bitmap2InputStream(bitmap);  
    }  
  
    // InputStream转换成Drawable  
    public static Drawable InputStream2Drawable(InputStream is)
    {  
        Bitmap bitmap = InputStream2Bitmap(is);  
        return bitmap2Drawable(bitmap);  
    }  
  
    // Drawable转换成byte[]  
    public static byte[] Drawable2Bytes(Drawable d)
    {  
        Bitmap bitmap = drawable2Bitmap(d);  
        return Bitmap2Bytes(bitmap);  
    }  
  
    // byte[]转换成Drawable  
    public static Drawable Bytes2Drawable(byte[] b)
    {  
        Bitmap bitmap = Bytes2Bitmap(b);  
        return bitmap2Drawable(bitmap);  
    }  
  
    // Bitmap转换成byte[]  
    public static byte[] Bitmap2Bytes(Bitmap bm)
    {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);  
        return baos.toByteArray();  
    }  
  
    // byte[]转换成Bitmap  
    public static Bitmap Bytes2Bitmap(byte[] b)
    {  
        if (b.length != 0)
        {  
            return BitmapFactory.decodeByteArray(b, 0, b.length);  
        }  
        return null;  
    }  
  
    // Drawable转换成Bitmap  
    public static Bitmap drawable2Bitmap(Drawable drawable)
    {  
        Bitmap bitmap = Bitmap  
                .createBitmap(  
                        drawable.getIntrinsicWidth(),  
                        drawable.getIntrinsicHeight(),  
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                                : Bitmap.Config.RGB_565);  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  
                drawable.getIntrinsicHeight());  
        drawable.draw(canvas);  
        return bitmap;  
    }  
  
    // Bitmap转换成Drawable  
    public static Drawable bitmap2Drawable(Bitmap bitmap)
    {  
        BitmapDrawable bd = new BitmapDrawable(bitmap);  
        Drawable d = (Drawable) bd;  
        return d;  
    }  
}
