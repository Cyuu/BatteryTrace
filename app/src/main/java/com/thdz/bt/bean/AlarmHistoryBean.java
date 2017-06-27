package com.thdz.bt.bean;

import java.io.Serializable;

/**
 * 设备状态bean
 * 序号，设备编号，站点名称，所属区域，告警类型，告警时间,地址，经纬度(原来，现在)
 */

public class AlarmHistoryBean implements Serializable {

    private String id;          // 设备编号
    private String region;      // 所在区域
    private String station;     // 站点

    private String type;    // 告警类型
    private String time;    // 告警时间

    private String addr;    // 地址

    private String longi;   // 经度
    private String lati;    // 纬度


    public AlarmHistoryBean() {

    }

    public AlarmHistoryBean(String id, String region, String station, String type, String time, String addr, String longi, String lati) {
        this.id = id;
        this.region = region;
        this.station = station;
        this.type = type;
        this.time = time;
        this.addr = addr;
        this.longi = longi;
        this.lati = lati;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    @Override
    public String toString() {
        return "AlarmHistoryBean{" +
                "id='" + id + '\'' +
                ", region='" + region + '\'' +
                ", station='" + station + '\'' +
                ", type='" + type + '\'' +
                ", time='" + time + '\'' +
                ", addr='" + addr + '\'' +
                ", longi='" + longi + '\'' +
                ", lati='" + lati + '\'' +
                '}';
    }
}
