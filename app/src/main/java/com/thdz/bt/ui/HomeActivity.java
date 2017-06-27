package com.thdz.bt.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.andrew.datechoosewheelviewdemo.DateChooseWheelViewDialog;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.thdz.bt.R;
import com.thdz.bt.adapter.SpinnerRegionListAdapter;
import com.thdz.bt.adapter.StationListAdapter;
import com.thdz.bt.app.MyApplication;
import com.thdz.bt.base.BaseActivity;
import com.thdz.bt.bean.AlarmBean;
import com.thdz.bt.bean.PushBaseBean;
import com.thdz.bt.bean.RegionBean;
import com.thdz.bt.bean.ReturnDataBean;
import com.thdz.bt.bean.StationBean;
import com.thdz.bt.bean.TraceBean;
import com.thdz.bt.event.StationAlarmEvent;
import com.thdz.bt.event.TraceHistoryEvent;
import com.thdz.bt.event.TraceRecentEvent;
import com.thdz.bt.util.DataUtils;
import com.thdz.bt.util.DialogUtil;
import com.thdz.bt.util.Finals;
import com.thdz.bt.util.MapUtils;
import com.thdz.bt.util.NotifyUtil;
import com.thdz.bt.util.VUtils;
import com.thdz.bt.view.NoScrollListView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import baidumapsdk.demo.clusterutil.clustering.Cluster;
import baidumapsdk.demo.clusterutil.clustering.ClusterManager;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import okhttp3.Call;

import static com.baidu.mapapi.map.BitmapDescriptorFactory.fromResource;
import static com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType.BD09LL;


/**
 * 主页
 * --------------
 * 请求站点列表，这个请求，需要真实所有站点，所以分页可能没什么用了
 * --------------
 * mBaiduMap.setOnMarkerClickListener，有多种Marker，怎么区分？
 * <p>
 * 地图上的站点由Clusters管理，不再使用List<Marker>定义
 * 地图上的轨迹点依旧由地图管理，而不由Clusters管理
 * ------------
 * 默认地图刷不出来是因为onMapLoaded()方法里，的zoom参数有误，不应该弄最大的，应该放<=13的数值
 * ---------------
 * 定位服务监听：onReceiveLocation
 * 如果是非法坐标数据，不予赋值
 */
public class HomeActivity extends BaseActivity implements BaiduMap.OnMapLoadedCallback {

    public static final int FLAG_MAP = 0X20;
    public static final int FLAG_STATION = 0X30;

    private static final int Code_Request_Mine = 0x89;

    private String markerType_station = "120";
    private String markerType_alarm = "121";
    private String markerType_trace = "122";


    @BindView(R.id.spinner_prov)
    Spinner spinner_prov;
    @BindView(R.id.spinner_city)
    Spinner spinner_city;
    @BindView(R.id.spinner_dist)
    Spinner spinner_dist;

    @BindView(R.id.layout_mapview)
    RelativeLayout layout_mapview;

    @BindView(R.id.mapview)
    MapView mapview;       // MapView 是地图主控件

    @BindView(R.id.swipy_layout)
    SwipyRefreshLayout swipy_layout;

    @BindView(R.id.lv_stations)
    NoScrollListView lv_stations;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rg_map_stations)
    RadioGroup rg_map_stations;

    @BindView(R.id.fab_clear_trace)
    FloatingActionButton fab_clear_trace; // 清除轨迹

    @BindView(R.id.fab_clear_info)
    FloatingActionButton fab_clear_info; // 清除info

    @BindView(R.id.fab_location)
    FloatingActionButton fab_location; // 定位

    @BindString(R.string.action_search)
    String action_search;
    @BindString(R.string.action_history)
    String action_history;
    @BindString(R.string.action_alarm)
    String action_alarm;
    @BindString(R.string.action_device)
    String action_device;

    @BindView(R.id.et_name)
    EditText et_name;

    @BindView(R.id.btn_search)
    TextView btn_search;

    @BindView(R.id.tv_alarm_tip)
    ImageView tv_alarm_tip;

    @BindView(R.id.tv_select_time)
    TextView tv_select_time;


    @BindColor(R.color.red_color)
    int lineColor; // 绘制轨迹的线条颜色

    @BindColor(R.color.trans_black_10)
    int popupBgColor; // Marker上的PopupWindow的背景颜色

    @BindColor(R.color.white)
    int popupTxtColor; // Marker上的PopupWindow的字体颜色

    private StationListAdapter stationAdapter; // 站点列表adapter
    private List<StationBean> stationList;     // 站点列表数据,用于展示listview的

    private BaiduMap mBaiduMap;   // 类

    private int curPage = 1; // 当前页 -- 站点会加载所有

    private boolean hasOpenTraceHis;// 是否已经正在显示 历史轨迹

    private int sta_offset = -60; // 站点Marker的往上的偏移量
    private int trace_offset = -36; // 轨迹Marker的往上的偏移量

    private int sta_zIndex = 6;
    private int trace_zIndex = 5;

    float scaleDefault = 15.0f; // 初始化缩放倍数  16.0, 以后都不再调用
    float scaleMax = 18.0f; // 最大放大倍数

    int lineWidth = 2; // 绘制轨迹的线条宽度


    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdFoot = fromResource(R.drawable.ic_round_red);   // 轨迹点

    BitmapDescriptor bdStation = fromResource(R.drawable.ic_loc_green); //站点
    BitmapDescriptor bdAlarm = fromResource(R.drawable.ic_loc_red);   // 告警

    // --------------- 点聚合功能使用----------------
    private MapStatus ms; // 地图状态
    private ClusterManager<StationBean> mClusterManager; // 点聚合综合管理类
    private List<Overlay> traceOverLayList = null; // 轨迹描点结果
    private Polyline mPolyline; // 轨迹连线
    private List<OverlayOptions> markerTraceList = null; // 轨迹点Marker

    /**
     * 选择的站点的位置,目前没什么用
     */
    private LatLng selectedLatLng;

    /**
     * 用户定位到的坐标，
     */
    private InfoWindow mInfoWindow;

    // 所有区域bean
    public static List<RegionBean> regionList = null;
    public static List<RegionBean> provinceList = new ArrayList<RegionBean>();


    // 当前省
    public static RegionBean provinceBean;
    // 当前市
    public static RegionBean cityBean;
    // 当前县
    public static RegionBean districtBean;

    private SpinnerRegionListAdapter provinceAdapter;
    private SpinnerRegionListAdapter cityAdapter;
    private SpinnerRegionListAdapter distAdapter;


    /**
     * 用户当前所在位置，或者默认一个位置, 天河电子门口
     */
    private LatLng myLatlng = new LatLng(38.927763, 115.469205); // 百度坐标

    private boolean isShowAllTrace = false; // 是否打开所有轨迹点


    // 导航相关
    public static List<Activity> activityList = new LinkedList<Activity>();
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
    public static final String RESET_END_NODE = "resetEndNode";
    public static final String VOID_MODE = "voidMode";

    private static final String[] authBaseArr = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static final String[] authComArr = {Manifest.permission.READ_PHONE_STATE};
    private static final int authBaseRequestCode = 111;
    private static final int authComRequestCode = 222;

    private boolean hasInitSuccess = false;
    private boolean hasRequestComAuth = false;

    String authinfo = null; // 权限信息

