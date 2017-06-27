package com.thdz.bt.bean;

import java.io.Serializable;

/**
 * 告警类型<br/>
 *
 * 全部告警
 * 低压
 * 位移
 * 震动
 * 失联
 * 失联恢复
 *
 */

public class AlarmTypeBean implements Serializable {

    private String id;
    private String name;

    public AlarmTypeBean(){

    }

    public AlarmTypeBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AlarmTypeBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


}
