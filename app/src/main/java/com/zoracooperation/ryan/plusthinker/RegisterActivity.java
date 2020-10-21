package com.zoracooperation.ryan.plusthinker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    public EditText userEmail, userPassword, userConfirmPassword;
    public Button createUserAccount;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;
    private Boolean isWifiConnected = false, isMobileConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        userEmail = findViewById(R.id.register_email);
        userPassword = findViewById(R.id.register_password);
        userConfirmPassword = findViewById(R.id.register_confirm_password);
        createUserAccount = findViewById(R.id.register_create_account);

        loadingBar = new ProgressDialog(this);

        createUserAccount.setOnClickListener(view -> {
            if (checkWifiNetworkConnection()){
                createNewAccount();
            }else if (checkMobileNetworkConnection()){
                createNewAccount();
            }else {
                showMessage();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null){
            if (currentUser.isEmailVerified()){
                sendUserToMainActivity();
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
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
    private void sendUserToMainActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, com.zoracooperation.ryan.plusthinker.MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void createNewAccount() {
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();
        String confirmPassword = userConfirmPassword.getText().toString().trim();
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write your email...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(this, "Please write your confirm password...", Toast.LENGTH_SHORT).show();
        }else if (!password.equals(confirmPassword)){
            Toast.makeText(this, "Your password don't match with your confirm password...", Toast.LENGTH_SHORT).show();
        }else {

            if (email.matches(emailPattern)){
                loadingBar.setTitle("Creating New Account");
                loadingBar.setMessage("Please wait, while we are creating your new account...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()){
                                        //sendUserToSetupActivity();
                                        sendUserToLoginActivity();
                                        Toast.makeText(RegisterActivity.this, "You are authenticated successfully, Please check your email for verification....", Toast.LENGTH_LONG).show();
                                        userEmail.setText("");
                                        userPassword.setText("");
                                        userConfirmPassword.setText("");
                                    }else {
                                        String message = null;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                            message = Objects.requireNonNull(task1.getException()).getMessage();
                                        }
                                        Toast.makeText(RegisterActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                    loadingBar.dismiss();
                                });
                            }else {
                                String message = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                    message = Objects.requireNonNull(task.getException()).getMessage();
                                }
                                Toast.makeText(RegisterActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        });
            }else {
                Toast.makeText(RegisterActivity.this, "Invalid Email address!, Please enter valid email address...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*private void sendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, SetUpActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }*/

    private void sendUserToLoginActivity(){
        Intent loginIntent = new Intent(RegisterActivity.this, com.zoracooperation.ryan.plusthinker.LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent loginIntent = new Intent(RegisterActivity.this, com.zoracooperation.ryan.plusthinker.LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}