package com.thdz.bt.bean;

import java.io.Serializable;

/**
 * 某站点的设备电压bean
 */
public class VoltageBean implements Serializable {

    private String StationId; //站点ID
    private String Voltage; //电压值，单位：伏特
    private String State; // 电压状态，0:正常 1:低压
    private String CurrentTime; //电压上传时间

    public VoltageBean(){

    }

    public VoltageBean(String stationId, String voltage, String state, String currentTime) {
        StationId = stationId;
        Voltage = voltage;
        State = state;
        CurrentTime = currentTime;
    }

    public String getStationId() {
        return StationId;
    }

    public void setStationId(String stationId) {
        StationId = stationId;
    }

    public String getVoltage() {
        return Voltage;
    }

    public void setVoltage(String voltage) {
        Voltage = voltage;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCurrentTime() {
        return CurrentTime;
    }

    public void setCurrentTime(String currentTime) {
        CurrentTime = currentTime;
    }
}
