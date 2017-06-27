package com.thdz.bt.http;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.thdz.bt.app.MyApplication;
import com.thdz.bt.base.BaseGet;
import com.thdz.bt.bean.RegionBean;
import com.thdz.bt.bean.UserInfo;
import com.thdz.bt.event.LoginEvent;
import com.thdz.bt.event.RegionDataEvent;
import com.thdz.bt.util.DataUtils;
import com.thdz.bt.util.Finals;
import com.thdz.bt.util.MD5Utils;
import com.thdz.bt.util.SpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

import static com.thdz.bt.util.TsUtils.showToast;

/**
 * 登录请求
 */
public class LoginGet extends BaseGet {

    public static final String msgTip = "登录失败";

    private static LoginGet instance;

    /**
     * 是否终止
     */
    private boolean isStop = false;

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public static LoginGet getInstance() {
        if (instance == null) {
            synchronized (LoginGet.class) {
                if (instance == null) {
                    instance = new LoginGet();
                }
            }
        }
        return instance;
    }

    public LoginGet() {

    }


    /**
     * 登录
     *
     * @param username
     * @param pwd
     * @param clientid
     */
    public void doLogin(final String username, final String pwd, String clientid) {
        isStop = false; // 每次请求时，置为false，在回调时，如果发现是true，则不再分发处理。
        String passwordMD5 = MD5Utils.encodePwdByMD5(pwd);
        String url = DataUtils.do4login(username, passwordMD5, clientid);

        doRequestGet("登录", url, new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                e.printStackTrace();
                if (isStop) {
                    return;
                }
                showToast(failTip);
                // EventBus分发处理
                LoginEvent event = new LoginEvent(false);
                post(event);

            }

            @Override
            public void onResponse(String value, int id) {
                Log.i(TAG, "登录请求 返回参数是：" + value);
                LoginEvent event = new LoginEvent();
                try {
                    if (isStop) {
                        return;
                    }
                    if (DataUtils.isReturnOK(value)) {
                        UserInfo user = JSON.parseObject(DataUtils.getReturnData(value), UserInfo.class);
                        String uid = user.getUserId();
                        MyApplication.getInstance().setUid(uid);
                        SpUtil.save(MyApplication.getInstance(), Finals.SP_UID, uid);
                        saveLoginInfo(username, pwd, "1");
                        // EventBus分发处理
                        event.setSuccess(true);
                    } else if (DataUtils.isReturnOKWith3(value)) {
                        UserInfo user = JSON.parseObject(DataUtils.getReturnData(value), UserInfo.class);
                        String uid = user.getUserId();
                        MyApplication.getInstance().setUid(uid);
                        SpUtil.save(MyApplication.getInstance(), Finals.SP_UID, uid);
                        saveLoginInfo(username, pwd, "1");
                        showToast(DataUtils.getReturnMsg(value));
                        // EventBus分发处理
                        event.setSuccess(true);
                    } else {
                        showToast(DataUtils.getReturnMsg(value));
                        // EventBus分发处理
                        event.setSuccess(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (isStop) {
                        return;
                    }
                    showToast(msgTip);
                    // EventBus分发处理
                    event.setSuccess(false);
                } finally {
                    if (isStop) {
                        return;
                    }
                    post(event);
                }
            }
        });
    }


    /**
     * 登录成功后保存登录信息
     */
    private void saveLoginInfo(String username, String pwd, String autoLogin) {
        SpUtil.save(MyApplication.getInstance(), Finals.SP_USERNAME, username);
        SpUtil.save(MyApplication.getInstance(), Finals.SP_PWD, pwd);
        SpUtil.save(MyApplication.getInstance(), Finals.SP_AUTOLOGIN, autoLogin);//自动登录

    }


    /**
     * 获取区域数据
     */
    public void getRegionData() {
        String url = DataUtils.do4regionlist(MyApplication.getInstance().getUid());
        doRequestGet("获取区域数据", url, new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                Log.i(TAG, "获取区域数据 -- 失败");
                e.printStackTrace();
                // todo EventBus分发处理
                RegionDataEvent event = new RegionDataEvent();
                post(event);
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Log.i(TAG, "获取区域数据 返回参数 = " + response);
                    String value = DataUtils.getReturnList(response);
                    // todo 把jsonStr存到本地，防止没有返回RegionList
                    MyApplication.getInstance().regionList = JSON.parseArray(value, RegionBean.class);
//                    // EventBus分发处理
//                    RegionDataEvent event = new RegionDataEvent();
//                    post(event);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "获取区域数据 -- 失败");
//                    // EventBus分发处理
//                    RegionDataEvent event = new RegionDataEvent();
//                    post(event);
                } finally {
                    // todo EventBus分发处理
                    RegionDataEvent event = new RegionDataEvent();
                    post(event);
                }
            }
        });
    }

}
