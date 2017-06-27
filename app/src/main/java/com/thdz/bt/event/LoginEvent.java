package com.thdz.bt.event;

import java.io.Serializable;

/**
 * 登录分发事件
 */
public class LoginEvent implements Serializable {

    private boolean isSuccess; // 是否登录成功

    public LoginEvent(){

    }

    public LoginEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    @Override
    public String toString() {
        return "LoginEvent{" +
                "isSuccess=" + isSuccess +
                '}';
    }
}
