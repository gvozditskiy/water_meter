package com.gvozditskiy.watermeter.activityNfragments;


import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
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
import com.gvozditskiy.watermeter.Person;
import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.Utils;
import com.gvozditskiy.watermeter.database.BaseHelper;
import com.gvozditskiy.watermeter.database.DbSchema;
import com.gvozditskiy.watermeter.interfaces.SendErrorCallback;
import com.gvozditskiy.watermeter.interfaces.OnTextChanged;
import com.gvozditskiy.watermeter.interfaces.RegisterIntents;
import com.gvozditskiy.watermeter.interfaces.RegisterInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class EneterIndicFragment extends Fragment implements SendErrorCallback {
    private static final String TAG_ADDFRAG = "AddDialogFragment";
    RecyclerView lastColdRecycler;
    RecyclerView curColdRecycler;
    RecyclerView lastHotRecycler;
    RecyclerView curHotRecycler;
    private TextView coldSumTv;
    private TextView hotSumTv;
    private TextView summaryTv;
    private AppCompatSpinner spinner;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    Date mCurDate;
    Calendar mCalendar;
    int mYear;
    int mMonth;
    int mDay;

    int mColdSum;
    int mHotSum;

    private RegisterInterface registerInterface;
    RegisterIntents registerIntents;

    List<Flat> flats = new ArrayList<>();
    List<Meter> coldMeters = new ArrayList<>();
    List<Meter> hotMeters = new ArrayList<>();
    List<Map<String, String>> listForColdRecycler = new ArrayList<>();
    List<Map<String, String>> listForHotRecycler = new ArrayList<>();
    List<Map<String, String>> listForCurColdRecycler = new ArrayList<>();
    List<Map<String, String>> listForCurHotRecycler = new ArrayList<>();

    AbstractCurrentAdapter coldLastAdapter;
    AbstractCurrentAdapter hotLastAdapter;
    AbstractCurrentAdapter coldCurrentAdapter;
    AbstractCurrentAdapter hotCurrentAdapter;

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
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        summaryTv = (TextView) view.findViewById(R.id.frag_enter_ind_summary);
        spinner = (AppCompatSpinner) view.findViewById(R.id.frag_enter_ind_spinner);
        lastColdRecycler = (RecyclerView) view.findViewById(R.id.frag_enter_ind_last_coldwater_recycler);
        lastHotRecycler = (RecyclerView) view.findViewById(R.id.frag_enter_ind_last_hotwater_recycler);
        curColdRecycler = (RecyclerView) view.findViewById(R.id.frag_enter_ind_cur_coldwater_recycler);
        curHotRecycler = (RecyclerView) view.findViewById(R.id.frag_enter_ind_cur_hotwater_recycler);
        coldSumTv = (TextView) view.findViewById(R.id.frag_enter_ind_coldDelta);
        hotSumTv = (TextView) view.findViewById(R.id.frag_enter_ind_hotDelta);


        lastColdRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        lastHotRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        curColdRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        curHotRecycler.setLayoutManager(new LinearLayoutManager(mContext));

        coldLastAdapter = new ColdLastAdapter(mContext, false);
        hotLastAdapter = new HotLastAdapter(mContext, false);
        coldCurrentAdapter = new ColdCurrentAdapter(mContext, true);
        hotCurrentAdapter = new HotCurrentAdapter(mContext, true);

        coldLastAdapter.setDataSet(listForColdRecycler);
        hotLastAdapter.setDataSet(listForHotRecycler);
        coldCurrentAdapter.setDataSet(listForCurColdRecycler);
        hotCurrentAdapter.setDataSet(listForCurHotRecycler);

        lastColdRecycler.setAdapter(coldLastAdapter);
        lastHotRecycler.setAdapter(hotLastAdapter);
        curColdRecycler.setAdapter(coldCurrentAdapter);
        curHotRecycler.setAdapter(hotCurrentAdapter);

        coldCurrentAdapter.setInterface(new OnTextChanged() {
            @Override
            public void onTextChanged() {
                //считаем расход холодной воды
                if (coldMeters.size() > 1) {
                    if (coldSumTv.getVisibility() == View.GONE) {
                        coldSumTv.setVisibility(View.VISIBLE);
                    }
                    setColdDeltas();
                }
            }
        });

        hotCurrentAdapter.setInterface(new OnTextChanged() {
            @Override
            public void onTextChanged() {
                //считаем расход холодной воды
                if (hotMeters.size() > 1) {
                    if (hotSumTv.getVisibility() == View.GONE) {
                        hotSumTv.setVisibility(View.VISIBLE);
                    }
                    setHotDeltas();
                }
            }
        });


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
                listForColdRecycler.clear();
                listForHotRecycler.clear();
                listForCurColdRecycler.clear();
                listForCurHotRecycler.clear();


                listForColdRecycler.addAll(getListForRecycler(coldMeters, 1));
                listForHotRecycler.addAll(getListForRecycler(hotMeters, 1));
                if (savedInstanceState == null || savedInstanceState.getInt("flat") != spinner.getSelectedItemPosition()) {
                    listForCurColdRecycler.addAll(getListForRecycler(coldMeters, 0));
                    listForCurHotRecycler.addAll(getListForRecycler(hotMeters, 0));
                } else {
                    List<Indication> tCold = (List<Indication>) savedInstanceState.getSerializable("coldInd");
                    List<Indication> tHot = (List<Indication>) savedInstanceState.getSerializable("hotInd");

                    listForCurColdRecycler.addAll(getListForRecycler(coldMeters, tCold));
                    listForCurHotRecycler.addAll(getListForRecycler(hotMeters, tHot));
                }

                coldLastAdapter.setDataSet(listForColdRecycler);
                hotLastAdapter.setDataSet(listForHotRecycler);
                coldCurrentAdapter.setDataSet(listForCurColdRecycler);
                hotCurrentAdapter.setDataSet(listForCurHotRecycler);

                coldLastAdapter.notifyDataSetChanged();
                hotLastAdapter.notifyDataSetChanged();
                coldCurrentAdapter.notifyDataSetChanged();
                hotCurrentAdapter.notifyDataSetChanged();

                // TODO: 11.01.2017 разобраться, когда прятать 
                if (savedInstanceState == null) {
//                    if (coldSumTv.getVisibility()==View.VISIBLE) {
//                        coldSumTv.setVisibility(View.GONE);
//                    }
//                    if (hotSumTv.getVisibility() == View.VISIBLE) {
//                        hotSumTv.setVisibility(View.GONE);
//                    }
                    if (coldMeters.size() <= 1) {
                        coldSumTv.setVisibility(View.GONE);
                    }
                    if (hotMeters.size() <= 1) {
                        hotSumTv.setVisibility(View.GONE);
                    }
                    setColdDeltas();
                    setHotDeltas();
                } else {
                    int pos = savedInstanceState.getInt("flat", 0);
                    if (pos != spinner.getSelectedItemPosition()) {
                        coldSumTv.setVisibility(View.GONE);
                        hotSumTv.setVisibility(View.GONE);
                    } else {
                        //обновить дельты
                        setColdDeltas();
                        setHotDeltas();
                    }
                }

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

    private void setHotDeltas() {
        List<Indication> coldList = hotCurrentAdapter.getIndicationsList();
        int hotSum = 0;
        int lastHotSum = 0;
        for (Indication ind : coldList) {
            hotSum += ind.getValue();
        }
        List<Indication> lastHotList = getListForPreviousMonth(hotMeters);
        for (Indication ind : lastHotList) {
            lastHotSum += ind.getValue();
        }
        mHotSum = hotSum - lastHotSum;
        hotSumTv.setText(
                String.format(mContext.getString(R.string.frag_enter_ind_delta), mHotSum));
        summaryTv.setText(
                String.format(mContext.getString(R.string.frag_enter_ind_summary),
                        mColdSum + mHotSum)
        );
    }

    private void setColdDeltas() {
        List<Indication> coldList = coldCurrentAdapter.getIndicationsList();
        int coldSum = 0;
        int lastColdSum = 0;
        for (Indication ind : coldList) {
            coldSum += ind.getValue();
        }
        List<Indication> lastColdList = getListForPreviousMonth(coldMeters);
        for (Indication ind : lastColdList) {
            lastColdSum += ind.getValue();
        }
        mColdSum = coldSum - lastColdSum;
        coldSumTv.setText(
                String.format(mContext.getString(R.string.frag_enter_ind_delta), mColdSum));
        summaryTv.setText(
                String.format(mContext.getString(R.string.frag_enter_ind_summary),
                        mColdSum + mHotSum)
        );
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
            case R.id.menu_add_send:
                // TODO: 12.01.2017 добавить проверку, добавлены ли уже показания
                List<Indication> indList = Utils.getIndicationsList(0, getContext());
                boolean b = false;
                List<Meter> mList = new ArrayList<>();
                mList.addAll(coldMeters);
                mList.addAll(hotMeters);
                for (Meter meter : mList) {
                    if (meter.getFlatUUID().equals(flats.get(spinner.getSelectedItemPosition()).getUuid().toString())) {
                        for (Indication ind : indList) {
                            if (ind.getYear() == mYear
                                    && ind.getMonth() == mMonth
                                    && ind.getMeterUuid().equals(meter.getUuid().toString())) {
                                b = true;
//                                break;
                            }
                        }
                    }
                }
                if (b) {
                    Toast.makeText(getContext(), getString(R.string.frag_enter_ind_present_message), Toast.LENGTH_SHORT).show();
                } else {
                    //добавляем показания в базу
                    for (Indication indication : coldCurrentAdapter.getIndicationsList()) {
                        mDatabase.insert(DbSchema.IndTable.NAME, null, getContentValues(indication));
                    }
                    for (Indication indication : hotCurrentAdapter.getIndicationsList()) {
                        mDatabase.insert(DbSchema.IndTable.NAME, null, getContentValues(indication));
                    }
                    //отправляем смс
                    try {
                        new AlertDialog.Builder(mContext)
                                .setTitle("Отправить показания?")
                                .setMessage(Utils.getMessageBody(mContext, coldCurrentAdapter.getIndicationsList(),
                                        hotCurrentAdapter.getIndicationsList(),
                                        flats.get(spinner.getSelectedItemPosition())))
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (sendSMS()) {

                                        }
                                    }
                                }).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private ContentValues getContentValues(Indication indication) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.IndTable.Cols.YEAR, indication.getYear());
        values.put(DbSchema.IndTable.Cols.MONTH, indication.getMonth());
        values.put(DbSchema.IndTable.Cols.UUID, indication.getUuid().toString());
        values.put(DbSchema.IndTable.Cols.METER_UUID, indication.getMeterUuid());
        values.put(DbSchema.IndTable.Cols.VALUE, indication.getValue());

        return values;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("coldInd", (Serializable) coldCurrentAdapter.getIndicationsList());
        outState.putSerializable("hotInd", (Serializable) hotCurrentAdapter.getIndicationsList());
        outState.putInt("flat", spinner.getSelectedItemPosition());
    }

    /**
     * реализация интерфейса
     */
    @Override
    public void onSend() {
        Utils.deleteIndications(getContext(),
                flats.get(spinner.getSelectedItemPosition()).getUuid().toString(),
                mYear, mMonth);
    }

    private boolean sendSMS() {
        String phoneNumber = "+";
        for (Person person : Utils.getPersonList(getContext())) {
            if (flats.get(spinner.getSelectedItemPosition()).getUuid().toString().equals(person.getFlat_uuid())) {
                phoneNumber += person.getPhone();
            }
        }

        String smsBody = Utils.getMessageBody(
                getContext(), coldCurrentAdapter.getIndicationsList(),
                hotCurrentAdapter.getIndicationsList(), flats.get(spinner.getSelectedItemPosition()));


        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(SMS_DELIVERED), 0);

        registerIntents.onRegisterIntent(sentPendingIntent, deliveredPendingIntent);

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> texts = smsManager.divideMessage(smsBody);
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();

