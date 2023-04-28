package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.util.RandomAsyncLoader;
import com.example.myapplication.util.RandomAsyncTask;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {


    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = Objects.requireNonNull(MainActivity.class.getPackage()).toString();
    private static final int RC_SIGN_IN = 973;
    private static final int SECRET_KEY = 99;
    EditText userNameET;
    EditText passwordET;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userNameET = findViewById(R.id.editTextUserName);
        passwordET = findViewById(R.id.editTextPassword);

        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mClient = GoogleSignIn.getClient(this, googleSignInOptions);
//          Random async
//        Button button = findViewById(R.id.loginAnonymusButton);
//        new RandomAsyncTask(button).execute();

        // async loader
        getSupportLoaderManager().restartLoader(0, null,this);
        Log.i(LOG_TAG, "onCreate");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){


        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(LOG_TAG, "Firebase auth with Google: " + account.getId());
                fireBaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException ex) {

                Log.w(LOG_TAG, "Google sign in failed", ex);

            }

        }

    }

    private void fireBaseAuthWithGoogle(String idToken) {


        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(

                this,
                new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    Log.d(LOG_TAG, "Login with Google was successfully");
                    startMenu();

                } else {

                    Log.w(LOG_TAG, "Error while login with Google", task.getException());

                }

            }

        });

    }

    public void login(View view) {


        String userName = userNameET.getText().toString();
        String password = passwordET.getText().toString();

        mAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(

                this,
                new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    Log.i(LOG_TAG, "Login was successfully");
                    startMenu();

                } else {

                    Log.d(LOG_TAG, "Error while login with user");
                    Toast.makeText(

                            MainActivity.this,
                            "Error while login with user: " + task.getException().getMessage(),
                            Toast.LENGTH_LONG

                    ).show();

                }

            }

        });

    }

    public void register(View view) {


        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);

    }

    public void startMenu(){


        Intent intent = new Intent(this, BillMenuActivity.class);
        startActivity(intent);

    }

    public void loginGuest(View view) {


        mAuth.signInAnonymously().addOnCompleteListener(

                this,
                new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    Log.i(LOG_TAG, "Login as anonymous was successfully");
                    startMenu();

                } else {

                    Log.d(LOG_TAG, "Error while login with anonymous user");
                    Toast.makeText(

                            MainActivity.this,
                            "Error while login with user: " + task.getException().getMessage(),
                            Toast.LENGTH_LONG

                    ).show();

                }

            }

        });

    }

    public void loginGoogle(View view) {


        Intent signInIntent = mClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userNameET.getText().toString());
        editor.putString("password", passwordET.getText().toString());
        editor.apply();

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

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {


        return new RandomAsyncLoader(this);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {


        Button button = findViewById(R.id.loginAnonymusButton);
        button.setText(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}