package com.thdz.bt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.thdz.bt.R;
import com.thdz.bt.base.BaseActivity;
import com.thdz.bt.bean.PushBaseBean;
import com.thdz.bt.event.ClientIdEvent;
import com.thdz.bt.event.LoginEvent;
import com.thdz.bt.event.RegionDataEvent;
import com.thdz.bt.http.LoginGet;
import com.thdz.bt.http.PermissionUtils;
import com.thdz.bt.util.Finals;
import com.thdz.bt.util.SpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * 登录页面<br/>
 * ip地址是否用自动输入完成框<br/>
 * 如果不是自动登录，则不填充控件<br/>
 * 这里默认自动登录，而只有退出登录，才取消自动登录<br/>
 * 登录--->请求app使用的状态码-->成功后goMain(),失败后，goMain()<br/>
 * 如果上次已经登录，尚未注销，那将不再登录，直接：--->请求app使用的状态码 <br/>
 * -----------------<br/>
 * 使用了通用请求封装网络部分，然后用EventBus分发结果<br/>
 */
public class LoginGetActivity extends BaseActivity {

    @BindView(R.id.tv_username)
    EditText tv_username;

    @BindView(R.id.tv_pwd)
    EditText tv_pwd;

    @BindView(R.id.btn_login)
    Button btn_login;

    @BindView(R.id.btn_login_test)
    Button btn_login_test;

    private String username = "";
    private String password = "";

    private String clientid = "";

    private boolean isPush = false;
    private PushBaseBean pushBean = null;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        btn_login.setOnClickListener(this);
        btn_login_test.setOnClickListener(this);

        // 获取到ClientId之前，将登陆按钮置灰，不可用
        if (TextUtils.isEmpty(SpUtil.getData(context.getApplicationContext(), Finals.SP_CLIENTID))) {
            btn_login.setBackgroundResource(R.color.gray);
            btn_login.setEnabled(false);
        }

        showLoginEditView();

        PermissionUtils.initGetuiService(context); // 集成个推

        // 获取推送信息
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                isPush = bundle.getBoolean("isPush", false);
                pushBean = (PushBaseBean) bundle.getSerializable("pushBean");
            }
        }

        if (isPush) {
            showToast("正在验证权限");
        }
    }


    @Override
    public void onClick(int resId) {
        switch (resId) {
            case R.id.btn_login:
                requestLogin();
                break;
            case R.id.btn_login_test:
                gotoMain();
                break;
            default:
                break;

        }
    }


    /**
     * 登录 请求
     */
    private void requestLogin() {
        if (!doCheck()) {// 输入验证
            return;
        }
        showProgressDialog();
        LoginGet.getInstance().doLogin(username, password, clientid);

    }


    /**
     * 根据UserId获取区域数据 请求
     */
    public void requestRegionData() {
        showProgressDialog();
        LoginGet.getInstance().getRegionData();
    }


    /**
     * 设置检测规则，检查输入是否正确, true 输入正确 // false 输入错误
     */
    private boolean doCheck() {

        username = tv_username.getText().toString().trim();
        password = tv_pwd.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            showToast("请输入用户名");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            showToast("请输入密码");
            return false;
        }
        return true;
    }


    /**
     * 跳转至首页
     */
    private void gotoMain() {

        Bundle bundle = null;
        if (isPush) {
            showToast("验证通过");
            bundle = new Bundle();
            bundle.putBoolean("isPush", isPush);
            bundle.putSerializable("pushBean", pushBean);
        }
        goActivity(HomeActivity.class, bundle);
    }


    /**
     * 判断是否是退出登录而来,这里默认自动登录，而只有退出登录，才取消自动登录<br/>
     * 根据sp里的值，填充到控件中，如果没有则，不动<br/>
     */
    private void showLoginEditView() {
        if (SpUtil.isAutoLogin(context.getApplicationContext())
                && !TextUtils.isEmpty(SpUtil.getUid(getApplicationContext()))) { // 自动登录
            tv_username.setText(SpUtil.getData(context.getApplicationContext(), Finals.SP_USERNAME));
            tv_pwd.setText(SpUtil.getData(context.getApplicationContext(), Finals.SP_PWD));

            tv_username.setSelection(tv_username.getText().toString().length());
            tv_pwd.setSelection(tv_pwd.getText().toString().length());

            application.setUid(SpUtil.getUid(context.getApplicationContext()));

            requestRegionData();
        } else { // 正常登录或者退出登录
            hideProgressDialog();
            tv_username.setText(SpUtil.getData(context.getApplicationContext(), Finals.SP_USERNAME));
            tv_username.setSelection(tv_username.getText().toString().length());
        }

    }


    @Override
    public void onBackPressed() {
        if (progressDialog != null && progressDialog.isShowing()) {
            hideProgressDialog();
            LoginGet.getInstance().setStop(true);
            return;
        }

        super.onBackPressed();

    }

    /**
     * 响应EventBus： 获取个推ClientId的 请求
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void dealClientIdEvent(ClientIdEvent event) {
        Log.i(TAG, "页面已获取到ClientId: " + event.getClientId());
        if (event == null || TextUtils.isEmpty(event.getClientId())) {
            return;
        }
        // 将登陆框置为绿色，并可用
        btn_login.setBackgroundResource(R.drawable.bg_green_selector);
        btn_login.setEnabled(true);

        clientid = event.getClientId();
        SpUtil.save(context, Finals.SP_CLIENTID, clientid);

    }


    /**
     * 响应EventBus： 登录数据返回
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealLoginEvent(LoginEvent event) {
        Log.i(TAG, "页面已获取到ClientId: " + event.toString());
        hideProgressDialog();
        if (event != null && event.isSuccess()) {
            requestRegionData();
        }

    }


    /**
     * 响应EventBus： 获取到区域数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void dealRegionDataEvent(RegionDataEvent event) {
        Log.i(TAG, "页面已获取到 区域数据: " + event.toString());
        hideProgressDialog();
        gotoMain();

    }


}
