package com.bancent.common;

public class Constant
{
    /*
     * application common define
     */
    public static final String TAG             = "Bancent";
    public static final String RESULT             = "result_code";

    /*
     * preference define
     */
    public static final String LOGIN_PREF      = "login";
    
    /*
     * xmpp operation type
     */
    public class XMPPType
    {
        public static final int OP_LOGIN = 0x2000;
        public static final int OP_REGISTER = 0x2001;
    }

    /*
     * keySet
     */
    public class LoginKeySet
    {
        public static final String KEY_HOST_NAME = "host_NAME";
        public static final String KEY_HOST_PORT = "host_port";
        public static final String KEY_SERVICE_NAME = "service_name";
    }
    
    /*
     * Log define
     */
    public class LogType
    {
        public static final int Type_Unkown  = 0x0000;
        public static final int Type_Info    = 0x0001;
        public static final int Type_Debug   = 0x0002;
        public static final int Type_Warning = 0x0003;
        public static final int Type_Error   = 0x0004;
    }
}
