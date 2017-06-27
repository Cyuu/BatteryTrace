package com.thdz.bt.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 站点详情bean
 */
public class StationDetailBean implements Serializable {

    private String StationId;	//	站点ID
    private String StationNo;	//	站点编号
    private String StationName;	//	站点名称
    private String StationType;	//	站点类型
    private String ModuleAddr;	//	模块地址
    private String RegionName;	//	所属区域
    private String SimCard;	    //	SIM卡号码
    private String Longitude;	//	站点经度
    private String Latitude;	//	站点纬度
    private String HeartBreakTime;	//	心跳时间间隔，单位：小时
    private String DataUploadTime;//		震动告警时间间隔，单位：秒
    private String LowVoltageAlarmTime;	//	低压告警时间间隔，单位：秒
    private String PosChangeAlarmTime;	//	位移告警时间间隔，单位：秒
    private String LowValue;	//	电压低压阈值，单位：伏特
    private String AlarmDistance;	//	位移告警阈值，单位：米
    private String ServerIP;	//	GPRS通讯服务IP
    private String ServerPort;	//	GPRS通讯服务端口
    private String TermName;	//	服务程序名字


    private List<ContactManBean> ContactManList; // 联系人列表

    public StationDetailBean() {
    }

    public String getStationId() {
        return StationId;
    }

    public void setStationId(String stationId) {
        StationId = stationId;
    }

    public String getStationNo() {
        return StationNo;
    }

    public void setStationNo(String stationNo) {
        StationNo = stationNo;
    }

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }

    public String getStationType() {
        return StationType;
    }

    public void setStationType(String stationType) {
        StationType = stationType;
    }

    public String getModuleAddr() {
        return ModuleAddr;
    }

    public void setModuleAddr(String moduleAddr) {
        ModuleAddr = moduleAddr;
    }

    public String getRegionName() {
        return RegionName;
    }

    public void setRegionName(String regionName) {
        RegionName = regionName;
    }

    public String getSimCard() {
        return SimCard;
    }

    public void setSimCard(String simCard) {
        SimCard = simCard;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getHeartBreakTime() {
        return HeartBreakTime;
    }

    public void setHeartBreakTime(String heartBreakTime) {
        HeartBreakTime = heartBreakTime;
    }

    public String getDataUploadTime() {
        return DataUploadTime;
    }

    public void setDataUploadTime(String dataUploadTime) {
        DataUploadTime = dataUploadTime;
    }

    public String getLowVoltageAlarmTime() {
        return LowVoltageAlarmTime;
    }

    public void setLowVoltageAlarmTime(String lowVoltageAlarmTime) {
        LowVoltageAlarmTime = lowVoltageAlarmTime;
    }

    public String getPosChangeAlarmTime() {
        return PosChangeAlarmTime;
    }

    public void setPosChangeAlarmTime(String posChangeAlarmTime) {
        PosChangeAlarmTime = posChangeAlarmTime;
    }

    public String getLowValue() {
        return LowValue;
    }

    public void setLowValue(String lowValue) {
        LowValue = lowValue;
    }

    public String getAlarmDistance() {
        return AlarmDistance;
    }

    public void setAlarmDistance(String alarmDistance) {
        AlarmDistance = alarmDistance;
    }

    public String getServerIP() {
        return ServerIP;
    }

    public void setServerIP(String serverIP) {
        ServerIP = serverIP;
    }

    public String getServerPort() {
        return ServerPort;
    }

    public void setServerPort(String serverPort) {
        ServerPort = serverPort;
    }

    public String getTermName() {
        return TermName;
    }

    public void setTermName(String termName) {
        TermName = termName;
    }

    public List<ContactManBean> getContactManList() {
        return ContactManList;
    }

    public void setContactManList(List<ContactManBean> contactManList) {
        ContactManList = contactManList;
    }
}
