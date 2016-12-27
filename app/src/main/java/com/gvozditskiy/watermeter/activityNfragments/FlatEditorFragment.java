package com.gvozditskiy.watermeter.activityNfragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gvozditskiy.watermeter.Flat;
import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.database.BaseHelper;
import com.gvozditskiy.watermeter.database.DbSchema;
import com.gvozditskiy.watermeter.interfaces.OnUpdate;

import java.util.ArrayList;

/**
 * Created by Alexey on 27.12.2016.
 */

public class FlatEditorFragment extends DialogFragment {

    OnUpdate onUpdate;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_editor_flat, null, false);
        ListViewCompat listView = (ListViewCompat) v.findViewById(R.id.frag_dialog_flat_listview);
        Button addBtn = (Button) v.findViewById(R.id.frag_dialog_flat_btn);
        final ArrayList<Flat> flatList = new ArrayList<>();
        flatList.add(new Flat("Моя квартира"));
        final MyAdapter adapter = new MyAdapter(getContext(), flatList);
        listView.setAdapter(adapter);
        adapter.setOnClickInterface(new OnClickInterface() {
            @Override
            public void onClick(int i) {
                flatList.remove(i);
                adapter.notifyDataSetChanged();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flatList.add(new Flat(""));
                adapter.notifyDataSetChanged();
            }
        });
        return new AlertDialog.Builder(getContext())
                .setView(v)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase database = new BaseHelper(getContext()).getWritableDatabase();
                        try {
                            database.execSQL("delete from "+ DbSchema.FlatsTable.NAME);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        for (Flat flat: flatList) {
                            ContentValues cv = new ContentValues();
                            cv.put(DbSchema.FlatsTable.Cols.NAME, flat.getName());
                            cv.put(DbSchema.FlatsTable.Cols.UUID, flat.getUuid().toString());
                            database.insert(DbSchema.FlatsTable.NAME, null, cv);

                        }
                        if (onUpdate!=null) {
                            onUpdate.onUpdate();
                        }
                    }
                })
                .show();
    }

    public void setOnUpdateListener(OnUpdate onUpdate) {
        this.onUpdate = onUpdate;
    }

    private static class MyAdapter extends BaseAdapter {
        Context mContext;
        LayoutInflater inflater;
        ArrayList<Flat> flats;
        OnClickInterface onClickInterface;

        public void setOnClickInterface(OnClickInterface onClickInterface) {
            this.onClickInterface = onClickInterface;
        }

        public MyAdapter(Context mContext, ArrayList<Flat> flats) {
            this.mContext = mContext;
            this.flats = flats;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return flats.size();
        }

        @Override
        public Object getItem(int i) {
            return flats.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(R.layout.flat_layout, viewGroup, false);
            }
            EditText name = (EditText) view.findViewById(R.id.flat_lt_name);
            Button btn = (Button) view.findViewById(R.id.flat_lt_btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (i==0) {
                       Toast.makeText(mContext, "Нельзя удалить основную квартиру", Toast.LENGTH_SHORT).show();
                    } else {
                        onClickInterface.onClick(i);
                    }
                }
            });
            name.setText(flats.get(i).getName());
            return view;
        }
    }

    public interface OnClickInterface {
        void onClick(int i);
    }
}
