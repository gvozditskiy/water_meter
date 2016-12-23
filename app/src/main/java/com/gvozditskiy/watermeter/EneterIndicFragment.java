package com.gvozditskiy.watermeter;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gvozditskiy.watermeter.database.BaseHelper;
import com.gvozditskiy.watermeter.database.DbSchema;
import com.gvozditskiy.watermeter.interfaces.OnSendListener;
import com.gvozditskiy.watermeter.interfaces.RegisterInterface;

import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class EneterIndicFragment extends Fragment implements OnSendListener {
    private EditText curCold;
    private EditText curHot;
    private EditText lastCold;
    private EditText lastHot;
    private TextView deltaColdTv;
    private TextView deltaHotTv;
    private TextView summaryTv;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private RegisterInterface registerInterface;

    public EneterIndicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mDatabase = new BaseHelper(mContext).getWritableDatabase();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterInterface) {
            registerInterface = (RegisterInterface) context;
        } else {
            throw new ClassCastException("Activity should implements RegisterInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eneter_indic, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        curCold = (EditText) view.findViewById(R.id.frag_enter_ind_current_coldwater);
        curHot = (EditText) view.findViewById(R.id.frag_enter_ind_current_hotwater);
        lastCold = (EditText) view.findViewById(R.id.frag_enter_ind_last_coldwater);
        lastHot = (EditText) view.findViewById(R.id.frag_enter_ind_last_hotwater);
        deltaColdTv = (TextView) view.findViewById(R.id.frag_enter_ind_coldDelta);
        deltaHotTv = (TextView) view.findViewById(R.id.frag_enter_ind_hotDelta);
        summaryTv = (TextView) view.findViewById(R.id.frag_enter_ind_summary);

        summaryTv.setText(String.format(getString(R.string.frag_enter_ind_summary), 0));

        curCold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int lastC = 0;
                if (!lastCold.getText().toString().equals("")) {
                    lastC = Integer.parseInt(lastCold.getText().toString());
                }
                int curC = 0;
                if (!charSequence.toString().equals("")) {
                    curC = Integer.parseInt(charSequence.toString());
                }
                int coldDelta = Math.abs(curC - lastC);
                int lastH = 0;
                if (!lastHot.getText().toString().equals("")) {
                    lastH = Integer.parseInt(lastHot.getText().toString());
                }
                int curH = 0;
                if (!curHot.getText().toString().equals("")) {
                    curH = Integer.parseInt(curHot.getText().toString());
                }
                int hotDelta = Math.abs(curH - lastH);
                deltaColdTv.setText(String.format(getString(R.string.frag_enter_ind_delta), coldDelta));
                summaryTv.setText(String.format(getString(R.string.frag_enter_ind_summary), coldDelta + hotDelta));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        curHot.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int lastC = 0;
                if (!lastCold.getText().toString().equals("")) {
                    lastC = Integer.parseInt(lastCold.getText().toString());
                }
                int curC = 0;
                if (!curCold.getText().toString().equals("")) {
                    curC = Integer.parseInt(curCold.getText().toString());
                }
                int coldDelta = Math.abs(curC - lastC);

                int lastH = 0;
                if (!lastHot.getText().toString().equals("")) {
                    lastH = Integer.parseInt(lastHot.getText().toString());
                }
                int curH = 0;
                if (!charSequence.toString().equals("")) {
                    curH = Integer.parseInt(charSequence.toString());
                }
                int hotDelta = Math.abs(curH - lastH);
                deltaHotTv.setText(String.format(getString(R.string.frag_enter_ind_delta), hotDelta));
                summaryTv.setText(String.format(getString(R.string.frag_enter_ind_summary), coldDelta + hotDelta));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        registerInterface.onRegisterInterface(this);
    }

    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        int curC =0;
        if (!curCold.getText().toString().equals("")) {
            curC = Integer.parseInt(curCold.getText().toString());
        }
        int curH =0;
        if (!curHot.getText().toString().equals("")) {
            curH = Integer.parseInt(curHot.getText().toString());
        }

        int lastH = 0;
        if (!lastHot.getText().toString().equals("")) {
            lastH = Integer.parseInt(lastHot.getText().toString());
        }
        int lastC = 0;
        if (!lastCold.getText().toString().equals("")) {
            lastC = Integer.parseInt(lastCold.getText().toString());
        }

        if (curC>=lastC) {
            values.put(DbSchema.IndTable.Cols.COLD, curC);
        } else {
            return null;
        }

        if (curH>=lastH) {
            values.put(DbSchema.IndTable.Cols.HOT, curH);
        }

        Date curDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        values.put(DbSchema.IndTable.Cols.YEAR, year);
        values.put(DbSchema.IndTable.Cols.MONTH, month);

        return values;
    }


    @Override
    public void onSend() {
        ContentValues contentValues = getContentValues();
        mDatabase.insert(DbSchema.IndTable.NAME,null, contentValues);
    }
}
