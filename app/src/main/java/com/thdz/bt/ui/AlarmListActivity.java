package com.thdz.bt.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.andrew.datechoosewheelviewdemo.DateChooseWheelViewDialog;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.thdz.bt.R;
import com.thdz.bt.adapter.AlarmListAdapter;
import com.thdz.bt.adapter.SpinnerRegionListAdapter;
import com.thdz.bt.base.BaseActivity;
import com.thdz.bt.bean.AlarmBean;
import com.thdz.bt.bean.RegionBean;
import com.thdz.bt.bean.ReturnAlarmBean;
import com.thdz.bt.event.StationAlarmEvent;
import com.thdz.bt.util.DataUtils;
import com.thdz.bt.util.VUtils;
import com.thdz.bt.view.ChoiceGroup;
import com.thdz.bt.view.NoScrollListView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import okhttp3.Call;

/**
 * todo 目前没在使用
 * 最新告警、历史告警
 * ----------------
 * 最新告警，默认没有任何参数约束，
 * 历史告警，默认有站点id传入 -- 只有时间，类型两个选择条件
 */
public class AlarmListActivity extends BaseActivity {

    @BindView(R.id.scrollview)
    ScrollView scrollview;

    @BindView(R.id.swipy_layout)
    SwipyRefreshLayout swipy_layout; // 刷新

    @BindView(R.id.lv_alarm)
    NoScrollListView lv_alarm; // 列表

    @BindView(R.id.group_type)
    ChoiceGroup group_type;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.btn_search)
    TextView btn_search;

    @BindView(R.id.tv_select_time_start)
    EditText tv_select_time_start;

    @BindView(R.id.tv_select_time_end)
    EditText tv_select_time_end;

    @BindArray(R.array.alarm_type)
    String[] alarm_type; // 告警类型

    @BindArray(R.array.alarm_type_id)
    String[] alarm_type_id; // 告警类型id

    @BindView(R.id.spinner_prov)
    Spinner spinner_prov;
    @BindView(R.id.spinner_city)
    Spinner spinner_city;
    @BindView(R.id.spinner_dist)
    Spinner spinner_dist;

    @BindView(R.id.layout_time_part)
    LinearLayout layout_time_part;

    @BindView(R.id.layout_selectaion_region)
    LinearLayout layout_selectaion_region;

    private AlarmListAdapter alarmAdapter;
    private List<AlarmBean> alarmList;

    private int curPage = 1;


    /**
     * 当前告警类型
     */
    private String type = "-1";  // 要进行查询的告警类型
    /**
     * 当前告警类型
     */
    private String tmpType = "-1";  // 每次选择了的告警类型
    private String startTime;   // 开始时间
    private String endTime;    // 结束时间

    /**
     * 是否是历史告警</br>
     * 如果是历史告警，需要隐藏站点选择这个条件
     * 如果是最新告警，需要展示站点选择。
     */
    private boolean isHistory = false;
    private String msgFail = "未查询到符合条件的告警";


    // -------- 省市县区域 --------

    // 所有区域bean
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
     * 查询历史告警时的站点id
     */
    private String stationId;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_alarmlist);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isHistory = bundle.getBoolean("isHistory", false);
            stationId = bundle.getString("stationid");
        }

        if (isHistory) {
            layout_time_part.setVisibility(View.VISIBLE);
            layout_selectaion_region.setVisibility(View.GONE);
            toolbar.setTitle("历史告警");
        } else {
            layout_time_part.setVisibility(View.GONE);
            layout_selectaion_region.setVisibility(View.VISIBLE);
            toolbar.setTitle("最新告警");
        }

        setToolbarBackEnable(toolbar);

        tv_select_time_start.setOnClickListener(this);
        tv_select_time_end.setOnClickListener(this);

