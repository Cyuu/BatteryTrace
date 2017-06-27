package com.thdz.bt.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * 推送工具类
 */
public class PollUtils {

    private static final String TAG = "PollUtils";

    /**
     * 开启轮询服务 --> 所有站点状态 / 刷新轨迹
     * @param  clz :区分是什么Service
     */
    public static void startPollingService(Context context, int seconds, Class<?> clz, String action) {
        // 获取AlarmManager系统服务
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // 包装需要执行service的Intent
        Intent intent = new Intent(context, clz);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 触发服务的起始时间
        long triggerAtTime = SystemClock.elapsedRealtime();

        // 使用AlarmManager的setRepeating方法设置定期执行的时间间隔（seconds）和需要执行的Service
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime, seconds * 1000, pendingIntent);

    }


    /**
     * 停止轮询服务
     */
    public static void stopPollingService(Context context, Class<?> clz, String action) {
        // 获取AlarmManager系统服务
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // 包装需要执行service的Intent
        Intent intent = new Intent(context, clz);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 取消正在执行的服务
//        TsUtils.showToast("已停止任务");
        Log.i(TAG, "已停止任务");
        manager.cancel(pendingIntent);

    }

}
