package com.gvozditskiy.watermeter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexey on 28.12.2016.
 */

public class ColdRecyclerAdapter extends RecyclerView.Adapter<ColdRecyclerAdapter.VH> {
    Context context;
    static List<Meter> meterList = new ArrayList<>();
    OnClickInterface onClickInterface;

    public void setOnClickInterface(OnClickInterface onClickInterface) {
        this.onClickInterface = onClickInterface;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ColdRecyclerAdapter(Context context, List<Meter> list) {
        this.context = context;
        meterList = list;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.meter_edit_layout, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        holder.nameText.setText(meterList.get(position).getName().toString());
        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickInterface.onClick(position);
            }
        });
        Glide.with(context).load("").placeholder(R.drawable.ic_meter_cold).into(holder.meter);
    }

    @Override
    public int getItemCount() {
        return meterList.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageButton delBtn;
        EditText nameText;
        ImageView meter;

        public VH(View itemView) {
            super(itemView);
            delBtn = (ImageButton) itemView.findViewById(R.id.meter_edit_delBtn);
            nameText = (EditText) itemView.findViewById(R.id.meter_edit_nametext);
            meter = (ImageView) itemView.findViewById(R.id.meter_edit_image);

        }
    }

    public interface OnClickInterface {
        void onClick(int i);
    }
}