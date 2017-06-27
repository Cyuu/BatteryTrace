package com.thdz.bt.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.thdz.bt.R;
import com.thdz.bt.base.BaseActivity;
import com.thdz.bt.util.VUtils;

import butterknife.BindView;

/**
 * 操作指导 -- 帮助页面
 * -------------
 * 编写方法，用Listview，
 * item的格式：1 图，2 文，3 文。图放在Assets里。
 */
public class HelpActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.layout_shots)
    ScrollView layout_shots;

    @BindView(R.id.layout_icons)
    ScrollView layout_icons;

    @BindView(R.id.layout_guides)
    ScrollView layout_guides;

    @BindView(R.id.tv_shots)
    TextView tv_shots;

    @BindView(R.id.tv_icons)
    TextView tv_icons;

    @BindView(R.id.tv_guides)
    TextView tv_guides;


    @BindView(R.id.iv_home1)
    ImageView iv_home1;

    @BindView(R.id.iv_home2)
    ImageView iv_home2;

    @BindView(R.id.iv_home3)
    ImageView iv_home3;

    @BindView(R.id.iv_set_notify)
    ImageView iv_set_notify;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_help);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        toolbar.setTitle("帮助");
        setToolbarBackEnable(toolbar);
        tv_shots.setOnClickListener(this);
        tv_icons.setOnClickListener(this);
        tv_guides.setOnClickListener(this);

        initImageView();

        resetTvState();
        tv_shots.setBackgroundResource(R.color.colorPrimary);
        tv_shots.setTextColor(getResources().getColor(R.color.white));
        layout_shots.setVisibility(View.VISIBLE);

    }


    /**
     * 设置图片显示比例
     * ---------------
     * 960 * 1642 约等于： 3 * 5
     * 720 * 640  约等于： 9 * 8
     */
    private void initImageView() {

        // VUtils.dp2px(getResources().getDimension(R.dimen.margin_30))
        int padding = VUtils.dp2px(30 * 2);// 直接用dimen里的数值即可
        int www = application.screenWidth - padding;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(www, www * 5 / 3);
        iv_home1.setLayoutParams(lp);
        iv_home2.setLayoutParams(lp);
        iv_home3.setLayoutParams(lp);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(www, www * 8 / 9);
        iv_set_notify.setLayoutParams(lp2);

    }

    private void resetTvState() {
        tv_shots.setBackgroundResource(R.color.gray_bg);
        tv_icons.setBackgroundResource(R.color.gray_bg);
        tv_guides.setBackgroundResource(R.color.gray_bg);

        tv_shots.setTextColor(getResources().getColor(R.color.black_light));
        tv_icons.setTextColor(getResources().getColor(R.color.black_light));
        tv_guides.setTextColor(getResources().getColor(R.color.black_light));

        layout_shots.setVisibility(View.GONE);
        layout_icons.setVisibility(View.GONE);
        layout_guides.setVisibility(View.GONE);

    }

    @Override
    public void onClick(int resId) {
        switch (resId) {
            case R.id.tv_shots:
                resetTvState();
                tv_shots.setBackgroundResource(R.color.colorPrimary);
                tv_shots.setTextColor(getResources().getColor(R.color.white));
                layout_shots.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_icons:
                resetTvState();
                tv_icons.setBackgroundResource(R.color.colorPrimary);
                layout_icons.setVisibility(View.VISIBLE);
                tv_icons.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_guides:
                resetTvState();
                tv_guides.setBackgroundResource(R.color.colorPrimary);
                layout_guides.setVisibility(View.VISIBLE);
                tv_guides.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                break;
        }
    }

}
