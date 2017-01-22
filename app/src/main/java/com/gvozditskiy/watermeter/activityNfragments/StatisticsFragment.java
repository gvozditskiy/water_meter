package com.gvozditskiy.watermeter.activityNfragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.gvozditskiy.watermeter.Flat;
import com.gvozditskiy.watermeter.Indication;
import com.gvozditskiy.watermeter.Meter;
import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.gvozditskiy.watermeter.Utils.sortByMonth;
import static com.gvozditskiy.watermeter.Utils.sortByYear;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {
    //    AppCompatSpinner spinner;
    RadioGroup radioGroup;
    TextView flatName;
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
        flatName = (TextView) view.findViewById(R.id.frag_stat_flats_caption);
        radioGroup = (RadioGroup) view.findViewById(R.id.frag_prof_radiogroup);
        lineChart = (LineChart) view.findViewById(R.id.frag_stat_linechart);
//        spinner = (AppCompatSpinner) view.findViewById(R.id.frag_stat_spinner);

        setupRadioGroup();

        if (savedInstanceState == null) {
            setupChart();
        }
    }

    private void setupRadioGroup() {
        final List<Flat> flatList = Utils.getFlatList(getContext());
        int id = 0;

        radioGroup.removeAllViews();

        for (Flat flat : flatList) {
            RadioButton rBtn = new RadioButton(getContext());
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(150, 150);
            params.setMargins(20, 20, 20, 20);
            rBtn.setLayoutParams(params);
            rBtn.setId(id++);
//                            rBtn.setText(flat.getName());
            rBtn.setButtonDrawable(null);
            rBtn.setBackground(getResources().getDrawable(R.drawable.flat_selector));
            radioGroup.addView(rBtn);
        }

        try {
            radioGroup.setOnCheckedChangeListener(null);
            radioGroup.clearCheck();
        } catch (Exception e) {
            e.printStackTrace();
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i>0) {
                    flatName.setText(flatList.get(i).getName());
                } else {
                    flatName.setText(flatList.get(0).getName());
                }
                setupChart();
            }
        });

        radioGroup.clearCheck();
        radioGroup.check(0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt("chart_pos", spinner.getSelectedItemPosition());
        outState.putInt("flats_pos", radioGroup.getCheckedRadioButtonId());
    }

    private void setupChart() {
        String flatId = "";
        try {
            flatId = flats.get(radioGroup.getCheckedRadioButtonId()).getUuid().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        for (Indication ind : coldIndications) {
            years.add(ind.getYear());
        }
        for (Indication ind : hotIndications) {
            years.add(ind.getYear());
        }

        Set<Integer> months = new HashSet<>();

        for (int year : years) {
            months.clear();
            for (Indication ind : coldIndications) {
                if (ind.getYear() == year) {
                    months.add(ind.getMonth());
                }
            }
            for (Indication ind : hotIndications) {
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

        if (newColdIndications.size() > 1) {

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
            try {
                lineChart.clear();
                lineChart.setDescription(null);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate(); // refresh
            } catch (Exception e) {
                e.printStackTrace();
            }

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

        try {
            lineChart.setData(lineData);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new MyXAxisValueFormatter(row.toArray(new String[row.size()])));
            xAxis.setGranularity(1f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawAxisLine(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            lineChart.setDescription(null);

            lineChart.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            lineChart.invalidate(); // refresh
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }


        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (mValues != null & value < mValues.length && value > 0) {
                return mValues[(int) value];
            } else {
                return "";
            }
        }

    }

}
