package com.gvozditskiy.watermeter.activityNfragments;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gvozditskiy.watermeter.Indication;
import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.Utils;
import com.gvozditskiy.watermeter.database.BaseHelper;
import com.gvozditskiy.watermeter.database.DbSchema;
import com.gvozditskiy.watermeter.database.IndicationCursorWrapper;
import com.gvozditskiy.watermeter.interfaces.OnSendListener;
import com.gvozditskiy.watermeter.interfaces.RegisterInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EneterIndicFragment extends Fragment implements OnSendListener {
    private AppCompatEditText curCold;
    private AppCompatEditText curHot;
    private AppCompatEditText lastCold;
    private AppCompatEditText lastHot;
    private TextView deltaColdTv;
    private TextView deltaHotTv;
    private TextView summaryTv;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    Date mCurDate;
    Calendar mCalendar;
    int mYear;
    int mMonth;
    int mDay;

    private RegisterInterface registerInterface;

    List<Indication> mIndList;

    public EneterIndicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mDatabase = new BaseHelper(mContext).getWritableDatabase();

        mCurDate = new Date();
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(mCurDate);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mIndList = new ArrayList<>();
        mIndList.addAll(getIndicationsList(0));


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
        curCold = (AppCompatEditText) view.findViewById(R.id.frag_enter_ind_current_coldwater);
        curHot = (AppCompatEditText) view.findViewById(R.id.frag_enter_ind_current_hotwater);
        lastCold = (AppCompatEditText) view.findViewById(R.id.frag_enter_ind_last_coldwater);
        lastHot = (AppCompatEditText) view.findViewById(R.id.frag_enter_ind_last_hotwater);
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

        initInd();

        registerInterface.onRegisterInterface(this);
    }

    private void initInd() {
        boolean b = false; //показыывает, есть ли запись за предыдущий месяц

        lastCold.setFocusable(false);
        lastCold.setCursorVisible(false);
        lastHot.setFocusable(false);
        lastHot.setCursorVisible(false);

        if (mDay > 10) {
            curCold.setText(lastCold.getText().toString());
            curHot.setText(lastHot.getText().toString());
            curCold.setFocusable(true);
            curCold.setCursorVisible(true);
            curHot.setFocusable(true);
            curHot.setCursorVisible(true);
        } else if (mDay < 10) {
            curCold.setFocusable(false);
            curCold.setCursorVisible(false);
            curHot.setFocusable(false);
            curHot.setCursorVisible(false);
        }

        for (Indication ind : mIndList) {
            if (mMonth == 0) {
                if (ind.getYear() == mYear - 1) {
                    if (ind.getMonth() == 11) {
                        lastCold.setText(String.valueOf(ind.getCold()));
                        lastHot.setText(String.valueOf(ind.getHot()));
                        b = true;
                    }
                }
            } else {
                if (ind.getYear() == mYear) {
                    if (ind.getMonth() == mMonth - 1) {
                        lastCold.setText(String.valueOf(ind.getCold()));
                        lastHot.setText(String.valueOf(ind.getHot()));
                        b = true;
                    }
                }
            }

            if (ind.getYear() == mYear) {
                if (ind.getMonth() == mMonth) {
//                    if (mDay <= 10) {
                    curHot.setText(String.valueOf(ind.getHot()));
                    curCold.setText(String.valueOf(ind.getCold()));
                    curCold.setFocusable(false);
                    curCold.setCursorVisible(false);
                    curHot.setFocusable(false);
                    curHot.setCursorVisible(false);
//                    }
                }
            }
        }
        if (!b) {
            lastCold.setText("0");
            lastHot.setText("0");
        }

    }

    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        int curC = 0;
        if (!curCold.getText().toString().equals("")) {
            curC = Integer.parseInt(curCold.getText().toString());
        }
        int curH = 0;
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

        if (curC >= lastC) {
            values.put(DbSchema.IndTable.Cols.COLD, curC);
        } else {
            return null;
        }

        if (curH >= lastH) {
            values.put(DbSchema.IndTable.Cols.HOT, curH);
        }


        values.put(DbSchema.IndTable.Cols.YEAR, mYear);
        values.put(DbSchema.IndTable.Cols.MONTH, mMonth);

        return values;
    }


    IndicationCursorWrapper queryIndication(String whereClaus, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DbSchema.IndTable.NAME,
                null,
                whereClaus,
                whereArgs,
                null,
                null,
                null
        );
        return new IndicationCursorWrapper(cursor);
    }

    /**
     * Возвращает список индикаций за год
     *
     * @param year
     * @return
     */
    public List<Indication> getIndicationsList(int year) {
        List<Indication> indList = new ArrayList<>();
        IndicationCursorWrapper cursorWrapper = queryIndication(null,
                null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                indList.add(cursorWrapper.getIndication());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }
        return indList;
    }

    /**
     * реализация интерфейса
     */
    @Override
    public void onSend() {
        ContentValues contentValues = getContentValues();
        boolean b = true;
        for (Indication ind : mIndList) {
            if (ind.getMonth() == mMonth) {
//                Toast.makeText(getContext(), "Запись уже добавлена", Toast.LENGTH_SHORT).show();
                b = false;
//                return;
            }
        }

        if (b) {
            mDatabase.insert(DbSchema.IndTable.NAME, null, contentValues);
            mIndList.clear();
            mIndList.addAll(getIndicationsList(0));
            initInd();
        }

        Toast.makeText(getContext(),
                Utils.getMessageBody(
                        getContext(),
                        Integer.parseInt(curCold.getText().toString()),
                        Integer.parseInt(curHot.getText().toString())),
                Toast.LENGTH_LONG).show();
    }
}
