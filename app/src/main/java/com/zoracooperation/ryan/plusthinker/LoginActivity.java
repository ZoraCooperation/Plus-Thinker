package com.zoracooperation.ryan.plusthinker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    public Button loginButton;
    public EditText userEmail, userPassword;
    public TextView needNewAccountLink, forgetLoginLink;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;
    public SignInButton google_signIn;
    public LoginButton facebook_signIn;
    private static final int RC_SIGN_IN = 1;
    //private GoogleApiClient mGoogleSignInClient;
    private GoogleSignInClient googleSignInClient;
    private Boolean isWifiConnected = false, isMobileConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();

        loginButton = findViewById(R.id.login_button);
        forgetLoginLink = findViewById(R.id.forget_password);
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        needNewAccountLink = findViewById(R.id.register_account_link);
        google_signIn = findViewById(R.id.google_signIn_button);
        facebook_signIn = findViewById(R.id.facebook_signIn_button);
        facebook_signIn.setReadPermissions("email","public_profile");
        callbackManager = CallbackManager.Factory.create();

        loadingBar = new ProgressDialog(this);

        needNewAccountLink.setOnClickListener(view -> sendUserToRegisterActivity());

        loginButton.setOnClickListener(view -> allowUserToLogging());

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        google_signIn.setOnClickListener(view -> {
            //signIn();
            if (checkWifiNetworkConnection()){
                signIn();
            }else if (checkMobileNetworkConnection()){
                signIn();
            }else {
                showMessage();
            }
        });
        facebook_signIn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "OnSuccess" + loginResult);
                if (checkWifiNetworkConnection()){
                    handleFacebookToken(loginResult.getAccessToken());
                }else if (checkMobileNetworkConnection()){
                    handleFacebookToken(loginResult.getAccessToken());
                }else {
                    showMessage();
                }
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "OnCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "OnError" + error);
                showMessage();
            }
        });
        forgetLoginLink.setOnClickListener(view -> sendUserToReSetPasswordActivity());
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        // Setting Alert Dialog Title
        alertDialogBuilder.setTitle("Connection error!!!");
        // Icon Of Alert Dialog
        alertDialogBuilder.setIcon(R.drawable.ic_wifi);
        // Setting Alert Dialog Message
        alertDialogBuilder.setMessage("This application requires an Internet connection. Please check your connection and try again.");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Ok", (arg0, arg1) -> System.exit(0));

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
    private void handleFacebookToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookToken" + accessToken);
        loadingBar.setTitle("Login");
        loadingBar.setMessage("Please wait, while we are allowing you to login using your Facebook Account...");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                Toast.makeText(LoginActivity.this, "Sign In with credential successfully...", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                sendUserToMainActivity();
            }else{
                Toast.makeText(LoginActivity.this, "Sign In with credential failure...", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please wait, while we are allowing you to login using your Google Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
                        Toast.makeText(this, "Please wait, while we are getting your auth result...", Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                loadingBar.dismiss();
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void firebaseAuthWithGoogle(String idToken) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        if (firebaseAuth.getCurrentUser().isEmailVerified()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            sendUserToMainActivity();
                        }else {
                            Toast.makeText(LoginActivity.this, "Please verify your email address...", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                       if (firebaseAuth.getCurrentUser().isEmailVerified()){
                           // If sign in fails, display a message to the user.
                           Log.w(TAG, "signInWithCredential:failure", task.getException());
                           String message = null;
                           if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                               message = Objects.requireNonNull(task.getException()).getMessage();
                           }
                           sendUserToLoginActivity();
                           Toast.makeText(LoginActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                       }else {
                           Toast.makeText(LoginActivity.this, "Please verify your email address...", Toast.LENGTH_SHORT).show();
                       }
                    }
                    loadingBar.dismiss();
                });
    }

    private void sendUserToReSetPasswordActivity(){
        Intent resetIntent = new Intent(LoginActivity.this, ReSetPasswordActivity.class);
        resetIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(resetIntent);
        finish();
    }
    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(LoginActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null){
            if (firebaseAuth.getCurrentUser().isEmailVerified()){
                sendUserToMainActivity();
            }
        }
    }

    private void allowUserToLogging() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write your email...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        }else {
            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please wait, while we are allowing you to login into your account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);


            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            if (firebaseAuth.getCurrentUser().isEmailVerified()){
                                sendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "You are Logged in successfully...", Toast.LENGTH_SHORT).show();
                                userEmail.setText("");
                                userPassword.setText("");
                            }else {
                                Toast.makeText(LoginActivity.this, "Please verify your email address...", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            String message = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                message = Objects.requireNonNull(task.getException()).getMessage();
                            }
                            Toast.makeText(LoginActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                        }
                        loadingBar.dismiss();
                    });
        }
    }

    private void sendUserToMainActivity() {
        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerIntent);
        finish();
    }
}