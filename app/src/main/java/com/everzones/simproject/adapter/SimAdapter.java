package com.everzones.simproject.adapter;

import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.everzones.simproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunny on 2017/8/17.
 * anthor:sunny
 * date: 2017/8/17
 * function:
 */

public class SimAdapter extends BaseAdapter {
    private Context context;
    ViewHolder holder;
    private List<SubscriptionInfo> list = new ArrayList<>();
    private LayoutInflater inflater;

    public SimAdapter(Context context,List<SubscriptionInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = inflater.inflate(R.layout.item,null,false);
            holder = new ViewHolder();
            holder.tv_simIcon = (TextView) view.findViewById(R.id.tv_simIcon);
            holder.tv_simName = (TextView) view.findViewById(R.id.tv_simName);
            holder.tv_simInfo = (TextView) view.findViewById(R.id.tv_carriedName);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tv_simIcon.setText("卡"+(i+1));
        holder.tv_simName.setText("SIM卡"+(i+1));
        String num = list.get(i).getNumber();
        holder.tv_simInfo.setText(list.get(i).getCarrierName());
        return view;
    }

    public static class ViewHolder{
        TextView tv_simIcon;
        TextView tv_simName;
        TextView tv_simInfo;
    }
}
