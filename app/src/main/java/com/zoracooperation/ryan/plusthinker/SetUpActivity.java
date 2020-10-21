package com.zoracooperation.ryan.plusthinker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {

     private EditText username, fullName, country, gender, dob, relationship;
     public Button save;
     public CircleImageView profilePicture;
     public FirebaseAuth firebaseAuth;
     private DatabaseReference databaseReference;
     private StorageReference storageReference;
     String currentUserId;
     String downloadUrl;
     String setup_username, setup_full_name, setup_country, setup_gender, setup_dob, setup_relationship;
     private ProgressDialog loadingBar;
     //final static int IMAGE_GALLERY_PIC = 1;
     Calendar mCalendar = Calendar.getInstance();
     Activity _activity=this;
     public Toolbar mToolbar;
     public TextView title;
     public Uri resultUri;
     private Boolean isWifiConnected = false, isMobileConnected = false;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        mToolbar = findViewById(R.id.setting_page_toolbar);
        title = findViewById(R.id.toolbar_title);
        if (getIntent().getStringExtra("update_account").equals("yes")){
            setSupportActionBar(mToolbar);
            title.setGravity(Gravity.START);
            title.setText(R.string.account_settings);
            Objects.requireNonNull(getSupportActionBar()).setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }else {
            setSupportActionBar(mToolbar);
            title.setGravity(Gravity.START);
            title.setText(R.string.account_settings);
            Objects.requireNonNull(getSupportActionBar()).setTitle("");
        }
        firebaseAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
            databaseReference.keepSynced(true);
        }
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");

        username = findViewById(R.id.setup_username);
        fullName = findViewById(R.id.setup_fullName);
        //country = findViewById(R.id.setup_country);
        save = findViewById(R.id.setup_save);
        profilePicture = findViewById(R.id.setup_profile_image);
        //gender = findViewById(R.id.setup_gender);
        //dob = findViewById(R.id.setup_birthday);
        //relationship = findViewById(R.id.setup_relationship);

        loadingBar = new ProgressDialog(this);

        save.setOnClickListener(view -> {
            if (checkWifiNetworkConnection()){
                validation();
            }else if (checkMobileNetworkConnection()){
                validation();
            }else {
                showMessage();
            }
        });
        profilePicture.setOnClickListener(view -> CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetUpActivity.this));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        if (getIntent().getStringExtra("update_account").equals("yes")){
                            //String edit_country = snapshot.child("country").getValue().toString();
                            //String edit_dob = snapshot.child("dob").getValue().toString();
                            String edit_fullName = snapshot.child("fullName").getValue().toString();
                            final String edit_profileImage = snapshot.child("profileImage").getValue().toString();
                            String edit_username = snapshot.child("username").getValue().toString();
                           // String edit_gender = snapshot.child("gender").getValue().toString();
                            //String edit_relationship = snapshot.child("relationshipStatus").getValue().toString();

                            Picasso.get().load(edit_profileImage).placeholder(R.drawable.ic_user_profile).networkPolicy(NetworkPolicy.OFFLINE).into(profilePicture, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(edit_profileImage).placeholder(R.drawable.ic_user_profile).into(profilePicture);
                                }
                            });
                            username.setText(edit_username);
                            username.setTypeface(Typeface.DEFAULT_BOLD);
                            //username.setTextColor(android.R.color.black);

                            fullName.setText(edit_fullName);
                            fullName.setTypeface(Typeface.DEFAULT_BOLD);
                            //fullName.setTextColor(android.R.color.black);

                            //gender.setText(edit_gender);
                            //gender.setTypeface(Typeface.DEFAULT_BOLD);
                            //gender.setTextColor(android.R.color.black);

                            //relationship.setText(edit_relationship);
                            //relationship.setTypeface(Typeface.DEFAULT_BOLD);
                            //relationship.setTextColor(android.R.color.black);

                            //dob.setText(edit_dob);
                            //dob.setTypeface(Typeface.DEFAULT_BOLD);
                            //dob.setTextColor(android.R.color.black);

                            //country.setText(edit_country);
                            //country.setTypeface(Typeface.DEFAULT_BOLD);
                            //country.setTextColor(android.R.color.black);

                            save.setText(R.string.update_account_settings);
                        }else {
                            if (snapshot.hasChild("profileImage")){
                                final String image = Objects.requireNonNull(snapshot.child("profileImage").getValue(String.class));
                                System.out.println("------------------------------------------------Image Link: " + image + " ----------------------------------------------");
                                Picasso.get().load(image).placeholder(R.drawable.ic_user_profile).networkPolicy(NetworkPolicy.OFFLINE).into(profilePicture, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(image).placeholder(R.drawable.ic_user_profile).into(profilePicture);
                                    }
                                });
                            }else {
                                Toast.makeText(SetUpActivity.this, "Please select profile image first.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //country.setShowSoftInputOnFocus(false);
        //gender.setShowSoftInputOnFocus(false);
        //dob.setShowSoftInputOnFocus(false);
        //relationship.setShowSoftInputOnFocus(false);

        /*country.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard(_activity);
            view.setOnClickListener(view1 -> showDialog("country"));
            return false;
        });
        gender.setOnTouchListener((v, event) -> {
            hideKeyboard(_activity);
            v.setOnClickListener(view -> showDialog("gender"));
            //v.performClick();
            return false;
        });
        dob.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard(_activity);
            view.setOnClickListener(view12 -> showDialog("dob"));
            return false;
        });
        relationship.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard(_activity);
            view.setOnClickListener(view13 -> showDialog("relationship"));
            return false;
        });*/
        /*gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(gender.getWindowToken(), 0);

                        final String[] singleChoiceItems = getResources().getStringArray(R.array.dialog_single_choice_array);
                        int itemSelected = 0;
                        AlertDialog.Builder builder = new AlertDialog.Builder(SetUpActivity.this);
                        builder.setTitle("Select your gender")
                                .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                                        gender.setText(singleChoiceItems[selectedIndex]);
                                        System.out.println("---------------------------------" + selectedIndex +"------------------------------");
                                    }
                                })
                                .setPositiveButton("Ok", null)
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                });
            }
        });*/
        /*dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dob.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(gender.getWindowToken(), 0);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(SetUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mCalendar.set(Calendar.YEAR, year);
                                mCalendar.set(Calendar.MONTH, monthOfYear);
                                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(mCalendar.getTime());
                                Log.d("MainActivity", "Selected date is " + date);
                                dob.setText(date);
                            }
                        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                    }
                });
            }
        });*/
        /*username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                username.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager= (InputMethodManager) SetUpActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(username, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        gender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                gender.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager= (InputMethodManager) SetUpActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(gender.getWindowToken(), 0);

                        final String[] singleChoiceItems = getResources().getStringArray(R.array.dialog_single_choice_array);
                        int itemSelected = 0;
                        AlertDialog.Builder builder = new AlertDialog.Builder(SetUpActivity.this);
                        builder.setTitle("Select your gender")
                                .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                                        gender.setText(singleChoiceItems[selectedIndex]);
                                        System.out.println("---------------------------------" + selectedIndex +"------------------------------");
                                    }
                                })
                                .setPositiveButton("Ok", null)
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                });
            }
        });
        dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                dob.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager= (InputMethodManager) SetUpActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(gender.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(SetUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mCalendar.set(Calendar.YEAR, year);
                                mCalendar.set(Calendar.MONTH, monthOfYear);
                                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(mCalendar.getTime());
                                Log.d("MainActivity", "Selected date is " + date);
                                dob.setText(date);
                            }
                        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                    }
                });
            }
        });
        username.requestFocus();
        gender.requestFocus();
        dob.requestFocus();*/
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SetUpActivity.this);
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
    private void showDialog(String type){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(gender.getWindowToken(), 0);
        DatePickerDialog datePickerDialog = new DatePickerDialog(SetUpActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(mCalendar.getTime());
            Log.d("MainActivity", "Selected date is " + date);
            dob.setText(date);
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        //datePickerDialog.show();

        final String[] singleCountryChoiceItems = getResources().getStringArray(R.array.country_arrays);
        int itemCountrySelected = 0;
        AlertDialog.Builder countryBuilder = new AlertDialog.Builder(SetUpActivity.this);
        countryBuilder.setTitle("Select your country")
                .setSingleChoiceItems(singleCountryChoiceItems, itemCountrySelected, (dialogInterface, selectedIndex) -> country.setText(singleCountryChoiceItems[selectedIndex]))
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    int selectedPosition = ((AlertDialog)dialogInterface).getListView().getCheckedItemPosition();
                    country.setText(singleCountryChoiceItems[selectedPosition]);
                    System.out.println("---------------------------------" + selectedPosition +"------------------------------");
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        Dialog countryDialog = countryBuilder.create();
        //countryDialog.show();

        final String[] singleGenderChoiceItems = getResources().getStringArray(R.array.dialog_single_choice_array);
        int itemGenderSelected = 0;
        AlertDialog.Builder genderBuilder = new AlertDialog.Builder(SetUpActivity.this);
        genderBuilder.setTitle("Select your gender")
                .setSingleChoiceItems(singleGenderChoiceItems, itemGenderSelected, (dialogInterface, selectedIndex) -> gender.setText(singleGenderChoiceItems[selectedIndex]))
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    int selectedPosition = ((AlertDialog)dialogInterface).getListView().getCheckedItemPosition();
                    gender.setText(singleGenderChoiceItems[selectedPosition]);
                    System.out.println("---------------------------------" + selectedPosition +"------------------------------");
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        Dialog genderDialog = genderBuilder.create();
        //genderDialog.show();

        final String[] singleRelationshipChoiceItems = getResources().getStringArray(R.array.dialog_relationship_choice_array);
        final int itemRelationshipSelected = 0;
        AlertDialog.Builder relationshipBuilder = new AlertDialog.Builder(SetUpActivity.this);
        relationshipBuilder.setTitle("Select your relationship")
                .setSingleChoiceItems(singleRelationshipChoiceItems, itemRelationshipSelected, (dialogInterface, selectedIndex) -> relationship.setText(singleRelationshipChoiceItems[selectedIndex]))
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    int selectedPosition = ((AlertDialog)dialogInterface).getListView().getCheckedItemPosition();
                    relationship.setText(singleRelationshipChoiceItems[selectedPosition]);
                    System.out.println("---------------------------------" + selectedPosition +"------------------------------");
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        Dialog relationshipDialog = relationshipBuilder.create();
        //relationshipDialog.show();

        if (type.equals("dob")){
            datePickerDialog.show();
            countryDialog.dismiss();
            genderDialog.dismiss();
            relationshipDialog.dismiss();
        }
        if (type.equals("country")){
            countryDialog.show();
            datePickerDialog.dismiss();
            genderDialog.dismiss();
            relationshipDialog.dismiss();
        }
        if (type.equals("gender")){
            genderDialog.show();
            countryDialog.dismiss();
            datePickerDialog.dismiss();
            relationshipDialog.dismiss();
        }
        if (type.equals("relationship")){
            relationshipDialog.show();
            countryDialog.dismiss();
            genderDialog.dismiss();
            datePickerDialog.dismiss();
        }
    }
    /*public void dobDialog(){
        dob.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(gender.getWindowToken(), 0);
                datePickerDialog = new DatePickerDialog(SetUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, monthOfYear);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(mCalendar.getTime());
                        Log.d("MainActivity", "Selected date is " + date);
                        dob.setText(date);
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                countryDialog.dismiss();
                genderDialog.dismiss();
                relationshipDialog.dismiss();
            }
        });
    }
    public void countryDialog(){
        country.post(new Runnable() {
            @Override
            public void run() {
                final String[] singleChoiceItems = getResources().getStringArray(R.array.country_arrays);
                int itemSelected = 0;
                AlertDialog.Builder builder = new AlertDialog.Builder(SetUpActivity.this);
                builder.setTitle("Select your gender")
                        .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                                country.setText(singleChoiceItems[selectedIndex]);
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int selectedPosition = ((AlertDialog)dialogInterface).getListView().getCheckedItemPosition();
                                country.setText(singleChoiceItems[selectedPosition]);
                                System.out.println("---------------------------------" + selectedPosition +"------------------------------");
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                countryDialog = builder.create();
                countryDialog.show();
                datePickerDialog.dismiss();
                genderDialog.dismiss();
                relationshipDialog.dismiss();
            }
        });
    }
    public void genderDialog(){
        gender.post(new Runnable() {
            @Override
            public void run() {
                final String[] singleGenderChoiceItems = getResources().getStringArray(R.array.dialog_single_choice_array);
                int itemGenderSelected = 0;
                AlertDialog.Builder genderBuilder = new AlertDialog.Builder(SetUpActivity.this);
                genderBuilder.setTitle("Select your gender")
                        .setSingleChoiceItems(singleGenderChoiceItems, itemGenderSelected, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                                gender.setText(singleGenderChoiceItems[selectedIndex]);
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int selectedPosition = ((AlertDialog)dialogInterface).getListView().getCheckedItemPosition();
                                gender.setText(singleGenderChoiceItems[selectedPosition]);
                                System.out.println("---------------------------------" + selectedPosition +"------------------------------");
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                genderDialog = genderBuilder.create();
                genderDialog.show();
                countryDialog.dismiss();
                datePickerDialog.dismiss();
                relationshipDialog.dismiss();
            }
        });
    }
    public void relationshipDialog(){
        relationship.post(new Runnable() {
            @Override
            public void run() {
                final String[] singleRelationshipChoiceItems = getResources().getStringArray(R.array.dialog_relationship_choice_array);
                final int itemRelationshipSelected = 0;
                AlertDialog.Builder relationshipBuilder = new AlertDialog.Builder(SetUpActivity.this);
                relationshipBuilder.setTitle("Select your relationship")
                        .setSingleChoiceItems(singleRelationshipChoiceItems, itemRelationshipSelected, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                                relationship.setText(singleRelationshipChoiceItems[selectedIndex]);
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int selectedPosition = ((AlertDialog)dialogInterface).getListView().getCheckedItemPosition();
                                relationship.setText(singleRelationshipChoiceItems[selectedPosition]);
                                System.out.println("---------------------------------" + selectedPosition +"------------------------------");
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                relationshipDialog = relationshipBuilder.create();
                relationshipDialog.show();
                countryDialog.dismiss();
                genderDialog.dismiss();
                datePickerDialog.dismiss();
            }
        });
    }*/

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK && result != null){

                resultUri = result.getUri();
                profilePicture.setImageURI(resultUri);
            }else {
                Toast.makeText(SetUpActivity.this, "Error occurred: Image can't be cropped. Try Again", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void storingImageToFirebaseStorage(){
        final StorageReference filepath = storageReference.child(currentUserId + ".jpg");

        final UploadTask uploadTask = filepath.putFile(resultUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();

            }
            // Continue with the task to get the download URL
            return filepath.getDownloadUrl();

        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                downloadUrl = task.getResult().toString();
                System.out.println("Link: " + downloadUrl);

                Toast.makeText(SetUpActivity.this, "Profile Image Stored to System Database Successfully...", Toast.LENGTH_SHORT).show();
                saveAccountSetupInformation();
            }else {
                String message;
                message = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(SetUpActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
            }
        })).addOnFailureListener(e -> {
            String message;
            message = e.getMessage();
            Toast.makeText(SetUpActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
        });
    }
    private void validation(){
        setup_username = username.getText().toString();
        setup_full_name = fullName.getText().toString();
        /*setup_country = country.getText().toString();
        setup_gender = gender.getText().toString();
        setup_dob = dob.getText().toString();
        setup_relationship = relationship.getText().toString();*/

        if (resultUri == null){
            Toast.makeText(this, "Please select your profile picture...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(setup_username)){
            Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(setup_full_name)){
            Toast.makeText(this, "Please write your full name...", Toast.LENGTH_SHORT).show();
        }/*else if (TextUtils.isEmpty(setup_country)){
            Toast.makeText(this, "Please write your country...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(setup_gender)){
            Toast.makeText(this, "Please select your gender...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(setup_dob)){
            Toast.makeText(this, "Please select your date of birth...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(setup_relationship)){
            Toast.makeText(this, "Please select your relationship...", Toast.LENGTH_SHORT).show();
        }*/else {
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait, while we are creating your new account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            storingImageToFirebaseStorage();
        }
    }
    private void saveAccountSetupInformation() {
        HashMap userMap = new HashMap();

        userMap.put("username", setup_username);
        userMap.put("fullName", setup_full_name);
        //userMap.put("country", setup_country);
        userMap.put("profileImage", downloadUrl);
        userMap.put("status", "Hey there, I am using Plus Thinker Application, developed by Zora Cooperation.");
        //userMap.put("gender", setup_gender);
        //userMap.put("dob", setup_dob);
        //userMap.put("relationshipStatus", setup_relationship);

        databaseReference.updateChildren(userMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                sendUserToMainActivity();
                if (getIntent().getStringExtra("update_account").equals("yes")){
                    Toast.makeText(SetUpActivity.this, "Your account is updated successfully", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(SetUpActivity.this, "Your account is created successfully", Toast.LENGTH_LONG).show();
                }
                username.setText("");
                fullName.setText("");
                //country.setText("");
                //gender.setText("");
                //dob.setText("");
                //relationship.setText("");
                profilePicture.setImageResource(R.drawable.ic_user_profile);
            }else {
                String message = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    message = Objects.requireNonNull(task.getException()).getMessage();
                }
                Toast.makeText(SetUpActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
            }
            loadingBar.dismiss();
        });
    }
    private void sendUserToMainActivity() {
        Intent loginIntent = new Intent(SetUpActivity.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            sendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendUserToMainActivity();
    }
}