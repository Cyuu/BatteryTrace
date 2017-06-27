package com.thdz.bt.event;

import java.io.Serializable;

/**
 * 最近轨迹事件
 */

public class TraceRecentEvent implements Serializable {

    private String stationid;

    private String name;

    public TraceRecentEvent(){

    }

    public TraceRecentEvent(String stationid) {
        this.stationid = stationid;
    }

    public String getStationid() {
        return stationid;
    }

    public void setStationid(String stationid) {
        this.stationid = stationid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TraceHistoryEvent{" +
                "stationid='" + stationid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
