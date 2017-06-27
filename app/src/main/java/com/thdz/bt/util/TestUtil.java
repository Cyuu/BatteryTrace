package com.thdz.bt.util;

/**
 * 测试工具类
 */

public class TestUtil {

//    public static List<LatLng> getLatLngList(int count) {
//        List<LatLng> latLngList = new ArrayList<LatLng>();
//        LatLng bean = null;
//        for (int i = 0; i < count; i++) {
//            double lat = 38.927763;
//            double lon = 115.469205;
//            if (i % 3 == 0) {
//                bean = new LatLng(lat + i * 0.0001, lon + i * 0.0001);
//            } else if (i % 3 == 1) {
//                bean = new LatLng(lat + i * 0.0001, lon + i * 0.0002);
//            } else { // if (i % 3 == 2)
//                bean = new LatLng(lat + i * 0.0001, lon + i * 0.00015);
//            }
////            bean = new LatLng(lat + i * 0.0001, lon + i * 0.0001);
//            latLngList.add(bean);
//        }
//
//        return latLngList;
//    }


//    /**
//     * LatLng llA = new LatLng(38.927763, 115.469205);
//     */
//    public static List<StationBean> getTestStationListData() {
//        List<StationBean> list = new ArrayList<StationBean>();
//        StationBean bean = null;
//        double rate1;
//        double rate2;
//
//        for (int i = 0; i < 18; i++) {
//            int cur = i + 1;
//            bean = new StationBean();
//            bean.setStationId(cur + "");
//            bean.setStationName("保定市新市区" + (i) + "站");
//            bean.setRegionName("保定市新市区" + cur);
//            bean.setModuleU("12");
//
//            rate1 = Math.random() * 0.0012;
//            rate2 = Math.random() * 0.0012;
//            String tempLat = new DecimalFormat("###,###,###.######").format(38.927763 + rate1);
//            String tempLon = new DecimalFormat("###,###,###.######").format(115.469205 + rate2);
//            bean.setNLatitude(tempLat);
//            bean.setNLongitude(tempLon);
//
////            bean.setLat((38.927763 + 0.000002 * i) + "");
////            bean.setLon((115.469205 + 0.000002 * i) + "");
//
//            bean.setRegionId((i + 22) + "");
//            bean.setStationType("12V蓄电池");
//
//            if (i % 5 == 3) {
//                bean.setIsLost("1"); // 告警
//            } else {
//                bean.setIsLost("0"); // 非告警
//            }
//
//
//            list.add(bean);
//        }
//
//        return list;
//    }


//    /**
//     * 创建模拟告警历史列表数据
//     */
//    public static List<AlarmHistoryBean> getTestAlarmHistoryData() {
//        List<AlarmHistoryBean> list = new ArrayList<AlarmHistoryBean>();
//        AlarmHistoryBean bean = null;
//        double rate;
//
//        for (int i = 0; i < 10; i++) {
//            int cur = i + 1;
//            bean = new AlarmHistoryBean();
//            bean.setId(cur + "");
//            bean.setRegion("保定市新市区" + cur);
//            bean.setStation("保定市新市区基站" + cur);
//            bean.setAddr("保定市铁塔分站50" + cur + "点");
//            bean.setTime("2017.4.3 23:11:11");
//
//            rate = Math.random() * 0.002;
//            String tempLat = new DecimalFormat("###,###,###.######").format(115.469205 + rate);
//            String tempLon = new DecimalFormat("###,###,###.######").format(38.927763 + rate);
//            bean.setLati(tempLat);
//            bean.setLongi(tempLon);
//
//            if (i % 3 == 0) {
//                bean.setType("丢失报警");
//            } else if (i % 3 == 1) {
//                bean.setType("低压告警");
//            } else {
//                bean.setType("工作告警");
//            }
//
//            list.add(bean);
//        }
//        return list;
//    }


//    /**
//     * 创建模拟雷达数据，数组长度：180
//     */
//    public static byte[] getTestRadarData() {
//        byte[] datas = new byte[180];
//        for (int i = 0; i < 180; i++) {
//            byte bb = 20;
//            datas[i] = bb;
//        }
//
//        return datas;
//    }

//    public static List<AlarmHistoryBean> getAlarmList() {
//        List<AlarmHistoryBean> alarms = new ArrayList<>();
//        AlarmHistoryBean bean = null;
//        for (int i = 0; i < 2; i++) {
//            bean = new AlarmHistoryBean();
//            bean.setSid((i * 2 + 1));
//            bean.setTime(DataUtils.getCurrentTimeStr());
//            alarms.add(bean);
//        }
//        return alarms;
//    }


//    /**
//     * 历史告警列表测试数据
//     */
//    public static List<AlarmHistoryBean> getAlarmHisList() {
//        List<AlarmHistoryBean> alarms = new ArrayList<>();
//        AlarmHistoryBean bean = null;
//        for (int i = 0; i < 5; i++) {
//            bean = new AlarmHistoryBean();
//            bean.setId(i + 1);
//            bean.setTid(i * 2 + 1);
//            bean.setDate(DataUtils.getCurrentTimeStr());
//            alarms.add(bean);
//        }
//        return alarms;
//    }


//    /**
//     * 模拟区域基础数据<br/>
//     * // 区域id，自增
//     * private String RegionId;
//     * // 区域编码
//     * private String RegionNo;
//     * // 区域名称
//     * private String RegionName;
//     * // 区域的父级id
//     * private String ParentId;
//     * // 区域路径
//     * private String RegionPath;
//     */
//    public static List<RegionBean> testRegionList() {
//        List<RegionBean> list = new ArrayList<RegionBean>();
//        RegionBean bean1 = null;
//
//        // 省
//        bean1 = new RegionBean("1", "1001", "北京", "0", "");
//        list.add(bean1);
//        bean1 = new RegionBean("2", "1432", "河北", "0", "");
//        list.add(bean1);
//
//        // 市
//        bean1 = new RegionBean("4", "10011", "海淀", "1", "");
//        list.add(bean1);
//        bean1 = new RegionBean("5", "10012", "朝阳", "1", "");
//        list.add(bean1);
//        bean1 = new RegionBean("6", "14321", "石家庄", "2", "");
//        list.add(bean1);
//        bean1 = new RegionBean("7", "14322", "保定", "2", "");
//        list.add(bean1);
//        bean1 = new RegionBean("8", "14323", "唐山", "2", "");
//        list.add(bean1);
//
//        // 县
//        bean1 = new RegionBean("9", "143211", "无极", "6", "");
//        list.add(bean1);
//        bean1 = new RegionBean("10", "143212", "井陉", "6", "");
//        list.add(bean1);
//        bean1 = new RegionBean("11", "143221", "满城", "7", "");
//        list.add(bean1);
//        bean1 = new RegionBean("12", "143222", "清苑", "7", "");
//        list.add(bean1);
//
//        return list;
//
//    }

}
