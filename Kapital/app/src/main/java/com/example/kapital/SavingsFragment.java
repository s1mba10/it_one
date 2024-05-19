package com.example.kapital;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class SavingsFragment extends Fragment {

    private EditText amountEditText;
    private EditText durationEditText;
    private Button calculateButton;
    private TextView dailyBalanceTextView;
    private ProgressBar progressBar;

    private double amount;
    private int durationDays;
    private double totalIncome;
    private double totalExpense;
    private Date startDate;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savings, container, false);

        amountEditText = view.findViewById(R.id.amount_edit_text);
        durationEditText = view.findViewById(R.id.duration_edit_text);
        calculateButton = view.findViewById(R.id.calculate_button);
        dailyBalanceTextView = view.findViewById(R.id.daily_balance_text_view);
        progressBar = view.findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            calculateButton.setEnabled(true);
        }

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateDailyBalance();
            }
        });

        return view;
    }

    private void calculateDailyBalance() {
        // Получение введенных пользователем данных
        try {
            amount = Double.parseDouble(amountEditText.getText().toString());
            durationDays = Integer.parseInt(durationEditText.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        // Установка даты начала на текущую дату
        startDate = new Date();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db = FirebaseFirestore.getInstance();
            CollectionReference operationsRef = db.collection("operations");

            operationsRef.whereEqualTo("userId", userId)
                    .whereGreaterThanOrEqualTo("date", new Timestamp(startDate))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.d("TAGTAG", "найдено");
                            totalIncome = 0;
                            totalExpense = 0;
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                double amount = document.getDouble("sum");
                                String type = document.getString("type");
                                if (type != null && type.equals("income")) {
                                    totalIncome += amount;
                                } else {
                                    totalExpense += amount;
                                }
                            }
                            Log.d("TAGTAG", Double.toString(totalIncome));

                            updateUI();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("TAGTAG", "Error getting documents: ", e);
                        }
                    });
        }
    }

    private void updateUI() {
        // Рассчет ежедневного баланса
        double remainingAmount = amount - (totalIncome - totalExpense);
        double dailyBalance = remainingAmount / durationDays;

        // Отображение ежедневного баланса
        DecimalFormat df = new DecimalFormat("#.##");
        dailyBalanceTextView.setText(getString(R.string.daily_balance, df.format(dailyBalance)));

        // Отображение прогресса
        double progress = ((totalIncome - totalExpense) / amount) * 100;
        progressBar.setProgress(30);
    }
}
