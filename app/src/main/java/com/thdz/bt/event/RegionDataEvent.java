package com.thdz.bt.event;

import com.thdz.bt.bean.RegionBean;

import java.io.Serializable;
import java.util.List;

/**
 * 区域数据分发事件
 */
public class RegionDataEvent implements Serializable {

    private List<RegionBean> regionData;

    public RegionDataEvent(){

    }

    public RegionDataEvent(List<RegionBean> regionData) {
        this.regionData = regionData;
    }

    public List<RegionBean> getRegionData() {
        return regionData;
    }

    public void setRegionData(List<RegionBean> regionData) {
        this.regionData = regionData;
    }

    @Override
    public String toString() {
        return "RegionDataEvent{" +
                "regionData=" + regionData +
                '}';
    }
}
