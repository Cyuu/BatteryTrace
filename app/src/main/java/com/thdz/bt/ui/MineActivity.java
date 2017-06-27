package com.thdz.bt.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thdz.bt.R;
import com.thdz.bt.base.BaseActivity;
import com.thdz.bt.util.DataUtils;
import com.thdz.bt.util.Finals;
import com.thdz.bt.util.SpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 我的 -- 关于， 升级, 清理缓存，注销
 */
public class MineActivity extends BaseActivity {

    @BindView(R.id.clear_cache_layout)
    RelativeLayout clear_cache_layout; // 清除缓存

    @BindView(R.id.check_update_layout)
    RelativeLayout check_update_layout; // 检查更新

    @BindView(R.id.version_tv)
    TextView version_tv; // 版本号

    @BindView(R.id.tv_username)
    TextView tv_username; // 用户名

    @BindView(R.id.layout_help)
    RelativeLayout layout_help; // 帮助

    @BindView(R.id.logout_btn)
    Button logout_btn;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    Intent intent;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_mine);
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        toolbar.setTitle("个人中心");
        setToolbarBackEnable(toolbar);

        intent = getIntent();

        clear_cache_layout.setOnClickListener(this);
        logout_btn.setOnClickListener(this);
        layout_help.setOnClickListener(this);

        String logoStr = "v" + DataUtils.getVerName(context);
        version_tv.setText(logoStr);
        String user = SpUtil.getData(context.getApplicationContext(), Finals.SP_USERNAME);
        tv_username.setText(TextUtils.isEmpty(user) ? "测试" : user);

    }


    /**
     * 清除缓存
     */
    private void clearAppCache() {
        showProgressDialog();
        // Glide框架清除内存缓存
        // Glide.get(context).clearMemory();
        showToast("缓存清除完毕"); // 如果需要异步清理，需要注释本行
        hideProgressDialog();

    }

//    /**
//     * 判断缓存目录下，缓存是否已清空
//     */
//    private void showTextFromCacheState() {
//        try {
//            File file = new File(Finals.imageCachePath);
//            if (!file.exists()) {//已清空
//                // clear_cache_count.setText("0 M");
//            } else {
//                // if (file.isDirectory()) {
//                // String count = CacheUtils.getTotalCacheSize(context);
//                // Log.i(TAG, "缓存目录大小：" + count);
//                // clear_cache_count.setText(count);
//                // } else {
//                // file.length();
//                // }
//
//                String count = CacheUtils.getTotalCacheSize(context, file);
//                Log.i(TAG, "缓存目录大小：" + count);
//                // clear_cache_count.setText(count);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            // clear_cache_count.setText("0 M");
//        }
//    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
     */
    private void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }


    /**
     * 清除缓存
     */
    private void doClearCache() {
        showSureDialog("确定要清除缓存吗?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearAppCache();
            }
        });
    }


    // 请求 检查新版本接口
    private void Request4CheckUpdate() {
//        hideProgressDialog();
//        showSureDialog("发现新版本，要下载更新吗?", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // TODO 执行下载更新安装
//            }
//        });

    }


    /**
     * 退出登录
     */
    private void doLogout() {
        showSureDialog("确定要退出当前帐号吗?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request4Logout();
            }
        });
    }


    /**
     * 请求 退出接口，关闭推送
     */
    private void Request4Logout() {
        showProgressDialog();
        String url = DataUtils.do4logout(application.getUid(), SpUtil.getData(getApplicationContext(), Finals.SP_CLIENTID));
        doRequestGet(url, new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                hideProgressDialog();
                showToast(failTip +"， 退出登录失败");
                e.printStackTrace();

            }

            @Override
            public void onResponse(String value, int id) {
                hideProgressDialog();
                Log.i(TAG, "退出登录 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {
                        SpUtil.save(getApplicationContext(), Finals.SP_AUTOLOGIN, "0");
                        SpUtil.save(getApplicationContext(), Finals.SP_PWD, "");
                        SpUtil.save(getApplicationContext(), Finals.SP_UID, "");
                        SpUtil.save(getApplicationContext(), Finals.SP_CLIENTID, "");

                        application.setUid("");

                        if (application.regionList != null && !application.regionList.isEmpty()) {
                            application.regionList.clear();
                        }

//                        Bundle bundle = new Bundle();
//                        bundle.putBoolean("hasExit", true);
//                        intent.putExtras(bundle);
//                        setResult(Activity.RESULT_OK, intent);
//                        finish();

                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        clearAlarmState();

                    } else {
                        showToast(DataUtils.getReturnMsg(value));
                    }
                } catch (Exception e) {
                    showToast("退出登录 失败");
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onClick(int resId) {
        switch (resId) {
            case R.id.clear_cache_layout:
                doClearCache();
                break;
            case R.id.logout_btn:
                doLogout();
                break;
            case R.id.layout_help:
                goActivity(HelpActivity.class, null);
                break;
            default:
                break;

        }
    }

}
