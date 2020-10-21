package com.zoracooperation.ryan.plusthinker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.ViewHolder> {

    private List<Post> postList;

    public UserPostAdapter(List<Post> listData) {
        this.postList = listData;
    }

    @NonNull
    @Override
    public UserPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_user_post_layout,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final UserPostAdapter.ViewHolder holder, int position) {
        final Post model = postList.get(position);

        final String postKey = model.getPostKey();
        final int currentPost = position;

        // Add fullName from model class (here
        // "Posts.class")to appropriate view in Card
        // view (here "all_user_post_layout.xml")
        holder.fullName.setText(model.getFullName());

        // Add date from model class (here
        // "Posts.class")to appropriate view in Card
        // view (here "all_user_post_layout.xml")
        holder.date.setText(model.getDate());

        // Add time from model class (here
        // "Posts.class")to appropriate view in Card
        // view (here "all_user_post_layout.xml")
        String[] timeArray = model.getTime().split(":");
        if (12 <= Integer.parseInt(timeArray[0])){
            holder.time.setText(model.getTime() + " pm");
        }else {
            holder.time.setText(model.getTime() + " am");
        }
        // Add post description from model class (here
        // "Posts.class")to appropriate view in Card
        // view (here "all_user_post_layout.xml")
        holder.description.setText(model.getDescription());

        // Add profileImage from model class (here
        // "Posts.class")to appropriate view in Card
        // view (here "all_user_post_layout.xml")
        Glide.with(holder.itemView.getContext()).load(model.getProfileImage()).into(holder.profileImage);

        // Add postImage from model class post_profile_image(here
        // "Posts.class")to appropriate view in Card
        // view (here "all_user_post_layout.xml")
        Glide.with(holder.itemView.getContext()).load(model.getPostImage()).placeholder(R.drawable.progress_animation).into(holder.postImage);
        //imageUrl = model.getPostImage();
        holder.loadingBar.dismiss();
        holder.itemView.setOnClickListener(view -> {
            Intent clickPostIntent = new Intent(holder.itemView.getContext(), com.zoracooperation.ryan.plusthinker.ClickPostActivity.class);
            clickPostIntent.putExtra("post_key", postKey);
            clickPostIntent.putExtra("current_post", currentPost);
            clickPostIntent.putExtra("total_post", model.getTotalPost());
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
                        if (dataSnapshot.child(postKey).hasChild(holder.currentUserId)){
                            holder.likeFirebaseDatabase.child(postKey).child(holder.currentUserId).removeValue();
                        }else {
                            holder.likeFirebaseDatabase.child(postKey).child(holder.currentUserId).setValue(true);
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
                            final String username = dataSnapshot.child("username").getValue().toString();
                            final String profile = dataSnapshot.child("profileImage").getValue().toString();
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

                        holder.postCommentFirebaseDatabase.child(postKey).child("Comments").child(getCommentRandomName).updateChildren(postMap)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()){
                                        Toast.makeText(holder.itemView.getContext(), "You have commented successfully...", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(holder.itemView.getContext(), "Error Occurred, try again...", Toast.LENGTH_SHORT).show();
                                    }
                                    holder.commentLayout.setVisibility(View.GONE);
                                });
                    }
                });
            }
        });

        holder.likeFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey).hasChild(holder.currentUserId)){
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
        holder.postCommentFirebaseDatabase.child(postKey).child("Comments").addValueEventListener(new ValueEventListener() {
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
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String uid = dataSnapshot.child("uid").getValue().toString();
                        String comment = dataSnapshot.child("comment").getValue().toString();
                        String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                        String username = dataSnapshot.child("username").getValue().toString();
                        String date = dataSnapshot.child("date").getValue().toString();
                        String time = dataSnapshot.child("time").getValue().toString();

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

    @Override
    public int getItemCount() {
        return postList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView fullName, date, time, description, like, comment;
        CircleImageView profileImage;
        ImageView postImage, favoriteIcon, commentIcon, sendIcon;
        //ImageView shareIcon
        ProgressDialog loadingBar;
        DatabaseReference likeFirebaseDatabase, userFirebaseDatabase, postCommentFirebaseDatabase;
        FirebaseAuth firebaseAuth;
        LinearLayout commentLayout;
        RecyclerView commentRecyclerView;
        EditText commentText;
        int likeCounts, commentCount;
        String currentUserId;
        Boolean isComment, isLike;
        Comments comments;
        List<Comments> commentsList;
        UserPostCommentAdapter postCommentAdapter;
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
            loadingBar = new ProgressDialog(itemView.getContext());
            commentLayout = itemView.findViewById(R.id.comment_layout);
            commentRecyclerView = itemView.findViewById(R.id.all_user_comment_list);
            commentRecyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            commentRecyclerView.setLayoutManager(linearLayoutManager);

            firebaseAuth = FirebaseAuth.getInstance();
            currentUserId = firebaseAuth.getCurrentUser().getUid();
            likeFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Likes");
            userFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
            postCommentFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");

            comments = new Comments();
            commentsList = new ArrayList<>();
            postCommentAdapter = null;
            likeCounts = 0;
            commentCount = 0;
            isComment = false;
            isLike = false;
        }
    }
}
