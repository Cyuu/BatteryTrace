package com.thdz.bt.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.thdz.bt.R;
import com.thdz.bt.app.MyApplication;
import com.thdz.bt.bean.StationBean;
import com.thdz.bt.event.TraceHistoryEvent;
import com.thdz.bt.event.TraceRecentEvent;
import com.thdz.bt.ui.AlarmListActivity;
import com.thdz.bt.ui.StationDetailActivity;

import org.greenrobot.eventbus.EventBus;

public class DialogUtil {


    public static void goActivity(Context context, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }


    /**
     * 公共对话框，如果不传值则隐藏
     * , final String id, final String title, String addr, String pointStr, String managername, final String telno, String power, String com_way
     */
    public static void bottomDialog(final Context context, final StationBean bean) {
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_common, null);
        //获得dialog的window窗口
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.dialogStyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        //将自定义布局加载到dialog上
        dialog.setContentView(dialogView);
        TextView tv_name = getView(dialogView, R.id.tv_name); // 名称
        TextView tv_addr = getView(dialogView, R.id.tv_addr); // 地址

        TextView tv_point_lon = getView(dialogView, R.id.tv_point_lon); // 经度
        TextView tv_point_lat = getView(dialogView, R.id.tv_point_lat); // 纬度

        TextView tv_tel = getView(dialogView, R.id.tv_tel); // 责任人电话
        TextView tv_power = getView(dialogView, R.id.tv_power); // 最新电压
        TextView tv_com_way = getView(dialogView, R.id.tv_com_way); // 通讯方式
        TextView tv_com_time = getView(dialogView, R.id.tv_com_time); // 最后通讯时间

        TextView tv_alarm_state = getView(dialogView, R.id.tv_alarm_state); // 告警状态

        tv_name.setText(bean.getStationName());
        tv_addr.setText(bean.getModuleAddr());


        // 位置信息：
        tv_point_lon.setText(DataUtils.getFormat6(Double.parseDouble(bean.getOLongitude())));
        tv_point_lat.setText(DataUtils.getFormat6(Double.parseDouble(bean.getOLatitude())));

        tv_tel.setText(bean.getSimCard());

        tv_power.setText(bean.getModuleU() + "V");
        tv_com_way.setText(bean.getCommType());
        tv_com_time.setText(bean.getCurrentTime());
        if (!bean.isAlarm()) {
            tv_alarm_state.setText("正常");
            tv_alarm_state.setTextColor(context.getResources().getColor(R.color.green_deep_color));
        } else {
            StringBuilder sb = new StringBuilder();
            if (bean.getIsLost().equals("1")) {
                sb.append("失联、");
            }
            if (bean.getIsVib().equals("1")) {
                sb.append("震动、");
            }
            if (bean.getIsMove().equals("1")) {
                sb.append("位移、");
            }
            if (bean.getIsLow().equals("1")) {
                sb.append("低压");
            }

            String str = sb.toString();
            if (str.length() > 1) {
                if (!str.contains("低压")) {
                    str = str.substring(0, str.length() - 1); // 含前，不含后
                }
//                str += " 告警";
            } else {
                str = "";
            }

            tv_alarm_state.setText(str);
            tv_alarm_state.setTextColor(Color.RED);
        }


        TextView btn_sure = (TextView) dialogView.findViewById(R.id.btn_sure);
        TextView btn_trace = (TextView) dialogView.findViewById(R.id.btn_trace);
        TextView btn_detail = (TextView) dialogView.findViewById(R.id.btn_detail);
        TextView btn_route = (TextView) dialogView.findViewById(R.id.btn_route);
        TextView btn_alarm_his = (TextView) dialogView.findViewById(R.id.btn_alarm_his);


        dialog.show();

        btn_alarm_his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TsUtils.showToast("查询历史告警");
                dialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putString("stationid", bean.getStationId());
                bundle.putBoolean("isHistory", true);
                goActivity(context, AlarmListActivity.class, bundle);
            }
        });


        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TsUtils.showToast("查询实时轨迹");
                TraceRecentEvent envet = new TraceRecentEvent(bean.getStationId());
                EventBus.getDefault().post(envet);
                dialog.dismiss();
            }
        });
        btn_trace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TsUtils.showToast("查询历史轨迹");
                TraceHistoryEvent envet = new TraceHistoryEvent(bean.getStationId());
                EventBus.getDefault().post(envet);
                dialog.dismiss();
            }
        });

        btn_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                // 屏蔽百度sdk自带的sdk
//                MyApplication.getInstance().gotoNavi(context, bean);
//                boolean hasBaidu = false;
//                if (DataUtils.isPkgInstalled(context, "com.baidu.BaiduMap")) {
//                    hasBaidu = true;
//                } else {
//                    hasBaidu = false;
//                }
//
//                boolean hasGaode = false;
//                if (DataUtils.isPkgInstalled(context, "com.autonavi.minimap")) {
//                    hasGaode = true;
//                } else {
//                    hasGaode = false;
//                }
//
//                if (!hasBaidu && !hasGaode) {
//                    MyApplication.getInstance().gotoNaviBySDK(bean);
//                } else {
//                    naviChoose(context, bean);
//                }

                naviChoose(context, bean);
                dialog.dismiss();

            }
        });

        btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putString("stationid", bean.getStationId());
                bundle.putSerializable("bean", bean);
                goActivity(context, StationDetailActivity.class, bundle);
            }
        });

    }

    /**
     * 点击导航后，选择导航方式，屏蔽自带导航方式
     */
    public static void naviChoose(Context context, final StationBean bean) {
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_navi_choose, null);
        //获得dialog的window窗口
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.dialogStyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        //将自定义布局加载到dialog上
        dialog.setContentView(dialogView);
        TextView tv_baidu = getView(dialogView, R.id.tv_baidu); // 百度导航
        TextView tv_gaode = getView(dialogView, R.id.tv_gaode); // 高德导航
//        TextView tv_sdk = getView(dialogView, R.id.tv_sdk);   // 集成的百度SDK导航

        final boolean haveBaidu = DataUtils.isPkgInstalled(context, "com.baidu.BaiduMap");
        if (haveBaidu) {
//            tv_baidu.setVisibility(View.VISIBLE);
            tv_baidu.setText("百度");
        } else {
//            tv_baidu.setVisibility(View.GONE);
            tv_baidu.setText("百度(未安装)");
        }

        final boolean haveGaode = DataUtils.isPkgInstalled(context, "com.autonavi.minimap");
        if (haveGaode) {
//            tv_gaode.setVisibility(View.VISIBLE);
            tv_gaode.setText("高德");
        } else {
//            tv_gaode.setVisibility(View.GONE);
            tv_gaode.setText("高德(未安装)");
        }

        // 百度导航
        tv_baidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (haveBaidu) {
                    MyApplication.getInstance().gotoNaviByBaidu(bean);
                } else {
                    TsUtils.showToast("请先安装‘百度地图’app");
                }
                dialog.dismiss();

            }
        });

        // 高德导航
        tv_gaode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveGaode) {
                    MyApplication.getInstance().gotoNaviByGaode(bean);
                } else {
                    TsUtils.showToast("请先安装‘高德地图’app");
                }
                dialog.dismiss();

            }
        });

        // 屏蔽百度sdk自带的sdk
//        // 百度sdk导航
//        tv_sdk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MyApplication.getInstance().gotoNaviBySDK(bean);
//                dialog.dismiss();
//            }
//        });

        dialog.show();

    }


    public static <E extends View> E getView(View view, int id) {
        try {
            return (E) view.findViewById(id);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

}
