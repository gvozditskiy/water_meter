package com.gvozditskiy.watermeter.activityNfragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.LineChartView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.gvozditskiy.watermeter.Flat;
import com.gvozditskiy.watermeter.Indication;
import com.gvozditskiy.watermeter.Meter;
import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.gvozditskiy.watermeter.Utils.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {
    //    AppCompatSpinner spinner;
    AppCompatSpinner flatSpinner;
    List<Flat> flats = new ArrayList<>();
    List<Meter> meters = new ArrayList<>();
    List<Integer> deltaCold = new ArrayList<>();
    List<Integer> deltaHot = new ArrayList<>();
    List<Integer> deltaSum = new ArrayList<>();
    List<Integer> summary = new ArrayList<>();
    List<String> row = new ArrayList<>();
    List<Indication> indications = new ArrayList<>();

    LineChart lineChart;

    int greenColor;
    int redColor;
    int blueColor;
    int blueColorA;
    int redColorA;


    public StatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(R.string.frag_stat_name);
        flats.addAll(Utils.getFlatList(getContext().getApplicationContext()));
        indications.addAll(Utils.getIndicationsList(0, getContext()));
        meters.addAll(Utils.getMeterLsit(getContext().getApplicationContext()));

        greenColor = getResources().getColor(R.color.summ);
        redColor = getResources().getColor(R.color.hotwater);
        blueColor = getResources().getColor(R.color.coldwater);
        blueColorA = Color.argb(50, 129, 212, 250);
        redColorA = Color.argb(50, 239, 154, 154);
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineChart = (LineChart) view.findViewById(R.id.frag_stat_linechart);
//        spinner = (AppCompatSpinner) view.findViewById(R.id.frag_stat_spinner);
        flatSpinner = (AppCompatSpinner) view.findViewById(R.id.frag_stat_spinner_flats);
//        try {
//            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
////                    switch (i) {
////                        case 0:
////                            if (Utils.getIndicationsList(0, getContext()).size() > 1) {
////                                chartView.setVisibility(View.VISIBLE);
////                                bsrChartView.setVisibility(View.GONE);
////                                chartView.show();
////                            }
////                            break;
////                        case 1:
////                            if (Utils.getIndicationsList(0, getContext()).size() > 1) {
////                                chartView.setVisibility(View.GONE);
////                                bsrChartView.setVisibility(View.VISIBLE);
////                                bsrChartView.show();
////                            }
////                            break;
////                    }
//                    setupChart();
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> adapterView) {
//
//                }
//            });
//            if (savedInstanceState != null) {
//                spinner.setSelection(savedInstanceState.getInt("chart_pos", 0));
//                flatSpinner.setSelection(savedInstanceState.getInt("flats_pos", 0));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        List<String> flatNames = new ArrayList<>();
        for (Flat flat : flats) {
            flatNames.add(flat.getName());
        }
        SpinnerAdapter flatsAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, flatNames);
        flatSpinner.setAdapter(flatsAdapter);
        flatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //// TODO: 19.01.2017 обновить данные для графиков
                setupChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        flatSpinner.setSelection(0);

        if (savedInstanceState == null) {
            setupChart();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt("chart_pos", spinner.getSelectedItemPosition());
        outState.putInt("flats_pos", flatSpinner.getSelectedItemPosition());
    }

    private void setupChart() {
        String flatId = flats.get(flatSpinner.getSelectedItemPosition()).getUuid().toString();
        List<Meter> coldMeters = new ArrayList<>();
        List<Meter> hotMeters = new ArrayList<>();
        List<Indication> coldIndications = new ArrayList<>();
        List<Indication> hotIndications = new ArrayList<>();
        for (Meter meter : meters) {
            if (meter.getFlatUUID().equals(flatId)) {
                if (meter.getType().equals(Meter.TYPE_COLD)) {
                    coldMeters.add(meter);
                    for (Indication ind : indications) {
                        if (ind.getMeterUuid().equals(meter.getUuid().toString())) {
                            coldIndications.add(ind);
                        }
                    }
                } else {
                    hotMeters.add(meter);
                    for (Indication ind : indications) {
                        if (ind.getMeterUuid().equals(meter.getUuid().toString())) {
                            hotIndications.add(ind);
                        }
                    }
                }
            }
        }


        List<Indication> newColdIndications = new ArrayList<>();
        List<Indication> newHotIndications = new ArrayList<>();

        Set<Integer> years = new HashSet<>();

        for (Indication ind : indications) {
            years.add(ind.getYear());
        }

        Set<Integer> months = new HashSet<>();

        for (int year : years) {
            months.clear();
            for (Indication ind : indications) {
                if (ind.getYear() == year) {
                    months.add(ind.getMonth());
                }
            }

            for (int month : months) {
                int sum = 0;
                for (Indication ind : coldIndications) {
                    if (ind.getYear() == year && ind.getMonth() == month) {
                        sum += ind.getValue();
                    }
                }
                Indication newInd = new Indication();
                newInd.setValue(sum);
                newInd.setYear(year);
                newInd.setMonth(month);
                newColdIndications.add(newInd);

                sum = 0;
                for (Indication ind : hotIndications) {
                    if (ind.getYear() == year && ind.getMonth() == month) {
                        sum += ind.getValue();
                    }
                }

                newInd = new Indication();
                newInd.setValue(sum);
                newInd.setYear(year);
                newInd.setMonth(month);
                newHotIndications.add(newInd);
            }
        }


        //отсортировать показания
        sortByMonth(newColdIndications);
        sortByYear(newColdIndications);
        sortByMonth(newHotIndications);
        sortByYear(newHotIndications);


        deltaCold.clear();
        deltaHot.clear();
        deltaSum.clear();
        row.clear();

        //посчитать дельты
        List<Entry> coldLineSet = new ArrayList<>();
        List<Entry> hotLineSet = new ArrayList<>();
        List<Entry> sumLineSet = new ArrayList<>();

        if (coldIndications.size() > 0) {

            for (int i = 1; i < newColdIndications.size(); i++) {
                deltaCold.add(Math.abs(newColdIndications.get(i).getValue() - newColdIndications.get(i - 1).getValue()));
                deltaHot.add(Math.abs(newHotIndications.get(i).getValue() - newHotIndications.get(i - 1).getValue()));
                deltaSum.add(deltaCold.get(i - 1) + deltaHot.get(i - 1));
                row.add(String.valueOf(newColdIndications.get(i).getMonth() + 1) + "." + String.valueOf(newColdIndications.get(i).getYear()).substring(2, 4));

//                coldBarSet.addBar(row.get(i-1),deltaCold.get(i-1));
//                hotBarSet.addBar(row.get(i-1),deltaHot.get(i-1));
//                sumBarSet.addBar(row.get(i-1),deltaSum.get(i-1));

                Entry coldEntry = new Entry((float) i - 1, (float) deltaCold.get(i - 1));
                coldLineSet.add(coldEntry);
                Entry hotEntry = new Entry((float) i - 1, (float) deltaHot.get(i - 1));
                hotLineSet.add(hotEntry);
                Entry sumEntry = new Entry((float) i - 1, (float) deltaSum.get(i - 1));
                sumLineSet.add(sumEntry);


            }

            setupChartStyle(coldLineSet, hotLineSet, sumLineSet);
        } else {
            lineChart.clear();
            lineChart.setDescription(null);
            lineChart.notifyDataSetChanged();
            lineChart.invalidate(); // refresh
        }


    }

    private void setupChartStyle(List<Entry> coldLineSet, List<Entry> hotLineSet, List<Entry> sumLineSet) {
        LineDataSet colddataSet = new LineDataSet(coldLineSet, "Холодная вода"); // add entries to dataset
        colddataSet.setColor(blueColor);

        LineDataSet hotdataSet = new LineDataSet(hotLineSet, "Горячая вода"); // add entries to dataset
        hotdataSet.setColor(redColor);

        LineDataSet sumdataSet = new LineDataSet(sumLineSet, "Суммарный расход"); // add entries to dataset
        sumdataSet.setColor(greenColor);

        colddataSet.setLineWidth(3f);
        hotdataSet.setLineWidth(3f);
        sumdataSet.setLineWidth(3f);

        colddataSet.setCircleRadius(5f);
        colddataSet.setCircleHoleRadius(3f);
        colddataSet.setCircleColor(blueColor);

        hotdataSet.setCircleRadius(5f);
        hotdataSet.setCircleHoleRadius(3f);
        hotdataSet.setCircleColor(redColor);

        sumdataSet.setCircleRadius(5f);
        sumdataSet.setCircleHoleRadius(3f);
        sumdataSet.setCircleColor(greenColor);

        LineData lineData = new LineData(colddataSet);
        lineData.addDataSet(hotdataSet);
        lineData.addDataSet(sumdataSet);

        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(row.toArray(new String[row.size()])));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        lineChart.setDescription(null);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate(); // refresh
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }


        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }

    }

}
