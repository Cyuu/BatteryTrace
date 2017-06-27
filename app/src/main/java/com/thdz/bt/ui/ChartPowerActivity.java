package com.thdz.bt.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.andrew.datechoosewheelviewdemo.DateChooseWheelViewDialog;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
import com.thdz.bt.R;
import com.thdz.bt.base.BaseActivity;
import com.thdz.bt.bean.VoltageBean;
import com.thdz.bt.util.DataUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 测试Chart图表
 */
public class ChartPowerActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.chart1)
    LineChart mChart;

    @BindView(R.id.tv_chart_title)
    TextView tv_chart_title;

    @BindView(R.id.tv_select_time_start)
    EditText tv_select_time_start;

    @BindView(R.id.tv_select_time_end)
    EditText tv_select_time_end;

    @BindView(R.id.btn_search)
    TextView btn_search;

    @BindView(R.id.btn_search_test)
    TextView btn_search_test;

    private List<VoltageBean> voltList;


    private String[] dataX = null;// 横轴 X轴数据
    private String[] dataY = null;// 纵轴 Y轴数据

    private DateChooseWheelViewDialog timeStartDialog = null;
    private DateChooseWheelViewDialog timeEndDialog = null;
    private String startTime; // 开始时间
    private String endTime;   // 结束时间

    private int offset = -1; // 几天前

    public final String msgFail = "未查询到符合条件的电压数据";

    private String stationid;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_voltage_chart);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        toolbar.setTitle("电压曲线图");
        setToolbarBackEnable(toolbar);

        stationid = getIntent().getExtras().getString("stationid");
        String name = getIntent().getExtras().getString("name");
        tv_chart_title.setText(name);

        if (TextUtils.isEmpty(stationid)) {
            showToast("站点不存在");
            finish();
            return;
        }
        tv_select_time_start.setOnClickListener(this);
        tv_select_time_end.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        btn_search_test.setOnClickListener(this);


        mChart.setVisibility(View.GONE);
        initChartView();

        initTime();

