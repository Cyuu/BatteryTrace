package com.thdz.bt.base;

import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

/**
 * 通用网络请求基类
 */
public abstract class BaseGet {

    public final String failTip = "通信失败，请重试"; // 请求失败信息提示
    public String msgTip = "未获取到相关数据"; // 处理异常信息提示

    public String TAG = this.getClass().getSimpleName();

    /**
     * get方式
     */
    public void doRequestGet(String title, String url, StringCallback callback) {
        Log.i(TAG, title + " 请求地址：" + url);
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(callback);
    }

    /**
     * post方式
     *
     * @param params post参数
     */
    public void doRequestPost(String title,  String url, String params, StringCallback callback) {
        Log.i(TAG, title + " 请求地址：" + url);
        OkHttpUtils
                .get()
                .url(url)
                .addParams("sCmd", params)
                .build()
                .execute(callback);
    }

    /**
     * EventBus分发事件
     */
    public void post(Object obj){
        EventBus.getDefault().post(obj);
    }


}
