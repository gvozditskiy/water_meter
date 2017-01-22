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
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gvozditskiy.watermeter.Flat;
import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.Utils;
import com.gvozditskiy.watermeter.database.BaseHelper;
import com.gvozditskiy.watermeter.database.DbSchema;
import com.gvozditskiy.watermeter.interfaces.OnUpdate;

import java.util.ArrayList;

/**
 * Created by Alexey on 27.12.2016.
 */

public class FlatEditorFragment extends DialogFragment {
    AlertDialog dialog;
    OnUpdate onUpdate;
    InputMethodManager imm;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_editor_flat, null, false);
        RecyclerView recycler = (RecyclerView) v.findViewById(R.id.frag_dialog_flat_recycler);
        ImageButton addBtn = (ImageButton) v.findViewById(R.id.frag_dialog_flat_btn);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        ArrayList<Flat> flatsFromDb = (ArrayList<Flat>) Utils.getFlatList(getContext());
        final ArrayList<Flat> flatList = new ArrayList<>();
        if (flatsFromDb.size() == 0) {
            flatList.add(new Flat("Моя квартира"));
        } else {
            flatList.addAll(flatsFromDb);
        }
        final MyRecyclerAdapter adapter = new MyRecyclerAdapter(getContext(), flatList);
        recycler.setAdapter(adapter);
//        recycler.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        adapter.setOnClickInterface(new OnClickInterface() {
            @Override
            public void onClick(int i) {
                flatList.remove(i);
                adapter.notifyItemRemoved(i);
                adapter.notifyItemRangeChanged(i, flatList.size());
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flatList.add(new Flat("Моя квартира"+flatList.size()));
                adapter.notifyDataSetChanged();
            }
        });
        dialog = new AlertDialog.Builder(getContext())
                .setView(v)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase database = new BaseHelper(getContext()).getWritableDatabase();
                        try {
                            database.execSQL("delete from " + DbSchema.FlatsTable.NAME);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        for (Flat flat : flatList) {
                            ContentValues cv = new ContentValues();
                            cv.put(DbSchema.FlatsTable.Cols.NAME, flat.getName());
                            cv.put(DbSchema.FlatsTable.Cols.UUID, flat.getUuid().toString());
                            database.insert(DbSchema.FlatsTable.NAME, null, cv);

                        }
                        if (onUpdate != null) {
                            onUpdate.onUpdate();
                        }
                    }
                })
                .create();
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        return dialog;
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
            AppCompatEditText name = (AppCompatEditText) view.findViewById(R.id.flat_lt_name);
            ImageButton btn = (ImageButton) view.findViewById(R.id.flat_lt_btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (i == 0) {
                        Toast.makeText(mContext, "Нельзя удалить основную квартиру", Toast.LENGTH_SHORT).show();
                    } else {
                        onClickInterface.onClick(i);
                    }
                }
            });
            name.setText(flats.get(i).getName());
//            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
            return view;
        }
    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<VH> {
        Context mContext;
        LayoutInflater inflater;
        ArrayList<Flat> flats;
        OnClickInterface onClickInterface;

        public void setOnClickInterface(OnClickInterface onClickInterface) {
            this.onClickInterface = onClickInterface;
        }

        public MyRecyclerAdapter(Context mContext, ArrayList<Flat> flats) {
            this.mContext = mContext;
            this.flats = flats;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.flat_layout, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(final VH holder, final int pos) {
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pos == 0) {
                        Toast.makeText(mContext, "Нельзя удалить основную квартиру", Toast.LENGTH_SHORT).show();
                    } else {
                        onClickInterface.onClick(pos);
                    }
                }
            });
            holder.name.setText(flats.get(pos).getName());
            holder.name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    } else {
                        imm.hideSoftInputFromInputMethod(view.getWindowToken(),0);

                    }
                }
            });
            holder.name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        flats.get(pos).setName(charSequence.toString());
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
            return flats.size();
        }
    }

    private static class VH extends RecyclerView.ViewHolder {
        AppCompatEditText name;
        ImageButton btn;

        public VH(View view) {
            super(view);
            name = (AppCompatEditText) view.findViewById(R.id.flat_lt_name);
            btn = (ImageButton) view.findViewById(R.id.flat_lt_btn);
        }
    }

    public interface OnClickInterface {
        void onClick(int i);
    }

}
