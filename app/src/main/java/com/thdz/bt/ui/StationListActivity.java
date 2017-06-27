package com.thdz.bt.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.thdz.bt.R;
import com.thdz.bt.adapter.StationListAdapter;
import com.thdz.bt.base.BaseActivity;
import com.thdz.bt.bean.StationBean;
import com.thdz.bt.util.Finals;
import com.thdz.bt.util.SpUtil;
import com.thdz.bt.util.VUtils;
import com.thdz.bt.view.NoScrollListView;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;

/**
 * 站点列表页面  todo 没在使用
 */
public class StationListActivity extends BaseActivity {

    @BindView(R.id.scrollview)
    ScrollView scrollview;

    @BindView(R.id.swipy_layout)
    SwipyRefreshLayout swipy_layout; // 刷新

    @BindView(R.id.lv_stations)
    NoScrollListView lv_stations; // 列表

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.btn_search)
    TextView btn_search;

    private StationListAdapter stationAdapter;
    private List<StationBean> stationList;

    private int curPage = 1;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_stations);
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        toolbar.setTitle("站点列表");

        setToolbarBackEnable(toolbar);

        btn_search.setOnClickListener(this);

        stationAdapter = new StationListAdapter(context);
        lv_stations.setAdapter(stationAdapter);
        lv_stations.setDividerHeight(0);

        VUtils.setSwipyColor(swipy_layout);
        swipy_layout.setDirection(SwipyRefreshLayoutDirection.BOTH); // 只能从上面刷新
        swipy_layout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                switch (direction) {
                    case TOP:
                        curPage = 1;
                        break;
                    case BOTTOM:
                        curPage += 1;
                        break;
                }
                requestStationList(curPage);

            }
        });

        swipy_layout.setRefreshing(false);

        requestStationList(0);

    }


    private void requestStationList(int page) {
        stationAdapter.setDataList(stationList);
        stationAdapter.notifyDataSetChanged();
        if (curPage == 0) {
            scrollview.smoothScrollTo(0, 0);
        }
    }


    private String createParams() {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject();
            jsonObj.put("UserName", "0");
            jsonObj.put("UserPwd", "0");
            jsonObj.put("ClientId", SpUtil.getData(context.getApplicationContext(), Finals.SP_CLIENTID));
            return jsonObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public void onClick(int resId) {
        switch (resId) {
            case R.id.btn_search:
                showToast("根据条件查询站点");
                break;
            default:
                break;

        }
    }

}
