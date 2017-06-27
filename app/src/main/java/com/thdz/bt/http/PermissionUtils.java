package com.thdz.bt.http;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.igexin.sdk.PushManager;
import com.thdz.bt.service.GetuiIntentService;
import com.thdz.bt.service.GetuiService;

import java.io.File;

/**
 * 申请权限 和 注册服务 的工具类
 *
 */
public class PermissionUtils {

    private static final String TAG = "PermissionUtils";


//    private Class pushService = GetuiService.class;

    private static final int REQUEST_PERMISSION = 10;


    /**
     * 初始化个推服务
     * @param context
     */
    public static void initGetuiService(Context context){
        PackageManager pkgManager = context.getPackageManager();

        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        boolean sdCardWritePermission = pkgManager.checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;

        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission = pkgManager.checkPermission(
                Manifest.permission.READ_PHONE_STATE, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission) {
            requestPermission(context);
        } else {
            PushManager.getInstance().initialize(context.getApplicationContext(), GetuiService.class);
        }

        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().registerPushIntentService(context.getApplicationContext(), GetuiIntentService.class);

//        // 应用未启动, 个推 service已经被唤醒,显示该时间段内离线消息
//        if (DemoApplication.payloadData != null) {
//            tLogView.append(DemoApplication.payloadData);
//        }

        // cpu 架构
        Log.d(TAG, "cpu arch = " + (Build.VERSION.SDK_INT < 21 ? Build.CPU_ABI : Build.SUPPORTED_ABIS[0]));

        // 检查 so 是否存在
        File file = new File(context.getApplicationInfo().nativeLibraryDir + File.separator + "libgetuiext2.so");
        Log.e(TAG, "libgetuiext2.so exist = " + file.exists());
    }


    private static void requestPermission(Context context) {
        ActivityCompat.requestPermissions(
                (Activity) context,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                REQUEST_PERMISSION);
    }

}
