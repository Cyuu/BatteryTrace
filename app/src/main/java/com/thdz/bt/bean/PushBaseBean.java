package com.thdz.bt.bean;

import java.io.Serializable;

/**
 * 推送返回的bean -- 韩冲用_分隔
 */

public class PushBaseBean implements Serializable {

    private String stationid;       // 站点id
    private String regionName;      // 区域=名称
    private String stationName;     // 站点名称
    private String alarmtype;       // 告警类型
    private String alarmtime;       // 告警time


    public PushBaseBean() {
    }

    public PushBaseBean(String stationid, String regionName, String stationName, String alarmtype, String alarmtime) {
        this.stationid = stationid;
        this.regionName = regionName;
        this.stationName = stationName;
        this.alarmtype = alarmtype;
        this.alarmtime = alarmtime;
    }

    public String getStationid() {
        return stationid;
    }

    public void setStationid(String stationid) {
        this.stationid = stationid;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getAlarmtime() {
        return alarmtime;
    }

    public void setAlarmtime(String alarmtime) {
        this.alarmtime = alarmtime;
    }

    public String getAlarmtype() {
        return alarmtype;
    }

    public void setAlarmtype(String alarmtype) {
        this.alarmtype = alarmtype;
    }

    @Override
    public String toString() {
        return "PushBaseBean{" +
                "stationid='" + stationid + '\'' +
                ", regionName='" + regionName + '\'' +
                ", stationName='" + stationName + '\'' +
                ", alarmtype='" + alarmtype + '\'' +
                ", alarmtime='" + alarmtime + '\'' +
                '}';
    }
}
