package com.thdz.bt.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 登录成功后返回的用户信息类
 */
public class UserInfo implements Serializable {

    private String UserId;
    private String UserName;
    private String Rights;
    private String RegionLevel;
    private String RegionIds; // 包含多个regionid

    public String [] getRegionArray(){
        if (TextUtils.isEmpty(RegionIds)) {
            return null;
        }
        if (RegionIds.contains(",")) {
            String [] array = RegionIds.split(",");
            return array;
        }

        return null;
    }

    public List<String> getRegionList(){
        if (TextUtils.isEmpty(RegionIds)) {
            return null;
        }
        if (RegionIds.contains(",")) {
            String [] array = RegionIds.split(",");
            return Arrays.asList(array);
        }
        return null;
    }

    public UserInfo(){

    }

    public UserInfo(String userId, String userName, String rights, String regionLevel, String regionIds) {
        UserId = userId;
        UserName = userName;
        Rights = rights;
        RegionLevel = regionLevel;
        RegionIds = regionIds;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getRights() {
        return Rights;
    }

    public void setRights(String rights) {
        Rights = rights;
    }

    public String getRegionLevel() {
        return RegionLevel;
    }

    public void setRegionLevel(String regionLevel) {
        RegionLevel = regionLevel;
    }

    public String getRegionIds() {
        return RegionIds;
    }

    public void setRegionIds(String regionIds) {
        RegionIds = regionIds;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "UserId='" + UserId + '\'' +
                ", UserName='" + UserName + '\'' +
                ", Rights='" + Rights + '\'' +
                ", RegionLevel='" + RegionLevel + '\'' +
                ", RegionIds='" + RegionIds + '\'' +
                '}';
    }
}
