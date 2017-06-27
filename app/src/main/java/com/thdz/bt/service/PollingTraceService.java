package com.thdz.bt.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.thdz.bt.app.MyApplication;
import com.thdz.bt.bean.TraceBean;
import com.thdz.bt.event.TarceRefreshEvent;
import com.thdz.bt.ui.HomeActivity;
import com.thdz.bt.util.DataUtils;
import com.thdz.bt.util.NotifyUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import okhttp3.Call;


/**
 * 站点的轨迹信息 轮询服务
 */
public class PollingTraceService extends Service {

    public static final String ACTION = "com.thdz.bt.service.PollingTraceService";
    private static final String TAG = "PollingTraceService";

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
            Log.i(TAG, "执行轮询任务 - 轨迹");

            // time
            String curTime = DataUtils.getCurrentTimeStr();

            // 当计数能被5整除时弹出通知
            Intent intent = new Intent(PollingTraceService.this, HomeActivity.class);
            NotifyUtil.showNotification(PollingTraceService.this,
                    "Trace Current Refresh!", "时间：" + curTime, intent, MyApplication.notyId);
            Log.i(TAG, "Trace Current Refresh!");

            // 执行查询任务
            String url = DataUtils.do4TraceNew(MyApplication.getInstance().curStationId4Trace);

            Log.i(TAG, "请求地址：" + url);
            OkHttpUtils
                    .get()
                    .url(url)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, final Exception e, int id) {
                            Log.i(TAG, "查询轨迹失败");
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(String value, int id) {
                            Log.i(TAG, "获取某站点的最新轨迹 返回参数是：" + value);
                            try {
                                if (DataUtils.isReturnOK(value)) {
                                    String listStr = DataUtils.getReturnList(value);
                                    List<TraceBean> list = JSONArray.parseArray(listStr, TraceBean.class);

                                    // 转换坐标
                                    list = DataUtils.convertTraceListByBaiduLoc(list);

                                    EventBus.getDefault().post(new TarceRefreshEvent(true, list));
                                } else {
                                    Log.i(TAG, "查询轨迹失败");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.i(TAG, "查询轨迹失败");
                            }
                        }
                    });
        }
    }


}
