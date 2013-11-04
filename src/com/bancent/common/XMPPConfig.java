package com.bancent.common;

public class XMPPConfig
{
    private String mLoginID = null; //用户名
    private String mLoginPWD = null;    //用户密码
    private String mHostIP = null;// 地址
    private int mHostPort = 0;// 端口
    private String mServiceName = null;// 服务器名称
    
    public void SetHostIP(String ip)
    {
        this.mHostIP = ip;
    }
    
    public String GetHostIP()
    {
        return this.mHostIP;
    }
    
    public void SetHostPort(int port)
    {
        this.mHostPort = port;
    }
    
    public int GetHostPort()
    {
        return this.mHostPort;
    }
    
    public void SetServiceName(String svcName)
    {
        this.mServiceName = svcName;
    }
    
    public String GetServiceName()
    {
        return this.mServiceName;
    }
    
    public void SetLoginName(String name)
    {
        this.mLoginID = name;
    }
    
    public String GetLoginName()
    {
        return this.mLoginID;
    }
    
    public void SetLoginPWD(String pwd)
    {
        this.mLoginPWD = pwd;
    }
    
    public String GetLoginPWD()
    {
        return this.mLoginPWD;
    }
}
