package com.thdz.bt.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

/**
 * 百度地图工具类
 */

public class MapUtils {


    /**
     * 地图以当前缩放比例展示
     */
    public static void setStatusByZoom(BaiduMap mBaiduMap, float scale) {
        if (scale != 0) {
            MapStatusUpdate status = MapStatusUpdateFactory.zoomTo(scale);
            mBaiduMap.setMapStatus(status);
        }
    }


    /**
     * 地图定位到当前点
     */
    public static void setStatusAtLatLng(BaiduMap mBaiduMap, LatLng loc) {
        if (loc != null) {
            MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(loc);
            mBaiduMap.setMapStatus(status);
        }
    }


    /**
     * 隐藏百度地图logo和比例尺，缩放按钮
     */
    public static void hideMapChildren(MapView mMapView) {
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }

        //地图上比例尺
        mMapView.showScaleControl(false);
        // 隐藏缩放控件
        mMapView.showZoomControls(false);
    }

}
