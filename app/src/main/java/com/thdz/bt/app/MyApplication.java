package com.thdz.bt.app;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.thdz.bt.bean.AlarmTypeBean;
import com.thdz.bt.bean.PushBaseBean;
import com.thdz.bt.bean.RegionBean;
import com.thdz.bt.bean.ReturnDataBean;
import com.thdz.bt.bean.StationBean;
import com.thdz.bt.ui.HomeActivity;
import com.thdz.bt.util.DataUtils;
import com.thdz.bt.util.FileUtil;
import com.thdz.bt.util.Finals;
import com.thdz.bt.util.NotifyUtil;
import com.thdz.bt.util.SpUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * Application
 * ----------------
 * 1 各种code存放在CodeUtils
 * 2 初始化配置地图，OKHttp，图片加载等
 * 3
 */
public class MyApplication extends Application {

    private static final String TAG = "BatteryTrace";

    /**
     * 已推送过来的告警list
     */
    private List<PushBaseBean> PushedList;


//    public List<PushBaseBean> getPushedList(){
//        if (PushedList == null) {
//            PushedList = new ArrayList<PushBaseBean>();
//        }
//        return PushedList;
//    }

    /**
     * 是否已推送过改条告警
     */
    public boolean hasPushed(PushBaseBean push){
        boolean temp = false;
        if (PushedList == null) {
            return false;
        }
        for (PushBaseBean bean : PushedList) {
            if (push.getStationid().equals(bean.getStationid()) &&
                    push.getAlarmtime().equals(bean.getAlarmtime())) {
                temp = true;
                break;
            }
        }
        return temp;
    }

    public static int notyId = 1; // 通知的index

    public boolean hasNewPush = false;
    public PushBaseBean pushBean = null;

    /**
     * 告警类型数据，程序里全部写死
     */
    public List<AlarmTypeBean> typeList;

    /**
     * 所有区域(包括页码等等) -- 用于地图展示
     */
    public ReturnDataBean AllstationObj;

//    /**
//     * 所有区域列表 -- 用于地图展示
//     */
//    public List<StationBean> stationList;

    /**
     * 当前需要展示轨迹的StationBean
     */
    public String curStationId4Trace;

//    public LocationService locationService;

    /**
     * 所有区域列表 -- 每次打开app获取一次
     */
    public List<RegionBean> regionList;

    /**
     * 是否打开告警,false:打开告警，true：关闭告警,
     */
    private static final boolean isCloseAlarm = false;

    // 网络请求的参数：
    private byte[] RSSetParams = null;

    public int screenWidth = 0;
    public int screenheigth = 0;

    public int getScreenHeigth() {
        return screenheigth;
    }

    public void setScreenHeigth(int screenheigth) {
        this.screenheigth = screenheigth;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    private String uid;

    public String getUid() {
        if (TextUtils.isEmpty(uid)) {
            uid = SpUtil.getUid(getApplicationContext());
        }

        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * EventBus分发导航事件，会产生两次点击效果，没找到原因，所以改用这种方式处理
     */
    public HomeActivity home;

//    public void gotoNavi(Context context, StationBean bean) {
//        if (home != null && bean != null) {
////            home.goChooseNavi(bean);
//            DialogUtil.naviChoose(context, bean);
//        }
//    }

    public void gotoNaviByBaidu(StationBean bean) {
        if (home != null && bean != null) {
            home.naviByBaidu(bean);
        }
    }

    public void gotoNaviByGaode(StationBean bean) {
        if (home != null && bean != null) {
            home.naviByGaode(bean);
        }
    }

    public void gotoNaviBySDK(StationBean bean) {
        if (home != null && bean != null) {
            home.naviBySDK(bean);
        }
    }


    private List<Activity> activityList;       // 全部activity集合
    private static MyApplication application; // 程序全局对象

    public static MyApplication getInstance() {
        if (null == application) {
            application = new MyApplication();
        }
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;

        activityList = new LinkedList<Activity>();

        typeList = DataUtils.initAlarmTypeData();
        regionList = new ArrayList<RegionBean>();
        PushedList = new ArrayList<PushBaseBean>();

        /**
         * 初始化定位sdk，建议在Application中创建
         */
//        locationService = new LocationService(getApplicationContext());
        // 初始化地图组件
        SDKInitializer.initialize(this);

        SDKInitializer.setCoordType(CoordType.BD09LL);//默认坐标类型为 BD09LL ,  改为： GCJ02

        FileUtil.createDirectory(Finals.FilePath);
        FileUtil.createDirectory(Finals.VideoPath);
        FileUtil.createDirectory(Finals.ImagePath);
        FileUtil.createDirectory(Finals.MapPath);

        // 初始化OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .writeTimeout(20000l, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);

    }


    public void addActivity(Activity activity) {
        if (activityList == null) {
            activityList = new LinkedList<Activity>();
        }
        activityList.add(activity);
    }

    /**
     * 移除Activity到容器中
     */
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    //================== 各种业务函数 =================

    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }

        activityList.clear();
        activityList = null;
        System.exit(0);
    }


    private void showNotifyMsg(Class<?> cls, String title, String content) {
        MyApplication.notyId++;
        if (cls == null) {
            NotifyUtil.showNotification(getApplicationContext(), title,
                    content, null, MyApplication.notyId);
        } else {
            Intent intent = new Intent(getApplicationContext(), cls);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NotifyUtil.showNotification(getApplicationContext(), title,
                    content, intent, MyApplication.notyId);
        }
    }


    // 定时任务
    TimerTask stationTask = new TimerTask() {
        @Override
        public void run() {

        }
    };

    // 时间间隔
    private long periodStation = 1 * 60 * 1000;

    // 调用定时任务
    private void quest() {
        Timer stationTimer = new Timer();
        stationTimer.schedule(stationTask, periodStation);

    }

}
