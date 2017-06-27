package com.thdz.bt.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.thdz.bt.app.MyApplication;
import com.thdz.bt.bean.PushBaseBean;
import com.thdz.bt.event.ClientIdEvent;
import com.thdz.bt.event.StationAlarmEvent;
import com.thdz.bt.ui.PushListenActivity;
import com.thdz.bt.util.Finals;
import com.thdz.bt.util.NotifyUtil;
import com.thdz.bt.util.SpUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * todo 处理所有推送消息，不在之前的PushBackReceiver里处理
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 * -----------------
 * onReceiveMessageData + handlePush 处理推送消息
 */
public class GetuiIntentService extends GTIntentService {

    private static final String TAG = "GetuiSdkDemo";

    public GetuiIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "onReceiveServicePid -> " + pid);
    }

    /**
     * 处理透传消息  -- 获取到推送消息
     */
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid +
                "\nmessageid = " + messageid + "\npkg = " + pkg + "\ncid = " + cid);

        if (payload == null) {
            Log.e(TAG, "推送data = null");
        } else {
            String resultStr = new String(payload);
            Log.d(TAG, "推送data = " + resultStr);

            try {
                String[] dataArray = resultStr.split("_");
                PushBaseBean pushBean = new PushBaseBean();
                pushBean.setStationid(dataArray[0]);
                pushBean.setRegionName(dataArray[1]);
                pushBean.setStationName(dataArray[2]);
                pushBean.setAlarmtype(dataArray[3]);
                pushBean.setAlarmtime(dataArray[4]);

                handlePush(context, pushBean);// 处理推送
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 接收 cid
     */
    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);

        SpUtil.save(context, Finals.SP_CLIENTID, clientid); // 登陆页面保存
        ClientIdEvent clientEvent = new ClientIdEvent();
        clientEvent.setClientId(clientid);
        EventBus.getDefault().post(clientEvent);

    }


    /**
     * cid 离线上线通知
     */
    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.d(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    /**
     * 各种事件处理回执
     */
    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();

        String text = "设置标签失败, 未知异常";
        switch (Integer.valueOf(code)) {
            case PushConsts.SETTAG_SUCCESS:
                text = "设置标签成功";
                break;

            case PushConsts.SETTAG_ERROR_COUNT:
                text = "设置标签失败, tag数量过大, 最大不能超过200个";
                break;

            case PushConsts.SETTAG_ERROR_FREQUENCY:
                text = "设置标签失败, 频率过快, 两次间隔应大于1s且一天只能成功调用一次";
                break;

            case PushConsts.SETTAG_ERROR_REPEAT:
                text = "设置标签失败, 标签重复";
                break;

            case PushConsts.SETTAG_ERROR_UNBIND:
                text = "设置标签失败, 服务未初始化成功";
                break;

            case PushConsts.SETTAG_ERROR_EXCEPTION:
                text = "设置标签失败, 未知异常";
                break;

            case PushConsts.SETTAG_ERROR_NULL:
                text = "设置标签失败, tag 为空";
                break;

            case PushConsts.SETTAG_NOTONLINE:
                text = "还未登陆成功";
                break;

            case PushConsts.SETTAG_IN_BLACKLIST:
                text = "该应用已经在黑名单中,请联系售后支持!";
                break;

            case PushConsts.SETTAG_NUM_EXCEED:
                text = "已存 tag 超过限制";
                break;

            default:
                break;
        }

        Log.d(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + text);
    }

    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        Log.d(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " +
                taskid + "\nactionid = " + actionid + "\nresult = " + result
                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }


    /**
     * 推送请求处理
     * ----------------
     * 1 目前只处理告警推， 2 使用EventBus分发
     * 处理方式： </br>
     * 通知知栏提示，
     * 1 点击调用HomeActivity，地图移动到告警站点
     * 2 打开告警详情页。
     */
    private void handlePush(Context context, PushBaseBean pushBean) {
        if (pushBean == null) {
            return;
        }
        if (Finals.IS_TEST) {
            Log.i(TAG, "推送数据 = " + pushBean.toString());
        }

        // 处理告警推送
        // 1 告警页列表刷新
        StationAlarmEvent listEvent = new StationAlarmEvent(pushBean);
        EventBus.getDefault().postSticky(listEvent);

        MyApplication.getInstance().hasNewPush = true;
        MyApplication.getInstance().pushBean = pushBean;

        // 2 弹出通知栏消息
        MyApplication.notyId++;
        Intent intent = new Intent(context, PushListenActivity.class); // PushListenActivity  LoginActivity
//        if (TextUtils.isEmpty(MyApplication.getInstance().getUid())) {
//            intent = new Intent(context, LoginActivity.class);
//        } else {
//            intent = new Intent(context, HomeActivity.class);
//        }
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("isPush", true);
//        bundle.putSerializable("pushBean", pushBean);
//        intent.putExtras(bundle);
//        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        NotifyUtil.showNotification(context, pushBean.getStationName() + " 发生 " + pushBean.getAlarmtype(),
                pushBean.getAlarmtime(), intent, MyApplication.notyId);

    }

}
