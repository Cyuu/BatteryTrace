package com.thdz.bt.bean;

import java.io.Serializable;

/**
 * 告警bean
 * ----------
 * 1 当前告警没有 AlarmId <br/>
 * 2 历史告警没有 ModuleType，RegionName <br/>
 */

public class AlarmBean implements Serializable {

    private String AlarmId; //
    private String StationId; //
    private String StationName; //
    private String ModuleType; //
    private String RegionName; //
    private String AlarmType; //
    private String AlarmTime; //

    public AlarmBean(){

    }

    public AlarmBean(String alarmId, String stationId, String stationName, String moduleType, String regionName, String alarmType, String alarmTime) {
        AlarmId = alarmId;
        StationId = stationId;
        StationName = stationName;
        ModuleType = moduleType;
        RegionName = regionName;
        AlarmType = alarmType;
        AlarmTime = alarmTime;
    }

//    public LatLng getPosition(){
//        if (bdLat <= 0 || bdLon <= 0) {
//            LatLng mLL = new LatLng(Double.parseDouble(NLatitude), Double.parseDouble(NLongitude));
//            return DataUtils.converter2BaiduLocation(mLL);
//        } else {
//            return new LatLng(bdLat, bdLon);
//        }
//    }

    public String getAlarmId() {
        return AlarmId;
    }

    public void setAlarmId(String alarmId) {
        AlarmId = alarmId;
    }

    public String getStationId() {
        return StationId;
    }

    public void setStationId(String stationId) {
        StationId = stationId;
    }

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }

    public String getModuleType() {
        return ModuleType;
    }

    public void setModuleType(String moduleType) {
        ModuleType = moduleType;
    }

    public String getRegionName() {
        return RegionName;
    }

    public void setRegionName(String regionName) {
        RegionName = regionName;
    }

    public String getAlarmType() {
        return AlarmType;
    }

    public void setAlarmType(String alarmType) {
        AlarmType = alarmType;
    }

    public String getAlarmTime() {
        return AlarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        AlarmTime = alarmTime;
    }
}
