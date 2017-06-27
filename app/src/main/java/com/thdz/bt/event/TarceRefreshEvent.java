package com.thdz.bt.event;

import com.thdz.bt.bean.TraceBean;

import java.io.Serializable;
import java.util.List;

/**
 * 轮询轨迹事件
 */
public class TarceRefreshEvent implements Serializable {

    private boolean flag;
    private List<TraceBean> list;

    public TarceRefreshEvent(){

    }

    public TarceRefreshEvent(boolean flag, List<TraceBean> list) {
        this.flag = flag;
        this.list = list;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public List<TraceBean> getList() {
        return list;
    }

    public void setList(List<TraceBean> list) {
        this.list = list;
    }
}
