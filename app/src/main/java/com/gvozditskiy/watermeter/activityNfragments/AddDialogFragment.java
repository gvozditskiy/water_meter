package com.gvozditskiy.watermeter.activityNfragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gvozditskiy.watermeter.Indication;
import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.Utils;
import com.gvozditskiy.watermeter.database.BaseHelper;
import com.gvozditskiy.watermeter.database.DbSchema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Alexey on 26.12.2016.
 */

public class AddDialogFragment extends DialogFragment {
    int selectedYear;
    Indication ind;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.frag_dialog_add, null, false);
        Spinner spinYear = (Spinner) v.findViewById(R.id.frag_add_year_spinner);
        final Spinner spinMonth = (Spinner) v.findViewById(R.id.frag_add_month_spinner);
        final EditText cold = (EditText) v.findViewById(R.id.frag_add_coldwater_recycler);
        final EditText hot = (EditText) v.findViewById(R.id.frag_add_hotwater_recycler);
        ind = new Indication();
        final List<Integer> yearsList = new ArrayList<>();
        List<Integer> monthList = new ArrayList<>();
        final List<Indication> indList = new ArrayList<>();

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
                        cold.setText(String.valueOf(ind.getCold()));
                        hot.setText(String.valueOf(ind.getHot()));
                        b = true;
                    }
                }
                if (!b) {
                    cold.setText("");
                    hot.setText("");
                }
                ind.setMonth(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return new AlertDialog.Builder(getContext())
                .setView(v)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int c = 0;
                        if (!cold.getText().toString().equals("")) {
                            c = Integer.parseInt(cold.getText().toString());
                        }
                        int h = 0;
                        if (!hot.getText().toString().equals("")) {
                            h = Integer.parseInt(hot.getText().toString());
                        }
                        ind.setCold(c);
                        ind.setHot(h);
                        SQLiteDatabase mDatabase = new BaseHelper(getContext()).getWritableDatabase();
                        mDatabase.insert(DbSchema.IndTable.NAME, null, getContentValues(ind));
                        Toast.makeText(getContext(), "Данные добавлены", Toast.LENGTH_SHORT).show();

                    }
                })
                .show();
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
        values.put(DbSchema.IndTable.Cols.COLD, ind.getCold());
        values.put(DbSchema.IndTable.Cols.HOT, ind.getHot());
        values.put(DbSchema.IndTable.Cols.YEAR, ind.getYear());
        values.put(DbSchema.IndTable.Cols.MONTH, ind.getMonth());

        return values;
    }
}
