package com.zoracooperation.ryan.plusthinker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    public Toolbar mToolbar;
    public ImageButton selectPostImage;
    public Button updatePostButton;
    public EditText postDescription;
    private static final int IMAGE_GALLERY_PIC = 1;
    private Uri imageUri;
    public String description, saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, currentUserId;
    private StorageReference storageReference;
    private DatabaseReference databaseReference, postDatabaseReference;
    public FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;
    public TextView title;
    private Boolean isWifiConnected = false, isMobileConnected = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        postDatabaseReference = FirebaseDatabase.getInstance().getReference("Posts");

        loadingBar = new ProgressDialog(this);

        mToolbar = findViewById(R.id.update_post_page_toolbar);
        title = findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        title.setGravity(Gravity.START);
        title.setText(R.string.update_post);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        selectPostImage = findViewById(R.id.select_imageButton);
        updatePostButton = findViewById(R.id.update_post_button);
        postDescription = findViewById(R.id.image_description);

        selectPostImage.setOnClickListener(view -> openGallery());
        updatePostButton.setOnClickListener(view -> {
            if (checkWifiNetworkConnection()){
                validatePostInfo();
            }else if (checkMobileNetworkConnection()){
                validatePostInfo();
            }else {
                showMessage();
            }
        });
    }

    private void validatePostInfo() {
        description = postDescription.getText().toString();

        if (imageUri == null){
            Toast.makeText(this, R.string.post_image, Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(description)){
            Toast.makeText(this, R.string.write_something, Toast.LENGTH_SHORT).show();
        }else {
            loadingBar.setTitle("Add New Post");
            loadingBar.setMessage("Please wait, while we are uploading your new post...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            storingImageToFirebaseStorage();
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostActivity.this);
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
    private void storingImageToFirebaseStorage() {

        Calendar date = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        saveCurrentDate = currentDate.format(date.getTime());

        Calendar time = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(time.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;
        final StorageReference filePath = storageReference.child("Post Images").child(imageUri.getLastPathSegment() + postRandomName + ".jpg");
        /*filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    selectPostImage.setImageResource(R.drawable.ic_select_image);
                    postDescription.setText("");

                    downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();

                    Toast.makeText(PostActivity.this, "Image uploaded successfully to storage...", Toast.LENGTH_SHORT).show();

                    savingPostInformationToDatabase();
                }else {
                    String message;
                    message = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(PostActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());

            }
            // Continue with the task to get the download URL
            return filePath.getDownloadUrl();

        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                downloadUrl = task.getResult().toString();
                System.out.println("Link: " + downloadUrl);

                selectPostImage.setImageResource(R.drawable.ic_select_image);
                postDescription.setText("");

                Toast.makeText(PostActivity.this, "Image uploaded successfully to storage...", Toast.LENGTH_SHORT).show();

                savingPostInformationToDatabase();
            }
        })).addOnFailureListener(e -> {
            String message;
            message = e.getMessage();
            Toast.makeText(PostActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
        });
    }

    private void savingPostInformationToDatabase() {
        databaseReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String userFullName = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                    String userProfileImage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();

                    HashMap postMap = new HashMap();

                    postMap.put("uid", currentUserId);
                    postMap.put("date", saveCurrentDate);
                    postMap.put("time", saveCurrentTime);
                    postMap.put("description", description);
                    postMap.put("postImage", downloadUrl);
                    postMap.put("profileImage", userProfileImage);
                    postMap.put("fullName", userFullName);

                    postDatabaseReference.child(currentUserId + postRandomName).updateChildren(postMap)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    sendUserToMainActivity();
                                    Toast.makeText(PostActivity.this, "New post updated successfully...", Toast.LENGTH_SHORT).show();
                                }else {
                                    String message;
                                    message = Objects.requireNonNull(task.getException()).getMessage();
                                    Toast.makeText(PostActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                                }
                                loadingBar.dismiss();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_GALLERY_PIC);
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
    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY_PIC && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            selectPostImage.setImageURI(imageUri);
        }
    }
}