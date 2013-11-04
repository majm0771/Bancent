package com.bancent.common;

public class RetCode
{
    // common return code
    public static final int RC_FAILED = -1;
    public static final int RC_OK = 0;
    
    //login
    public class LoginResult
    {
        public static final int RET_ERROR_SERVER_NR = 1;
        public static final int RET_ERROR_ACCOUNT_PWD = 2;
    }
    
    //regist
    public class RegistResult
    {
        public static final int RET_ERROR_SERVER_NR = 1;
        public static final int RET_ERROR_ACCOUNT_EXIST = 2;
    }
}
