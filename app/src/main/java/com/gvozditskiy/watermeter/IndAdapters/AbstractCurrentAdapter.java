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

import com.gvozditskiy.watermeter.Indication;
import com.gvozditskiy.watermeter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexey on 08.01.2017.
 */

public abstract class AbstractCurrentAdapter extends RecyclerView.Adapter<AbstractCurrentAdapter.CurrentVH> {
    Context mContext;
    /**
     * Map<String, String> - map, в которой содержатся name счетчика и показание value, delta для дельты
     */
    List<Map<String,String>> meters;
    List<Indication> indications;
    boolean vis;

    public AbstractCurrentAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public AbstractCurrentAdapter(Context mContext, boolean vis) {
        this.mContext = mContext;
        this.vis = vis;
    }

    public void setDataSet(List<Map<String, String>> list) {
        meters=list;
        indications = new ArrayList<>(meters.size());
        for (int i=0; i<meters.size(); i++) {
            Indication indication = new Indication();
            indication.setMeterUuid(meters.get(i).get("uuid"));
            indications.add(i,indication);

        }
    }

    public List<Indication> getIndicationsList() {
        return indications;
    }

    @Override
    public abstract CurrentVH onCreateViewHolder(ViewGroup parent, int viewType);


    @Override
    public void onBindViewHolder(CurrentVH holder, final int position) {
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
                    indications.get(position).setValue(Integer.parseInt(charSequence.toString()));
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

    public static class CurrentVH extends RecyclerView.ViewHolder {
        TextView tv;
        EditText et;
        TextView delta;

        public CurrentVH(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.ind_layout_tv);
            et = (EditText) itemView.findViewById(R.id.ind_layout_et);
            delta = (TextView) itemView.findViewById(R.id.ind_layout_delta);
        }
    }
}
