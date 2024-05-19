package com.example.kapital;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class CreateIncomeFragment extends Fragment {
    AppCompatButton createButton;
    EditText sumET;
    RadioGroup category;
    RadioButton salary, business, other;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_income, container, false);

        sumET = view.findViewById(R.id.sum);
        category = view.findViewById(R.id.category);
        salary = view.findViewById(R.id.salary);
        business = view.findViewById(R.id.business);
        other = view.findViewById(R.id.other);
        createButton = view.findViewById(R.id.create_income_button);

        mAuth = FirebaseAuth.getInstance();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIncome();
            }
        });

        return view;
    }

    private void createIncome() {
        double sum = Double.parseDouble(sumET.getText().toString());
        String category;
        if (salary.isChecked()) {
            category = "Зарплата";
        } else if (business.isChecked()){
            category = "Бизнес";
        } else if (other.isChecked()){
            category = "Другое";
        } else{
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Date date = new Date();

        Map<String, Object> income = new HashMap<>();
        income.put("sum", sum);
        income.put("category", category);
        income.put("userId", userId);
        income.put("date", date);
        income.put("type", "income");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("operations")
                .add(income)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(requireContext(), "Доход добавлен!", Toast.LENGTH_SHORT).show();
                        sumET.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error", e);
                    }
                });
    }
}
