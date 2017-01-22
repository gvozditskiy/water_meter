package com.gvozditskiy.watermeter.IndAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gvozditskiy.watermeter.Indication;
import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.interfaces.OnTextChanged;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    OnTextChanged onTextChanged;
    int mYear;
    int mMonth;
    Date mCurDate;
    Calendar mCalendar;
    public AbstractCurrentAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public AbstractCurrentAdapter(Context mContext, boolean vis) {
        this.mContext = mContext;
        this.vis = vis;
        mCurDate = new Date();
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(mCurDate);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
    }

    public void setInterface(OnTextChanged onTextChanged) {
        this.onTextChanged = onTextChanged;
    }

    public void setDataSet(List<Map<String, String>> list) {
        meters=list;
        indications = new ArrayList<>(meters.size());
        for (int i=0; i<meters.size(); i++) {
            Indication indication = new Indication();
            indication.setMeterUuid(meters.get(i).get("uuid"));
            indication.setYear(mYear);
            indication.setMonth(mMonth);
            indication.setValue(Integer.parseInt(list.get(i).get("value")));
            indications.add(i,indication);
        }
    }

    public List<Indication> getIndicationsList() {
        return indications;
    }

    @Override
    public abstract CurrentVH onCreateViewHolder(ViewGroup parent, int viewType);


    @Override
    public void onBindViewHolder(final CurrentVH holder, final int position) {
        holder.tv.setText(meters.get(position).get("name"));
        holder.et.setText(meters.get(position).get("value"));
        if (vis) {
            holder.delta.setText(String.format(
                    mContext.getString(R.string.frag_enter_ind_delta),
                    Integer.parseInt(meters.get(position).get("delta"))));
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
                    if (vis == true) {
                        int prevVal = Integer.parseInt(meters.get(position).get("value"));
                        int val = Integer.parseInt(charSequence.toString());
                        if (val-prevVal>=0) {
                            int delta = Integer.parseInt(meters.get(position).get("delta"));
                            holder.delta.setText(String.format(
                                    mContext.getString(R.string.frag_enter_ind_delta),
                                    Math.abs(-prevVal + val)));
                        } else {
                            holder.delta.setText("");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("textWatcher", "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("textWatcher", "afterTextChanged");
                if (onTextChanged!=null) {
                    onTextChanged.onTextChanged();
                }
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
