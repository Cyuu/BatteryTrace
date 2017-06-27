package com.thdz.bt.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.thdz.bt.R;
import com.thdz.bt.app.MyApplication;
import com.thdz.bt.util.NotifyUtil;
import com.thdz.bt.util.TsUtils;
import com.thdz.bt.util.VUtils;
import com.thdz.bt.view.BTProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * Activity框架<br/>
 * (请求接口步骤)<br/>
 * 1 initQueue() <br/>
 * 2 拼接参数 先调用buildCommonHttpParams()，然后构建自己的私有参数buildParams4XX()，返回map <br/>
 * 3 发送请求 volleyPostRequest() <br/>
 * ------------------------- <br/>
 * 公共的loading 需要在每个页面的initView方法里，加上common_null_layout.setOnClickListener(this);
 * 然后再pressEvent()里，添加该控件的点击事件
 * ---------------
 * 各个页面发送参数的方法：
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    public final String failTip = "通信异常";

    public MyApplication application;

    public Context context;
    public ProgressDialog progressDialog = null;

    public String TAG = this.getClass().getSimpleName();

    public InputMethodManager imm = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        application = MyApplication.getInstance();// 获取应用程序全局的实例引用
//        MyApplication application = (MyApplication) MyApplication.getInstance();
//        application.activityList.add(this); // 把当前Activity放入集合中
        application.addActivity(this);

        if (application.screenWidth == 0) {
            application.screenWidth = getScreenWidth();
        }

        if (application.screenWidth == 0) {
            application.screenheigth = getScreenHeight();
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView();
        ButterKnife.bind(this);
//        initLoadingView();

        initView(savedInstanceState);

    }


    public void setToolbarBackEnable(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 显示左上角返回按钮
            // 左上角返回键
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    /**
     * 获得屏幕宽度
     */
    public int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获得屏幕高度
     */
    public int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }


    /**
     * get方式
     */
    public void doRequestGet(String url, StringCallback callback) {
        Log.i(TAG, "请求地址：" + url);
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(callback);
    }

    /**
     * post方式
     *
     * @param params post参数
     */
    public void doRequestPost(String url, String params, StringCallback callback) {
        Log.i(TAG, "请求地址：" + url);
        OkHttpUtils
                .get()
                .url(url)
                .addParams("sCmd", params)
                .build()
                .execute(callback);
    }


    /**
     * 请求数据接口
     */
    public void requestData(String title, String params) { // String msg
        Log.i(TAG, title + "请求参数：" + new String(params));
//        application.logout(); // boolean res =
//        if (res) {
//            showToast("手动发送成功");
//        } else {
//            showToast("手动发送失败");
//        }
    }


    /**
     * setContentView
     */
    public abstract void setContentView();

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new BTProgressDialog(context);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("");
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    /**
     * init view after setcontentView()
     */
    public abstract void initView(Bundle savedInstanceState);

    // @Subscribe
    @Override
    public void onClick(View v) {
        if (VUtils.isFastDoubleClick()) {
            showToast("点击太过频繁~");
            return;
        } else {
            onClick(v.getId());
        }
    }

    public abstract void onClick(int resId);


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void finish() {
        // 之前是在这里做的清理Activity的操作，现转到ondestroy里
        super.finish();
        overridePendingTransition(0, R.anim.push_right_out);
    }


    @Override
    protected void onPause() {
        super.onPause();
        hideInputMethod();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        application.removeActivity(this); // 把当前Activity从集合中移除

    }


    /**
     * 设置title for Activity
     */
    public void setTitle(String title) {
        try {
            TextView titletv = (TextView) findViewById(R.id.tv_title);
            titletv.setText(title);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    /**
     * 隐藏返回箭头 for Activity
     */
    public void setBackGone() {
        try {
            ImageView back = (ImageView) findViewById(R.id.iv_left);
            back.setVisibility(View.GONE);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }


    /**
     * 设置返回箭头动作
     */
    public void setBackActive() {
        try {
            ImageView back = (ImageView) findViewById(R.id.iv_left);
            back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    /**
     * 隐藏title栏右侧按钮 for Activity
     */
    public void setRightTopGone() {
        try {
            ImageView back = (ImageView) findViewById(R.id.iv_right);
            back.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    /**
     * 隐藏title栏右侧按钮 for Activity
     */
    public void setRightActive(View.OnClickListener clickListener) {
        try {
            ImageView back = (ImageView) findViewById(R.id.iv_right);
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(clickListener);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }


    /**
     * 打开确认对话框
     */
    public void showSureDialog(String tip, DialogInterface.OnClickListener sureListener) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View mView = layoutInflater.inflate(R.layout.dialog_sure, null);
        TextView dialog_sure_tv = (TextView) mView.findViewById(R.id.dialog_sure_tv);
        dialog_sure_tv.setText(tip);
        mBuilder.setView(mView);
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("确认", sureListener);

        mBuilder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog sureDialog = mBuilder.create();
        sureDialog.show();
    }

    /**
     * 隐藏输入框
     */
    public void hideInputMethod() {
        if (imm != null) {
            View view = ((Activity) context).getCurrentFocus();
            if (view != null) {
                IBinder mBinder = view.getWindowToken();
                if (mBinder != null) {
                    imm.hideSoftInputFromWindow(mBinder, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (progressDialog != null && progressDialog.isShowing()) {
            hideProgressDialog();
            return;
        }

        super.onBackPressed();
    }


    /**
     * 清楚app内缓存的告警状态, 并清除消息栏告警
     */
    public void clearAlarmState(){
        MyApplication.getInstance().hasNewPush = false;
        MyApplication.getInstance().pushBean = null;
        NotifyUtil.clearNotification();
    }


    public void goActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void goActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }


    public void showToast(String info) {
        TsUtils.showToast(info);
    }


    private void showToastOnUIThread(final String msg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                showToast(msg);
            }
        });
    }

}
