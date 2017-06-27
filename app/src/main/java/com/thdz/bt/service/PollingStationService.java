package com.thdz.bt.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.thdz.bt.app.MyApplication;
import com.thdz.bt.bean.ReturnDataBean;
import com.thdz.bt.event.StationsRefresh4MapEvent;
import com.thdz.bt.util.DataUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Call;


/**
 * 全部站点信息(用于地图刷新展示) 轮询服务
 */
public class PollingStationService extends Service {

    public static final String ACTION = "com.thdz.bt.service.PollingStationService";
    private static final String TAG = "PollingStationService";
    private MyApplication application = MyApplication.getInstance();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        new PollingThread().start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service Destory");
    }

    class PollingThread extends Thread {
        @Override
        public void run() {
            super.run();
            Log.i(TAG, "执行轮询任务 - 站点");

//            // todo 测试通知栏，
//            String curTime = DataUtils.getCurrentTimeStr();
//            Intent i = new Intent(PollingStationService.this, HomeActivity.class);
//            NotifyUtil.showNotification(PollingStationService.this,
//                    "StationList Refresh!", "时间：" + curTime, i, MyApplication.notyId);
//            Log.i(TAG, "StationList Refresh!");

            // 执行查询任务
            String url = DataUtils.do4AllStationList(MyApplication.getInstance().getUid());
            Log.i(TAG, "Station轮询-url：" + url);
            OkHttpUtils
                    .get()
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, final Exception e, int id) {
                            Log.i(TAG, "查询站点失败");
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(String value, int id) {
                            Log.i(TAG, "获取所有站点列表 返回参数是：" + value);
                            try {
                                if (DataUtils.isReturnOK(value)) {
                                    String content = DataUtils.getReturnData(value);
                                    MyApplication.getInstance().AllstationObj =
                                            JSONObject.parseObject(content, ReturnDataBean.class);

                                    // 转换坐标
                                    DataUtils.convertStationListByBaiduLoc();

                                    StationsRefresh4MapEvent event = new StationsRefresh4MapEvent("refresh successful", true);
                                    EventBus.getDefault().post(event);
                                    Log.i(TAG, "查询站点成功");
                                } else {
                                    Log.i(TAG, DataUtils.getReturnMsg(value));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.i(TAG, "查询站点失败");
                            }
                        }
                    });

        }
    }

}
