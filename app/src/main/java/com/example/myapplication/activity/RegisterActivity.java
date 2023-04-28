package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final int SECRET_KEY = 99;
    private static final String PREF_KEY = Objects.requireNonNull(RegisterActivity.class.getPackage()).toString();
    EditText registerUserNameEditText;
    EditText registerUserEmailEditText;
    EditText registerUserPasswordEditText;
    EditText registerUserConfirmPasswordEditText;
    EditText registerUserPhoneEditText;
    EditText registerAddressEditText;
    Spinner spinner;
    RadioGroup registerUserType;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if(secret_key != 99) {
            finish();
        }

        registerUserNameEditText = findViewById(R.id.registerUserNameEditText);
        registerUserEmailEditText = findViewById(R.id.registerUserEmailEditText);
        registerUserPasswordEditText = findViewById(R.id.registerUserPasswordEditText);
        registerUserConfirmPasswordEditText = findViewById(R.id.registerUserConfirmPasswordEditText);
        registerUserPhoneEditText = findViewById(R.id.registerUserPhoneNumberEditText);
        spinner = findViewById(R.id.registerUserSpinner);
        registerAddressEditText = findViewById(R.id.registerUserAddressEditText);
        registerUserType = findViewById(R.id.registerUserTypeGroup);

        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        String userName = sharedPreferences.getString("userName", "");
        String password = sharedPreferences.getString("password", "");

        registerUserEmailEditText.setText(userName);
        registerUserPasswordEditText.setText(password);
        registerUserConfirmPasswordEditText.setText(password);

        spinner.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.phone_dropdown,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        Log.i(LOG_TAG, "onCreate");

    }

    public void register(View view) {


        String registerUserName = registerUserNameEditText.getText().toString();
        String registerEmail = registerUserEmailEditText.getText().toString();
        String registerPassword = registerUserPasswordEditText.getText().toString();
        String registerConfirmPassword = registerUserConfirmPasswordEditText.getText().toString();

        if(!registerPassword.equals(registerConfirmPassword)){
            Log.e(LOG_TAG, "Hibas nem egyenlo a jelszo es a megerosites");
            return;
        }

        String registerUserPhone = registerUserPhoneEditText.getText().toString();
        String registerUserPhoneType = spinner.getSelectedItem().toString();
        String address = registerAddressEditText.getText().toString();

        int checkedId = registerUserType.getCheckedRadioButtonId();
        RadioButton radioButton = registerUserType.findViewById(checkedId);
        String userType = radioButton.getText().toString();

        Log.i(LOG_TAG, "Regisztralt: " + registerUserName + ", Email: " + registerEmail + " ,Jelszo: " + registerPassword);

        mAuth.createUserWithEmailAndPassword(registerEmail, registerPassword).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Log.d(LOG_TAG, "User created successfully");
                    startMenu();

                } else {

                    Log.d(LOG_TAG, "Error while creating user");
                    Toast.makeText(

                            RegisterActivity.this,
                            "Error while creating user: " + task.getException().getMessage(),
                            Toast.LENGTH_LONG

                    ).show();

                }
            }
        });

    }

    public void startMenu(){


        Intent intent = new Intent(this, BillMenuActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onStart() {


        super.onStart();
        Log.i(LOG_TAG, "onStart");

    }

    @Override
    protected void onStop() {


        super.onStop();
        Log.i(LOG_TAG, "onStop");

    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");

    }

    @Override
    protected void onPause() {


        super.onPause();
        Log.i(LOG_TAG, "onPause");

    }

    @Override
    protected void onResume() {


        super.onResume();
        Log.i(LOG_TAG, "onResume");

    }

    @Override
    protected void onRestart(){


        super.onRestart();
        Log.i(LOG_TAG, "onRestart");

    }

    public void cancel(View view) {


        finish();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        String selectedItem = parent.getItemAtPosition(position).toString();
        Log.i(LOG_TAG, selectedItem);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


        //TODO

    }
}