package com.bancent.component;

import com.bancent.GlobalApplication;

public interface ISupport
{
    /*
     * 获取全局application
     */
    public GlobalApplication GetGlobalApplication();
    
    /*
     * 检查网络状态
     */
    public boolean CheckNetworkState();
    
    /*
     * 检查登录状态
     */
    public boolean CheckLoginState();
}
