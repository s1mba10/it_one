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

public class CreateOperationFragment extends Fragment {
    AppCompatButton createButton;
    EditText sumET;
    RadioGroup category;
    RadioButton food, fun, other;
    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_operation, container, false);

        sumET = view.findViewById(R.id.sum);
        category = view.findViewById(R.id.category);
        food = view.findViewById(R.id.food);
        fun = view.findViewById(R.id.fun);
        other = view.findViewById(R.id.other);
        createButton = view.findViewById(R.id.create_operation_button);

        mAuth = FirebaseAuth.getInstance();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOperation();
            }
        });

        return view;
    }

    private void createOperation() {
        double sum = Double.parseDouble(sumET.getText().toString());
        String category;
        if (food.isChecked()) {
            category = "Еда";
        } else if (fun.isChecked()){
            category = "Развлечения";
        } else if (other.isChecked()){
            category = "Другое";
        } else{
            return;
        }


        Map<String, Object> operations = new HashMap<>();
        operations.put("sum", sum);
        operations.put("category", category);

        // Добавляем объявление в коллекцию "ads" в Cloud Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("operations")
                .add(operations)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                        // Объявление успешно добавлено
                        Toast.makeText(requireContext(), "Трата добавлена!", Toast.LENGTH_SHORT).show();
                        sumET.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error", e);
                        // Возникла ошибка при добавлении объявления
                        // Вы можете обработать эту ошибку, например, отобразить сообщение об ошибке
                    }
                });
    }
}
