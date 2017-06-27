package com.thdz.bt.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.PushManager;
import com.thdz.bt.R;
import com.thdz.bt.base.BaseActivity;
import com.thdz.bt.bean.PushBaseBean;
import com.thdz.bt.bean.RegionBean;
import com.thdz.bt.bean.UserInfo;
import com.thdz.bt.event.ClientIdEvent;
import com.thdz.bt.service.GetuiIntentService;
import com.thdz.bt.service.GetuiService;
import com.thdz.bt.util.DataUtils;
import com.thdz.bt.util.Finals;
import com.thdz.bt.util.MD5Utils;
import com.thdz.bt.util.SpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 登录页面<br/>
 * ip地址是否用自动输入完成框<br/>
 * 如果不是自动登录，则不填充控件<br/>
 * 这里默认自动登录，而只有退出登录，才取消自动登录<br/>
 * 登录--->请求app使用的状态码-->成功后goMain(),失败后，goMain()<br/>
 * 如果上次已经登录，尚未注销，那将不再登录，直接：--->请求app使用的状态码 <br/>
 * <br/>
 */
public class LoginActivity extends BaseActivity {

    private static final int REQUEST_PERMISSION = 10;

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
    private String passwordMD5 = "";

    private String clientid = "";

    private String msgFail = "登录失败";

    private Class pushService = GetuiService.class;

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

        initGetuiPermission(); // 集成个推


//        // 获取推送信息
//        Intent intent = getIntent();
//        if (intent != null) {
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                isPush = bundle.getBoolean("isPush", false);
//                pushBean = (PushBaseBean) bundle.getSerializable("pushBean");
//            }
//        }
//
//        if (isPush) {
//            showToast("正在验证权限");
//        }

    }


    @Override
    public void onClick(int resId) {
        switch (resId) {
            case R.id.btn_login:
                doLogin();
                break;
            case R.id.btn_login_test:
                gotoMain();
//                    DataUtils.testConvert(); // 测试Gps坐标转换为百度坐标系

                break;
            default:
                break;
        }

    }


    /**
     * 响应 获取个推ClientId的 请求
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetClientIdEvent(ClientIdEvent event) {
        Log.i(TAG, "已获取到ClientId: " + event.getClientId());
        // showToast("获取到ClientId："+ event.getClientId());
        // 如果本地未保存，则调整登陆按钮颜色，并保存（第一次登录使用），否则，不做任何处理(以后使用)
        if (event == null || TextUtils.isEmpty(event.getClientId())) {
            return;
        }
//        if (TextUtils.isEmpty(SpUtil.getData(context.getApplicationContext(), Finals.SP_CLIENTID))) {
        // 将登陆框置为绿色，并可用
        btn_login.setBackgroundResource(R.drawable.bg_green_selector);
        btn_login.setEnabled(true);

        clientid = event.getClientId();
        SpUtil.save(context, Finals.SP_CLIENTID, clientid);
//        }

    }


    /**
     * 获取区域列表(根据UserId)
     */
    public void RequestRegionData() {
        showProgressDialog();
        String url = DataUtils.do4regionlist(application.getUid());
        doRequestGet(url, new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                Log.i(TAG, "获取区域列表 -- 失败");
                e.printStackTrace();
                hideProgressDialog();
                showToast(failTip + ", 未获取到初始化数据，请重新登录");
//                gotoMain();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Log.i(TAG, "获取区域列表 返回参数 = " + response);
                    String value = DataUtils.getReturnList(response);
                    // todo 把jsonStr存到本地，防止没有返回RegionList
                    if (application.regionList == null) {
                        application.regionList = new ArrayList<RegionBean>();
                    }
                    if (!application.regionList.isEmpty()) {
                        application.regionList.clear();
                    }
                    application.regionList = JSON.parseArray(value, RegionBean.class);
                    gotoMain();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "获取区域列表 -- 失败");
                    showToast("未获取到初始化数据，请重新登录");
                }
                hideProgressDialog();
            }
        });
    }


    /**
     * 登录请求
     */
    private void doLogin() {
        if (!doCheck()) {// 输入验证
            return;
        }
        showProgressDialog();
        passwordMD5 = MD5Utils.encodePwdByMD5(password);
        String url = DataUtils.do4login(username, passwordMD5, clientid);
        doRequestGet(url, new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                hideProgressDialog();
                showToast(failTip + ", " + msgFail);
                e.printStackTrace();

            }

            @Override
            public void onResponse(String value, int id) {
                hideProgressDialog();
                Log.i(TAG, "登录请求 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {
                        UserInfo user = JSON.parseObject(DataUtils.getReturnData(value), UserInfo.class);
                        String uid = user.getUserId();
                        application.setUid(uid);
                        SpUtil.save(context, Finals.SP_UID, uid);
                        saveLoginInfo();
                        RequestRegionData();
                    } else if (DataUtils.isReturnOKWith3(value)) {
                        UserInfo user = JSON.parseObject(DataUtils.getReturnData(value), UserInfo.class);
                        String uid = user.getUserId();
                        application.setUid(uid);
                        SpUtil.save(context, Finals.SP_UID, uid);
                        saveLoginInfo();
                        RequestRegionData();
                        showToast(DataUtils.getReturnMsg(value));
                    } else {
                        showToast(DataUtils.getReturnMsg(value));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(msgFail);
                }
            }
        });
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
     * 跳转至首页 --- 推送监听页
     */
    private void gotoMain() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("fromLogin", true);
        goActivity(PushListenActivity.class, bundle);

