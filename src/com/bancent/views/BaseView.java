package com.bancent.views;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseView extends Activity
{    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        DoRestore(savedInstanceState);
        
        InitParam();
        InitControls();
        SetViewLayout();
    }
    
    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
    }
    
    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
    }
    
    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
    }
    
    @Override
    protected void onStop()
    {
        // TODO Auto-generated method stub
        super.onStop();
    }
    
    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }
    
    /*
     * 回收后的activity重新启动，在这里恢复参数
     */
    private void DoRestore(Bundle savedInstanceState)
    {
    }
    
    /*
     * 初始化一些activity的参数
     */
    protected abstract boolean InitParam();
    
    /*
     * 初始化ui控件
     */
    protected abstract boolean InitControls();
    
    /*
     * 设置view layout
     */
    protected abstract void SetViewLayout();
    
    /*
     * activity destroy
     */
    protected abstract void DoActivityFinish();
}
