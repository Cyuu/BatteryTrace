package com.thdz.bt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thdz.bt.R;
import com.thdz.bt.bean.StationBean;
import com.thdz.bt.util.DataUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 站点列表适配器
 * ---------------
 * 图片重用问题
 */
public class StationListAdapter extends BaseAdapter {

    private List<StationBean> dataList = null;
    private Context context;

    public StationListAdapter(Context context) {
        this.context = context;
    }

    public StationListAdapter(Context context, List<StationBean> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    public List<StationBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<StationBean> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_station, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final StationBean bean = dataList.get(position);

        holder.item_name.setText(bean.getStationName());
        holder.item_addr.setText(bean.getModuleAddr());
        holder.item_region.setText(bean.getRegionName());
        holder.item_simcard.setText(bean.getSimCard());

        holder.item_battery_type.setText(bean.getStationType());


        // 经纬度坐标
        double lon = Double.parseDouble(bean.getNLongitude()) == 0 ?
                Double.parseDouble(bean.getOLongitude()) :
                Double.parseDouble(bean.getNLongitude());
        double lat = Double.parseDouble(bean.getNLatitude()) == 0 ?
                Double.parseDouble(bean.getOLatitude()) :
                Double.parseDouble(bean.getNLatitude());

        // 格式化
        String lonStr = DataUtils.getFormat6(lon);
        holder.item_lon.setText(lonStr);
        // 格式化
        String latStr = DataUtils.getFormat6(lat);
        holder.item_lat.setText(latStr);

        holder.item_power.setText(bean.getModuleU() + "V");


        if (bean.isAlarm()) {
            holder.item_state.setBackgroundColor(context.getResources().getColor(R.color.red));
        } else {
            holder.item_state.setBackgroundColor(context.getResources().getColor(R.color.green_color));
        }

        // todo 是否有告警，是何种？ - 目前，这里不展示
        holder.tv_alarm_type.setText("");


        return convertView;
    }


    static class ViewHolder {

        @BindView(R.id.item_state)
        View item_state;     // 通信状态

        @BindView(R.id.item_name)
        TextView item_name;        // 站点名称

        @BindView(R.id.tv_alarm_type)
        TextView tv_alarm_type;    // 告警类型

        @BindView(R.id.item_addr)
        TextView item_addr;        // 地址号

        @BindView(R.id.item_region)
        TextView item_region;      // 所属区域

        @BindView(R.id.item_simcard)
        TextView item_simcard;     // simcard

        @BindView(R.id.item_battery_type)
        TextView item_battery_type; // 电池类型

        @BindView(R.id.item_power)
        TextView item_power;       // 电压

        @BindView(R.id.item_lon)
        TextView item_lon;         // 经度

        @BindView(R.id.item_lat)
        TextView item_lat;         // 纬度


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