//    private LocationService locationService; // 定位

    private boolean isPush = false;
    private PushBaseBean pushBean = null;
    private StationBean clickedStation; // 点击的站点Marker
    private boolean isShowTraceInfo = false; // 是否正在显示轨迹点的info


    @Override
    public void setContentView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        activityList.add(this);
        setContentView(R.layout.activity_home);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        MyApplication.getInstance().home = this;
        Log.i(TAG, "OnCreate," + this.toString());
        EventBus.getDefault().register(this);
        setSupportActionBar(toolbar);

        btn_search.setOnClickListener(this);
        tv_alarm_tip.setOnClickListener(this);
        tv_select_time.setOnClickListener(this);
        tv_select_time.setVisibility(View.GONE);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                isPush = bundle.getBoolean("isPush", false);
                pushBean = (PushBaseBean) bundle.getSerializable("pushBean");
            }
        }
        initTraceHisTime();

        mBaiduMap = mapview.getMap();

        initLocation(); // 开启定位

        mBaiduMap.setOnMapLoadedCallback(this);

//        MapUtils.hideMapChildren(mapview); // 是否隐藏比例尺和缩放按钮

        BNOuterLogUtil.setLogSwitcher(true);
        initNavi(); // 初始化导航

        stationAdapter = new StationListAdapter(context);
        lv_stations.setAdapter(stationAdapter);

        lv_stations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StationBean bean = stationList.get(position);
                rg_map_stations.check(R.id.rb_map);
                String stationId = bean.getStationId();
                StationBean curBean = getStationByStaionId(stationId);

                hideInfoAll();

                showInfo4Station(curBean.getPosition(), curBean.getStationName());

                // 最大zoom
                ms = new MapStatus.Builder().target(curBean.getPosition()).zoom(scaleMax).build();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));

            }
        });


        fab_clear_trace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                showToast("清除轨迹");
                tv_select_time.setVisibility(View.GONE);
                hasOpenTraceHis = false;
                clearLineMap();

                // todo 是否正在展示轨迹的info 基于函数：clearLineMap()
                if (isShowTraceInfo) {
                    hideInfoAll();
                }

                stopTraceTask(); // 关闭轮询任务 --> 所有站点状态

                stopStationTask();
                startStationTask(0, Finals.Time_Station_Common * 1000);

            }
        });

        fab_clear_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showToast("清除info窗口");
                hideInfoAll();
            }
        });

        fab_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showToast("定位");
                backCurrentLocation();
            }
        });


        rg_map_stations.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_map:
                        switchMapOrStation(FLAG_MAP);
                        break;
                    case R.id.rb_stations:
                        switchMapOrStation(FLAG_STATION);
                        break;
                }
            }
        });

        switchMapOrStation(FLAG_MAP);
//        registBaiduMapEvent();

        VUtils.setSwipyColor(swipy_layout);
        swipy_layout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        swipy_layout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                switch (direction) {
                    case TOP:
                        curPage = 1;
                        break;
                    case BOTTOM:
                        curPage += 1;
                        break;
                }
                String name = et_name.getText().toString().trim();
                requestStationList(getCurrentChildId(), name);

            }
        });
        swipy_layout.setRefreshing(false);


        // 省市县的初始化
        initSpinnerData();

        provinceAdapter = new SpinnerRegionListAdapter(context);
        spinner_prov.setAdapter(provinceAdapter);
        provinceAdapter.setDataList(provinceList);
        provinceAdapter.notifyDataSetChanged();

        cityAdapter = new SpinnerRegionListAdapter(context);
        spinner_city.setAdapter(cityAdapter);

        distAdapter = new SpinnerRegionListAdapter(context);
        spinner_dist.setAdapter(distAdapter);

        initSpinnerListener();

        requestStationList("0", ""); // 初始化

        initAnim(); // 初始化告警闪烁动画

//        // 开启轮询任务 --> 所有站点状态
//        PollUtils.startPollingService(context, Finals.Time_Station_Common,
//                PollingStationService.class, PollingStationService.ACTION);


        // 如果是推送打开的，需要刷新所有站点
//        if (isPush && pushBean != null) { // pushBean.getStationid()
//            requestAllStationList(pushBean.getStationid());
//        }

        // 开启轮询任务 --> 所有站点状态
        startStationTask(0, Finals.Time_Station_Common * 1000);


