package com.thdz.bt.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.thdz.bt.app.MyApplication;

import java.util.List;

public class VUtils {

    /**
     * 上次点击时间
     */
    private static long lastClickTime;
    private final static long interval_time = 300;

    /**
     * 防止重复点击，需要加：click select itemClick
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < interval_time) {
            // showToast("按太快了");
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static void setSwipeColor(SwipeRefreshLayout sLayout) {
        sLayout.setColorSchemeResources(
                android.R.color.holo_blue_dark,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
    }

    /**
     * 设置SwipyLayout的颜色
     */
    public static void setSwipyColor(SwipyRefreshLayout sLayout) {
        sLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
    }


//    /**
//     * TODO 查看已下载的大图
//     */
//    public static void gotoImage(Context context, String image_name) {
//        Intent intent = new Intent(context, ImageActivity.class);
//        intent.putExtra("path", image_name);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(intent);
//
//    }


    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获得屏幕高度
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }


    /**
     * 校验某个服务是否还存在
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        // 校验服务是否还存在
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : services) {
            // 得到所有正在运行的服务的名称
            String name = info.service.getClassName();
            if (serviceName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) { // Context context,
        final float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        final float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
