package com.zoracooperation.ryan.plusthinker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public NavigationView navigationView;
    public DrawerLayout drawerLayout;
    public RecyclerView recyclerView;
    public Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public CircleImageView NavProfileImage;
    public ImageButton addNewPost;
    public TextView NavProfileUserName, title;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;
    public DatabaseReference postFirebaseReference;
    public static int totalPost;
    public static String getCurrentUserId;
    /*private UserPostAdapter userPostAdapter;
    private List<Post> postList;
    private Post post;*/
    private InterstitialAd mInterstitialAd;
    private FirebaseRecyclerAdapter adapter;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    AdView adView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*postList = new ArrayList<>();
        post = new Post();*/

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseDatabase.keepSynced(true);

        postFirebaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        postFirebaseReference.keepSynced(true);

        mToolbar = findViewById(R.id.main_page_toolbar);
        title = findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        title.setGravity(Gravity.START);
        title.setText(R.string.home);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        drawerLayout = findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.nav_layout);
        addNewPost = findViewById(R.id.add_new_post);

        recyclerView = findViewById(R.id.all_user_post_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = navView.findViewById(R.id.nav_user_full_name);

        /*//Banner Ads
        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-4896913136356344/7863545592");
        // TODO: Add adView to your view hierarchy.*/


        navigationView.setNavigationItemSelectedListener(item -> {
            userMenuSelector(item);
            return false;
        });
        addNewPost.setOnClickListener(view -> sendUserToPostActivity());

        // It is a class provide by the FirebaseUI to make a
        // query in the database to fetch appropriate data
        /*FirebaseRecyclerOptions<Posts> options
                = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(postFirebaseReference, Posts.class)
                .build();*/
        // Connecting object of required Adapter class to
        // the Adapter class itself

        //adapter = new PostAdapter(options);

        // Connecting Adapter class with the Recycler view*/

        //recyclerView.setAdapter(adapter);



        postFirebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //postList.clear();
                    totalPost = (int) dataSnapshot.getChildrenCount();
                    /*String getTotalPost = String.valueOf(dataSnapshot.getChildrenCount());
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String postKey = snapshot.getRef().getKey();
                        String time = snapshot.child("time").getValue().toString();
                        String date = snapshot.child("date").getValue().toString();
                        String uid = snapshot.child("uid").getValue().toString();
                        String fullName = snapshot.child("fullName").getValue().toString();
                        String profileImage = snapshot.child("profileImage").getValue().toString();
                        String postImage = snapshot.child("postImage").getValue().toString();
                        String description = snapshot.child("description").getValue().toString();

                        post = new Post(uid, time, date, postImage, description, profileImage, fullName, postKey, getTotalPost);
                        postList.add(post);
                    }
                    userPostAdapter = new UserPostAdapter(postList);
                    recyclerView.setAdapter(userPostAdapter);*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Initialize the Mobile Ads SDK

        MobileAds.initialize(this, initializationStatus -> {

        });

        fetch();
        initAds();
    }
    private void initAds(){
        adView = findViewById(R.id.adViewId);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        Log.d("Ads:","");

        //InterstitialAd
        mInterstitialAd = new InterstitialAd(MainActivity.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4896913136356344/8964696668");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());//

    }
    private void sendUserToPostActivity() {
        Intent postIntent = new Intent(MainActivity.this, PostActivity.class);
        postIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(postIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null){
            sendUserToLoginActivity();
        }else {
            checkUserExistence();
            adapter.startListening();
        }
    }
    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

    private void checkUserExistence(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            final String currentUserID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
            getCurrentUserId = currentUserID;

            firebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.hasChild(currentUserID)){
                        sendUserToSetupActivity();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            firebaseDatabase.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        if(dataSnapshot.hasChild("fullName"))
                        {
                            String fullName = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                            NavProfileUserName.setText(fullName);
                        }
                        if(dataSnapshot.hasChild("profileImage"))
                        {
                            final String image = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                            Picasso.get().load(image).placeholder(R.drawable.profile).networkPolicy(NetworkPolicy.OFFLINE).into(NavProfileImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    private void sendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetUpActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        setupIntent.putExtra("update_account", "no");
        startActivity(setupIntent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendUserToProfileActivity() {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
        finish();
    }

    private void sendUserToSetupActivityForEditUserAccount(){
        Intent setupIntent = new Intent(MainActivity.this, SetUpActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        setupIntent.putExtra("update_account", "yes");
        startActivity(setupIntent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    private void userMenuSelector(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_post:
                sendUserToPostActivity();
                break;
            case R.id.nav_profile:
                sendUserToProfileActivity();
                break;
            case R.id.nav_settings:
                sendUserToSetupActivityForEditUserAccount();
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                sendUserToLoginActivity();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            logout();
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                logout();
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

    }

    private void logout() {
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        // Setting Alert Dialog Title
        alertDialogBuilder.setTitle("Exit");
        // Icon Of Alert Dialog
        alertDialogBuilder.setIcon(R.drawable.ic_question);
        // Setting Alert Dialog Message
        alertDialogBuilder.setMessage("If you like our app, please take a moment to rate it on Google Play.");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Rate", (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()))));

        alertDialogBuilder.setNegativeButton("Exit", (dialog, which) -> System.exit(0));
        alertDialogBuilder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void fetch(){
        final Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Posts");
        query.keepSynced(true);
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(query, snapshot -> new Post(
                                Objects.requireNonNull(snapshot.child("uid").getValue()).toString(),
                                Objects.requireNonNull(snapshot.child("time").getValue()).toString(),
                                Objects.requireNonNull(snapshot.child("date").getValue()).toString(),
                                Objects.requireNonNull(snapshot.child("postImage").getValue()).toString(),
                                Objects.requireNonNull(snapshot.child("description").getValue()).toString(),
                                Objects.requireNonNull(snapshot.child("profileImage").getValue()).toString(),
                                Objects.requireNonNull(snapshot.child("fullName").getValue()).toString()))
                        .build();

        adapter = new FirebaseRecyclerAdapter<Post, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_user_post_layout, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Post model) {
                final String postKey = getRef(position).getKey();

                holder.setDate(model.getDate());
                holder.setTime(model.getTime());
                holder.setDescription(model.getDescription());
                holder.setFullName(model.getFullName());
                holder.setPostImage(model.getPostImage());
                holder.setProfileImage(model.getProfileImage());

                holder.itemView.setOnClickListener(view -> {
                    Intent clickPostIntent = new Intent(holder.itemView.getContext(), ClickPostActivity.class);
                    clickPostIntent.putExtra("post_key", postKey);
                    clickPostIntent.putExtra("current_post", position);
                    clickPostIntent.putExtra("total_post", String.valueOf(totalPost));
                    holder.itemView.getContext().startActivity(clickPostIntent);
                    ((Activity)holder.itemView.getContext()).finish();
                });
                holder.favoriteIcon.setOnClickListener(view -> {
                    holder.isLike = true;

                    holder.likeFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (holder.isLike){
                                if (postKey != null) {
                                    if (dataSnapshot.child(postKey).hasChild(holder.currentUserId)){
                                        holder.likeFirebaseDatabase.child(postKey).child(holder.currentUserId).removeValue();
                                    }else {
                                        holder.likeFirebaseDatabase.child(postKey).child(holder.currentUserId).setValue(true);
                                    }
                                }
                                holder.isLike = false;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                });
                holder.commentIcon.setOnClickListener(view -> {
                    if (!holder.isComment){
                        holder.isComment = true;
                        holder.commentLayout.setVisibility(View.VISIBLE);
                    }else {
                        holder.isComment = false;
                        holder.commentLayout.setVisibility(View.GONE);
                    }
                });
                holder.sendIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.userFirebaseDatabase.child(holder.currentUserId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    final String username = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                                    final String profile = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                                    Thread thread = new Thread(() -> validateComment(username, profile));
                                    thread.setDaemon(true);
                                    thread.start();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    private void validateComment(final String username, final String profile) {

                        holder.sendIcon.post(() -> {
                            String comment = holder.commentText.getText().toString();

                            if (TextUtils.isEmpty(comment)){
                                Toast.makeText(holder.itemView.getContext(), "Please write text to comment...", Toast.LENGTH_SHORT).show();
                            } else if (profile == null){
                                Toast.makeText(holder.itemView.getContext(), R.string.post_image, Toast.LENGTH_SHORT).show();
                            }else {
                                Calendar date = Calendar.getInstance();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
                                final String getCurrentDate = currentDate.format(date.getTime());

                                Calendar time = Calendar.getInstance();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                                final String getCurrentTime = currentTime.format(time.getTime());

                                final String getCommentRandomName = getCurrentDate + getCurrentTime;

                                HashMap postMap = new HashMap();

                                postMap.put("uid", holder.currentUserId);
                                postMap.put("comment", comment);
                                postMap.put("profileImage", profile);
                                postMap.put("username", username);
                                postMap.put("date", getCurrentDate);
                                postMap.put("time", getCurrentTime);

                                if (postKey != null) {
                                    holder.postCommentFirebaseDatabase.child(postKey).child("Comments").child(getCommentRandomName).updateChildren(postMap)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(holder.itemView.getContext(), "You have commented successfully...", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(holder.itemView.getContext(), "Error Occurred, try again...", Toast.LENGTH_SHORT).show();
                                                }
                                                //holder.commentLayout.setVisibility(View.GONE);
                                            });
                                }
                            }
                        });
                    }
                });
                holder.likeFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Objects.requireNonNull(postKey)).hasChild(holder.currentUserId)){
                            holder.likeCounts = (int) dataSnapshot.child(postKey).getChildrenCount();
                            holder.like.setText(holder.likeCounts + " Likes");
                            holder.favoriteIcon.setImageResource(R.drawable.ic_favorite);
                        }else {
                            holder.likeCounts = (int) dataSnapshot.child(postKey).getChildrenCount();
                            holder.like.setText(holder.likeCounts + " Likes");
                            holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_border);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.postCommentFirebaseDatabase.child(Objects.requireNonNull(postKey)).child("Comments").addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            holder.commentCount = (int) dataSnapshot.getChildrenCount();
                            holder.comment.setText(holder.commentCount + " Comments");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.postCommentFirebaseDatabase.child(postKey).child("Comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            holder.commentsList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                String uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                                String comment = Objects.requireNonNull(dataSnapshot.child("comment").getValue()).toString();
                                String profileImage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                                String username = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                                String date = Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString();
                                String time = Objects.requireNonNull(dataSnapshot.child("time").getValue()).toString();

                                holder.comments = new Comments(uid, comment, profileImage, username, date, time);
                                holder.commentsList.add(holder.comments);
                            }
                            holder.postCommentAdapter = new UserPostCommentAdapter(holder.commentsList);
                            holder.commentRecyclerView.setAdapter(holder.postCommentAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fullName, date, time, description, like, comment;
        public CircleImageView profileImage;
        public ImageView postImage, favoriteIcon, commentIcon, sendIcon;
        public DatabaseReference likeFirebaseDatabase, userFirebaseDatabase, postCommentFirebaseDatabase;
        public FirebaseAuth firebaseAuth;
        public LinearLayout commentLayout;
        public RecyclerView commentRecyclerView;
        public EditText commentText;
        public int likeCounts, commentCount;
        public String currentUserId;
        public Boolean isComment, isLike;
        public Comments comments;
        public List<Comments> commentsList;
        public UserPostCommentAdapter postCommentAdapter;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            fullName = itemView.findViewById(R.id.post_profile_name);
            profileImage = itemView.findViewById(R.id.post_profile_image);
            date = itemView.findViewById(R.id.post_date);
            time = itemView.findViewById(R.id.post_time);
            description = itemView.findViewById(R.id.post_description);
            postImage = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.commentsCounts);
            commentText = itemView.findViewById(R.id.comment_text);
            favoriteIcon = itemView.findViewById(R.id.favorite);
            commentIcon = itemView.findViewById(R.id.comment);
            //shareIcon = itemView.findViewById(R.id.share);
            sendIcon = itemView.findViewById(R.id.send);
            commentLayout = itemView.findViewById(R.id.comment_layout);
            commentRecyclerView = itemView.findViewById(R.id.all_user_comment_list);
            commentRecyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            commentRecyclerView.setLayoutManager(linearLayoutManager);

            firebaseAuth = FirebaseAuth.getInstance();
            currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
            likeFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Likes");
            likeFirebaseDatabase.keepSynced(true);
            userFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
            userFirebaseDatabase.keepSynced(true);
            postCommentFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
            postCommentFirebaseDatabase.keepSynced(true);

            comments = new Comments();
            commentsList = new ArrayList<>();
            postCommentAdapter = null;
            likeCounts = 0;
            commentCount = 0;
            isComment = false;
            isLike = false;
        }

        @SuppressLint("SetTextI18n")
        public void setTime(String time) {
            String[] timeArray = time.split(":");
            if (12 <= Integer.parseInt(timeArray[0])){
                this.time.setText(time + " pm");
            }else {
                this.time.setText(time + " am");
            }
        }

        public void setDate(String date) {
            this.date.setText(date);
        }

        public void setFullName(String fullName){
            this.fullName.setText(fullName);
        }

        public void setDescription(String description){
            this.description.setText(description);
        }

        public void setProfileImage(final String profileImage){
            final CircleImageView image = this.profileImage;
            //Picasso.get().load(profileImage).into(this.profileImage);
            Picasso.get().load(profileImage).networkPolicy(NetworkPolicy.OFFLINE).into(this.profileImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(profileImage).into(image);
                }
            });
        }

        public void setPostImage(final String postImage){
            final ImageView imageView = this.postImage;
            Picasso.get().load(postImage).placeholder(R.drawable.progress_animation).networkPolicy(NetworkPolicy.OFFLINE).into(this.postImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(postImage).placeholder(R.drawable.progress_animation).into(imageView);
                }
            });
        }
    }
}