package com.thdz.bt.util;

/**
 * 通信协议的相关全局变量
 */
public class CodeUtils {

    /**
     * 新告警code
     */
    public static final int CODE_NEW_ALARM = 4204;          // 有新告警

    public static final int CODE_CANCEL_ALARM = 4203;       // 告警取消

    public static final String CODE_LOGOUT = "23";          // 用户退出登录


    public static final int CODE_TIMEOUT = -20;                   // 网络超时
    public static final String CODE_TIMEOUT_NAME = "网络超时";     // 网络超时
    public static final int CODE_NO_NETWORK = -21;                 // 没有网络
    public static final String CODE_NO_NETWORK_NAME = "没有网络";   // 没有网络
    public static final int CODE_JSONPARSE = -22;                    // Json解析错误
    public static final String CODE_JSONPARSE_NAME = "Json解析错误";   // Json解析错误

    public static final int CODE_OTHER_FAULT = -30;                // 其他错误
    public static final String CODE_OTHER_FAULT_NAME = "其他错误";  // 其他错误


}
