package com.thdz.bt.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.thdz.bt.R;
import com.thdz.bt.app.MyApplication;


/**
 * 通知栏工具类
 */
public class NotifyUtil {

    /**
     * 通知
     */
//    public static void CreateNotify(Context context, String title, String content, int index, Intent intent) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());
//
//        PendingIntent pi = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 0);
//        // 震动通知
//        // long[] vibratePattern = new long[] {400,800,1200,1600};
//        // 创建一个 Notification
//        Notification notification = builder
//                .setContentTitle(title)
//                .setContentText(content)
//                .setContentIntent(pi)
//                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
//                .setColor(context.getApplicationContext().getResources().getColor(R.color.white))
//                .setSmallIcon(R.mipmap.ic_launcher)
//                // .setVibrate(vibratePattern)
//                .build();
//
//        notification.flags |= Notification.FLAG_AUTO_CANCEL; // 设置点击后自动消失
//        // 使用 NotificationManagerCompat 的 notify 方法展示你设置了 id 的那个 Notification
//        NotificationManagerCompat notificationManager =
//                NotificationManagerCompat.from(context.getApplicationContext());
//        notificationManager.notify(index, notification);
//    }

//
//    public static void showNotify4Nubia(Context context, int no) {
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        //新建一个Notification管理器;
//        //API level 11
//        Notification.Builder builder = new Notification.Builder(context);//新建Notification.Builder对象
//        PendingIntent intent = PendingIntent.getActivity(context, 0, new Intent(context, LoginActivity.class), 0);
//        //PendingIntent点击通知后所跳转的页面
//        builder.setContentTitle("通知消息 Test Title");
//        builder.setContentText("message");
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setContentIntent(intent);//执行intent
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//悬挂式Notification，5.0后显示
//            builder.setFullScreenIntent(intent, true);
//            builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
//            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
//        }
//
//        Notification notification = builder.getNotification();//将builder对象转换为普通的notification
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;//点击通知后通知消失
//        manager.notify(no, notification);//运行notification
//    }


    public static void showNotification(Context context, String title, String content, Intent intent, int notyId) {
        // 获取一个 NotificationCompat.Builder 实例。
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());
        PendingIntent pi = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 0);
        // 震动通知
        long[] vibratePattern = new long[]{400, 800, 1200, 1600};
        // 创建一个 Notification
        Notification notification = builder
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pi)
                .setColor(context.getApplicationContext().getResources().getColor(R.color.white))
                .setSmallIcon(R.drawable.push)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .build();

//        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        notification.defaults |= Notification.DEFAULT_SOUND;

        notification.flags |= Notification.FLAG_AUTO_CANCEL; // 设置点击后自动消失
        // 使用 NotificationManagerCompat 的 notify 方法展示你设置了 id 的那个 Notification
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context.getApplicationContext());
        notificationManager.notify(notyId, notification);

    }


    /**
     * 清空通知栏消息
     */
    public static void clearNotification() {
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(MyApplication.getInstance());
        notificationManager.cancelAll();
    }

    /**
     * 点击消失的通知
     */
    public static void showNullNotify(Context context, String title, String content, int notyId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());
        Notification notification = builder
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(null)
                .setColor(context.getApplicationContext().getResources().getColor(R.color.white))
                .setSmallIcon(R.drawable.push)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL; // 设置点击后自动消失
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context.getApplicationContext());
        notificationManager.notify(notyId, notification);
    }

}