//        requestPowerList();

    }

    private void initTime() {
        endTime = DataUtils.getFormatToday();
        startTime = DataUtils.getFormatTime(offset); // 几天前
        Log.i(TAG, "start=" + startTime + ", end=" + endTime);

        tv_select_time_start.setText(startTime);
        tv_select_time_end.setText(endTime);

    }


    /**
     * 获取设备电压值列表
     */
    private void requestPowerList() {
        showProgressDialog();
        String url = DataUtils.do4PowerList(stationid, startTime, endTime);

        doRequestGet(url, new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                hideProgressDialog();
                showToast(failTip + ", 查询电压数据失败");
                e.printStackTrace();
            }

            @Override
            public void onResponse(String value, int id) {
                hideProgressDialog();
                Log.i(TAG, "获取设备电压 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {
                        hideProgressDialog();
                        String content = DataUtils.getReturnList(value);

                        voltList = JSONArray.parseArray(content, VoltageBean.class);

                        if (voltList == null || voltList.isEmpty()) {
                            showToast("查询到的电压数据为空");
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


    /**
     * 设置数据，展示Chart
     */
    private void showView() {
        if (voltList == null || voltList.isEmpty()) {
            mChart.setVisibility(View.GONE);
            showToast("未获取到电压数据");
            return;
        }

        List<String> XData = new ArrayList<String>();
        List<String> YData = new ArrayList<String>();
        for (VoltageBean bean : voltList) {
            XData.add(bean.getCurrentTime());
            YData.add(bean.getVoltage());
        }

        /**
         * 为Chart设置数据
         */
        dataX = XData.toArray(new String[XData.size()]);
        dataY = YData.toArray(new String[YData.size()]);

        mChart.setVisibility(View.VISIBLE);
        loadChartView();
    }


    /**
     * 设置测试数据
     */
    private void setTestData() {
        dataX = new String[]{"2017.04.25", "2017.04.26", "2017.04.27", "2017.04.28", "2017.04.29",
                "2017.04.30", "2017.05.01", "2017.05.02", "2017.05.03", "2017.05.04"};
        dataY = new String[]{"122.00", "234.34", "85.67", "117.90", "332.33",
                "113.33", "120.78", "122.00", "234.34", "85.67"};
    }


    /**
     * 初始化ChartView
     */
    private void initChartView() {

        //设置字体格式，如正楷
//        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
//        mChart.setValueTypeface(tf);


        // 设置在Y轴上是否是从0开始显示
        mChart.setStartAtZero(true);
        //是否在Y轴显示数据，就是曲线上的数据
        mChart.setDrawYValues(true);
        //设置网格
        mChart.setDrawBorder(true);
        mChart.setBorderPositions(new BarLineChartBase.
                BorderPosition[]{BarLineChartBase.BorderPosition.BOTTOM});
        //在chart上的右下角加描述
        mChart.setDescription("电压值曲线图");
        //设置Y轴上的单位
        mChart.setUnit("V");
        //设置透明度
        mChart.setAlpha(0.8f);
        //设置网格底下的那条线的颜色
        mChart.setBorderColor(Color.rgb(213, 216, 214));
        //设置Y轴前后倒置
        mChart.setInvertYAxisEnabled(false);
        //设置高亮显示
        mChart.setHighlightEnabled(true);
        //设置是否可以触摸，如为false，则不能拖动，缩放等
        mChart.setTouchEnabled(true);
        //设置是否可以拖拽，缩放
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        //设置是否能扩大扩小
        mChart.setPinchZoom(true);
        // 设置背景颜色
        // mChart.setBackgroundColor(Color.GRAY);

//        //设置点击chart图对应的数据弹出标注
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//        mv.setOffsets(-mv.getMeasuredWidth() / 2, -mv.getMeasuredHeight());
//        // set the marker to the chart
//        mChart.setMarkerView(mv);

        // enable/disable highlight indicators (the lines that indicate the
        // highlighted Entry)
        mChart.setHighlightIndicatorEnabled(false);

        XLabels xl = mChart.getXLabels();
//        xl.setAvoidFirstLastClipping(true);
//        xl.setAdjustXLabels(true);

        xl.setPosition(XLabels.XLabelPosition.BOTTOM); // 设置X轴的数据在底部显示
//        xl.setTypeface(tf); // 设置字体
        xl.setTextSize(10f); // 设置字体大小
        xl.setSpaceBetweenLabels(3); // 设置数据之间的间距

        YLabels yl = mChart.getYLabels();
//        yl.setPosition(YLabels.YLabelPosition.LEFT_INSIDE); // set the position
//        yl.setTypeface(tf); // 设置字体
        yl.setTextSize(10f); // s设置字体大小
        yl.setLabelCount(5); // 设置Y轴最多显示的数据个数

    }


    /**
     * 根据数据，加载Chart显示
     */
    private void loadChartView() {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < dataX.length; i++) {
            xVals.add(dataX[i]);
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < dataY.length; i++) {
            yVals.add(new Entry(Float.parseFloat(dataY[i]), i));
        }

        String name = "横轴名称";
        LineDataSet set1 = new LineDataSet(yVals, ""); // name

        set1.setDrawCubic(false);  //设置曲线为圆滑的线
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(false);  //设置包括的范围区域填充颜色
        set1.setDrawCircles(true);  //设置有圆点
        set1.setLineWidth(2f);    //设置线的宽度
        set1.setCircleSize(5f);   //设置小圆的大小
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setColor(Color.rgb(104, 241, 175));    // 设置曲线的颜色

        // create a data object with the datasets
        LineData data = new LineData(xVals, set1);

        // set data
        mChart.setData(data);

        mChart.animateX(4000);   //从X轴进入的动画
        mChart.animateY(3000);   //从Y轴进入的动画
        // todo animateXY可能会引起一个抖动的bug，可以注释掉
//        mChart.animateXY(3000, 3000);   //从XY轴一起进入的动画，

        //设置最小的缩放
        mChart.setScaleMinima(0.5f, 1f);
        //设置视口
        // mChart.centerViewPort(10, 50);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);  //设置图最下面显示的类型
//        l.setTypeface(tf);
        l.setTextSize(15);
        l.setTextColor(Color.rgb(104, 241, 175));
        l.setFormSize(30f); // set the size of the legend forms/shapes

        // 刷新图表
        mChart.invalidate();

    }


    private void chooseStartTime() {
        if (timeStartDialog == null) {
            timeStartDialog = new DateChooseWheelViewDialog(context,
                    new DateChooseWheelViewDialog.DateChooseInterface() {
                        @Override
                        public void getDateTime(String time, boolean longTimeChecked) {
                            startTime = time;
                            tv_select_time_start.setText(startTime);
                        }
                    });
            timeStartDialog.setDateDialogTitle("请选择开始时间");
        }

        if (!TextUtils.isEmpty(startTime)) {
            timeStartDialog.setDefaultValue(startTime);
        }

        timeStartDialog.showDateChooseDialog();
    }


    private void chooseEndTime() {
        if (timeEndDialog == null) {
            timeEndDialog = new DateChooseWheelViewDialog(context,
                    new DateChooseWheelViewDialog.DateChooseInterface() {
                        @Override
                        public void getDateTime(String time, boolean longTimeChecked) {
                            endTime = time;
                            tv_select_time_end.setText(endTime);
                        }
                    });
            timeEndDialog.setDateDialogTitle("请选择结束时间");
        }

        if (!TextUtils.isEmpty(endTime)) {
            timeEndDialog.setDefaultValue(endTime);
        }

        timeEndDialog.showDateChooseDialog();
    }


    @Override
    public void onClick(int resId) {
        switch (resId) {
            case R.id.tv_select_time_start:
                chooseStartTime();
                break;
            case R.id.tv_select_time_end:
                chooseEndTime();
                break;
            case R.id.btn_search:
                requestPowerList();
                break;
            case R.id.btn_search_test: // todo 模拟数据
//                    testValue();
                break;
            default:
                break;
        }

    }

}
