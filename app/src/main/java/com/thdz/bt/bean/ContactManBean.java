package com.thdz.bt.bean;

import java.io.Serializable;

/**
 * 联系人
 */

public class ContactManBean implements Serializable {

    private String ManName;
    private String ManTel;

    public ContactManBean(){

    }

    public ContactManBean(String manName, String manTel) {
        ManName = manName;
        ManTel = manTel;
    }

    public String getManName() {
        return ManName;
    }

    public void setManName(String manName) {
        ManName = manName;
    }

    public String getManTel() {
        return ManTel;
    }

    public void setManTel(String manTel) {
        ManTel = manTel;
    }

    @Override
    public String toString() {
        return "ContactManBean{" +
                "ManName='" + ManName + '\'' +
                ", ManTel='" + ManTel + '\'' +
                '}';
    }
}
