版本号更新记录：
versionCode   versionName   desc
1               1.0         开发提交测试
2               1.0.0.1     回归测试
  f





-------------------
<h1>开发过程记录</h1>
<h4>开始时间: 2017.4.6</h4>

<h4>项目名称：蓄电池防盗</h4>
<h4>android工程名： BatteryTrace (蓄电池追踪)</h4>
<h4>包名:com.thdz.bt</h4>
<h4>版本：1.0  版本号： 1</h4>
<h4>签名：galaxy.jks</h4>
曾用签名：thdz.jks，因为百度地图静音问题，修改了签名，重新在百度注册了app

-------------------
<font color="orange">对应功能：</font><br/>
点击地图上的Marker点时，在Marker上生成一个按钮，点击按钮有动作响应。
<br/><font color="orange">代码办法：</font><br/>
mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button)
    , ll, -47, listener);
mBaiduMap.showInfoWindow(mInfoWindow);

-------------------
<font color="orange">标准返回json数据格式：</font><br/>
{"code":"","msg":"","data":{"uid":"1"},"list":[{"uid":"1"},{"uid":"1"}]}

-------------
折线图，确认默认时间选项，初始化时不查询信息。
-------------
查询区域：省市县3级联动view，使用Spinner。在HomeActivity集成。
目前耦合重，后续提出来单独view封装。
-------------
2017.5.15 hc开始写接口，准备调试.

需要展示时间的页面，
需要传递给服务端一个默认的时间

告警类型：
-1 全部    0低压告警  , 1位移告警 , 2震动告警 , 7失联告警 , 8失联告警恢复。若查询全部告警

导航：因为静音问题，不适用sdk自带的导航，使用百度地图app/高德地图app导航，如未安装，则提示安装。

-----------------
<font size='20' color="red">bug：</font><br/>

1 默认地图刷不出来是因为:
onMapLoaded()方法里的<font color="red">zoom</font>参数有误，不应该弄最大的，应该放<font color="red"><=13</font>的数值

定位功能代码，详见：WorkSpace/BaiduLocDemo/
----------------

请求所有站点接口的地方：
1 HomeActivity里的requestAllStationList
2 PollingStationService的Thread

因为努比亚手机的问题：
1 收不到推送，已集成了新版推送代码
2 不能使用PollingStationService,已改为：TimerTask和Timer
-----------
需要转换坐标的地方：
1 请求两个站点接口的地方。
2 发送到百度/高德导航坐标的时候

--------------
服务端推送了告警过来，如何处理:
1 弹出通知栏消息
2 点击消息进入主页后，刷新地图，定位到告警站点，弹出info和Dialog
需要不需要：
  HomeActivity的dealStationAlarmEvent
  onCreate里，处理：getIntent,huoqu
3 告警列表页面刷新
4 Push页面处理
5 PushListenActivity页面是否应该 Sticky=true ???

## 告警推送处理：
1 如果尚未打开app，则：只弹出通知栏消息

2 如果打开了app
  2.1 如果正在HomeActivity，则展示一个闪烁告警按钮，
  2.2 如果其他页面，则只弹出通知栏消息

3 如果点击通知栏消息：
  1 打开HomeActivity(已登录)/LoginActivity(尚未登录),
  2 定位到告警站点
  3 showInfo
  4 showBottomDialog

4 刷新最新告警列表

4 刷新告警详情页面 -- 如果是当前站点的话
-----------------------
集成新版定位的so包，只有arm64-v8a 和arm版本的so包：liblocSDK7a.so

// 刷新地图，可能有效
mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mBaiduMap.getMapStatus()));

// 点聚合时，清除后，强制刷新展示
mClusterManager.clearItems();  // 1 清空历史Items
mClusterManager.addItems(list);// 2 增加新的Items
mClusterManager.cluster();     // 3 强制刷新点聚合

-------------
轮询任务：
因为韩冲的Nubia手机接收不到Service里的线程处理
所以：舍弃了PollingStationService和PollingTraceService，改为使用：
     Timer和TimeTask来实现。

-----------------







