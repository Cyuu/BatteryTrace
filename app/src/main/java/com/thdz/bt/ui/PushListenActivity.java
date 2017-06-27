package com.thdz.bt.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.thdz.bt.R;
import com.thdz.bt.app.MyApplication;
import com.thdz.bt.bean.PushBaseBean;
import com.thdz.bt.event.StationAlarmEvent;
import com.thdz.bt.util.SpUtil;
import com.thdz.bt.util.VUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.thdz.bt.util.TsUtils.showToast;

/**
 * 推送监听页面
 * -------------
 * 登录 --> this -->主页
 * ---------
 * 该页面有两种打开方式
 * 1 登录跳转， 需要传递一个标识
 * 2 点推送打开，不登录
 */
public class PushListenActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "PushListenActivity";
    public MyApplication application;

    private Context context = this;

    private boolean isPush = false;
    private PushBaseBean pushBean = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_listen);
        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        application = MyApplication.getInstance();
        application.addActivity(this);

        initView(savedInstanceState);
    }


    public void initView(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getBoolean("fromLogin")) { // 从登录来，直接登录
            goMain();
        } else {
            if (MyApplication.getInstance().hasNewPush) {
                dealPush();
            } else {
                goMain();
            }
        }

    }

    private void dealPush() {

        showToast("正在验证权限");
        if (TextUtils.isEmpty(application.getUid())) {
            if (TextUtils.isEmpty(SpUtil.getUid(getApplicationContext()))) { // 未登录
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else { // 已登录
                application.setUid(SpUtil.getUid(getApplicationContext()));
                goMain();
            }
        } else { // 已登录
            goMain();
        }

        MyApplication.getInstance().hasNewPush = false;
        MyApplication.getInstance().pushBean = null;
    }

    private void goActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    /**
     * 跳转至首页
     */
    private void goMain() {
        Bundle params = null;
        if (isPush) {
            showToast("验证通过");
            params = new Bundle();
            params.putBoolean("isPush", isPush);
            params.putSerializable("pushBean", pushBean);
        }
        goActivity(HomeActivity.class, params);
    }


    @Override
    public void onClick(View v) {
        if (VUtils.isFastDoubleClick()) {
            return;
        } else {
            switch (v.getId()) {
                default:
                    break;
            }
        }
    }


    /**
     * 响应 有告警
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void dealStationAlarmEvent(StationAlarmEvent event) {
        Log.i(TAG, "处理推送告警！");
        if (event != null) {
            pushBean = event.getPushBean();
        }
        isPush = true;
        dealPush();

    }

}
