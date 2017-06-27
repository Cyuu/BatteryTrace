package com.thdz.bt.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 服务端返回站点数据,站点列表接口的数据，"data"对应的 AlarmList
 */
public class ReturnAlarmBean implements Serializable {

    private String TotalDataNum;
    private String PerPageNum;
    private String TotalPageNum;
    private String CurrentPageNum;
    private List<AlarmBean> AlarmList;

    public ReturnAlarmBean() {
    }

    public ReturnAlarmBean(String totalDataNum, String perPageNum, String totalPageNum, String currentPageNum, List<AlarmBean> alarmList) {
        TotalDataNum = totalDataNum;
        PerPageNum = perPageNum;
        TotalPageNum = totalPageNum;
        CurrentPageNum = currentPageNum;
        AlarmList = alarmList;
    }

    public String getTotalDataNum() {
        return TotalDataNum;
    }

    public void setTotalDataNum(String totalDataNum) {
        TotalDataNum = totalDataNum;
    }

    public String getPerPageNum() {
        return PerPageNum;
    }

    public void setPerPageNum(String perPageNum) {
        PerPageNum = perPageNum;
    }

    public String getTotalPageNum() {
        return TotalPageNum;
    }

    public void setTotalPageNum(String totalPageNum) {
        TotalPageNum = totalPageNum;
    }

    public String getCurrentPageNum() {
        return CurrentPageNum;
    }

    public void setCurrentPageNum(String currentPageNum) {
        CurrentPageNum = currentPageNum;
    }

    public List<AlarmBean> getAlarmList() {
        return AlarmList;
    }

    public void setAlarmList(List<AlarmBean> alarmList) {
        AlarmList = alarmList;
    }
}


