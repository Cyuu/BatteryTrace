package com.thdz.bt.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thdz.bt.R;
import com.thdz.bt.bean.AlarmBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 告警历史列表适配器
 * -------
 * 1 当前告警没有 AlarmId <br/>
 * 2 历史告警没有 ModuleType，RegionName <br/>
 */
public class AlarmListAdapter extends BaseAdapter {

    private List<AlarmBean> dataList = null;
    private Context context;

    public AlarmListAdapter(Context context) {
        this.context = context;
    }

    public AlarmListAdapter(Context context, List<AlarmBean> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    public List<AlarmBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<AlarmBean> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_alarm, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final AlarmBean bean = dataList.get(position);

        holder.item_station_name.setText(bean.getStationName());
        holder.item_time.setText(bean.getAlarmTime());

        String region = bean.getRegionName();
        if (TextUtils.isEmpty(region)) {
            holder.item_layout_region.setVisibility(View.GONE);
        } else {
            holder.item_layout_region.setVisibility(View.VISIBLE);
            holder.item_region.setText(region);
        }

        holder.item_type.setText(bean.getAlarmType());

        return convertView;
    }


    static class ViewHolder {

        @BindView(R.id.item_layout_region)
        LinearLayout item_layout_region;

        @BindView(R.id.item_station_name)
        TextView item_station_name;     // 站点名称

        @BindView(R.id.item_region)
        TextView item_region;        // 区域

        @BindView(R.id.item_time)
        TextView item_time;         // 告警时间

        @BindView(R.id.item_type)
        TextView item_type;        // 告警类型


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
