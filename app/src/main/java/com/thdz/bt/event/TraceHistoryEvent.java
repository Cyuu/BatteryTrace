package com.thdz.bt.event;

import java.io.Serializable;

/**
 * 历史轨迹事件
 */

public class TraceHistoryEvent implements Serializable {

    private String stationid;

    private String name;

    public TraceHistoryEvent(){

    }

    public TraceHistoryEvent(String stationid) {
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
