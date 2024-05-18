package com.example.kapital;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.start_page, new StartFragment())
                .commit();




    }
}