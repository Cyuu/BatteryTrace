package com.thdz.bt.bean;

import com.baidu.mapapi.model.LatLng;
import com.thdz.bt.util.DataUtils;

import java.io.Serializable;

/**
 * 轨迹类
 */
public class TraceBean implements Serializable {

    private String StationId; //  3135,  站点id
    private String Longitude; //  115.466,  纬度
    private String Latitude;  //  38.931,   经度
    private String Altitude;  //  0,  海拔
    private String CurrentTime; //  2016/2/29 10; // 56; // 00 当前时间

    private double bdLat; // 百度坐标纬度
    private double bdLon; // 百度坐标经度

    public TraceBean() {

    }

    public TraceBean(String stationId, String longitude, String latitude, String currentTime) {
        StationId = stationId;
        Longitude = longitude;
        Latitude = latitude;
        CurrentTime = currentTime;
    }

    public String getStationId() {
        return StationId;
    }

    public void setStationId(String stationId) {
        StationId = stationId;
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

    public String getAltitude() {
        return Altitude;
    }

    public void setAltitude(String altitude) {
        Altitude = altitude;
    }

    public String getCurrentTime() {
        return CurrentTime;
    }

    public void setCurrentTime(String currentTime) {
        CurrentTime = currentTime;
    }

    public double getBdLat() {
        return bdLat;
    }

    public void setBdLat(double bdLat) {
        this.bdLat = bdLat;
    }

    public double getBdLon() {
        return bdLon;
    }

    public void setBdLon(double bdLon) {
        this.bdLon = bdLon;
    }

    public static TraceBean convert2BaiduLocBean(TraceBean resBean) {
        TraceBean desbean = resBean;
        try {
            LatLng mLL = new LatLng(Double.parseDouble(resBean.getLatitude()),
                    Double.parseDouble(resBean.getLongitude()));

            LatLng bdLatlng = DataUtils.converter2BaiduLocation(mLL);
            desbean.setBdLat(bdLatlng.latitude);
            desbean.setBdLon(bdLatlng.longitude);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return desbean;
    }

}
