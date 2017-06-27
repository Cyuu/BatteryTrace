package com.thdz.bt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thdz.bt.R;
import com.thdz.bt.bean.RegionBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Spinner三级联动选择区域的适配器
 */
public class SpinnerRegionListAdapter extends BaseAdapter {

    private List<RegionBean> dataList = null;
    private Context context;

    public SpinnerRegionListAdapter(Context context) {
        this.context = context;
    }

    public SpinnerRegionListAdapter(Context context, List<RegionBean> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    public List<RegionBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<RegionBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return null == dataList ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_region_spinner, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RegionBean item = dataList.get(position);
        holder.item_tv_region.setText(item.getRegionName());

        return convertView;
    }


    static class ViewHolder {

        @BindView(R.id.item_tv_region)
        TextView item_tv_region;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
