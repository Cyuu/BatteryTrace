package com.thdz.bt.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 服务端返回站点数据,站点列表接口的数据，"data"对应的DataList
 */
public class ReturnDataBean implements Serializable {

    private String TotalDataNum;
    private String PerPageNum;
    private String TotalPageNum;
    private String CurrentPageNum;
    private List<StationBean> DataList;

    public ReturnDataBean() {
    }

    public ReturnDataBean(String totalDataNum, String perPageNum, String totalPageNum, String currentPageNum, List<StationBean> dataList) {
        TotalDataNum = totalDataNum;
        PerPageNum = perPageNum;
        TotalPageNum = totalPageNum;
        CurrentPageNum = currentPageNum;
        DataList = dataList;
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

    public List<StationBean> getDataList() {
        return DataList;
    }

    public void setDataList(List<StationBean> dataList) {
        DataList = dataList;
    }
}


