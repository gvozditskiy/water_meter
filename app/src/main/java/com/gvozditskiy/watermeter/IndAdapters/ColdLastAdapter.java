package com.gvozditskiy.watermeter.IndAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gvozditskiy.watermeter.R;

/**
 * Created by Alexey on 04.01.2017.
 */

public class ColdLastAdapter extends AbstractCurrentAdapter {
    public ColdLastAdapter(Context mContext) {
        super(mContext);
    }

    public ColdLastAdapter(Context mContext, boolean vis) {
        super(mContext, vis);
    }

    @Override
    public CurrentVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.ind_last_cold_layout, parent, false);
        return new CurrentVH(v);
    }
}
