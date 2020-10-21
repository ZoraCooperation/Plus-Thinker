package com.zoracooperation.ryan.plusthinker;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ReSetPasswordActivity extends AppCompatActivity {

    public Toolbar mToolbar;
    public FirebaseAuth firebaseAuth;
    public EditText email;
    public ImageView send;
    public TextView title;
    private Boolean isWifiConnected = false, isMobileConnected = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_set_password);

        firebaseAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.reset_page_toolbar);
        title = findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        title.setGravity(Gravity.START);
        title.setText(R.string.reset_password);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        email = findViewById(R.id.email_text);
        send = findViewById(R.id.send);

        /*send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userEmail = email.getText().toString().trim();
                final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ReSetPasswordActivity.this, "Email text field cannot be empty!...", Toast.LENGTH_SHORT).show();
                }else {
                    email.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (userEmail.matches(emailPattern) && editable.length() > 0){
                                firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(ReSetPasswordActivity.this, "Please check your Email Account, If you want reset your password...", Toast.LENGTH_SHORT).show();
                                            sendUserToLoginActivity();
                                        }else {
                                            String message = null;
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                                message = Objects.requireNonNull(task.getException()).getMessage();
                                            }
                                            Toast.makeText(ReSetPasswordActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(ReSetPasswordActivity.this, "Invalid Email address!, Please enter valid email address...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });*/
        /*email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (userEmail.matches(emailPattern) && editable.length() > 0){

                }else {
                    Toast.makeText(ReSetPasswordActivity.this, "Invalid Email address!, Please enter valid email address...", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        send.setOnClickListener(view -> {
            if (checkWifiNetworkConnection()){
                validation();
            }else if (checkMobileNetworkConnection()){
                validation();
            }else {
                showMessage();
            }
        });
    }
    private void validation(){
        final String userEmail = email.getText().toString().trim();
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(userEmail)){
            Toast.makeText(ReSetPasswordActivity.this, "Email text field cannot be empty!...", Toast.LENGTH_SHORT).show();
        }else {
            if (userEmail.matches(emailPattern)){
                firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {

                    if (task.isSuccessful()){
                        Toast.makeText(ReSetPasswordActivity.this, "Please check your Email Account, If you want reset your password...", Toast.LENGTH_SHORT).show();
                        sendUserToLoginActivity();
                    }else {
                        String message = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            message = Objects.requireNonNull(task.getException()).getMessage();
                        }
                        Toast.makeText(ReSetPasswordActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                    }

                });
            }else {
                Toast.makeText(ReSetPasswordActivity.this, "Invalid Email address!, Please enter valid email address...", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private Boolean checkWifiNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()){
            isWifiConnected = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return isWifiConnected;
    }
    private Boolean checkMobileNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()){
            isMobileConnected = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return isMobileConnected;
    }
    private void showMessage(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReSetPasswordActivity.this);
        // Setting Alert Dialog Title
        alertDialogBuilder.setTitle("Connection error!!!");
        // Icon Of Alert Dialog
        alertDialogBuilder.setIcon(R.drawable.ic_wifi);
        // Setting Alert Dialog Message
        alertDialogBuilder.setMessage("This application requires an Internet connection. Please check your connection and try again.");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Ok", (dialogInterface, arg1) -> dialogInterface.cancel());

        /*alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });*/

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void sendUserToLoginActivity(){
        Intent loginIntent = new Intent(ReSetPasswordActivity.this, com.zoracooperation.ryan.plusthinker.LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            sendUserToLoginActivity();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendUserToLoginActivity();
    }
}