//        Button btn1 = (Button) findViewById(R.id.btn1);
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tv_alarm_tip.setVisibility(View.VISIBLE);
//                tv_alarm_tip.startAnimation(alphaAnimation);
//            }
//        });
//
//        Button btn2 = (Button) findViewById(R.id.btn2);
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tv_alarm_tip.setVisibility(View.GONE);
//                tv_alarm_tip.clearAnimation();
//            }
//        });

    }


    LocationClient mLocClient;

    private void initLocation() {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null)); // bdMyLoc

        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(mBDLocListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(6000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }


    /**
     * 获取所有站点列表, 推送告警时调用
     *
     * @param alarmStationid 当前正在告警的站点id
     */
    private void requestAllStationList(final String alarmStationid) {
//        showProgressDialog();
        String url = DataUtils.do4AllStationList(application.getUid());

        doRequestGet(url, new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                e.printStackTrace();
                hideProgressDialog();
                showToast(failTip + "，更新站点数据失败");
                moveToAlarmStation(alarmStationid);

            }

            @Override
            public void onResponse(String value, int id) {
                hideProgressDialog();
                Log.i(TAG, "获取所有站点列表 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {
                        String content = DataUtils.getReturnData(value);
                        application.AllstationObj = JSONObject.parseObject(content, ReturnDataBean.class);

                        if (application.AllstationObj.getDataList() == null
                                || application.AllstationObj.getDataList().isEmpty()) {
                            showToast("查询的所有站点为空");
                        }

                        // 转换坐标
                        DataUtils.convertStationListByBaiduLoc();

                        // 刷新站点展示
                        showView4Map();

                        moveToAlarmStation(alarmStationid);

                    } else {
                        showToast(DataUtils.getReturnMsg(value));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("更新站点数据失败");
                    moveToAlarmStation(alarmStationid);
                }
            }
        });
    }


    private void moveToAlarmStation(String alarmStationid) {
        // 着重展示告警站点 alarmStationid
        if (!TextUtils.isEmpty(alarmStationid)) {
            StationBean alarmBean = getStationByStaionId(alarmStationid);
            LatLng alarmLatlng = alarmBean.getPosition();
            ms = new MapStatus.Builder().target(alarmLatlng).zoom(scaleMax).build();
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
            showInfo4Station(alarmLatlng, alarmBean.getStationName());
            DialogUtil.bottomDialog(context, alarmBean);
        }
    }


    /**
     * 根据站点id，获取站点bean
     */
    private StationBean getStationByStaionId(String stationid) {
        StationBean bean = null;
        for (StationBean item : application.AllstationObj.getDataList()) {
            if (item.getStationId().equals(stationid)) {
                bean = item;
            }
        }
        return bean;
    }


    private void initSpinnerData() {
        regionList = application.regionList;
        if (!provinceList.isEmpty()) {
            provinceList.clear();
        }
        for (RegionBean region : regionList) {
            if (region.getParentId().equals("0")) {
                provinceList.add(region);
            }
        }
        provinceList.add(0, new RegionBean("", "", "全部", "", ""));
    }


    /**
     * 初始化所有Spinner的事件
     */
    private void initSpinnerListener() {
        spinner_prov.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 重置
                cityAdapter.setDataList(null);
                cityAdapter.notifyDataSetChanged();

                distAdapter.setDataList(null);
                distAdapter.notifyDataSetChanged();

                // 1 city更新数据适配
                if (provinceList != null && !provinceList.isEmpty()) {
                    provinceBean = provinceList.get(position);
                }

                if (provinceBean == null || provinceBean.getRegionId() == null) {
                    provinceBean = new RegionBean();
                }
                // 重新选择后，初始化这些缓存变量
                cityBean = null;
                districtBean = null;

                List<RegionBean> cities = getChildListByRegionId(provinceBean.getRegionId());
                if (cities == null) {
                    return;
                }
                cities.add(0, new RegionBean("", "", "全部", "", ""));
                cityAdapter.setDataList(cities);
                cityAdapter.notifyDataSetChanged();

                // 2 dist更新数据适配
                if (cities != null && !cities.isEmpty()) {
                    cityBean = cities.get(0);
                }

                if (cityBean == null || cityBean.getRegionId() == null) {
                    cityBean = new RegionBean();
                }

                List<RegionBean> dists = getChildListByRegionId(cityBean.getRegionId());
                if (dists == null) {
                    return;
                }
                dists.add(0, new RegionBean("", "", "全部", "", ""));
                distAdapter.setDataList(dists);
                distAdapter.notifyDataSetChanged();


                // 3 打印所有选择的数据
                districtBean = (dists == null || dists.isEmpty()) ? new RegionBean() : dists.get(0);
                Log.i(TAG, provinceBean.getRegionName() + "," +
                        cityBean.getRegionName() + "," +
                        districtBean.getRegionName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 重置
                distAdapter.setDataList(null);
                distAdapter.notifyDataSetChanged();

                // 1 dist更新数据适配
                List<RegionBean> cityes = getChildListByRegionId(provinceBean.getRegionId());
                if (cityes == null || cityes.isEmpty()) {
                    return;
                }
                cityes.add(0, new RegionBean("", "", "全部", "", ""));
                cityBean = cityes.get(position);
                if (cityBean == null) {
                    cityBean = new RegionBean();
                }
                List<RegionBean> dists = getChildListByRegionId(cityBean.getRegionId());
                if (dists == null) {
                    return;
                }
                dists.add(0, new RegionBean("", "", "全部", "", ""));
                distAdapter.setDataList(dists);
                distAdapter.notifyDataSetChanged();

                // 2 打印所有选择的数据
                districtBean = (dists == null || dists.isEmpty()) ? new RegionBean() : dists.get(0);
                Log.i(TAG, provinceBean.getRegionName() + "," +
                        cityBean.getRegionName() + "," +
                        districtBean.getRegionName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner_dist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<RegionBean> temps = getChildListByRegionId(cityBean.getRegionId());
                if (temps == null) {
                    return;
                }
                temps.add(0, new RegionBean("", "", "全部", "", ""));
                districtBean = temps.get(position);
                if (districtBean == null) {
                    districtBean = new RegionBean();
                }
                Log.i(TAG, provinceBean.getRegionName() + "," +
                        cityBean.getRegionName() + "," +
                        districtBean.getRegionName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    /**
     * 获取最子层的region_id
     */
    private List<RegionBean> getChildListByRegionId(String id) {
        List<RegionBean> list = new ArrayList<RegionBean>();
        if (TextUtils.isEmpty(id) || regionList == null || regionList.isEmpty()) {
            return null;
        }
        for (RegionBean bean : regionList) {
            if (id.equals(bean.getParentId())) {
                list.add(bean);
            }
        }
        return list;
    }


    /**
     * 自定义Marker的点击事件，
     */
//    private void registBaiduMapEvent() {
//
//        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//            public boolean onMarkerClick(final Marker marker) {
//
//                LatLng ll = marker.getPosition();
//                mBaiduMap.hideInfoWindow();
//
//                // 1 地图移动到当前位置 -- 根据经纬度确定点坐标
//                BDLocation location = new BDLocation();
//                location.setLongitude(ll.longitude);
//                location.setLatitude(ll.latitude);
//
//                Bundle bundle = marker.getExtraInfo();
//                String value = bundle.getString("type");
//
//                if (TextUtils.isEmpty(value)) {
//                    // 不处理
//                } else if (value.equals(markerType_station)) { // 站点
//
//                    // 弹Popupwindow到Marker上
//                    showInfo4Station(ll, "保定智博园门口站");
//
//                    // 位置信息：
//                    String msg = "经度：" + DataUtils.getFormat6(ll.longitude)
//                            + ", 纬度：" + DataUtils.getFormat6(ll.latitude);
//
//                    // 2 弹出对话框
//                    DialogUtil.bottomDialog(context,
//                            "",
//                            "站点M",
//                            "保定市新市区急救中心附近",
//                            msg,
//                            "叶良辰",
//                            "18812344321",
//                            "12v",
//                            "GPRS"
//                    );
//                } else if (value.equals(markerType_trace)) { // 轨迹
//                    showInfo4Trace(ll, "2017-4-23 13:55:21");
//                }
//
//                MapUtils.setStatusAtLatLng(mBaiduMap, ll);
//
//                return true;
//            }
//        });
//
//    }


    /**
     * 在 站点 Marker上展示信息
     *
     * @param ll   坐标
     * @param name 站点名称
     */
    private void showInfo4Station(LatLng ll, String name) {
        TextView tv = new TextView(context);
        tv.setBackgroundResource(R.drawable.bg_red_corner);
        tv.setTextColor(popupTxtColor);
        tv.setTextSize(12);
        tv.setLayoutParams(new RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setPadding(12, 6, 12, 6);
        tv.setText(name);
        mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(tv), ll, sta_offset, null);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    /**
     * 在 轨迹 Marker上展示信息
     *
     * @param ll    百度坐标，用于地图显示
     * @param llStr Gps坐标，用于tv上展示
     * @param time  时间
     */
    private void showInfo4Trace(LatLng ll, String llStr, String time) {
        TextView tv = new TextView(context);
        tv.setBackgroundResource(R.drawable.bg_bluesky_corner);
        tv.setTextColor(popupTxtColor);
        tv.setTextSize(12);
        String content = "";
        tv.setLayoutParams(new RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        content = llStr + "\n时间：" + time;
        tv.setPadding(12, 6, 12, 6);
        tv.setText(content);
        mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(tv), ll, trace_offset, null);
        mBaiduMap.showInfoWindow(mInfoWindow);

    }


    /**
     * 隐藏地图上的infoWindow信息
     */
    private void hideInfoAll() {
        clickedStation = null;
        mBaiduMap.hideInfoWindow();
        isShowTraceInfo = false;
    }


    /**
     * todo 获取所有站点列表，展示到地图上
     */
    private void showView4Map() {
        Log.i(TAG, "返回全部站点数据，刷新地图");
        // 1 清除地图上的站点标记
//        clearOverlay();

        // 2 加载新的聚合点
        // 询问是否打开全部，还是打开300个
        initCluster(application.AllstationObj.getDataList());

        // 数据不统一，数据偏移，
//        if (clickedStation != null) {
//            StationBean item = getStationByStaionId(clickedStation.getStationId());
//            showInfo4Station(item.getPosition(), item.getStationName());
//        }

    }

    /**
     * 根据已选条件，检索站点列表
     *
     * @param regionId    最子层的区域id，countyId
     * @param stationName 用户模糊查询的站点名称
     */
    private void requestStationList(String regionId, String stationName) {
        if (swipy_layout.getVisibility() == View.VISIBLE) {
            showProgressDialog();
        }

        String url = DataUtils.do4StationList(application.getUid(), regionId, stationName, curPage);

        doRequestGet(url, new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                hideProgressDialog();
                showToast(failTip + "，查询站点失败");
                e.printStackTrace();
                swipy_layout.setRefreshing(false);

            }

            @Override
            public void onResponse(String value, int id) {
                hideProgressDialog();
                swipy_layout.setRefreshing(false);
                Log.i(TAG, "检索站点列表 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {
                        String content = DataUtils.getReturnData(value);
                        List<StationBean> list = JSONObject.parseObject(content, ReturnDataBean.class).getDataList();

                        if (list == null || list.isEmpty()) {
                            return;
                        }
                        // 转换坐标
                        int i = 0;
                        for (StationBean bean : list) {
                            StationBean tmp = StationBean.convert2BaiduLocBean(bean);
                            list.set(i, tmp);
                            i++;
                        }

                        if (curPage == 1) {
                            stationList = list;
                        } else {
                            if (stationList == null) {
                                stationList = new ArrayList<StationBean>();
                            }
                            stationList.addAll(list);
                        }
                        showView4StaitonList();
                    } else {
                        showToast(DataUtils.getReturnMsg(value));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("未查询到符合条件的站点");
                }
            }
        });
    }


    /**
     * 将条件筛选到的站点列表，展示到listview
     */
    private void showView4StaitonList() {
        try {
            stationAdapter.setDataList(stationList);
            stationAdapter.notifyDataSetChanged();
            swipy_layout.setRefreshing(false);

//            // todo 设置第一个点还是最后一个点，目前是最后一个
            selectedLatLng = stationList.get(stationList.size() - 1).getPosition();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "站点信息有误，请重试");
//            showToast("没有站点");
        }
    }


    /**
     * 获取某站点的最新轨迹
     */
    private void requestTraceNew(String stationid) {
//        showProgressDialog();
        String url = DataUtils.do4TraceNew(stationid);

        doRequestGet(url, new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                hideProgressDialog();
                showToast(failTip + ", 查询实时轨迹失败");
                e.printStackTrace();
                swipy_layout.setRefreshing(false);

            }

            @Override
            public void onResponse(String value, int id) {
                hideProgressDialog();
                swipy_layout.setRefreshing(false);
                Log.i(TAG, "获取某站点的最新轨迹 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {
                        String listStr = DataUtils.getReturnList(value);
                        List<TraceBean> list = JSONArray.parseArray(listStr, TraceBean.class);
                        if (list != null && !list.isEmpty()) {
                            list = DataUtils.convertTraceListByBaiduLoc(list);
                            showTraceMarkers(list, true);
                        }
                    } else {
                        showToast(DataUtils.getReturnMsg(value));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("未查询到实时轨迹");
                }
            }
        });
    }


    /**
     * 获取某站点的历史轨迹
     */
    private void requestTraceHistory(String stationid, String timeFrom, String timeTo) {
        showProgressDialog();
        showToast("查询历史轨迹请求");
        String url = DataUtils.do4TraceHistory(stationid, timeFrom, timeTo);

        doRequestGet(url, new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                hideProgressDialog();
                showToast(failTip + ", 查询历史轨迹失败");
                e.printStackTrace();
                swipy_layout.setRefreshing(false);

            }

            @Override
            public void onResponse(String value, int id) {
                hideProgressDialog();
                swipy_layout.setRefreshing(false);
                Log.i(TAG, "获取某站点的历史轨迹 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {
                        String listStr = DataUtils.getReturnList(value);
                        List<TraceBean> list = JSONArray.parseArray(listStr, TraceBean.class);
                        if (list == null || list.isEmpty()) {
                            return;
                        }

                        list = DataUtils.convertTraceListByBaiduLoc(list);

                        if (list.size() > Finals.Max_Show_Trace_Count) {
                            showDialog4HisTrace(list);
                        } else {
                            showTraceMarkers(list, false);
                        }

                    } else {
                        showToast(DataUtils.getReturnMsg(value));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("未查询到历史轨迹");
                }
            }
        });
    }


    /**
     * 打开确认对话框--询问是否打开全部，还是打开300个
     */
    public void showDialog4HisTrace(final List<TraceBean> traceList) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View mView = layoutInflater.inflate(R.layout.dialog_sure, null);
        TextView dialog_sure_tv = (TextView) mView.findViewById(R.id.dialog_sure_tv);
        dialog_sure_tv.setText("请选择要展示多少轨迹点？");
        mBuilder.setView(mView);
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("最近" + Finals.Max_Show_Trace_Count + "个", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isShowAllTrace = false;
                showTraceMarkers(traceList, false);
            }
        });

        mBuilder.setNeutralButton("全部", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isShowAllTrace = true;
                showTraceMarkers(traceList, false);
            }
        });
        AlertDialog sureDialog = mBuilder.create();
        sureDialog.show();
    }


    /**
     * 展示轨迹，并连线
     * 增加到Overlay层，然后绘制
     *
     * @param isCurrentTrace 是最新轨迹还是历史轨迹  true：当前轨迹， false：历史轨迹
     */
    private void showTraceMarkers(List<TraceBean> traceList, boolean isCurrentTrace) {

        Log.i(TAG, "返回轨迹点数据，刷新地图");
        clearLineMap(); // 先清除原有轨迹点

        List<TraceBean> tmpList = null; // 保存经过截取后的轨迹点列表
        int allTraceCount = traceList.size(); // 所有轨迹的长度
        if (isCurrentTrace) {// 当前最新轨迹
            tmpList = traceList;
        } else { // 历史轨迹
            if (isShowAllTrace) { // 所有点
                tmpList = traceList;
            } else { // 最近的N个点
                if (allTraceCount <= Finals.Max_Show_Trace_Count) {
                    tmpList = traceList;
                } else {
//                    tmpList = traceList.subList(allTraceCount - Finals.Max_Show_Trace_Count, allTraceCount);
                    tmpList = traceList.subList(0, Finals.Max_Show_Trace_Count);
                }
            }
        }

        // 提取坐标值，创建LatLng
        List<LatLng> latLngList = new ArrayList<LatLng>(); // 坐标值列表
        for (TraceBean item : tmpList) {
            latLngList.add(new LatLng(item.getBdLat(), item.getBdLon()));
//                    Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude())));
        }
        if (markerTraceList == null) {
            markerTraceList = new ArrayList<OverlayOptions>();
        }
        if (!markerTraceList.isEmpty()) {
            markerTraceList.clear();
        }
        for (LatLng bean : latLngList) {
            MarkerOptions option = new MarkerOptions();
            option.icon(bdFoot).zIndex(trace_zIndex).draggable(true).position(bean);
            markerTraceList.add(option);

        }

        if (traceOverLayList != null) {
            traceOverLayList.clear();
        }
        traceOverLayList = mBaiduMap.addOverlays(markerTraceList);


        for (int j = 0; j < tmpList.size(); j++) {
            Overlay lay = traceOverLayList.get(j);
            TraceBean traceItem = tmpList.get(j);

            String lat = DataUtils.getFormat6(traceItem.getBdLat()) + "";
            String lng = DataUtils.getFormat6(traceItem.getBdLon()) + "";

            String time = traceItem.getCurrentTime();
            Bundle bundle = new Bundle();
            bundle.putString("type", markerType_trace);
            bundle.putString("time", time);
            // bundle.putString("index", j+"");
            bundle.putString("lat", lat);
            bundle.putString("lng", lng);
            lay.setExtraInfo(bundle);
            traceOverLayList.set(j, lay);

        }

        // 绘制折线
        OverlayOptions ooPolyline = new PolylineOptions()
                .width(lineWidth)
                .color(lineColor)
                .points(latLngList);
        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);


        // 初始化轨迹点的点击事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {

                Bundle bundle = marker.getExtraInfo();
                if (bundle != null) {
                    String value = bundle.getString("type");
                    String time = bundle.getString("time");
                    // String index = bundle.getString("index");

                    String lat = bundle.getString("lat");
                    String lng = bundle.getString("lng");

                    String llStr = "经度：" + lng + ", 纬度：" + lat;

                    if (value.equals(markerType_trace)) {
                        mBaiduMap.hideInfoWindow();
                        LatLng ll = marker.getPosition();
                        // MapUtils.setStatusAtLatLng(mBaiduMap, ll);
                        showInfo4Trace(ll, llStr, time);
                        isShowTraceInfo = true;

                    }
                }

                return true;
            }
        });

        // 如果是历史轨迹，定位到最后一个轨迹
        if (!isCurrentTrace) {
            ms = new MapStatus.Builder().target(latLngList.get(latLngList.size() - 1)).zoom(scaleMax).build();
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
        }

    }


