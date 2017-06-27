package com.thdz.bt.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thdz.bt.R;
import com.thdz.bt.bean.ContactManBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 联系人列表适配器 ，用于站点详情页里
 */
public class ContactListAdapter extends BaseAdapter {

    private List<ContactManBean> dataList = null;
    private Context context;

    public ContactListAdapter(Context context) {
        this.context = context;
    }

    public ContactListAdapter(Context context, List<ContactManBean> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    public List<ContactManBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<ContactManBean> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_contact, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ContactManBean bean = dataList.get(position);

        holder.tv_manager.setText(bean.getManName());
        holder.tv_telno.setText(bean.getManTel());

        // CardView的点击事件
        holder.tv_telno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + bean.getManTel()));
                context.startActivity(intent);
            }
        });

        return convertView;
    }


    static class ViewHolder {

        @BindView(R.id.tv_manager)
        TextView tv_manager;      // 名称

        @BindView(R.id.tv_telno)
        TextView tv_telno;        // 电话

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
