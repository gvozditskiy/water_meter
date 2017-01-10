package com.gvozditskiy.watermeter.activityNfragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.gvozditskiy.watermeter.Flat;
import com.gvozditskiy.watermeter.IndAdapters.AbstractCurrentAdapter;
import com.gvozditskiy.watermeter.IndAdapters.ColdCurrentAdapter;
import com.gvozditskiy.watermeter.IndAdapters.HotCurrentAdapter;
import com.gvozditskiy.watermeter.Indication;
import com.gvozditskiy.watermeter.Meter;
import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.Utils;
import com.gvozditskiy.watermeter.database.BaseHelper;
import com.gvozditskiy.watermeter.database.DbSchema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Добавляет показания за предыдущий месяц
 * Created by Alexey on 26.12.2016.
 */

public class AddDialogFragment extends DialogFragment {
    AlertDialog dialog;
    int selectedYear;
    List<Meter> coldMeterList = new ArrayList<>();
    List<Meter> hotMeterList = new ArrayList<>();
    List<Map<String, String>> listForColdRecycler = new ArrayList<>();
    List<Map<String, String>> listForHotRecycler = new ArrayList<>();
    List<Flat> flatList;
    AbstractCurrentAdapter coldCurrentAdapter;
    HotCurrentAdapter hotCurrentAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.frag_dialog_add, null, false);
        Spinner spinYear = (Spinner) v.findViewById(R.id.frag_add_year_spinner);
        final Spinner spinMonth = (Spinner) v.findViewById(R.id.frag_add_month_spinner);
        RecyclerView coldRcycler = (RecyclerView) v.findViewById(R.id.frag_enter_ind_cur_coldwater_recycler);
        RecyclerView hotRcycler = (RecyclerView) v.findViewById(R.id.frag_enter_ind_cur_hotwater_recycler);
        AppCompatSpinner flatSpinner = (AppCompatSpinner) v.findViewById(R.id.frag_add_flat_spinner);
        final Button addBtn = (Button) v.findViewById(R.id.frag_add_btn);

        coldCurrentAdapter = new ColdCurrentAdapter(getContext(), false);
        hotCurrentAdapter = new HotCurrentAdapter(getContext(), false);
        coldRcycler.setLayoutManager(new LinearLayoutManager(getContext()));
        hotRcycler.setLayoutManager(new LinearLayoutManager(getContext()));
        coldRcycler.setAdapter(coldCurrentAdapter);
        hotRcycler.setAdapter(hotCurrentAdapter);
        coldCurrentAdapter.setDataSet(listForColdRecycler);
        hotCurrentAdapter.setDataSet(listForHotRecycler);

        final List<Integer> yearsList = new ArrayList<>();
        final List<Indication> indList = new ArrayList<>();
        List<String> flatNameList = new ArrayList<>();
        flatList = Utils.getFlatList(getContext().getApplicationContext());
        for (Flat flat : flatList) {
            flatNameList.add(flat.getName());

        }

        if (flatList.size()<=1) {
            addBtn.setText(getResources().getString(R.string.frag_add_add));
        } else {
            addBtn.setText(getResources().getString(R.string.frag_add_add_flat));
        }

        indList.clear();
        indList.addAll(Utils.getIndicationsList(0, getContext()));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.flat_spinner_view, R.id.flat_spinner_tv, flatNameList);
        flatSpinner.setAdapter(spinnerAdapter);
        flatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Flat flat = flatList.get(i);
                String flatUUID = flat.getUuid().toString();
                coldMeterList = new ArrayList<Meter>();
                hotMeterList = new ArrayList<Meter>();
                for (Meter meter : Utils.getMeterLsit(getContext().getApplicationContext())) {
                    if (meter.getFlatUUID().equals(flatUUID)) {
                        if (meter.getType().equals(Meter.TYPE_COLD)) {
                            coldMeterList.add(meter);
                        } else if (meter.getType().equals(Meter.TYPE_HOT)) {
                            hotMeterList.add(meter);
                        }
                    }
                }
                listForColdRecycler.clear();
                listForHotRecycler.clear();



                try {
                    int pos = spinMonth.getSelectedItemPosition();
                    setupColdIndications(pos, indList);
                    setupHotIndications(pos, indList);
                } catch (Exception e) {
                    setupColdIndications();
                    setupHotIndications();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        flatSpinner.setSelection(0);


        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        setSelectedYear(calendar.get(Calendar.YEAR));


        for (int i = 0; i < 10; i++) {
            yearsList.add(calendar.get(Calendar.YEAR) - i);
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_list_item_1, yearsList);
        spinYear.setAdapter(yearAdapter);
        spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getMonth(yearsList.get(i)));
                setSelectedYear(yearsList.get(i));
                spinMonth.setAdapter(monthAdapter);
                spinMonth.setSelection(0, true);

                int mSize = getMonth(selectedYear).size();
                if (mSize==0) {
                    addBtn.setEnabled(false);

                } else {
                    addBtn.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinYear.setSelection(0, true);

        spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setupColdIndications(i, indList);

                setupHotIndications(i, indList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase mDatabase = new BaseHelper(getContext()).getWritableDatabase();
                List<Indication> coldIndList = coldCurrentAdapter.getIndicationsList();
                List<Indication> hotIndList = hotCurrentAdapter.getIndicationsList();
                coldIndList.size();
                for (int k = 0; k < coldIndList.size(); k++) {
                    Indication indication = findIndication(selectedYear,
                            spinMonth.getSelectedItemPosition(),
                            coldMeterList.get(k).getUuid().toString());
                    if (indication!=null) {
                        indication.setValue(coldIndList.get(k).getValue());
                        mDatabase.update(DbSchema.IndTable.NAME, getContentValues(indication),
                                DbSchema.IndTable.Cols.UUID + " = ?",
                                new String[] {indication.getUuid().toString()});
                    } else {
                        coldIndList.get(k).setYear(selectedYear);
                        coldIndList.get(k).setMonth(spinMonth.getSelectedItemPosition());
                        mDatabase.insert(DbSchema.IndTable.NAME, null, getContentValues(coldIndList.get(k)));
                    }
                }
                for (int k = 0; k < hotIndList.size(); k++) {
                    Indication indication = findIndication(selectedYear,
                            spinMonth.getSelectedItemPosition(),
                            hotMeterList.get(k).getUuid().toString());
                    if (indication!=null) {
                        indication.setValue(hotIndList.get(k).getValue());
                        mDatabase.update(DbSchema.IndTable.NAME, getContentValues(indication),
                                DbSchema.IndTable.Cols.UUID + " = ?",
                                new String[] {indication.getUuid().toString()});
                    } else {
                        hotIndList.get(k).setYear(selectedYear);
                        hotIndList.get(k).setMonth(spinMonth.getSelectedItemPosition());
                        mDatabase.insert(DbSchema.IndTable.NAME, null, getContentValues(hotIndList.get(k)));
                    }
                }

                Toast.makeText(getContext(), "Данные добавлены", Toast.LENGTH_SHORT).show();
            }
        });

        dialog = new AlertDialog.Builder(getContext())
                .setView(v)
                .create();
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return dialog;
    }

    private void setupHotIndications(int i, List<Indication> indList) {
        listForHotRecycler.clear();
        for (int j = 0; j < hotMeterList.size(); j++) {
            Meter meter = hotMeterList.get(j);
            Map<String, String> map = new HashMap<>();
            map.put("name", meter.getName());
            map.put("uuid", meter.getUuid().toString());
            map.put("value", "0");

            for (Indication ind : indList) {
                if (ind.getYear() == selectedYear && ind.getMonth() == i) {
                    if (ind.getMeterUuid().equals(meter.getUuid().toString())) {
                        map.put("value", String.valueOf(ind.getValue()));
                    }
                }
            }
            listForHotRecycler.add(map);
        }
        hotCurrentAdapter.setDataSet(listForHotRecycler);
        hotCurrentAdapter.notifyDataSetChanged();
    }

    private void setupColdIndications(int i, List<Indication> indList) {
        listForColdRecycler.clear();
        for (int j = 0; j < coldMeterList.size(); j++) {
            Meter meter = coldMeterList.get(j);
            Map<String, String> map = new HashMap<>();
            map.put("name", meter.getName());
            map.put("uuid", meter.getUuid().toString());
            map.put("value", "0");

            for (Indication ind : indList) {
                if (ind.getYear() == selectedYear && ind.getMonth() == i) {
                    if (ind.getMeterUuid().equals(meter.getUuid().toString())) {
                        map.put("value", String.valueOf(ind.getValue()));
                    }
                }
            }
            listForColdRecycler.add(map);
            coldCurrentAdapter.setDataSet(listForColdRecycler);
            coldCurrentAdapter.notifyDataSetChanged();

        }
    }

    private void setupHotIndications() {
        for (Meter meter : hotMeterList) {
            Map<String, String> map = new HashMap<>();
            map.put("name", meter.getName());
            map.put("value", "0");
            map.put("uuid", meter.getUuid().toString());
            listForHotRecycler.add(map);
        }
        hotCurrentAdapter.setDataSet(listForHotRecycler);
        hotCurrentAdapter.notifyDataSetChanged();

    }

    private void setupColdIndications() {
        for (Meter meter : coldMeterList) {
            Map<String, String> map = new HashMap<>();
            map.put("name", meter.getName());
            map.put("value", "0");
            map.put("uuid", meter.getUuid().toString());
            listForColdRecycler.add(map);
        }
        coldCurrentAdapter.setDataSet(listForColdRecycler);
        coldCurrentAdapter.notifyDataSetChanged();

    }

    private ArrayList<String> getMonth(int year) {
        ArrayList<String> rez = new ArrayList<>();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String[] months = getResources().getStringArray(R.array.month);
        if (year == calendar.get(Calendar.YEAR)) {
            int month = calendar.get(Calendar.MONTH);
            for (int i = 0; i < month; i++) {
                rez.add(months[i]);
            }
        } else {
            rez.addAll(Arrays.asList(months));
        }

        return rez;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    private ContentValues getContentValues(Indication ind) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.IndTable.Cols.YEAR, ind.getYear());
        values.put(DbSchema.IndTable.Cols.MONTH, ind.getMonth());
        values.put(DbSchema.IndTable.Cols.METER_UUID, ind.getMeterUuid());
        values.put(DbSchema.IndTable.Cols.VALUE, ind.getValue());
        values.put(DbSchema.IndTable.Cols.UUID, ind.getUuid().toString());

        return values;
    }

    private Indication findIndication(int year, int month, String meterUuid) {
        Indication rez = null;
        List<Indication> indList = Utils.getIndicationsList(0, getContext().getApplicationContext());
        for (Indication indication: indList) {
            if (indication.getMeterUuid().equals(meterUuid)) {
                if (indication.getYear()==year && indication.getMonth() == month) {
                    rez = indication;
                }
            }
        }
        return rez;
    }
}
