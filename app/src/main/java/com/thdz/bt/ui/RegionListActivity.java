package com.thdz.bt.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thdz.bt.R;
import com.thdz.bt.adapter.RegionListAdapter;
import com.thdz.bt.base.BaseActivity;
import com.thdz.bt.bean.RegionBean;
import com.thdz.bt.util.DataUtils;

import java.util.List;

import butterknife.BindView;

/**
 * 区域列表页面，选择站点 - todo 没在使用
 * ----------------
 * 1 选择省，市，县
 * 2 使用3个Spinner
 */
public class RegionListActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.lv_region)
    ListView lv_region;

    private RegionListAdapter mAdapter;
    private List<RegionBean> regionList;

    /**
     * 最新告警 --- 需要扩展，有省，市，县
     */
    private String regionId; // 区域id

    private String selectedId; // 选择的区域id

    private Intent intent;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_region_select);
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        toolbar.setTitle("选择区域");
        setToolbarBackEnable(toolbar);

        intent = getIntent();

        Bundle bundle = intent.getExtras();
        regionId = bundle.getString("regionId");
        if (TextUtils.isEmpty(regionId)) {
            regionId = "0";
        }

        mAdapter = new RegionListAdapter(context);
        lv_region.setAdapter(mAdapter);

        lv_region.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedId = regionList.get(position).getRegionId();
                finishChoose();
            }
        });

        requestRegionList();

    }


    /**
     * 根据regionId，查询所有子Region
     */
    private void requestRegionList() {
        if (TextUtils.isEmpty(regionId)) {
            regionList = application.regionList;
        } else {
            regionList = DataUtils.getRegionChildrenById(regionId);
        }

        mAdapter.setDataList(regionList);
        mAdapter.notifyDataSetChanged();

        // 网路请求
//        String url = "";
//        doRequestGet(url, new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                hideProgressDialog();
//                showToast(failTip);
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(String value, int id) {
//                hideProgressDialog();
//                Log.i(TAG, "返回参数是：" + value);
//                try {
//                    if (DataUtils.isReturnOK(value)) {
//                        hideProgressDialog();
//
//
//                    } else {
//                        showToast(DataUtils.getReturnMsg(value));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    showToast(errorTip);
//                }
//            }
//        });

    }

    /**
     * 自定义 右上角的菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commit, menu);
        return true;
    }


    /**
     * 已通过onCreateOptionsMenu()自定义了含icon的Menu，这个方法将不再生效
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_finish) {
            finishChoose();
        }
        return super.onOptionsItemSelected(item);
    }


    private void finishChoose() {
        Bundle bundle = new Bundle();
        bundle.putString("selectedId", selectedId);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    @Override
    public void onClick(int resId) {
        switch (resId) {

            default:
                break;

        }
    }

}