//    /**
//     * 根据获取到的站点列表，绘制Marker图钉，
//     */
//    public void initStationsMap() {
//
//        if (stationMarkerList != null) {
//            stationMarkerList.clear();
//        }
//
//        // add marker overlay
//        LatLng llA = new LatLng(38.927763, 115.469205);
////        LatLng llB = new LatLng(38.927413, 115.470613);
////        LatLng llC = new LatLng(38.927279, 115.474200);
////        LatLng llD = new LatLng(38.926858, 115.474083);
//
////        MarkerOptions optionStation = new MarkerOptions().position(llA).icon(bdStation).zIndex(sta_zIndex)
////                .draggable(true);
////        // .rotate(30)
////        optionStation.animateType(MarkerOptions.MarkerAnimateType.grow);
////        markerNormal = (Marker) (mBaiduMap.addOverlay(optionStation));
////        Bundle bundle = new Bundle();
////        bundle.putString("type", markerType_station);
////        markerNormal.setExtraInfo(bundle);
//
//        Bundle bundle = new Bundle();
//        bundle.putString("type", markerType_station);
//        BitmapDescriptor tempBd = null;
//
//        for (StationBean bean : stationList) {
//            // todo 判断是否告警
//            if (bean.getIs_alarm().equals("1")) {
////                bundle.putString("type", markerType_alarm);
//                tempBd = bdAlarm;
//            } else {
////                bundle.putString("type", markerType_station);
//                tempBd = bdStation;
//            }
//
//            llA = new LatLng(Double.parseDouble(bean.getLat()), Double.parseDouble(bean.getLon()));
//            MarkerOptions optionStation = new MarkerOptions().position(llA).icon(tempBd).zIndex(sta_zIndex)
//                    .draggable(true);
//            optionStation.animateType(MarkerOptions.MarkerAnimateType.grow);
//            Marker mark = (Marker) (mBaiduMap.addOverlay(optionStation)); // markerNormal
//            mark.setExtraInfo(bundle);
//            stationMarkerList.add(mark);
//
//        }
//
//        selectedLatLng = llA;
//
//        // 当前地图定位到这一点 -- 告警点
//        backCurrentLocation();
//
//    }


    /**
     * 回到用户所在位置
     */
    private void backCurrentLocation() {
        if (myLatlng.longitude != 0 && myLatlng.latitude != 0) {
            MapUtils.setStatusAtLatLng(mBaiduMap, myLatlng);
        }
    }


    /**
     * 只清除各种轨迹展示，不操作其他Marker
     * todo 还有问题，不能清除脚印Marker
     */
    private void clearLineMap() {
        // 擦除直线
        if (mPolyline != null) {
            mPolyline.remove();
        }

        // 擦除轨迹点-脚印
        if (traceOverLayList != null && !traceOverLayList.isEmpty()) {
            for (Overlay lay : traceOverLayList) {
                lay.remove(); // 逐个删除Overlay对象
            }

            traceOverLayList.clear();
        }

        if (markerTraceList != null) {
            markerTraceList.clear();
        }

    }


    /**
     * 切换地图和站点列表的展示
     */
    private void switchMapOrStation(int flag) {
        switch (flag) {
            case FLAG_MAP:
                layout_mapview.setVisibility(View.VISIBLE);
                if (hasOpenTraceHis) {
                    tv_select_time.setVisibility(View.VISIBLE);
                } else {
                    tv_select_time.setVisibility(View.GONE);
                }
                swipy_layout.setVisibility(View.GONE);
                break;
            case FLAG_STATION:
                if (!swipy_layout.isRefreshing()) {
                    String name = et_name.getText().toString().trim();
                    requestStationList(getCurrentChildId(), name);
                }
                layout_mapview.setVisibility(View.GONE);
                tv_select_time.setVisibility(View.GONE);
                swipy_layout.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public void onClick(int resId) {
        switch (resId) {
            case R.id.btn_search: // 检索站
                curPage = 1;
                String name = et_name.getText().toString().trim();
                requestStationList(getCurrentChildId(), name);
                showProgressDialog();
                break;

            case R.id.tv_alarm_tip: // 根据告警信息，刷新站点
                requestAllStationList(curAlarmStationId);
                tv_alarm_tip.setVisibility(View.GONE);
                tv_alarm_tip.clearAnimation();
                NotifyUtil.clearNotification(); // 点击刷新按钮，清除所有告警
                break;
            case R.id.tv_select_time: // 选择历史轨迹的起止时间
                showDialogStart();
                break;

        }
    }

    /**
     * 初始化 历史轨迹请求 起止时间 设置
     */
    private void initTraceHisTime() {
        endTime4TraceHis = DataUtils.getFormatToday();
        startTime4TraceHis = DataUtils.getFormatTime(-1);

    }


    DateChooseWheelViewDialog startDateChooseDialog = null;
    DateChooseWheelViewDialog endDateChooseDialog = null;

    private String startTime4TraceHis = "";  // 查询历史轨迹开始时间
    private String endTime4TraceHis = "";   // 查询历史轨迹结束时间
    private String stationId4TraceHis; // 历史轨迹查询的站点

    /**
     * 选择开始时间 -
     */
    private void showDialogStart() {
        if (startDateChooseDialog == null) {
            startDateChooseDialog = new DateChooseWheelViewDialog(context,
                    new DateChooseWheelViewDialog.DateChooseInterface() {
                        @Override
                        public void getDateTime(String time, boolean longTimeChecked) {
                            startTime4TraceHis = time;
                            showDialogStop();
                        }
                    });
            startDateChooseDialog.setDateDialogTitle("开始时间");
        }
        if (!TextUtils.isEmpty(startTime4TraceHis)) {
            startDateChooseDialog.setDefaultValue(startTime4TraceHis);
        }
        startDateChooseDialog.showDateChooseDialog();
    }

    /**
     * 选择结束时间
     */
    private void showDialogStop() {
        if (endDateChooseDialog == null) {
            endDateChooseDialog = new DateChooseWheelViewDialog(context,
                    new DateChooseWheelViewDialog.DateChooseInterface() {
                        @Override
                        public void getDateTime(String time, boolean longTimeChecked) {
                            endTime4TraceHis = time;
                            requestTraceHistory(stationId4TraceHis, startTime4TraceHis, endTime4TraceHis);
                        }
                    });
            endDateChooseDialog.setDateDialogTitle("结束时间");
        }
        if (!TextUtils.isEmpty(endTime4TraceHis)) {
            endDateChooseDialog.setDefaultValue(endTime4TraceHis);
        }
        endDateChooseDialog.showDateChooseDialog();
    }


    /**
     * 获取当前最子层的区域id, regionId默认值是0
     */
    private String getCurrentChildId() {
        String tmp = "0";
        if (districtBean == null || TextUtils.isEmpty(districtBean.getRegionId())) {
            if (cityBean == null || TextUtils.isEmpty(cityBean.getRegionId())) {
                if (provinceBean != null) {
                    tmp = provinceBean.getRegionId();
                }
            } else {
                tmp = cityBean.getRegionId();
            }
        } else {
            tmp = districtBean.getRegionId();
        }
        if (TextUtils.isEmpty(tmp)) {
            tmp = "0";
        }
        return tmp;
    }


    @Override
    protected void onPause() {
        mapview.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mapview.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopAllTask();
        super.onDestroy();

    }


    private void stopAllTask() {
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mapview.onDestroy();
        // 回收 bitmap 资源
        bdFoot.recycle();
        bdStation.recycle();
        bdAlarm.recycle();

        // 关闭轮询任务 --> 所有站点状态
//        PollUtils.stopPollingService(context, PollingStationService.class, PollingStationService.ACTION);

        // 关闭轮询任务 --> 最新轨迹状态
//        PollUtils.stopPollingService(context, PollingTraceService.class, PollingTraceService.ACTION);
        stopStationTask();
        stopTraceTask();

        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);

        tv_alarm_tip.setVisibility(View.GONE);
        tv_alarm_tip.clearAnimation();

    }


    /**
     * 自定义 右上角的菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /**
     * 如果已通过onCreateOptionsMenu()自定义了含icon的Menu，那这个方法将不再生效
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            goActivity(StationListActivity.class, null);
            // 历史告警需要 传 stationid，从底部对话框里放入口，这里屏蔽掉
        } else if (item.getItemId() == R.id.action_history) {
            Bundle bbb = DataUtils.createBundleWithData("isHistory", true);
            goActivity(AlarmListActivity.class, bbb);
        } else if (item.getItemId() == R.id.action_alarm) {
            goActivity(AlarmListActivity.class, null);
        } else if (item.getItemId() == R.id.action_mine) {
//            goActivityForResult(MineActivity.class, null, Code_Request_Mine);
            goActivity(MineActivity.class, null);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (requestCode == Code_Request_Mine) {
                if (bundle != null && bundle.getBoolean("hasExit")) {
                    finish();
                }
            }
        }
    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 再按一次退出
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showToast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                stopAllTask();
                application.exit();
                finish();
            }
            return true;
        }

        // // 最小化程序
        // if (keyCode == KeyEvent.KEYCODE_BACK) {
        // Intent intent = new Intent(Intent.ACTION_MAIN);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // intent.addCategory(Intent.CATEGORY_HOME);
        // startActivity(intent);
        // return true;
        // }

        return super.onKeyDown(keyCode, event);
    }


    /**
     * 导航路线计划
     */
    private void routeplanToNavi(StationBean bean) {
        if (!hasInitSuccess) {
//            showToast("导航尚未初始化!");
        }
        // 权限申请
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // 保证导航功能完备
            if (!hasCompletePhoneAuth()) {
                if (!hasRequestComAuth) {
                    hasRequestComAuth = true;
                    this.requestPermissions(authComArr, authComRequestCode);
                    return;
                } else {
                    showToast("没有开启导航需要的完备的权限!");
                }
            }
        }

        if (bean != null) {
            if (myLatlng == null || myLatlng.longitude == 0 || myLatlng.latitude == 0) {
                myLatlng = selectedLatLng;
            }
            BNRoutePlanNode sNode = null;
            BNRoutePlanNode eNode = null;
            // coType = GCJ02  WGS84  BD09_MC  BD09LL
            sNode = new BNRoutePlanNode(myLatlng.longitude, myLatlng.latitude, "我的位置", null, BD09LL);
            eNode = new BNRoutePlanNode(bean.getBdLon(), bean.getBdLat(), bean.getStationName(), null, BD09LL);

            if (sNode != null && eNode != null) {
                List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
                list.add(sNode);
                list.add(eNode);
                BaiduNaviManager.getInstance().launchNavigator(
                        this, list, 1, true, new DemoRoutePlanListener(sNode));
            }
        }
    }


    private void initNavi() {

        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }

        BaiduNaviManager.getInstance().init(
                this,
                Finals.FilePath,
                Finals.MapPathName.replace("/", ""),
                new BaiduNaviManager.NaviInitListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        if (0 == status) {
                            authinfo = "key校验成功!";
                        } else {
                            authinfo = "key校验失败, " + msg;
                        }
                        Log.i(TAG, authinfo);
                    }

                    public void initSuccess() {
                        Log.i(TAG, "百度导航引擎初始化成功");
                        hasInitSuccess = true;
                        initSetting();
                    }

                    public void initStart() {
                        Log.i(TAG, "百度导航引擎初始化开始");
                    }

                    public void initFailed() {
                        Log.i(TAG, "百度导航引擎初始化失败");
                    }

                },
                null, // mTTSCallback  null
                ttsHandler,
                ttsPlayStateListener); // ttsPlayStateListener null

    }


    /**
     * 导航初始化： todo 静音问题
     */
    private void initSetting() {

        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager.setShowTotalRoadConditionBar(
                BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        BNaviSettingManager.setIsAutoQuitWhenArrived(true);

        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音  地图的appid：9497689  tts语音的appid：17737
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "9804044"); //  9801340  ---9497689  9354030
        BNaviSettingManager.setNaviSdkParam(bundle);
    }


    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            /**
             * 设置途径点以及resetEndNode会回调该接口
             */
            for (Activity ac : activityList) {
                if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {
                    return;
                }
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, mBNRoutePlanNode);
            goActivity(BDNaviGuideActivity.class, bundle);

        }

        @Override
        public void onRoutePlanFailed() {
            showToast("算路失败");
        }
    }


    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    Log.i(TAG, "Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    Log.i(TAG, "Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };


    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

        @Override
        public void stopTTS() {
            Log.i("test_TTS", "stopTTS");
        }

        @Override
        public void resumeTTS() {
            Log.i("test_TTS", "resumeTTS");
        }

        @Override
        public void releaseTTSPlayer() {
            Log.i("test_TTS", "releaseTTSPlayer");
        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            Log.i("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);
            return 1;
        }

        @Override
        public void phoneHangUp() {
            Log.i("test_TTS", "phoneHangUp");
        }

        @Override
        public void phoneCalling() {
            Log.i("test_TTS", "phoneCalling");
        }

        @Override
        public void pauseTTS() {
            Log.i("test_TTS", "pauseTTS");
        }

        @Override
        public void initTTSPlayer() {
            Log.i("test_TTS", "initTTSPlayer");
        }

        @Override
        public int getTTSState() {
            Log.i("test_TTS", "getTTSState");
            return 1;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == authBaseRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                } else {
                    showToast("缺少导航基本的权限");
                    return;
                }
            }
            initNavi();
        } else if (requestCode == authComRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                }
            }
            showToast("权限已开启，可以进行导航！");
        }

    }


    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener =
            new BaiduNaviManager.TTSPlayStateListener() {

                @Override
                public void playEnd() {
                }

                @Override
                public void playStart() {
                }
            };

    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean hasCompletePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authComArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    // 初始化地图时加载到故宫
    @Override
    public void onMapLoaded() {
        ms = new MapStatus.Builder().target(myLatlng).zoom(14).build();// .target(myLatlng)
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));

    }

    /**
     * 是否是第一次加载地图
     */
    private boolean isFirstTime = true;

    /**
     * 初始化
     */
    private void initCluster(List<StationBean> list) {
        Log.i(TAG, "所有站点个数 =" + list.size());

        // target的坐标：myLatlng,或者是list的第一个的坐标位置
        if (isFirstTime) {
            isFirstTime = false;
            ms = new MapStatus.Builder().target(myLatlng).zoom(scaleDefault).build(); //
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));

        }

        // 定义点聚合管理类ClusterManager
        if (mClusterManager == null) {
            mClusterManager = new ClusterManager<StationBean>(this, mBaiduMap);
        }

        // 清除站点Marker
        mClusterManager.clearItems();  // 1 清空历史Items
        mClusterManager.getMarkerCollection().clear();
        mClusterManager.getClusterMarkerCollection().clear();

        mClusterManager.addItems(list);// 2 增加新的Items
        mClusterManager.cluster();     // 3 强制刷新点聚合地图展示
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mBaiduMap.getMapStatus()));

        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);

        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);

        // 点击点聚合的蓝色圆圈
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<StationBean>() {
            @Override
            public boolean onClusterClick(Cluster<StationBean> cluster) {
                Log.i(TAG, "有" + cluster.getSize() + "个站点");
                return false;
            }
        });

        // 点击站点Marker
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<StationBean>() {
            @Override
            public boolean onClusterItemClick(StationBean item) {

                mBaiduMap.hideInfoWindow();
                clickedStation = item;

                showInfo4Station(item.getPosition(), item.getStationName());// 展示infoWindow
                DialogUtil.bottomDialog(context, item);// 2 弹出对话框
                return false;
            }
        });

        if (isPush && pushBean != null) { // pushBean.getStationid()
            StationBean bean = getStationByStaionId(pushBean.getStationid());
            showInfo4Station(bean.getPosition(), bean.getStationName());
            DialogUtil.bottomDialog(context, bean);
            isPush = false;
            pushBean = null;
        }

    }


    /**
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mBDLocListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {

            // map view 销毁后不在处理新接收的位置
            if (location != null
                    && location.getLocType() != BDLocation.TypeServerError
                    && location.getLatitude() != 0
                    && location.getLongitude() != 0) {

                Log.i(TAG, "更新定位信息");
                String lat = DataUtils.getFormat6(location.getLatitude());
                String lon = DataUtils.getFormat6(location.getLongitude());

                // 这是非法数据,不处理
                if (location.getLatitude() < 1
                        && location.getLatitude() > -1
                        && DataUtils.getlenStr(lat, 6).equals("000000")
                        && location.getLongitude() < 1
                        && location.getLongitude() > -1
                        && DataUtils.getlenStr(lon, 6).equals("000000")) {

                } else {
                    myLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                }
                setLocateOverLayOnMapView(location);
            } else {
                setLocateOverLayOnMapView(location);
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };


    private void setLocateOverLayOnMapView(BDLocation location) {
        if (mapview != null) {
            location.setLongitude(myLatlng.longitude);
            location.setLatitude(myLatlng.latitude);

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);


        }
    }


    /**
     * 开启了实时轨迹
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealTraceRecentEvent(TraceRecentEvent event) {

        String id = event.getStationid();
        if (TextUtils.isEmpty(id)) {
            return;
        }
        application.curStationId4Trace = id;

        // 1 先关闭轮询站点请求任务
//        PollUtils.stopPollingService(context, PollingStationService.class, PollingStationService.ACTION);
//        PollUtils.stopPollingService(context, PollingTraceService.class, PollingTraceService.ACTION);

        // 2 同时开启轮询轨迹任务 --> 最新轨迹
//        PollUtils.startPollingService(context, Finals.Time_Station_Polling, PollingStationService.class, PollingStationService.ACTION);
//        PollUtils.startPollingService(context, Finals.Time_Station_Polling, PollingTraceService.class, PollingTraceService.ACTION);

        stopStationTask();
        stopTraceTask();

        startStationTask(0, Finals.Time_Station_Polling * 1000);
        startTraceTask(0, Finals.Time_Station_Polling * 1000);

    }


    /**
     * 请求历史轨迹
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealTraceHistoryEvent(TraceHistoryEvent event) {
        hasOpenTraceHis = true;
//        tv_trace.setVisibility(View.VISIBLE);
        tv_select_time.setVisibility(View.VISIBLE);
        stationId4TraceHis = event.getStationid();
        showDialogStart();

        // 擦除轨迹和轨迹上的info
        clearLineMap();

        // todo 是否正在展示轨迹的info 基于函数：clearLineMap()
        if (isShowTraceInfo) {
            hideInfoAll();
        }

    }


    /**
     * 点击告警列表，定位到告警站点
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void dealAlarmBean(AlarmBean bean) {
        rg_map_stations.check(R.id.rb_map);
        // todo 返回MapView，并定位到选中的站点
        String tmpId = bean.getStationId();

        StationBean item = getStationByStaionId(tmpId);

        showInfo4Station(item.getPosition(), bean.getStationName());
        ms = new MapStatus.Builder().target(item.getPosition()).zoom(scaleMax).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));// 最大zoom

    }


//    /**
//     * 轮询任务-->更新站点状态信息，刷新地图
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void dealStationsRefresh4MapEvent(StationsRefresh4MapEvent event) {
//        if (event != null && event.isFlag()) {
//            Log.i(TAG, "刷新站点地图");
//            showView4Map();
//        }
//    }


//    /**
//     * 轮询任务-->最新轨迹信息，刷新地图
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void dealTarceRefreshEvent(TarceRefreshEvent event) {
//        if (event != null) {
//
//            List<TraceBean> list = event.getList();
//            if (list != null && !list.isEmpty()) {
//                clearLineMap();
//                showTraceMarkers(list, true);
//            } else {
//                showToast("尚无轨迹点!");
//            }
//        }
//    }


//    /**
//     * 导航事件处理 BDNaviEvent
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void dealBDNaviEvent(BDNaviEvent event) {
////        if (!BaiduNaviManager.isNaviInited()) {
////            showToast("导航尚未初始化");
////            initNavi(); // 初始化导航
////        }
//        if (event != null) {
//            StationBean bean = event.getStationBean();
////            routeplanToNavi(bean);
//            goChooseNavi(bean);
//        }
//        showToast("导航1");
//        Log.i(TAG, "导航1");
//    }

    private Animation alphaAnimation;

    private void initAnim() {
        if (alphaAnimation == null) {
            alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(300);
            alphaAnimation.setInterpolator(new LinearInterpolator());
            alphaAnimation.setRepeatCount(Animation.INFINITE);
            alphaAnimation.setRepeatMode(Animation.REVERSE);
        }

    }


    /**
     * 当前告警的站点id
     */
    private String curAlarmStationId;

    /**
     * 服务端推送新告警
     */
    @Subscribe(threadMode = ThreadMode.MAIN , sticky = true)
    public void dealStationAlarmEvent(StationAlarmEvent event) {
//        showToast("处理推送告警！");
        Log.i(TAG, "主页-处理推送告警！");
        PushBaseBean bean = null;
        if (event != null) {
            bean = event.getPushBean();
        }

        // 清除app内缓存的饿告警状态
        MyApplication.getInstance().hasNewPush = false;
        MyApplication.getInstance().pushBean = null;

        if (application.hasPushed(bean)) {
            return;
        }

        /**
         * todo 展示一个按钮，点击后可展示告警站点
         */
        curAlarmStationId = bean.getStationid();
        initAnim();
        tv_alarm_tip.setVisibility(View.VISIBLE);
        tv_alarm_tip.startAnimation(alphaAnimation);

    }


    private Timer time_station = null; // 定时器
    private TimerTask task_station = null;

    private void startStationTask(long delay, long period) {
        if (time_station == null) {
            time_station = new Timer();
        }

        if (task_station == null) {
            task_station = new TimerTask() {
                @Override
                public void run() {
                    taskHandler.sendMessage(new Message());
                }
            };
        }

        if (time_station != null && task_station != null) {
            time_station.schedule(task_station, delay, period);
        }
    }


    private Handler taskHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            showToast("轮询站点任务");
            Log.i(TAG, "轮询站点任务");
            requestAllStationList("");
        }
    };


    private void stopStationTask() {
        if (time_station != null) {
            time_station.cancel();
            time_station = null;
        }
        if (task_station != null) {
            task_station.cancel();
            task_station = null;
        }
    }


    private Timer time_trace = null; // 重连定时器
    private TimerTask task_trace = null;

    private void startTraceTask(long delay, long period) {
        if (time_trace == null) {
            time_trace = new Timer();
        }

        if (task_trace == null) {
            task_trace = new TimerTask() {
                @Override
                public void run() {
                    traceTaskHandler.sendMessage(new Message());
                }
            };
        }

        if (time_trace != null && task_trace != null) {
            time_trace.schedule(task_trace, delay, period);
        }
    }


    private Handler traceTaskHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            showToast("轮询轨迹任务");
            Log.i(TAG, "轮询轨迹任务");
            requestTraceNew(application.curStationId4Trace);
        }
    };


    private void stopTraceTask() {
        if (time_trace != null) {
            time_trace.cancel();
            time_trace = null;
        }
        if (task_trace != null) {
            task_trace.cancel();
            task_trace = null;
        }
    }


//    /**
//     * 跳转至导航，优先级：百度-->高德-->webapp
//     */
//    public void goChooseNavi(StationBean bean) {
//        Log.i(TAG, "进入导航1");
//        if (DataUtils.isPkgInstalled(context, "com.baidu.BaiduMap")) {
//            DataUtils.gotoBaiduNavi(context, selectedLatLng, bean);
//        } else if (DataUtils.isPkgInstalled(context, "com.autonavi.minimap")) {
//            DataUtils.gotoGaodeNavi(context, selectedLatLng, bean);
//        } else {
//            routeplanToNavi(bean);
//        }
//    }


    public void naviByBaidu(StationBean bean) {
        DataUtils.gotoBaiduNavi(context, myLatlng, bean);
    }

    public void naviByGaode(StationBean bean) {
        DataUtils.gotoGaodeNavi(context, myLatlng, bean);
    }

    public void naviBySDK(StationBean bean) {
        routeplanToNavi(bean);
    }

}
