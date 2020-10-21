package com.zoracooperation.ryan.plusthinker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ClickPostActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private static final int PERMISSION_READ = 1001;
    private static final int PERMISSION_WRITE = 1002;
    public ImageView post_image_button, deleteButton, editButton, download, share;
    public TextView post_description, title;
    public String postKey, currentUserId, databaseUserId, description, imageUrl;
    public DatabaseReference databaseReference;
    public FirebaseAuth firebaseAuth;
    public Toolbar mToolbar;
    public int current_post;
    public String total_post;
    //public String fileUri;
    private Boolean isWifiConnected = false, isMobileConnected = false;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        current_post = getIntent().getIntExtra("current_post", 0) + 1;
        total_post = getIntent().getStringExtra("total_post");

        mToolbar = findViewById(R.id.clickPost_page_toolbar);
        title = findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        title.setText(current_post + "/" + total_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        post_image_button = findViewById(R.id.post_imageButton);
        post_description = findViewById(R.id.post_image_description);
        deleteButton = findViewById(R.id.delete_post);
        editButton = findViewById(R.id.edit_post);
        download = findViewById(R.id.download);
        share = findViewById(R.id.share);

        deleteButton.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);

        postKey = getIntent().getStringExtra("post_key");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey);
        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    description = Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString();
                    imageUrl = Objects.requireNonNull(dataSnapshot.child("postImage").getValue()).toString();
                    databaseUserId = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();

                    post_description.setText(description);
                    Picasso.get().load(imageUrl).placeholder(R.drawable.loading).networkPolicy(NetworkPolicy.OFFLINE).into(post_image_button, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(imageUrl).placeholder(R.drawable.loading).into(post_image_button);
                        }
                    });

                    if (currentUserId.equals(databaseUserId)){
                        deleteButton.setVisibility(View.VISIBLE);
                        editButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        deleteButton.setOnClickListener(view -> {
            if (checkWifiNetworkConnection()){
                showDeleteMessage();
            }else if (checkMobileNetworkConnection()){
                showDeleteMessage();
            }else {
                showMessage();
            }
        });
        editButton.setOnClickListener(view -> {
            if (checkWifiNetworkConnection()){
                editCurrentPost(description);
            }else if (checkMobileNetworkConnection()){
                editCurrentPost(description);
            }else {
                showMessage();
            }
        });
        download.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED){
                    //permission denied, request it
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //show popup for runtime permission
                    requestPermissions(permissions, PERMISSION_WRITE);
                }else {
                    //permission already granted, perform download
                    if (checkWifiNetworkConnection()){
                        startDownloading();
                    }else if (checkMobileNetworkConnection()){
                        startDownloading();
                    }else {
                        showMessage();
                    }
                }
            }else {
                //System os is less than marshmallow, perform download
                if (checkWifiNetworkConnection()){
                    startDownloading();
                }else if (checkMobileNetworkConnection()){
                    startDownloading();
                }else {
                    showMessage();
                }
            }
        });
        share.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED){
                    //permission denied, request it
                    String[] writePermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    String[] readPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    //show popup for runtime permission
                    requestPermissions(writePermissions, PERMISSION_STORAGE_CODE);
                    requestPermissions(readPermissions, PERMISSION_READ);
                }else {
                    //permission already granted, perform download
                    //shareImage(imageUrl);
                    shareImage();
                }
            }else {
                //System os is less than marshmallow, perform download
                //shareImage(imageUrl);
                shareImage();
            }
        });
    }

    private void showDeleteMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Delete Post");
        builder.setMessage("Do you want to delete this post?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            deleteCurrentPost();
            sendUserToMainActivity();
            Toast.makeText(ClickPostActivity.this, "Post has been deleted.", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
        Dialog dialog = builder.create();
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.colorPrimary);
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ClickPostActivity.this);
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
    //share Image other method
    /*private void shareImage(String postImage) {
        Picasso.get().load(postImage.trim()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    File direct = new File(Environment.getExternalStorageDirectory() + "/Plus Thinker");
                    if (!direct.exists()) {
                        direct.mkdirs();
                    }

                    fileUri = direct.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
                    FileOutputStream outputStream = new FileOutputStream(fileUri);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                Uri uri= Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), BitmapFactory.decodeFile(fileUri),"Share","Downloading image..."));
                // use intent to share image
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }*/
    private void startDownloading() {
        //create custom directory
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/Plus Thinker");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        //get image url
        String url = imageUrl.trim();

        //create download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //allow type of network to download image
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download");//set title in download notification
        request.setDescription("Downloading image...");//set description in download notification

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir("/Plus Thinker", System.currentTimeMillis() + ".jpg"); //get current timestamp as image name
        //get download service and enqueue image
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
    private void shareImage(){
        // Get access to bitmap image from view

        ImageView ivImage = findViewById(R.id.post_imageButton);

        // Get access to the URI for the bitmap

        Uri bmpUri = getLocalBitmapUri(ivImage);

        if (bmpUri != null) {

            // Construct a ShareIntent with link to image

            Intent shareIntent = new Intent();

            shareIntent.setAction(Intent.ACTION_SEND);

            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out this app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);

            shareIntent.setType("image/*");

            // Launch sharing dialog for image

            startActivity(Intent.createChooser(shareIntent, "Share Image"));

        } else {
            Toast.makeText(this, "Please select post, if you want to share!", Toast.LENGTH_SHORT).show();
            // ...sharing failed, handle error

        }
    }
    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {

        // Extract Bitmap from ImageView drawable

        Drawable drawable = imageView.getDrawable();

        Bitmap bmp;

        if (drawable instanceof BitmapDrawable){

            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        } else {

            return null;

        }

        // Store image to default external storage directory

        Uri bmpUri = null;

        try {

            // Use methods on Context to access package-specific directories on external storage.

            // This way, you don't need to request external read/write permission.

            // See https://youtu.be/5xVh-7ywKpE?t=25m25s

            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");

            FileOutputStream out = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);

            out.close();

            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.

            bmpUri = Uri.fromFile(file);

        } catch (IOException e) {

            e.printStackTrace();

        }

        return bmpUri;

    }

