package com.gvozditskiy.watermeter.activityNfragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.SQLException;
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
import android.widget.EditText;
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
    Indication ind;
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
        RecyclerView coldRcycler = (RecyclerView) v.findViewById(R.id.frag_add_coldwater_recycler);
        RecyclerView hotRcycler = (RecyclerView) v.findViewById(R.id.frag_add_hotwater_recycler);
        AppCompatSpinner flatSpinner = (AppCompatSpinner) v.findViewById(R.id.frag_add_flat_spinner);

        coldCurrentAdapter = new ColdCurrentAdapter(getContext(), false);
        hotCurrentAdapter = new HotCurrentAdapter(getContext(), false);
        coldRcycler.setLayoutManager(new LinearLayoutManager(getContext()));
        hotRcycler.setLayoutManager(new LinearLayoutManager(getContext()));
        coldRcycler.setAdapter(coldCurrentAdapter);
        hotRcycler.setAdapter(hotCurrentAdapter);
        coldCurrentAdapter.setDataSet(listForColdRecycler);
        hotCurrentAdapter.setDataSet(listForHotRecycler);

        ind = new Indication();
        final List<Integer> yearsList = new ArrayList<>();
        final List<Indication> indList = new ArrayList<>();
        List<String> flatNameList = new ArrayList<>();
        flatList = Utils.getFlatList(getContext().getApplicationContext());
        for (Flat flat : flatList) {
            flatNameList.add(flat.getName());

        }

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
                for (Meter meter:coldMeterList) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", meter.getName());
                    map.put("value", "0");
                    map.put("uuid", meter.getUuid().toString());
                    listForColdRecycler.add(map);
                    coldCurrentAdapter.notifyDataSetChanged();
                }
                coldCurrentAdapter.setDataSet(listForColdRecycler);

                for (Meter meter:hotMeterList) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", meter.getName());
                    map.put("value", "0");
                    map.put("uuid", meter.getUuid().toString());
                    listForHotRecycler.add(map);
                    hotCurrentAdapter.notifyDataSetChanged();
                }
                hotCurrentAdapter.setDataSet(listForHotRecycler);
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
                indList.clear();
                indList.addAll(Utils.getIndicationsList(yearsList.get(i), getContext()));
                ind.setYear(yearsList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinYear.setSelection(0, true);

        spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boolean b = false;
                for (Indication ind : indList) {
                    if (ind.getYear() == selectedYear && ind.getMonth() == i) {
                        b = true;
                    }
                }
                if (!b) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dialog = new AlertDialog.Builder(getContext())
                .setView(v)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase mDatabase = new BaseHelper(getContext()).getWritableDatabase();
//                        mDatabase.insert(DbSchema.IndTable.NAME, null, getContentValues(ind));
                        List<Indication> coldIndList = coldCurrentAdapter.getIndicationsList();
                        List<Indication> hotIndList = hotCurrentAdapter.getIndicationsList();
                        coldIndList.size();
                        for (int k=0; k<coldIndList.size(); k++) {
                            coldIndList.get(k).setYear(selectedYear);
                            coldIndList.get(k).setMonth(spinMonth.getSelectedItemPosition());
                            mDatabase.insert(DbSchema.IndTable.NAME, null, getContentValues(coldIndList.get(k)));
                        }
                        for (int k=0; k<hotIndList.size(); k++) {
                            hotIndList.get(k).setYear(selectedYear);
                            hotIndList.get(k).setMonth(spinMonth.getSelectedItemPosition());
                            mDatabase.insert(DbSchema.IndTable.NAME, null, getContentValues(hotIndList.get(k)));
                        }

                        Toast.makeText(getContext(), "Данные добавлены", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return dialog;
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
}
