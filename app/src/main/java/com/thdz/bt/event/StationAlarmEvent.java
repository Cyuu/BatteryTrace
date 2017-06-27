package com.thdz.bt.event;

import com.thdz.bt.bean.PushBaseBean;

import java.io.Serializable;

/**
 * 最新告警列表发生变化，其中某条告警的状态和信息发生变化, BUT,目前使用的是：通知栏，不用EvnentBus分发了
 */
public class StationAlarmEvent implements Serializable {

    private PushBaseBean pushBean;

    public StationAlarmEvent() {
    }

    public StationAlarmEvent(PushBaseBean pushBean) {
        this.pushBean = pushBean;
    }

    public PushBaseBean getPushBean() {
        return pushBean;
    }

    public void setPushBean(PushBaseBean pushBean) {
        this.pushBean = pushBean;
    }

    @Override
    public String toString() {
        return "StationAlarmEvent{" +
                "pushBean=" + pushBean +
                '}';
    }
}
