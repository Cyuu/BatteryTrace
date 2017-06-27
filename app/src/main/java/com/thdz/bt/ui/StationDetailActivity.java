package com.thdz.bt.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.thdz.bt.R;
import com.thdz.bt.adapter.ContactListAdapter;
import com.thdz.bt.base.BaseActivity;
import com.thdz.bt.bean.ContactManBean;
import com.thdz.bt.bean.StationBean;
import com.thdz.bt.bean.StationDetailBean;
import com.thdz.bt.event.StationAlarmEvent;
import com.thdz.bt.util.DataUtils;
import com.thdz.bt.util.Finals;
import com.thdz.bt.view.NoScrollListView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 站点详情页面
 */
public class StationDetailActivity extends BaseActivity {

    @BindView(R.id.scrollview)
    ScrollView scrollview;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tv_name)
    TextView tv_name; //
    @BindView(R.id.tv_addr)
    TextView tv_addr; //
    @BindView(R.id.tv_region)
    TextView tv_region; //
    @BindView(R.id.tv_point_lon)
    TextView tv_point_lon; //
    @BindView(R.id.tv_point_lat)
    TextView tv_point_lat; //
    @BindView(R.id.tv_simcard)
    TextView tv_simcard; //
    @BindView(R.id.tv_tick_time)
    TextView tv_tick_time; //
    @BindView(R.id.tv_upload_time)
    TextView tv_upload_time; //
    @BindView(R.id.tv_lowpower_time)
    TextView tv_lowpower_time; //
    @BindView(R.id.tv_move_time)
    TextView tv_move_time; //
    @BindView(R.id.tv_lowpower_value)
    TextView tv_lowpower_value; //
    @BindView(R.id.tv_move_alarm_value)
    TextView tv_move_alarm_value; //
    @BindView(R.id.tv_conn_ip)
    TextView tv_conn_ip; //
    @BindView(R.id.tv_conn_port)
    TextView tv_conn_port; //
    @BindView(R.id.tv_term_name)
    TextView tv_term_name; //
    @BindView(R.id.tv_contact_title)
    TextView tv_contact_title; //

    @BindView(R.id.lv_contact)
    NoScrollListView lv_contact; //

    @BindView(R.id.tv_commtype)
    TextView tv_commtype; // 通讯方式

    @BindView(R.id.tv_commtime)
    TextView tv_commtime; // 最后通讯时间

    @BindView(R.id.tv_power)
    TextView tv_power; // 电压

    private StationBean orgBean; // 从上一页传递来的bean

    private StationDetailBean detailBean; // 站点详情

    private String stationid;

    private ContactListAdapter contactAdapter;

    private final String errorTip = "未查询到站点详情数据";


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_station_detail);
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        toolbar.setTitle("站点详情");
        setToolbarBackEnable(toolbar);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            orgBean = (StationBean) bundle.getSerializable("bean");
            stationid = bundle.getString("stationid");
            if (TextUtils.isEmpty(stationid)) {
                if (Finals.IS_TEST) {
                    stationid = "1";
                } else {
                    showToast("站点id为空");
                    finish();
                }
            }
        } else {
            if (Finals.IS_TEST) {
                stationid = "1";
            } else {
                showToast("站点id为空");
                finish();
            }
        }

        // 从列表页 需要展示的数据
        if (orgBean != null) {
            setTextView(tv_commtype, orgBean.getCommType());
            setTextView(tv_commtime, orgBean.getCurrentTime());
            setTextView(tv_power, orgBean.getModuleU());
        }

        contactAdapter = new ContactListAdapter(context);
        lv_contact.setAdapter(contactAdapter);

        requestDetail();

    }

    private void delTelno(String telno) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telno));
        context.startActivity(intent);
    }


    /**
     * 获取详情
     */
    private void requestDetail() {
        showProgressDialog();
        String url = DataUtils.do4StationDetail(stationid);
        doRequestGet(url, new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                hideProgressDialog();
                showToast(failTip + ", 查询站点详情数据失败");
                e.printStackTrace();
            }

            @Override
            public void onResponse(String value, int id) {
                hideProgressDialog();
                Log.i(TAG, "获取详情 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {
                        String data = DataUtils.getReturnData(value);
                        detailBean = JSON.parseObject(data, StationDetailBean.class);
                        showView();
                    } else {
                        showToast(DataUtils.getReturnMsg(value));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(errorTip);
                }
            }
        });


    }

    /**
     * todo 展示详情数据 -- detailBean
     */
    private void showView() {
        if (detailBean == null) {
            return;
        }

        setTextView(tv_name, detailBean.getStationName()); //
        setTextView(tv_addr, detailBean.getModuleAddr()); //
        setTextView(tv_region, detailBean.getRegionName()); //

        // 格式化
        String lonStr = DataUtils.getFormat6(Double.parseDouble(detailBean.getLongitude()));
        setTextView(tv_point_lon, lonStr); //

        // 格式化
        String latStr = DataUtils.getFormat6(Double.parseDouble(detailBean.getLatitude()));
        setTextView(tv_point_lat, latStr); //

        setTextView(tv_simcard, detailBean.getSimCard()); //
        setTextView(tv_tick_time, detailBean.getHeartBreakTime()); //
        setTextView(tv_upload_time, detailBean.getDataUploadTime()); //
        setTextView(tv_lowpower_time, detailBean.getLowVoltageAlarmTime()); //
        setTextView(tv_move_time, detailBean.getPosChangeAlarmTime()); //

        setTextView(tv_lowpower_value, detailBean.getLowValue()); //
        setTextView(tv_move_alarm_value, detailBean.getAlarmDistance()); //
        setTextView(tv_conn_ip, detailBean.getServerIP()); //
        setTextView(tv_conn_port, detailBean.getServerPort()); //
        setTextView(tv_term_name, detailBean.getTermName()); //

        tv_simcard.setOnClickListener(this);

        List<ContactManBean> contactList = detailBean.getContactManList();
        if (contactList == null || contactList.isEmpty()) {
            tv_contact_title.setVisibility(View.VISIBLE);
        } else {
            tv_contact_title.setVisibility(View.GONE);
            contactAdapter.setDataList(contactList);
            contactAdapter.notifyDataSetChanged();
        }

        scrollview.smoothScrollTo(0, 0);
    }


    private void setTextView(TextView tv, String value) {
        if (TextUtils.isEmpty(value)) {
            value = "";
        }
        tv.setText(value);

    }


    /**
     * 自定义 右上角的菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;

    }


    /**
     * 已通过onCreateOptionsMenu()自定义了含icon的Menu，这个方法将不再生效
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_power) {
            Bundle bundle = new Bundle();
            bundle.putString("stationid", stationid);
            bundle.putString("name", detailBean.getStationName());
            goActivity(ChartPowerActivity.class, bundle);
        } else if (item.getItemId() == R.id.action_refresh) {
            requestDetail();
        }
        return super.onOptionsItemSelected(item);
    }


//    private void tel(String telno) {
//        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telno));
//        context.startActivity(intent);
//    }


    @Override
    public void onClick(int resId) {
        switch (resId) {
            case R.id.tv_simcard:
                String no = tv_simcard.getText().toString().trim();
                if (!TextUtils.isEmpty(no)) {
                    delTelno(no);
                }
                break;
            default:
                break;

        }
    }

    /**
     * 服务端推送新告警, 如果是当前站点，刷新站点详情
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealStationAlarmEvent(StationAlarmEvent event) {
        Log.i(TAG, "有新告警产生！");
        if (event != null &&
                event.getPushBean() != null &&
                !TextUtils.isEmpty(event.getPushBean().getStationid()) &&
                event.getPushBean().getStationid().equals(stationid)) {

            requestDetail();
        }
    }


}
