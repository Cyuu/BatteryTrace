package com.thdz.bt.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.thdz.bt.R;
import com.thdz.bt.util.AnimUtils;

/**
 * 自定义loadingview
 */
public class BTProgressDialog extends ProgressDialog {

    ImageView iv_loading_tip;

    RotateAnimation rotateAnimation;


    public BTProgressDialog(Context context) {
        super(context);
    }

    public BTProgressDialog(Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        View view = LayoutInflater.from(getContext()).inflate(R.layout.sample_btprogress_dialog, null);

        iv_loading_tip = (ImageView) view.findViewById(R.id.iv_loading_tip);

        rotateAnimation = AnimUtils.initRotateAnimation(
                false,
                1200,
                true,
                Animation.INFINITE);

        setContentView(view);
    }

    @Override
    public void show(){
        super.show();
        iv_loading_tip.startAnimation(rotateAnimation);
    }


    @Override
    public void dismiss() {
        super.dismiss();
        iv_loading_tip.clearAnimation();
    }
}
