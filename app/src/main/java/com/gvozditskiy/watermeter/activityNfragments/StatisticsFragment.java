package com.gvozditskiy.watermeter.activityNfragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.LineChartView;
import com.gvozditskiy.watermeter.Indication;
import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {


    public StatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Indication> list = new ArrayList<>();
        list.addAll(Utils.getIndicationsList(0, getContext()));
        Log.d("boom", String.valueOf(list.size()));
        List<Integer> deltaCold = new ArrayList<>();
        List<Integer> deltaHot = new ArrayList<>();
        List<Integer> summary = new ArrayList<>();
        List<String> row = new ArrayList<>();
        LineSet coldSet = new LineSet();
        LineSet hotSet = new LineSet();
        LineSet sumSet = new LineSet();

        BarSet coldBarSet = new BarSet();
        BarSet hotBarSet = new BarSet();
        BarSet sumBarSet = new BarSet();
        if (list.size() > 1) {
            for (int i = 1; i < list.size(); i++) {
                deltaCold.add(list.get(i).getCold() - list.get(i - 1).getCold());
                deltaHot.add(list.get(i).getHot() - list.get(i - 1).getHot());
                summary.add(list.get(i).getCold() - list.get(i - 1).getCold() +
                        list.get(i).getHot() - list.get(i - 1).getHot());
                row.add(String.valueOf(list.get(i).getMonth()) + "." + String.valueOf(list.get(i).getYear()));
                String label = String.valueOf(list.get(i).getMonth() + 1) + "." + String.valueOf(list.get(i).getYear());
                int cold = Math.abs(list.get(i).getCold() - list.get(i - 1).getCold());
                int hot = Math.abs(list.get(i).getHot() - list.get(i - 1).getHot());
                int sum = Math.abs(list.get(i).getCold() - list.get(i - 1).getCold()) +
                        Math.abs(list.get(i).getHot() - list.get(i - 1).getHot());
                coldSet.addPoint(label, cold);
                hotSet.addPoint(label, hot);
                sumSet.addPoint(label, sum);
                coldBarSet.addBar(label, cold);
                hotBarSet.addBar(label, hot);
                sumBarSet.addBar(label, sum);
            }

        }
        int greenColor = getResources().getColor(R.color.summ);
        int redColor = getResources().getColor(R.color.hotwater);
        int blueColor = getResources().getColor(R.color.coldwater);
        int blueColorA = Color.argb(50, 129, 212, 250);
        int redColorA = Color.argb(50, 239, 154, 154);



        LineChartView chartView = (LineChartView) view.findViewById(R.id.frag_stat_linechart);
        coldSet.setColor(blueColor);
        coldSet.setDotsRadius(18);
        coldSet.setDotsColor(blueColor);
        hotSet.setDotsColor(redColor);
        sumSet.setDotsColor(greenColor);
        hotSet.setDotsRadius(18);
        sumSet.setDotsRadius(18);
        hotSet.setColor(redColor);
        sumSet.setColor(greenColor);
        hotSet.setFill(redColorA);
        coldSet.setFill(blueColorA);
        chartView.addData(coldSet);
        chartView.addData(hotSet);
        chartView.addData(sumSet);
        chartView.show();

        BarChartView bsrChartView = (BarChartView) view.findViewById(R.id.frag_stat_barchart);
        coldBarSet.setColor(blueColor);
        hotBarSet.setColor(redColor);
        sumBarSet.setColor(greenColor);
        bsrChartView.setBarSpacing(300);
        bsrChartView.setSetSpacing(24);
        bsrChartView.addData(coldBarSet);
        bsrChartView.addData(hotBarSet);
        bsrChartView.addData(sumBarSet);
        bsrChartView.show();

    }
}
