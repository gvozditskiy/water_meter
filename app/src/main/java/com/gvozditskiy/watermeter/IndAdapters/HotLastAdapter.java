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

public class HotLastAdapter extends RecyclerView.Adapter<HotLastAdapter.HotLastVH> {
    Context mContext;
    /**
     * Map<String, String> - map, в которой содержатся name счетчика и показание value
     */
    List<Map<String,String>> meters;

    public HotLastAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setDataSet(List<Map<String, String>> list) {
        meters=list;
    }

    @Override
    public HotLastVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.ind_last_hot_layout, parent, false);
        return null;
    }

    @Override
    public void onBindViewHolder(HotLastVH holder, int position) {
        holder.tv.setText(meters.get(position).get("name"));
        holder.et.setText(meters.get(position).get("value"));
    }

    @Override
    public int getItemCount() {
        return meters.size();
    }

    public static class HotLastVH extends RecyclerView.ViewHolder {
        TextView tv;
        EditText et;

        public HotLastVH(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.ind_layout_tv);
            et = (EditText) itemView.findViewById(R.id.ind_layout_et);
        }
    }
}
