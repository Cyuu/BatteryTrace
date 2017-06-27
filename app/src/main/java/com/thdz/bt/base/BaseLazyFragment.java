package com.thdz.bt.base;

import android.view.View.OnClickListener;

/**
 * 懒加载Fragment框架<br/>
 */
public abstract class BaseLazyFragment extends BaseFragment implements OnClickListener {

    /**
     * 是否可见<br/>
     * Fragment多适用于Viewpager嵌套，而对于Viewpager的第一个子Fragment，<br/>
     * <b><font color="red">需要在onCreate()里添加：isVisible = true;</font></b><br/>
     */
    public boolean isVisible;
    /**
     * 有两个用途：<br/>
     * 1 标志位，标志已经初始化完成<br/>
     * ~~~ 放在onCreate()中，<br/>
     * ----------------------<br/>
     * 2 是否已加载过 -- 如果已经加载过，则不再重复加载<br/>
     * ~~~ 放在RequestData()，loadOK后，表示已加载过数据了<br/>
     */
    public boolean isPrepared;

    /**
     * 在这里实现Fragment数据的缓加载.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getUserVisibleHint()) {
                isVisible = true;
                onVisible();
            } else {
                isVisible = false;
            }
        } else {

        }
    }

    public void onVisible() {
        lazyLoad();
    }

    public abstract void lazyLoad();

}
