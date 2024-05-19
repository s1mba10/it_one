package com.example.kapital;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AnalyticsFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.analytic_layout, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db = FirebaseFirestore.getInstance();
            CollectionReference operationsRef = db.collection("operations");
            operationsRef.whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            double food = 0, fun = 0, other = 0;
                            Map<String, Double> monthlyIncome = new HashMap<>();
                            Map<String, Double> monthlyExpenses = new HashMap<>();
                            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());

                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                String type = document.getString("type");
                                double amount = document.getDouble("sum");
                                Timestamp timestamp = document.getTimestamp("date");
                                String month = monthFormat.format(((Timestamp) timestamp).toDate());

                                if (type != null && type.equals("income")) {
                                    monthlyIncome.put(month, monthlyIncome.getOrDefault(month, 0.0) + amount);
                                } else {
                                    monthlyExpenses.put(month, monthlyExpenses.getOrDefault(month, 0.0) + amount);
                                    String category = document.getString("category");
                                    if (category != null) {
                                        switch (category) {
                                            case "Еда":
                                                food += amount;
                                                break;
                                            case "Развлечения":
                                                fun += amount;
                                                break;
                                            default:
                                                other += amount;
                                                break;
                                        }
                                    }
                                }
                            }

                            setupPieChart(view, food, fun, other);
                            setupBarChart(view, monthlyIncome, monthlyExpenses);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("TAGTAG", "Error getting documents: " + e.getMessage());
                        }
                    });
        }
        return view;
    }

    private void setupPieChart(View view, double food, double fun, double other) {
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
        pieChart.setDrawEntryLabels(true);

        pieChart.getLegend().setEnabled(false);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(com.google.android.material.R.color.material_dynamic_neutral10);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setHoleRadius(50f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.animateY(1000);
        dataSet.setColors(Color.rgb(117, 10, 10), Color.rgb(26, 89, 32), Color.rgb(8, 24, 126));
        pieChart.invalidate();
    }

    private void setupBarChart(View view, Map<String, Double> monthlyIncome, Map<String, Double> monthlyExpenses) {
        BarChart barChart = view.findViewById(R.id.bar_chart);
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int index = 0;
        for (String month : monthlyExpenses.keySet()) {
            double expense = monthlyExpenses.getOrDefault(month, 0.0);
            double income = monthlyIncome.getOrDefault(month, 0.0);
            double balance = income - expense;
            entries.add(new BarEntry(index, (float) balance));
            labels.add(month);
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Разница между доходами и расходами");

        ArrayList<Integer> colors = new ArrayList<>();
        for (BarEntry entry : entries) {
            if (entry.getY() >= 0) {
                colors.add(Color.rgb(26, 89, 32));
                 // Зеленый для положительной разницы
            } else {
                colors.add(Color.rgb(117, 10, 10));// Красный для отрицательной разницы
            }
        }

        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setAxisMinimum(Math.min(0, barChart.getAxisLeft().getAxisMinimum())); // Устанавливаем минимум оси Y на ноль или меньше
        barChart.getAxisLeft().setAxisMaximum(Math.max(0, barChart.getAxisLeft().getAxisMaximum())); // Устанавливаем максимум оси Y на ноль или больше
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}
