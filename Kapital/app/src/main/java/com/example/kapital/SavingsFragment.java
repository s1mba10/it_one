package com.example.kapital;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.Calendar;

public class SavingsFragment extends Fragment {

    private EditText amountEditText;
    private EditText durationEditText;
    private AppCompatButton calculateButton;
    private TextView monthlyBalanceTextView;
    private ProgressBar progressBar;

    private double amount;
    private int durationMonths;
    private double totalIncome;
    private double totalExpense;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savings, container, false);

        amountEditText = view.findViewById(R.id.amount_edit_text);
        durationEditText = view.findViewById(R.id.duration_edit_text);
        calculateButton = view.findViewById(R.id.calculate_button);
        monthlyBalanceTextView = view.findViewById(R.id.monthly_balance_text_view);
        progressBar = view.findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db = FirebaseFirestore.getInstance();
            CollectionReference operationsRef = db.collection("operations");

            operationsRef.whereEqualTo("userId", userId)
                    .whereGreaterThanOrEqualTo("date", getFirstDayOfMonth())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            totalIncome = 0;
                            totalExpense = 0;
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                double amount = document.getDouble("amount");
                                String type = document.getString("type");
                                if (type != null && type.equals("Доход")) {
                                    totalIncome += amount;
                                } else {
                                    totalExpense += amount;
                                }
                            }
                            calculateButton.setEnabled(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            calculateButton.setEnabled(false);
                            // Обработка ошибки получения данных из базы данных
                        }
                    });
        }

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateMonthlyBalance();
            }
        });

        return view;
    }

    private void calculateMonthlyBalance() {
        // Получение введенных пользователем данных
        try {
            amount = Double.parseDouble(amountEditText.getText().toString());
            durationMonths = Integer.parseInt(durationEditText.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        // Рассчет месячного баланса
        double monthlyBalance = (amount - totalIncome + totalExpense) / durationMonths;

        // Отображение месячного баланса
        DecimalFormat df = new DecimalFormat("#.##");
        monthlyBalanceTextView.setText(getString(R.string.monthly_balance, df.format(monthlyBalance)));

        // Отображение прогресса
        double progress = ((amount - totalIncome + totalExpense) / amount) * 100;
        progressBar.setProgress((int) progress);
    }

    private Calendar getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
