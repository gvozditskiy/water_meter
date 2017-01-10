package com.gvozditskiy.watermeter.activityNfragments;


import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gvozditskiy.watermeter.Flat;
import com.gvozditskiy.watermeter.IndAdapters.AbstractCurrentAdapter;
import com.gvozditskiy.watermeter.IndAdapters.ColdCurrentAdapter;
import com.gvozditskiy.watermeter.IndAdapters.ColdLastAdapter;
import com.gvozditskiy.watermeter.IndAdapters.HotCurrentAdapter;
import com.gvozditskiy.watermeter.IndAdapters.HotLastAdapter;
import com.gvozditskiy.watermeter.Indication;
import com.gvozditskiy.watermeter.Meter;
import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.Utils;
import com.gvozditskiy.watermeter.database.BaseHelper;
import com.gvozditskiy.watermeter.database.DbSchema;
import com.gvozditskiy.watermeter.database.IndicationCursorWrapper;
import com.gvozditskiy.watermeter.interfaces.OnSendListener;
import com.gvozditskiy.watermeter.interfaces.OnTextChanged;
import com.gvozditskiy.watermeter.interfaces.RegisterIntents;
import com.gvozditskiy.watermeter.interfaces.RegisterInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class EneterIndicFragment extends Fragment implements OnSendListener {
    private static final String TAG_ADDFRAG = "AddDialogFragment";
    RecyclerView lastColdRecycler;
    RecyclerView curColdRecycler;
    RecyclerView lastHotRecycler;
    RecyclerView curHotRecycler;
    private TextView summaryTv;
    private AppCompatSpinner spinner;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    Date mCurDate;
    Calendar mCalendar;
    int mYear;
    int mMonth;
    int mDay;

    private RegisterInterface registerInterface;
    RegisterIntents registerIntents;

    List<Flat> flats = new ArrayList<>();
    List<Meter> coldMeters = new ArrayList<>();
    List<Meter> hotMeters = new ArrayList<>();
    List<Map<String, String>> listForColdRecycler;
    List<Map<String, String>> listForHotRecycler;
    List<Map<String, String>> listForCurColdRecycler;
    List<Map<String, String>> listForCurHotRecycler;

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

        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.app_name));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterInterface) {
            registerInterface = (RegisterInterface) context;
        } else {
            throw new ClassCastException("Activity should implements RegisterInterface");
        }

        registerIntents = (RegisterIntents) context;
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
        summaryTv = (TextView) view.findViewById(R.id.frag_enter_ind_summary);
        spinner = (AppCompatSpinner) view.findViewById(R.id.frag_enter_ind_spinner);
        lastColdRecycler = (RecyclerView) view.findViewById(R.id.frag_enter_ind_last_coldwater_recycler);
        lastHotRecycler = (RecyclerView) view.findViewById(R.id.frag_enter_ind_last_hotwater_recycler);
        curColdRecycler = (RecyclerView) view.findViewById(R.id.frag_enter_ind_cur_coldwater_recycler);
        curHotRecycler = (RecyclerView) view.findViewById(R.id.frag_enter_ind_cur_hotwater_recycler);
        lastColdRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        lastHotRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        curColdRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        curHotRecycler.setLayoutManager(new LinearLayoutManager(mContext));

        final AbstractCurrentAdapter coldLastAdapter = new ColdLastAdapter(mContext, false);
        final AbstractCurrentAdapter hotLastAdapter = new HotLastAdapter(mContext, false);
        final AbstractCurrentAdapter coldCurrentAdapter = new ColdCurrentAdapter(mContext, true);
        final AbstractCurrentAdapter hotCurrentAdapter = new HotCurrentAdapter(mContext, true);




        flats = Utils.getFlatList(mContext);
        List<String> flatList = new ArrayList<>();
        for (Flat flat : flats) {
            flatList.add(flat.getName());
        }
        ArrayAdapter<String> spinAdapter = new ArrayAdapter(getContext(),
                R.layout.flat_spinner_view, R.id.flat_spinner_tv, flatList);
        spinner.setAdapter(spinAdapter);
        /**
         * по смене квартиры загружаем счетчики
         */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<Meter> tMeters = Utils.getMeterLsit(mContext);
                coldMeters = new ArrayList<>();
                hotMeters = new ArrayList<>();
                for (Meter meter : tMeters) {
                    if (meter.getFlatUUID().equals(flats.get(spinner.getSelectedItemPosition()).getUuid().toString())) {
                        if (meter.getType().equals(Meter.TYPE_COLD)) {
                            coldMeters.add(meter);
                        } else if (meter.getType().equals(Meter.TYPE_HOT)) {
                            hotMeters.add(meter);
                        }
                    }
                }
                listForColdRecycler = getListForRecycler(coldMeters, 1);
                listForHotRecycler = getListForRecycler(hotMeters, 1);
                listForCurColdRecycler = getListForRecycler(coldMeters, 0);
                listForCurHotRecycler = getListForRecycler(hotMeters, 0);

                /**
                 * если текущее значение меньше, чем предыдущее,
                 * приравниваем текущее к предыдущему
                 */