//        tv_select_province.setOnClickListener(this);
//        tv_select_city.setOnClickListener(this);
//        tv_select_county.setOnClickListener(this);

        btn_search.setOnClickListener(this);

        alarmAdapter = new AlarmListAdapter(context);
        lv_alarm.setAdapter(alarmAdapter);

        lv_alarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlarmBean bean = alarmList.get(position);
                EventBus.getDefault().post(bean);
                finish();
            }
        });

        VUtils.setSwipyColor(swipy_layout);
        swipy_layout.setDirection(SwipyRefreshLayoutDirection.BOTH); // 只能从上面刷新
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
                requestAlarmList();

            }
        });

        swipy_layout.setRefreshing(false);

        initType();

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

        initTime();

        requestAlarmList();

    }


    private void initTime() {
        endTime = DataUtils.getFormatToday();
        startTime = DataUtils.getFormatTime(-1);

        tv_select_time_start.setText(startTime);
        tv_select_time_end.setText(endTime);

    }


    private void initSpinnerData() {
        provinceList.clear();
        for (RegionBean region : application.regionList) {
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
        if (TextUtils.isEmpty(id) || application.regionList == null || application.regionList.isEmpty()) {
            return null;
        }
        for (RegionBean bean : application.regionList) {
            if (id.equals(bean.getParentId())) {
                list.add(bean);
            }
        }
        return list;
    }


    /**
     * 初始化类型控件数据
     */
    private void initType() {
        group_type.setColumn(3);//设置列数
        group_type.setValues(alarm_type);//设置记录列表
        group_type.setView(this);//设置视图
        group_type.setInitChecked(0);//设置最初默认被选按钮

    }


    // 获取告警列表
    private void requestAlarmList() {
        showProgressDialog();

        String typeName = group_type.getCurrentValue();
        tmpType = DataUtils.getAlarmTypeIdByName(typeName);

        if (tmpType != type) {
            type = tmpType;
            curPage = 1;
        }

        String url = "";
        if (isHistory) {
            url = DataUtils.do4AlarmHistoryList(stationId, type, startTime, endTime, curPage);
        } else {
            url = DataUtils.do4AlarmCurrentList(application.getUid(), getCurrentChildId(), type, curPage);
        }

        doRequestGet(url, new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                hideProgressDialog();
                showToast(failTip+"， 查询告警数据失败");
                e.printStackTrace();
                swipy_layout.setRefreshing(false);

            }

            @Override
            public void onResponse(String value, int id) {
                hideProgressDialog();
                swipy_layout.setRefreshing(false);
                Log.i(TAG, "获取告警列表 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {

                        String content = DataUtils.getReturnData(value);
                        List<AlarmBean> list = JSONObject.parseObject(content, ReturnAlarmBean.class).getAlarmList();
                        if (list == null || list.isEmpty()) {
                            showToast("查询到的告警为空");
                        }
                        if (curPage == 1) {
                            alarmList = list;
                        } else {
                            if (alarmList == null) {
                                alarmList = new ArrayList<AlarmBean>();
                            }
                            alarmList.addAll(list);
                        }

                        showView();
                    } else {
                        showToast(DataUtils.getReturnMsg(value));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(msgFail);
                }
            }
        });

    }


    private void showView() {
        alarmAdapter.setDataList(alarmList);
        alarmAdapter.notifyDataSetChanged();
        if (curPage == 1) {
            scrollview.smoothScrollTo(0, 0);
        }
    }


    private void showNullView() {
        alarmAdapter.setDataList(null);
        alarmAdapter.notifyDataSetChanged();
    }


//    /**
//     * post请求方式，构造参数
//     */
//    private String createParams() {
//        try {
//            org.json.JSONObject jsonObj = new org.json.JSONObject();
//            jsonObj.put("stationid", getCurrentChildId());
//            jsonObj.put("type", type);
//            jsonObj.put("start_time", startTime);
//            jsonObj.put("end_time", endTime);
//            return jsonObj.toString();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }


    /**
     * 获取当前最子层的区域id
     */
    private String getCurrentChildId() {
        String tmp = "";
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


    DateChooseWheelViewDialog startDateChooseDialog = null;
    DateChooseWheelViewDialog endDateChooseDialog = null;

    /**
     * 选择开始时间
     */
    private void showDialogStart() {
        if (startDateChooseDialog == null) {
            startDateChooseDialog = new DateChooseWheelViewDialog(context,
                    new DateChooseWheelViewDialog.DateChooseInterface() {
                        @Override
                        public void getDateTime(String time, boolean longTimeChecked) {
                            startTime = time;
                            tv_select_time_start.setText(time);
                        }
                    });
            startDateChooseDialog.setDateDialogTitle("开始时间");
        }

        if (!TextUtils.isEmpty(endTime)) {
            startDateChooseDialog.setDefaultValue(endTime);
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
                            endTime = time;
                            tv_select_time_end.setText(time);
                        }
                    });
            endDateChooseDialog.setDateDialogTitle("结束时间");
        }

        if (!TextUtils.isEmpty(endTime)) {
            endDateChooseDialog.setDefaultValue(endTime);
        }

        endDateChooseDialog.showDateChooseDialog();
    }


    @Override
    public void onClick(int resId) {
        switch (resId) {
            case R.id.tv_select_time_start:
                showDialogStart();
                break;
            case R.id.tv_select_time_end:
                showDialogStop();
                break;
            case R.id.btn_search:
                curPage = 1;
                requestAlarmList();
                break;
            default:
                break;
        }

    }


    /**
     * 服务端推送新告警
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealStationAlarmEvent(StationAlarmEvent event) {
        Log.i(TAG, "有新告警产生！");
        if (!isHistory) {
            requestAlarmList();
        }
    }

}
