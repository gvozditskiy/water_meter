package com.gvozditskiy.watermeter.IndAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gvozditskiy.watermeter.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Alexey on 04.01.2017.
 */

public class HotCurrentAdapter extends RecyclerView.Adapter<HotCurrentAdapter.HotCurrentVH> {
    Context mContext;
    /**
     * Map<String, String> - map, в которой содержатся name счетчика и показание value, delta для дельты
     */
    List<Map<String,String>> meters;

    public HotCurrentAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setDataSet(List<Map<String, String>> list) {
        meters=list;
    }

    @Override
    public HotCurrentVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.ind_cur_hot_layout, parent, false);
        return new HotCurrentVH(v);
    }

    @Override
    public void onBindViewHolder(HotCurrentVH holder, int position) {
        holder.tv.setText(meters.get(position).get("name"));
        holder.et.setText(meters.get(position).get("value"));
        holder.delta.setText(meters.get(position).get("delta"));
    }

    @Override
    public int getItemCount() {
        return meters.size();
    }

    public static class HotCurrentVH extends RecyclerView.ViewHolder {
        TextView tv;
        EditText et;
        TextView delta;

        public HotCurrentVH(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.ind_layout_tv);
            et = (EditText) itemView.findViewById(R.id.ind_layout_et);
            delta = (TextView) itemView.findViewById(R.id.ind_layout_delta);

        }
    }
}