//                for(int k=0; k<listForColdRecycler.size(); k++) {
//                    int prevVal = Integer.parseInt(listForColdRecycler.get(k).get("value"));
//                    int curVal = Integer.parseInt(listForCurColdRecycler.get(k).get("value"));
//                    if (curVal<prevVal) {
//                        listForCurColdRecycler.get(k).put("value", String.valueOf(prevVal));
//                    }
//                }
//                for(int k=0; k<listForHotRecycler.size(); k++) {
//                    int prevVal = Integer.parseInt(listForHotRecycler.get(k).get("value"));
//                    int curVal = Integer.parseInt(listForCurHotRecycler.get(k).get("value"));
//                    if (curVal<prevVal) {
//                        listForCurHotRecycler.get(k).put("value", String.valueOf(prevVal));
//                    }
//                }
// TODO: 10.01.2017 пересмотреть создание recycler 
                coldLastAdapter.setDataSet(listForColdRecycler);
                hotLastAdapter.setDataSet(listForHotRecycler);
                coldCurrentAdapter.setDataSet(listForCurColdRecycler);
                hotCurrentAdapter.setDataSet(listForCurHotRecycler);

                lastColdRecycler.setAdapter(coldLastAdapter);
                lastHotRecycler.setAdapter(hotLastAdapter);
                curColdRecycler.setAdapter(coldCurrentAdapter);
                curHotRecycler.setAdapter(hotCurrentAdapter);

                coldLastAdapter.notifyDataSetChanged();
                hotLastAdapter.notifyDataSetChanged();
                coldCurrentAdapter.notifyDataSetChanged();
                hotCurrentAdapter.notifyDataSetChanged();

                coldCurrentAdapter.setInterface(new OnTextChanged() {
                    @Override
                    public void onTextChanged() {
                        //считаем расход холодной воды
                        List<Indication> coldList = coldCurrentAdapter.getIndicationsList();
                        int coldSum = 0;
                        int lastColdSum = 0;
                        for (Indication ind: coldList) {
                            coldSum+=ind.getValue();
                        }
                        List<Indication> lastColdList = getListForPreviousMonth(coldMeters);
                        for (Indication ind: lastColdList) {
                            lastColdSum+=ind.getValue();
                        }
                        Log.d("delta", String.valueOf(coldSum-lastColdSum));
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner.setSelection(0);

        summaryTv.setText(String.format(getString(R.string.frag_enter_ind_summary), 0));

//        initInd();

        registerInterface.onRegisterInterface(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_enter_indications_frag, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_add:
                FragmentManager fm = getChildFragmentManager();
                AddDialogFragment fragment = new AddDialogFragment();
                fragment.show(fm, TAG_ADDFRAG);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initInd() {
        boolean b = false; //показыывает, есть ли запись за предыдущий месяц

        if (mDay > 10) {

        } else if (mDay < 10) {

        }

        /*for (Indication ind : mIndList) {
            if (mMonth == 0) {
                if (ind.getYear() == mYear - 1) {
                    if (ind.getMonth() == 11) {
//                        lastCold.setText(String.valueOf(ind.getCold()));
//                        lastHot.setText(String.valueOf(ind.getHot()));
                        b = true;
                    }
                }
            } else {
                if (ind.getYear() == mYear) {
                    if (ind.getMonth() == mMonth - 1) {
//                        lastCold.setText(String.valueOf(ind.getCold()));
//                        lastHot.setText(String.valueOf(ind.getHot()));
                        b = true;
                    }
                }
            }

            if (ind.getYear() == mYear) {
                if (ind.getMonth() == mMonth) {
//                    if (mDay <= 10) {
//                    curHot.setText(String.valueOf(ind.getHot()));
//                    curCold.setText(String.valueOf(ind.getCold()));
                    curCold.setFocusable(false);
                    curCold.setCursorVisible(false);
                    curHot.setFocusable(false);
                    curHot.setCursorVisible(false);
//                    }
                }
            }
        }*/
        if (!b) {

        }

    }

    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        int curC = 0;
//        if (!curCold.getText().toString().equals("")) {
//            curC = Integer.parseInt(curCold.getText().toString());
//        }
//        int curH = 0;
//        if (!curHot.getText().toString().equals("")) {
//            curH = Integer.parseInt(curHot.getText().toString());
//        }
//
//        int lastH = 0;
//        if (!lastHot.getText().toString().equals("")) {
//            lastH = Integer.parseInt(lastHot.getText().toString());
//        }
//        int lastC = 0;
//        if (!lastCold.getText().toString().equals("")) {
//            lastC = Integer.parseInt(lastCold.getText().toString());
//        }

//        if (curC >= lastC) {
//            values.put(DbSchema.IndTable.Cols.COLD, curC);
//        } else {
//            return null;
//        }

//        if (curH >= lastH) {
//            values.put(DbSchema.IndTable.Cols.HOT, curH);
//        }
//
//
//        values.put(DbSchema.IndTable.Cols.YEAR, mYear);
//        values.put(DbSchema.IndTable.Cols.MONTH, mMonth);

        return values;
    }


    /**
     * реализация интерфейса
     */
    @Override
    public void onSend() {
        final ContentValues contentValues = getContentValues();
        boolean b = true;
//        for (Indication ind : mIndList) {
//            if (ind.getMonth() == mMonth) {
//                Toast.makeText(getContext(), "Запись за текущий период уже добавлена", Toast.LENGTH_SHORT).show();
//                b = false;
////                return;
//            }
//        }

        if (b) {
            try {
//                new AlertDialog.Builder(getContext())
//                        .setTitle(getString(R.string.frag_enter_ind_title_warn))
//                        .setMessage(String.format(getString(R.string.frag_enter_ind_message_warn), Utils.getMessageBody(getContext(),
////                                Integer.parseInt(curCold.getText().toString()),
////                                Integer.parseInt(curHot.getText().toString()))))
//                        .setNegativeButton(android.R.string.cancel, null)
//                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                ////////////
//                            }
//                        }).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Показания счетчиков не отправлены. Проверьте введенные данные", Toast.LENGTH_SHORT).show();
            }

        }


    }

    private void sendSMS() {
        String phoneNumber = "+" + Utils.getPhone(getContext());
//        String smsBody = Utils.getMessageBody(
//                getContext(),
//                Integer.parseInt(curCold.getText().toString()),
//                Integer.parseInt(curHot.getText().toString())
//);


        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(SMS_DELIVERED), 0);

        registerIntents.onRegisterIntent(sentPendingIntent, deliveredPendingIntent);

        SmsManager smsManager = SmsManager.getDefault();
//        ArrayList<String> texts = smsManager.divideMessage(smsBody);
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();

//        for (int i = 0; i < texts.size(); i++) {
//            sentPendingIntents.add(sentPendingIntent);
//            deliveredPendingIntents.add(deliveredPendingIntent);
//        }

//        if (!phoneNumber.equals("") && !smsBody.equals("")) {
////            if (texts.size()==1) {
////                smsManager.sendTextMessage(phoneNumber, null, smsBody, sentPendingIntent, deliveredPendingIntent);
////            } else {
////                smsManager.sendMultipartTextMessage(phoneNumber, null, texts, null, null);
////            }
//            smsManager.sendMultipartTextMessage(phoneNumber, null, texts, sentPendingIntents, deliveredPendingIntents);
//
//        } else {
//            Toast.makeText(getContext(), "Показания счетчиков не отправлены. Проверьте введенные данные", Toast.LENGTH_SHORT).show();
//        }

//        Toast.makeText(getContext(),
//                Utils.getMessageBody(
//                        getContext(),
//                        Integer.parseInt(curCold.getText().toString()),
//                        Integer.parseInt(curHot.getText().toString())),
//                Toast.LENGTH_LONG).show();
    }

    private List<Map<String, String>> getListForRecycler(List<Meter> list, int i) {
        List<Map<String, String>> rezList = new ArrayList<>();
        List<Indication> indications = Utils.getIndicationsList(0, mContext);
        int year = mYear;
        int month = mMonth;
        if (i == 1) {
            if (mMonth == 0) {
                month = 11;
                year -= 1;
            } else {
                month = mMonth - i;
            }
        }

        for (Meter meter : list) {
            Map<String, String> map = new HashMap<>();
            map.put("name", meter.getName());
            int value = 0;
            int lastValue = 0;
            for (Indication indication : indications) {
                if (indication.getMeterUuid().equals(meter.getUuid().toString())) {
                    if (indication.getYear() == year && indication.getMonth() == month) {
                        value = indication.getValue();
                    }
                    if (i != 1) {
                        int lastMonth = month - 1;
                        int lastYear = year;
                        if (lastMonth < 0) {
                            lastMonth = 11;
                            lastYear -= 1;
                        }
                        if (indication.getYear() == lastYear && indication.getMonth() == lastMonth) {
                            lastValue = indication.getValue();
                        }
                        if (value < lastValue) {
                            value = lastValue;
                        }
                    }
                }

            }
            map.put("value", String.valueOf(value));
            map.put("uuid", meter.getUuid().toString());
            map.put("delta", String.valueOf(Math.abs(value - lastValue)));
            rezList.add(map);
        }

        return rezList;
    }

    private List<Indication> getListForPreviousMonth(List<Meter> meters) {
        List<Indication> rezList = new ArrayList<>();
        List<Indication> indications = Utils.getIndicationsList(0, mContext);
        int year = mYear;
        int month;
        if (mMonth==0) {
            year--;
            month = 11;
        } else {
            month=mMonth-1;
        }

        for (Meter meter : meters) {
            Map<String, String> map = new HashMap<>();
            map.put("name", meter.getName());
            for (Indication indication : indications) {
                if (indication.getMeterUuid().equals(meter.getUuid().toString())) {
                    if (indication.getYear() == year && indication.getMonth() == month) {
                        rezList.add(indication);
                    }

                }

            }

        }

        return rezList;
    }

}
