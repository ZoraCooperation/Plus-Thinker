package com.zoracooperation.ryan.plusthinker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    public TextView country, username, fullName, dob, gender, status, relationship;
    public CircleImageView profilePic;
    public Toolbar mToolbar;
    public FirebaseAuth firebaseAuth;
    public DatabaseReference databaseReference;
    String currentUserId;
    public TextView title;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //country = findViewById(R.id.my_profile_country);
        username = findViewById(R.id.my_profile_username);
        fullName = findViewById(R.id.my_profile_fullName);
        //dob = findViewById(R.id.my_profile_dob);
        //gender = findViewById(R.id.my_profile_gender);
        status = findViewById(R.id.my_profile_status);
        //relationship = findViewById(R.id.my_profile_relationship);
        profilePic = findViewById(R.id.my_profile_image);

        mToolbar = findViewById(R.id.profile_page_toolbar);
        title = findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        title.setGravity(Gravity.START);
        title.setText(R.string.profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
            databaseReference.keepSynced(true);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //String profile_country = snapshot.child("country").getValue().toString();
                    //String profile_dob = snapshot.child("dob").getValue().toString();
                    String profile_fullName = snapshot.child("fullName").getValue().toString();
                    final String profile_profileImage = snapshot.child("profileImage").getValue().toString();
                    String profile_username = snapshot.child("username").getValue().toString();
                    //String profile_gender = snapshot.child("gender").getValue().toString();
                    //String profile_relationship = snapshot.child("relationshipStatus").getValue().toString();
                    String profile_status = snapshot.child("status").getValue().toString();

                    Picasso.get().load(profile_profileImage).placeholder(R.drawable.profile).networkPolicy(NetworkPolicy.OFFLINE).into(profilePic, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(profile_profileImage).placeholder(R.drawable.profile).into(profilePic);
                        }
                    });
                    username.setText("@" + profile_username);
                    fullName.setText(profile_fullName);
                    //gender.setText("Gender: " + profile_gender);
                    //dob.setText("DOB: " + profile_dob);
                    //country.setText("Country: " + profile_country);
                    //relationship.setText("Relationship: " + profile_relationship);
                    status.setText(profile_status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendUserToMainActivity() {
        Intent loginIntent = new Intent(ProfileActivity.this, com.zoracooperation.ryan.plusthinker.MainActivity.class);
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