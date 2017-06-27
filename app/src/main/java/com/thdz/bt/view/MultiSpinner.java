package com.thdz.bt.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.thdz.bt.adapter.SpinnerRegionListAdapter;
import com.thdz.bt.bean.RegionBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 省市县三层Spinner
 */
public class MultiSpinner extends ViewGroup {

    // 所有区域bean
    public static List<RegionBean> regionList = null;
    // 所有省的列表
    public static List<RegionBean> provinceList = new ArrayList<RegionBean>();


    // 选中的当前省
    public static RegionBean provinceBean;
    // 选中的当前市
    public static RegionBean cityBean;
    // 选中的当前县
    public static RegionBean districtBean;

    private SpinnerRegionListAdapter provinceAdapter; // 省适配器
    private SpinnerRegionListAdapter cityAdapter; // 市适配器
    private SpinnerRegionListAdapter distAdapter; // 县适配器



    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MultiSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }





}
