package com.thdz.bt.util;

import android.os.Environment;

/**
 * 常量
 * -------------
 * url示例： http://192.163.5.119/WebService.asmx/login?username=1&pwd=2
 */
public class Finals {

    public static final boolean IS_TEST;    // 是否测试


    private static final String IP_test = "133.124.42.166"; // 韩冲的ip
    //    public static  static final String IP_test = "192.163.5.119"; // 台式机
    private static final int PORT_test = 888;

    private static final String IP_Official = "121.18.76.102";
    private static final int PORT_Official = 7501;

    public static final String Url_COMMON; //


    static { // TODO 打包前修改 测试地址和正式地址

        IS_TEST = false; // false  true

//        Url_COMMON = "http://" + IP_test + ":" + PORT_test + "/GblsAppService.asmx/";
        Url_COMMON = "http://" + IP_Official + ":" + PORT_Official + "/GblsAppService.asmx/";

    }

    // 定义接口

    public static final String login = "login"; // 登录
    public static final String logout = "logout"; // 退出登录
    public static final String getRegionList = "getRegionList"; // 获取区域列表

    public static final String getAllStation = "getAllStation"; // 获取所有站点
    public static final String getStationList = "getStationList"; // 站点列表
    public static final String getStationDetail = "getStationDetail"; // 站点详情
    public static final String getVoltageList = "getVoltageList"; // 电压值列表
    public static final String getCurrentAlarm = "getCurrentAlarm"; // 最新告警
    public static final String getHistoryAlarm = "getHistoryAlarm"; // 历史告警
    public static final String getNewTrace = "getNewTrace"; // 最新轨迹
    public static final String getHistoryTrace = "getHistoryTrace"; // 历史轨迹


    // 文件缓存至磁盘路径
    public static final String FilePath = Environment.getExternalStorageDirectory() + "/bt/";
    // 视频文件缓存至磁盘路径
    public static final String VideoPath = FilePath + "video/";
    // 图片文件缓存至磁盘路径
    public static final String ImagePath = FilePath + "image/";
    // 导航需要的路径
    public static final String MapPath = FilePath + "map/";
    public static final String MapPathName = "map/";

    // ----sp----
    public static final String SP_NAME = "SP_batterytrace";
    public static final String SP_USERNAME = "SP_username";    // 用户名称
    public static final String SP_PWD = "SP_PWD";
    public static final String SP_UID = "SP_uid";              // 用户id
    public static final String SP_IP_Official = "SP_IP_Official";       // 中心的ip
    public static final String SP_PORT_Official = "SP_PORT_Official";   // 中心的port
    public static final String SP_AUTOLOGIN = "SP_AUTOLOGIN";

    public static final String SP_CLIENTID = "SP_CLIENTID";     // 推送唯一码

    /**
     * 刷新站点/轨迹点状态的时间间隔  单位(秒) - 当正在请求实时轨迹时
     */
    public static final int Time_Station_Polling = 60; // 60
    /**
     * 刷新站点的时间间隔  单位(秒) - 未请求实时轨迹时,
     */
    public static final int Time_Station_Common = 120;   // 120

    public static final int Max_Show_Trace_Count = 300;// 默认打开历史轨迹点的最近个数

    public static final String Default_User = "g001"; // 默认登录用户名
    public static final String Default_Pwd = "123456"; // 默认密码


    /**
     * 默认定位最短间隔
     */
    public static final int Time_Loc_Interval = 10000;   // 120
}

