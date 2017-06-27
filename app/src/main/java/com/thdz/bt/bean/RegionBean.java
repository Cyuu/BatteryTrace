package com.thdz.bt.bean;

import java.io.Serializable;

/**
 * 区域bean
 */

public class RegionBean implements Serializable {

    // 区域id，自增
    private String RegionId;
    // 区域编码
    private String RegionNo;
    // 区域名称
    private String RegionName;
    // 区域的父级id
    private String ParentId;
    // 区域路径
    private String RegionPath;

    public RegionBean(){

    }

    public RegionBean(String regionId, String regionNo, String regionName, String parentId, String regionPath) {
        RegionId = regionId;
        RegionNo = regionNo;
        RegionName = regionName;
        ParentId = parentId;
        RegionPath = regionPath;
    }

    public String getRegionId() {
        return RegionId;
    }

    public void setRegionId(String regionId) {
        RegionId = regionId;
    }

    public String getRegionNo() {
        return RegionNo;
    }

    public void setRegionNo(String regionNo) {
        RegionNo = regionNo;
    }

    public String getRegionName() {
        return RegionName;
    }

    public void setRegionName(String regionName) {
        RegionName = regionName;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }

    public String getRegionPath() {
        return RegionPath;
    }

    public void setRegionPath(String regionPath) {
        RegionPath = regionPath;
    }
}
