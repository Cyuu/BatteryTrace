package com.thdz.bt.util;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.thdz.bt.app.MyApplication;
import com.thdz.bt.bean.AlarmTypeBean;
import com.thdz.bt.bean.RegionBean;
import com.thdz.bt.bean.StationBean;
import com.thdz.bt.bean.TraceBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;


/**
 * 数据工具类
 */
public class DataUtils {

    private static String TAG = "DataUtils";


    /**
     * StationBean,优先展示N坐标，如果N坐标为0，则展示O坐标
     */
    public static LatLng getLLFromStationBean(StationBean bean) {
        LatLng ll = null;
        // 经纬度坐标
        double lon = Double.parseDouble(bean.getNLongitude()) == 0 ?
                Double.parseDouble(bean.getOLongitude()) :
                Double.parseDouble(bean.getNLongitude());
        double lat = Double.parseDouble(bean.getNLatitude()) == 0 ?
                Double.parseDouble(bean.getOLatitude()) :
                Double.parseDouble(bean.getNLatitude());

        // 格式化
        String lonStr = DataUtils.getFormat6(lon);
        String latStr = DataUtils.getFormat6(lat);
        ll = new LatLng(Double.parseDouble(latStr), Double.parseDouble(lonStr));
        return ll;

    }


    /**
     * 将站点列表里的坐标，转化为一个百度坐标
     */
    public static void convertStationListByBaiduLoc() {
        MyApplication application = MyApplication.getInstance();
        // 转换坐标
        List<StationBean> list = application.AllstationObj.getDataList();
        int i = 0;
        for (StationBean bean : list) {
            StationBean tmp = StationBean.convert2BaiduLocBean(bean);
            list.set(i, tmp);
            i++;
        }
        application.AllstationObj.setDataList(list);
    }


    /**
     * 将站点列表里的坐标，转化为一个百度坐标
     */
    public static List<TraceBean> convertTraceListByBaiduLoc(List<TraceBean> list) {
        // 转换坐标
        int i = 0;
        for (TraceBean bean : list) {
            TraceBean tmp = TraceBean.convert2BaiduLocBean(bean);
            list.set(i, tmp);
            i++;
        }
        return list;
    }


    public static void testConvert() {
        // 纬度：38.92229，经度：115.4567
        double lat = 38.92229;
        double lon = 115.4567;
        LatLng resll = new LatLng(lat, lon);
        LatLng mll = converter2BaiduLocation(resll);
        Log.i(TAG, mll.toString());

    }


