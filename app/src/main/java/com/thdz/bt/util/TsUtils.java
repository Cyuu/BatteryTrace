package com.thdz.bt.util;

import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.thdz.bt.R;
import com.thdz.bt.app.MyApplication;


/**
 * Toast工具类
 */

public class TsUtils {

    private static Toast toast = null;
    private static boolean hasInit = false;

    private TsUtils() {
            /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    /**
     * 弹出各种Toast
     */
    public static void showToast(String tip) {
//        Toast.makeText(MyApplication.getInstance().getApplicationContext(), tip, Toast.LENGTH_SHORT).show();

        if (toast == null || !hasInit) {
            toast = Toast.makeText(MyApplication.getInstance().getApplicationContext(), "", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 80);
            View view = toast.getView();
            int outerH = 10;
            int innerH = 5;
            view.setPadding(20, 8, 20, 8);
            int colorId = MyApplication.getInstance().getResources().getColor(R.color.colorPrimary);
            float[] outerRadii = {outerH, outerH, outerH, outerH, outerH, outerH, outerH, outerH};//外矩形 左上、右上、右下、左下 圆角半径
//            RectF inset = new RectF(70, 70, 20, 20);//内矩形距外矩形，左上角x,y距离， 右下角x,y距离
            float[] innerRadii = {innerH, innerH, innerH, innerH, innerH, innerH, innerH, innerH};//内矩形 圆角半径

            RoundRectShape rectShape = new RoundRectShape(outerRadii, null, innerRadii);
            ShapeDrawable drawable = new ShapeDrawable(rectShape);
            drawable.getPaint().setColor(colorId);
            drawable.getPaint().setStyle(Paint.Style.FILL); //填充
            view.setBackgroundDrawable(drawable);
            hasInit = true;
        }

        toast.setText(tip);
        toast.show();

    }
}