// Method when launching drawable within Glide

    /*public Uri getBitmapFromDrawable(Bitmap bmp){



        // Store image to default external storage directory

        Uri bmpUri = null;

        try {

            // Use methods on Context to access package-specific directories on external storage.

            // This way, you don't need to request external read/write permission.

            // See https://youtu.be/5xVh-7ywKpE?t=25m25s

            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");

            FileOutputStream out = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);

            out.close();



            // wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration

            bmpUri = FileProvider.getUriForFile(ClickPostActivity.this, "com.zoracooperation.ryan.plusthinker.file_provider", file);  // use this version for API >= 24



            // **Note:** For API < 24, you may use bmpUri = Uri.fromFile(file);



        } catch (IOException e) {

            e.printStackTrace();

        }

        return bmpUri;

    }*/

    //handle perform result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted from popup, perform download
                if (checkWifiNetworkConnection()){
                    startDownloading();
                }else if (checkMobileNetworkConnection()){
                    startDownloading();
                }else {
                    showMessage();
                }
            } else {
                //permission denied from popup, show error message
                Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISSION_STORAGE_CODE && requestCode == PERMISSION_READ) {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                //permission granted from popup, perform download
                //shareImage(imageUrl);
                shareImage();
            } else {
                //permission denied from popup, show error message
                Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void editCurrentPost(String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post:");

        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", (dialogInterface, i) -> {
            databaseReference.child("description").setValue(inputField.getText().toString());
            Toast.makeText(ClickPostActivity.this, "Post Update Successfully...", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
    }

    private void deleteCurrentPost() {
        databaseReference.removeValue();
    }
    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(ClickPostActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
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