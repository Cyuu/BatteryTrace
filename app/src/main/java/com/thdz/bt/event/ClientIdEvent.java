package com.thdz.bt.event;

import java.io.Serializable;

/**
 * 登录状态发生变化
 */
public class ClientIdEvent implements Serializable{

    private String ClientId;

    public ClientIdEvent(){}

    public ClientIdEvent(String ClientId){this.ClientId = ClientId;}

    public String getClientId() {
        return ClientId;
    }

    public void setClientId(String ClientId) {
        this.ClientId = ClientId;
    }
}
