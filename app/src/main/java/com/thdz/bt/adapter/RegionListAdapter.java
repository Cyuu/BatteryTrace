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
 * 区域列表适配器
 */
public class RegionListAdapter extends BaseAdapter {

    private List<RegionBean> dataList = null;
    private Context context;

    public RegionListAdapter(Context context) {
        this.context = context;
    }

    public RegionListAdapter(Context context, List<RegionBean> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_region, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final RegionBean bean = dataList.get(position);

        // UI，不同level区域，使用不同颜色
//        if (bean.getParentId().equals("0")) {
//            holder.item_region_level.setText("省");
//        }

        holder.item_region_id.setText(bean.getRegionNo());
        holder.item_region_name.setText(bean.getRegionName());


        return convertView;
    }


    static class ViewHolder {

        @BindView(R.id.item_region_level)
        TextView item_region_level;

        @BindView(R.id.item_region_id)
        TextView item_region_id; // 编号

        @BindView(R.id.item_region_name)
        TextView item_region_name;  // 名称

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
