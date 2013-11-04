package com.bancent.common;

public class RetCode
{
    // common return code
    public static final int RC_FAILED = -1;
    public static final int RC_OK = RC_FAILED + 1;
    
    //login
    public class LoginCode
    {
        public static final int RET_UNKNOWN = -1;
        public static final int RET_SUCCESS = 0x0100;
    }
    
    //regist
    public class RegistCode
    {
        public static final int RET_UNKNOWN = -1;
        public static final int RET_SUCCESS = 0x0200;
        public static final int RET_NO_RESPONSE = 0x0201;
        public static final int RET_EXIST = 0x0202;
    }
}