//        for (int i = 0; i < texts.size(); i++) {
//            sentPendingIntents.add(sentPendingIntent);
//            deliveredPendingIntents.add(deliveredPendingIntent);
//        }

        if (!phoneNumber.equals("+") && !smsBody.equals("")) {
            if (texts.size() >= 1) {
                smsManager.sendTextMessage(phoneNumber, null, smsBody, sentPendingIntent, deliveredPendingIntent);
            } else {
                smsManager.sendMultipartTextMessage(phoneNumber, null, texts, sentPendingIntents, deliveredPendingIntents);
            }
            return true;
        } else {
            Toast.makeText(getContext(), "Показания счетчиков не отправлены. Проверьте введенные данные", Toast.LENGTH_SHORT).show();
            return false;
        }

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

    private List<Map<String, String>> getListForRecycler(List<Meter> list, List<Indication> inds) {
        List<Map<String, String>> rezList = new ArrayList<>();
        List<Indication> indications = Utils.getIndicationsList(0, mContext);
        int year = mYear;
        int month = mMonth;
        if (month == 0) {
            year--;
            month = 11;
        } else {
            month = mMonth - 1;
        }
        for (int i = 0; i < list.size(); i++) {
            Map<String, String> map = new HashMap<>();
            Meter meter = list.get(i);
            map.put("name", meter.getName());
            Indication indication = inds.get(i);
            int lastValue = 0;
            for (Indication ind : indications) {
                if (ind.getMeterUuid().equals(meter.getUuid().toString())) {
                    if (ind.getYear() == year && ind.getMonth() == month) {
                        lastValue = ind.getValue();
                    }
                }
            }
            map.put("value", String.valueOf(indication.getValue()));
            map.put("uuid", meter.getUuid().toString());
            map.put("delta", String.valueOf(Math.abs(indication.getValue() - lastValue)));
            rezList.add(map);
        }
        return rezList;
    }

    private List<Indication> getListForPreviousMonth(List<Meter> meters) {
        List<Indication> rezList = new ArrayList<>();
        List<Indication> indications = Utils.getIndicationsList(0, mContext);
        int year = mYear;
        int month;
        if (mMonth == 0) {
            year--;
            month = 11;
        } else {
            month = mMonth - 1;
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
