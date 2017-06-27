package com.thdz.bt.event;

import java.io.Serializable;

/**
 * 轮询站点事件
 */
public class StationsRefresh4MapEvent implements Serializable {

    private String value;
    private boolean flag;

    public StationsRefresh4MapEvent(){

    }

    public StationsRefresh4MapEvent(String value, boolean flag) {
        this.value = value;
        this.flag = flag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
