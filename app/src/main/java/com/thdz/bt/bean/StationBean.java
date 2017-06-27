package com.thdz.bt.bean;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.model.LatLng;
import com.thdz.bt.R;
import com.thdz.bt.util.DataUtils;

import java.io.Serializable;

import baidumapsdk.demo.clusterutil.clustering.ClusterItem;

/**
 * 站点bean
 * -----------
 * 判断通信状态，是不是0
 */

public class StationBean implements Serializable, ClusterItem {

    private String StationId;   //  ID
    private String StationNo;   //  站点编码
    private String StationName; //  站点名称
    private String StationType; //  站点类型：蓄电池2V，蓄电池12V
    private String RegionId;    //  站点所属区域ID
    private String RegionName;  //  站点所属区域名称
    private String ModuleAddr;  //  站点地址（设备地址）
    private String SimCard;     //  站点SIM卡
    private String ModuleU;     //  12,

    private String Distance;   //  位移距离，米
    private String OLongitude; //  原始经度
    private String OLatitude;  //  原始纬度
    private String NLongitude; //  115.456009,当前经度
    private String NLatitude;  //  38.9208527,当前纬度

    private String IsLost; //  失联告警，0正常 1告警
    private String IsLow; //   低压告警，0正常 1告警
    private String IsMove; //  位移告警，0正常 1告警
    private String IsVib; //   震动告警，0正常 1告警
    private String CommType;    //  通讯方式
    private String CurrentTime; //  最后通信时间

    private double bdLat; // 百度坐标纬度
    private double bdLon; // 百度坐标经度


    public String getAlarmType(){
        if (IsLost.equals("1")) {
            return "失联告警";
        } else if (IsLow.equals("1")) {
            return "低压告警";
        }else if (IsMove.equals("1")) {
            return "位移告警";
        }else if (IsVib.equals("1")) {
            return "震动告警";
        }
        return "没有告警";

    }


    public StationBean() {

    }

    public StationBean(LatLng ll) {
        this.OLongitude = ll.longitude + "";
        this.OLatitude = ll.latitude + "";
    }

    public StationBean(String stationId, String stationNo, String stationName, String stationType, String regionId, String regionName, String moduleAddr, String simCard, String moduleU, String distance, String OLongitude, String OLatitude, String NLongitude, String NLatitude, String isLost, String isLow, String isMove, String isVib, String commType, String currentTime) {
        StationId = stationId;
        StationNo = stationNo;
        StationName = stationName;
        StationType = stationType;
        RegionId = regionId;
        RegionName = regionName;
        ModuleAddr = moduleAddr;
        SimCard = simCard;
        ModuleU = moduleU;
        Distance = distance;
        this.OLongitude = OLongitude;
        this.OLatitude = OLatitude;
        this.NLongitude = NLongitude;
        this.NLatitude = NLatitude;
        IsLost = isLost;
        IsLow = isLow;
        IsMove = isMove;
        IsVib = isVib;
        CommType = commType;
        CurrentTime = currentTime;
    }


    public static StationBean convert2BaiduLocBean(StationBean resBean){
        StationBean desbean = resBean;
        try {
            LatLng mLL = null;
            if (Double.parseDouble(resBean.getNLatitude()) == 0 || Double.parseDouble(resBean.getNLongitude()) == 0) {
                mLL = new LatLng(Double.parseDouble(resBean.getOLatitude()), Double.parseDouble(resBean.getOLongitude()));
            } else {
                mLL = new LatLng(Double.parseDouble(resBean.getNLatitude()), Double.parseDouble(resBean.getNLongitude()));
            }

            LatLng bdLatlng = DataUtils.converter2BaiduLocation(mLL);
            desbean.setBdLat(bdLatlng.latitude);
            desbean.setBdLon(bdLatlng.longitude);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return desbean;
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

    public String getRegionId() {
        return RegionId;
    }

    public void setRegionId(String regionId) {
        RegionId = regionId;
    }

    public String getRegionName() {
        return RegionName;
    }

    public void setRegionName(String regionName) {
        RegionName = regionName;
    }

    public String getModuleAddr() {
        return ModuleAddr;
    }

    public void setModuleAddr(String moduleAddr) {
        ModuleAddr = moduleAddr;
    }

    public String getSimCard() {
        return SimCard;
    }

    public void setSimCard(String simCard) {
        SimCard = simCard;
    }

    public String getModuleU() {
        return ModuleU;
    }

    public void setModuleU(String moduleU) {
        ModuleU = moduleU;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    /**
     * todo 转换坐标
     */
    public String getOLongitude() {
        return OLongitude;
    }

    public void setOLongitude(String OLongitude) {
        this.OLongitude = OLongitude;
    }

    /**
     * todo 转换坐标
     */
    public String getOLatitude() {
        return OLatitude;
    }

    public void setOLatitude(String OLatitude) {
        this.OLatitude = OLatitude;
    }

    /**
     * todo 转换坐标
     */
    public String getNLongitude() {
        return NLongitude;
    }

    public void setNLongitude(String NLongitude) {
        this.NLongitude = NLongitude;
    }

    /**
     * todo 转换坐标
     */
    public String getNLatitude() {
        return NLatitude;
    }

    public void setNLatitude(String NLatitude) {
        this.NLatitude = NLatitude;
    }

    public String getIsLost() {
        return IsLost;
    }

    public void setIsLost(String isLost) {
        IsLost = isLost;
    }

    public String getIsLow() {
        return IsLow;
    }

    public void setIsLow(String isLow) {
        IsLow = isLow;
    }

    public String getIsMove() {
        return IsMove;
    }

    public void setIsMove(String isMove) {
        IsMove = isMove;
    }

    public String getIsVib() {
        return IsVib;
    }

    public void setIsVib(String isVib) {
        IsVib = isVib;
    }

    public String getCommType() {
        return CommType;
    }

    public void setCommType(String commType) {
        CommType = commType;
    }

    public String getCurrentTime() {
        return CurrentTime;
    }

    public void setCurrentTime(String currentTime) {
        CurrentTime = currentTime;
    }


    /**
     * todo 使用非空坐标, 是否需要判断
     */
    @Override
    public LatLng getPosition() {
        if (bdLat <= 0 || bdLon <= 0) {
            LatLng mLL = new LatLng(Double.parseDouble(NLatitude), Double.parseDouble(NLongitude));
            return DataUtils.converter2BaiduLocation(mLL);
        } else {
            return new LatLng(bdLat, bdLon);
        }

//        return new LatLng(Double.parseDouble(OLatitude), Double.parseDouble(OLongitude));
    }


    public boolean isAlarm() {
        try {
            if (IsLost.equals("1") || IsLow.equals("1") || IsMove.equals("1") || IsVib.equals("1")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public BitmapDescriptor getBitmapDescriptor() {
        int resId = -1;
        if (isAlarm()) {
            resId = R.drawable.ic_loc_red;
        } else {
            resId = R.drawable.ic_loc_green;
        }
        return BitmapDescriptorFactory.fromResource(resId);
    }

}