//        goActivity(HomeActivity.class, bundle);

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

            clientid = SpUtil.getData(getApplicationContext(), Finals.SP_CLIENTID);

            doLogin();

//            RequestRegionData();
        } else { // 正常登录或者退出登录
            hideProgressDialog();

            tv_username.setText(SpUtil.getData(context.getApplicationContext(), Finals.SP_USERNAME));
            tv_username.setSelection(tv_username.getText().toString().length());
        }

//        if (Finals.IS_TEST) { // TODO 测试数据
//            tv_username.setText(Finals.Default_User);
//            tv_pwd.setText(Finals.Default_Pwd); // 需要加密
//
//            tv_username.setSelection(tv_username.getText().toString().length());
//            tv_pwd.setSelection(tv_pwd.getText().toString().length());
//        }

    }


    /**
     * 登录成功后保存登录信息
     */
    private void saveLoginInfo() {
        SpUtil.save(context.getApplicationContext(), Finals.SP_USERNAME, username);
        SpUtil.save(context.getApplicationContext(), Finals.SP_PWD, password);
        SpUtil.save(context.getApplicationContext(), Finals.SP_AUTOLOGIN, "1");//自动登录

    }


    /**
     * 集成个推
     */
    private void initGetuiPermission() {
        PackageManager pkgManager = getPackageManager();

        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        boolean sdCardWritePermission = pkgManager.checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission = pkgManager.checkPermission(
                Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission) {
            requestPermission();
        } else {
            PushManager.getInstance().initialize(this.getApplicationContext(), pushService);
        }

        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), GetuiIntentService.class);

//        // 应用未启动, 个推 service已经被唤醒,显示该时间段内离线消息
//        if (DemoApplication.payloadData != null) {
//            tLogView.append(DemoApplication.payloadData);
//        }

        // cpu 架构
        Log.d(TAG, "cpu arch = " + (Build.VERSION.SDK_INT < 21 ? Build.CPU_ABI : Build.SUPPORTED_ABIS[0]));

        // 检查 so 是否存在
        File file = new File(this.getApplicationInfo().nativeLibraryDir + File.separator + "libgetuiext2.so");
        Log.e(TAG, "libgetuiext2.so exist = " + file.exists());

    }




    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                PushManager.getInstance().initialize(this.getApplicationContext(), pushService);
            } else {
                Log.e(TAG, "We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
                        + "functions will not work");
                PushManager.getInstance().initialize(this.getApplicationContext(), pushService);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

//    private Handler loginHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case 11:
//                    Message msg = loginHandler.obtainMessage();
//                    loginHandler.sendMessage(msg);
//                    break;
//                case 22:
//
//                    break;
//            }
//        }
//    };


}
