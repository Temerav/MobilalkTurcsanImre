package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BillCalculatorActivity extends AppCompatActivity {

    private TextView prizeTextView;
    private EditText entered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_calculator);

        entered = findViewById(R.id.billEditText);
        prizeTextView = findViewById(R.id.billPrizeTextView);

    }


    public void calculator(View view) {

        String prize = String.valueOf(entered.getText());
        int calced = Integer.parseInt(prize) * 36;
        prizeTextView.setText("Befizetendo " + calced + " FT");

    }
}