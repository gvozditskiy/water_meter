package com.gvozditskiy.watermeter.IndAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gvozditskiy.watermeter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexey on 04.01.2017.
 */

public class ColdCurrentAdapter extends RecyclerView.Adapter<ColdCurrentAdapter.ColdCurrentVH> {
    Context mContext;
    /**
     * Map<String, String> - map, в которой содержатся name счетчика и показание value, delta для дельты
     */
    List<Map<String,String>> meters;
    List<String> values;
    boolean vis;

    public ColdCurrentAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public ColdCurrentAdapter(Context mContext, boolean visibl) {
        this.mContext = mContext;
        vis = visibl;
    }

    public void setDataSet(List<Map<String, String>> list) {
        meters=list;
        values = new ArrayList<>(meters.size());
    }

    public List<String> getValuesList() {
        return values;
    }

    @Override
    public ColdCurrentVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.ind_cur_cold_layout, parent, false);
        return new ColdCurrentVH(v);
    }

    @Override
    public void onBindViewHolder(ColdCurrentVH holder, final int position) {
        holder.tv.setText(meters.get(position).get("name"));
        holder.et.setText(meters.get(position).get("value"));
        if (vis) {
            holder.delta.setText(meters.get(position).get("delta"));
        } else {
            holder.delta.setVisibility(View.GONE);
        }
        holder.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    values.set(position,charSequence.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return meters.size();
    }

    public static class ColdCurrentVH extends RecyclerView.ViewHolder {
        TextView tv;
        EditText et;
        TextView delta;

        public ColdCurrentVH(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.ind_layout_tv);
            et = (EditText) itemView.findViewById(R.id.ind_layout_et);
            delta = (TextView) itemView.findViewById(R.id.ind_layout_delta);
        }
    }
}
