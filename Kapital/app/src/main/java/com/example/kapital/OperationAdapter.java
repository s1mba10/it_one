package com.example.kapital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.widget.ArrayAdapter;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class OperationAdapter extends ArrayAdapter<DocumentSnapshot> {
    private Context context;

    public OperationAdapter(@NonNull Context context, @NonNull List<DocumentSnapshot> operations) {
        super(context, 0, operations);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.operation_item, parent, false);
        }

        DocumentSnapshot documentSnapshot = getItem(position);

        TextView sumTextView = convertView.findViewById(R.id.sum_text_view);
        TextView categoryTextView = convertView.findViewById(R.id.category_text_view);
        TextView dateTextView = convertView.findViewById(R.id.date_text_view);

        double sum = documentSnapshot.getDouble("sum");
        String category = documentSnapshot.getString("category");
        String type = documentSnapshot.getString("type");
        Date date = documentSnapshot.getDate("date");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String dateString = dateFormat.format(date);

        sumTextView.setText(String.valueOf(sum));
        categoryTextView.setText(category);
        dateTextView.setText(dateString);

        int color = type.equals("income") ? ContextCompat.getColor(context, R.color.green) : ContextCompat.getColor(context, R.color.red);
        convertView.setBackgroundColor(color);

        return convertView;
    }
}
