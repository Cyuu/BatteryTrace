package com.thdz.bt.event;

import com.thdz.bt.bean.StationBean;

import java.io.Serializable;

/**
 * 导航事件 --
 */

public class BDNaviEvent implements Serializable {

    private StationBean stationBean;

    public BDNaviEvent(){

    }

    public BDNaviEvent(StationBean stationBean) {
        this.stationBean = stationBean;
    }

    public StationBean getStationBean() {
        return stationBean;
    }

    public void setStationBean(StationBean stationBean) {
        this.stationBean = stationBean;
    }

    @Override
    public String toString() {
        return "BDNaviEvent{" +
                "stationBean=" + stationBean +
                '}';
    }
}