    /**
     * Gps坐标转换为百度坐标
     */
    public static LatLng converter2BaiduLocation(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    /**
     * Gps坐标转换为百度坐标
     */
    public static LatLng converterLocation(LatLng sourceLatLng) {
        // 将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }


    /**
     * 根据告警类型获取告警类型名称
     */
    public static String getAlarmTypeNameById(String type) {
        if (TextUtils.isEmpty(type)) {
            return "";
        } else if (type.equals("-1")) {
            return "告警";
        } else if (type.equals("0")) {
            return "低压告警";
        } else if (type.equals("1")) {
            return "位移告警";
        } else if (type.equals("2")) {
            return "震动告警";
        } else if (type.equals("7")) {
            return "失联告警";
        } else if (type.equals("8")) {
            return "失联恢复";
        }
        return "";

    }


    /**
     * 根据告警类型获取告警类型名称
     */
    public static String getAlarmTypeIdByName(String name) {
        if (TextUtils.isEmpty(name) || name.equals("全部")) {
            return "-1";
        } else if (name.equals("低压告警")) {
            return "0";
        } else if (name.equals("位移告警")) {
            return "1";
        } else if (name.equals("震动告警")) {
            return "2";
        } else if (name.equals("失联告警")) {
            return "7";
        } else if (name.equals("失联恢复")) {
            return "8";
        }
        return "";

    }


    /**
     * 返回今天的时间格式化值
     */
    public static String getFormatToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        return str;
    }


    /**
     * 返回时间格式化值
     *
     * @param offset 距离今天的天的间隔
     */
    public static String getFormatTime(int offset) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, offset);
        long value = cal.getTimeInMillis();
        Date date = new Date();
        date.setTime(value);
        String str = sdf.format(date);
        return str;
    }

    /**
     * 从MyApplication里，根据接口返回的Ids，遍历筛选符合条件的站点列表
     */
    public static List<StationBean> getSelectedStationList() {

        return null;
    }


    /**
     * 根据region的id，查询其所有子区域
     */
    public static List<RegionBean> getRegionChildrenById(String regionId) {
        List<RegionBean> regions = new ArrayList<RegionBean>();
        for (RegionBean bean : MyApplication.getInstance().regionList) {
            if (bean.getParentId().equals(regionId)) {
                regions.add(bean);
            }
        }
        return regions;
    }


    /**
     * 根据region的id，获取名称
     */
    public static String getRegioNameById(String regionId) {
        for (RegionBean bean : MyApplication.getInstance().regionList) {
            if (bean.getRegionId().equals(regionId)) {
                return bean.getRegionName();
            }
        }
        return "";
    }


    /**
     * 告警类型<br/>
     * <p>
     * 全部告警
     * 低压告警
     * 位移告警
     * 震动告警
     * 失联告警
     * 失联恢复
     */
    public static List<AlarmTypeBean> initAlarmTypeData() {
        List<AlarmTypeBean> typeList = new ArrayList<AlarmTypeBean>();
        AlarmTypeBean bean1 = new AlarmTypeBean("1", "全部告警");
        typeList.add(bean1);
        AlarmTypeBean bean2 = new AlarmTypeBean("2", "低压告警");
        typeList.add(bean2);
        AlarmTypeBean bean3 = new AlarmTypeBean("3", "位移告警");
        typeList.add(bean3);
        AlarmTypeBean bean4 = new AlarmTypeBean("4", "震动告警");
        typeList.add(bean4);
        AlarmTypeBean bean5 = new AlarmTypeBean("5", "失联告警");
        typeList.add(bean5);
        AlarmTypeBean bean6 = new AlarmTypeBean("6", "失联恢复");
        typeList.add(bean6);
        return typeList;
    }


    public static Bundle createBundleWithData(String key, boolean flag) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(key, flag);
        return bundle;
    }


    /**
     * 截取字符串的后len个字符
     *
     * @param str
     * @param len
     * @return
     */
    public static String getlenStr(String str, int len) {
        if (TextUtils.isEmpty(str) || str.length() < len) {
            return "";
        } else {
            return str.substring(str.length() - len, str.length());
        }
    }


    /**
     * 小数点格式化位数
     */
    public static String getFormat6(double dd) {
        return new DecimalFormat("#.000000").format(dd);
    }


    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode;
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }


    public static String getPackageName(Context context) {
        String pkName = "";
        try {
            pkName = context.getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkName;
    }


    /**
     * 获得系统当前时间，格式 yyyy-MM-dd HH:MM:ss
     */
    public static String getTimeStrByLong(long time) {
        Date now = new Date(time);

        java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatTime = format.format(now);
        return formatTime;
    }


    /**
     * 获得系统当前时间，格式 yyyy-MM-dd HH:MM:ss
     */
    public static String getCurrentTimeStr() {
        Date now = new Date();
        java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatTime = format.format(now);
        return formatTime;
    }


    /**
     * 是否是合理的密码 校验: 长度要大于4位，
     */
    public static boolean isPwdValid(String password) {
        return password.length() > 4;
    }

    /**
     * 检验是否是合法的IP地址
     */
    public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            if (text.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检验是否是合法的IP地址
     */
    public static boolean portCheck(int value) {
        if (value < 0 || value > 65535) {
            return false;
        }
        return true;

    }


    /**
     * 获取手机ip地址
     */
    public static String getPhoneIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }


    public static String getLocalIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress address : inetAddresses) {
                    if (!address.isLoopbackAddress()) {
                        String sAddr = address.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } // for now eat exceptions
        return "";
    }

    /**
     * 去掉回车符，换行符
     */
    public static String delEnterFromStr(String str) {
        String value = str;
        value = value.replaceAll("(\r\n|\r|\n|\n\r)", "");
        return value;
    }


    /**
     * 把RAW里的文件复制到本地目录里
     */
    private void copyResourceFile(int rid, String targetFile) {
        InputStream fin = MyApplication.getInstance().getResources().openRawResource(rid);
        FileOutputStream fos = null;
        int length;
        try {
            fos = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            while ((length = fin.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


//    /**
//     * 解析出完整的json结构
//     */
//    public static String getRespString(String response) {
//        if (response.contains("{") && response.contains("}")) {
//            return response;
//        }
//        return null;
//    }

    public static boolean isReturnOK(String value) throws JSONException {
        JSONObject jsonObj = new JSONObject(value);
        String code = jsonObj.getString("code");
        if (code.equals("0")) {
            return true;
        }
        return false;

    }

    /**
     * 如果登录请求返回的code是3，则说明未获取到ClientId，无法推送，但不影响其他http功能
     */
    public static boolean isReturnOKWith3(String value) throws JSONException {
        JSONObject jsonObj = new JSONObject(value);
        String code = jsonObj.getString("code");
        if (code.equals("3")) {
            return true;
        }
        return false;

    }


    public static String getReturnMsg(String value) throws JSONException {
        JSONObject jsonObj = new JSONObject(value);
        String msg = jsonObj.getString("msg");
        return msg;
    }


    public static String getReturnUid(String value) throws JSONException {
        JSONObject jsonObj = new JSONObject(value);
        String data = jsonObj.getString("data");

        JSONObject dataObj = new JSONObject(data);
        String uid = dataObj.getString("uid");
        return uid;
    }

    /**
     * 从json结构中解析中完整DATA结构
     */
    public static String getReturnData(String value) throws JSONException {
        JSONObject jsonObj = new JSONObject(value);
        String msg = jsonObj.getString("data");
        return msg;
    }


    /**
     * 获取版本号
     */
    public static String getVersion(Context context) {
        String value = "";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            value = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    /**
     * 从json结构中解析中完整DATA结构
     */
    public static String getReturnList(String value) throws Exception {
        JSONObject jsonObj = new JSONObject(value);
        String msg = jsonObj.getString("list");
        return msg;
    }


    /**
     * 生成url，for Get请求方式
     *
     * @param module 方法名(模块名)
     */
    public static String createReqUrl4Get(String module) {
        String uid = MyApplication.getInstance().getUid();
        if (TextUtils.isEmpty(uid)) {
            uid = "0";
        }

        // 正式环境
//        String url = Finals.Url_COMMON + module + "?uid=" + uid;

        // 测试环境
        String url = Finals.Url_COMMON + module + "?";

        return url;
    }


    /**
     * 登录
     */
    public static String do4login(String username, String pwd, String clientid) {
        return createReqUrl4Get(Finals.login) +
//                "username=" + username + "&pwd=" + pwd + "&clientid=" + clientid; // 正式
                "name=" + username +
                "&pwd=" + pwd +
                "&clientId=" + clientid;
    }


    /**
     * 退出登录
     */
    public static String do4logout(String UserId, String clientid) {
        return createReqUrl4Get(Finals.logout) +
                "userId=" + UserId +
                "&clientId=" + clientid;
    }


    /**
     * 获取区域列表
     */
    public static String do4regionlist(String userId) {
        return createReqUrl4Get(Finals.getRegionList) +
                "userId=" + userId;
    }


    /**
     * 获取站点详情
     */
    public static String do4StationDetail(String stationid) {
        return createReqUrl4Get(Finals.getStationDetail) +
                "stationId=" + stationid; // 测试
    }


    /**
     * 获取所有站点列表
     */
    public static String do4AllStationList(String userId) {
        return createReqUrl4Get(Finals.getAllStation) +
                "userId=" + userId;
    }


    /**
     * 获取站点列表 - 筛选
     */
    public static String do4StationList(String uid, String regionId, String stationName, int page) {
        return createReqUrl4Get(Finals.getStationList) +
                "userId=" + uid +
                "&regionId=" + regionId +
                "&stationField=" + stationName +
                "&pageNum=" + page;
    }


    /**
     * 获取当前告警列表
     */
    public static String do4AlarmCurrentList(String userId, String regionId, String type, int page) {
        return createReqUrl4Get(Finals.getCurrentAlarm) +
                "userId=" + userId +
                "&regionId=" + regionId +
                "&type=" + type +
                "&pageNum=" + page;
    }


    /**
     * 获取历史告警列表
     */
    public static String do4AlarmHistoryList(String stationId, String type, String dateStart, String dateEnd, int page) {
        return createReqUrl4Get(Finals.getHistoryAlarm) +
                "stationId=" + stationId +
                "&type=" + type +
                "&timeFrom=" + dateStart +
                "&timeTo=" + dateEnd +
                "&pageNum=" + page;
    }


    /**
     * 获取设备电压值列表
     */
    public static String do4PowerList(String stationid, String timeFrom, String timeTo) {
        return createReqUrl4Get(Finals.getVoltageList) +
                "stationid=" + stationid +
                "&timeFrom=" + timeFrom +
                "&timeTo=" + timeTo;
    }


    /**
     * 获取最新轨迹
     */
    public static String do4TraceNew(String stationid) {
        return createReqUrl4Get(Finals.getNewTrace) +
                "stationid=" + stationid;
    }


    /**
     * 获取历史轨迹
     */
    public static String do4TraceHistory(String stationid, String timeFrom, String timeTo) {
        return createReqUrl4Get(Finals.getHistoryTrace) +
                "stationid=" + stationid +
                "&timeFrom=" + timeFrom +
                "&timeTo=" + timeTo;
    }

    /**
     * 是否安装了百度地图app
     */
    private boolean isInstallBaidumap(Context context) {
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> pkgList = manager.getInstalledPackages(0);
        for (int i = 0; i < pkgList.size(); i++) {
            PackageInfo pI = pkgList.get(i);
            if (pI.packageName.equalsIgnoreCase("com.baidu.baidumap"))
                return true;
        }
        return false;

    }


    /**
     * 是否安装了某款app
     */
    public static boolean isPkgInstalled(Context context, String packagename) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /**
     * 跳转到百度地图app进行导航
     *
     * @param context
     * @param station 目的地
     */
    public static void gotoBaiduNavi(Context context, LatLng myLatlng, StationBean station) {
        if (station == null) {
            return;
        }

        // 调起百度地图app的方法3
        Intent intent = new Intent();
        String url = "baidumap://map/direction?" +
                "origin=latlng:" + myLatlng.latitude + "," + myLatlng.longitude + "|name:我的位置" +
                "&destination=name:" + station.getStationName() +
                "|latlng:" + station.getBdLat() + "," + station.getBdLon() +
                "&mode=transit&sy=3&index=0&target=1";
        Uri uri = Uri.parse(url);
        //将功能Scheme以URI的方式传入data
        intent.setData(uri);
        //启动该页面即可
        context.startActivity(intent);

    }


    /**
     * 跳转到高德地图app进行导航, 需要先转换坐标
     *
     * @param context
     * @param myLatlng 起始点
     * @param station  目的地
     */
    public static void gotoGaodeNavi(Context context, LatLng myLatlng, StationBean station) {
        if (station == null) {
            return;
        }
        double[] gaodeArray = bdToGaoDe(myLatlng.latitude, myLatlng.longitude);
        double[] estArray = bdToGaoDe(station.getBdLat(), station.getBdLon());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        // 将百度坐标转换为高德坐标系
        String url = "androidamap://route?sourceApplication=amap&slat=" +
                gaodeArray[1] + "&slon=" + gaodeArray[0]
                + "&dlat=" + estArray[1] + "&dlon=" + estArray[0] +
                "&dname=" + station.getStationName() + "&dev=0&t=1";

        // 使用终点位置为：站点最新坐标，(这种没有上一中准确)
//        String url = "androidamap://route?sourceApplication=amap&slat=" +
//                gaodeArray[1] + "&slon=" + gaodeArray[0]
//                + "&dlat=" + station.getNLatitude() + "&dlon=" + station.getNLongitude() +
//                "&dname=" + station.getStationName() + "&dev=0&t=1";

        Uri uri = Uri.parse(url);// 将功能Scheme以URI的方式传入data
        intent.setData(uri);
        context.startActivity(intent); // 启动该页面即可

    }


    /**
     * 高德转百度（火星坐标gcj02ll–>百度坐标bd09ll）
     */
    public static double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }


    /**
     * 百度转高德（百度坐标bd09ll–>火星坐标gcj02ll）
     */
    public static double[] bdToGaoDe(double bd_lat, double bd_lon) {
        double[] gd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        gd_lat_lon[0] = z * Math.cos(theta);
        gd_lat_lon[1] = z * Math.sin(theta);
        return gd_lat_lon;
    }

    //////////////////Runtime
    public static void testRuntime() {
        Runtime tRuntime = Runtime.getRuntime();
        int count = tRuntime.availableProcessors(); // 当前系统cpu的数目
        long memory = tRuntime.freeMemory() / 1000; // 可用内容kb
        tRuntime.totalMemory();
    }
}
