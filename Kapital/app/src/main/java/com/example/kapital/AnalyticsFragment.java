package com.example.kapital;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;

public class AnalyticsFragment extends Fragment {
    private double food=1000, fun=760, other=230;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.analytic_layout, container, false);
        PieChart pieChart = view.findViewById(R.id.pie_chart);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) food, "Еда"));
        entries.add(new PieEntry((float) fun, "Развлечения"));
        entries.add(new PieEntry((float) other, "Другое"));
        PieDataSet dataSet = new PieDataSet(entries, "");


        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setDrawEntryLabels(false);
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(com.google.android.material.R.color.material_dynamic_neutral10);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setHoleRadius(50f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.animateY(2000);
        dataSet.setColors(Color.rgb(255, 0, 0), Color.rgb(0, 255, 0), Color.rgb(0, 0, 255), Color.rgb(255, 255, 0));
        pieChart.invalidate();




        return view;
    }
}
