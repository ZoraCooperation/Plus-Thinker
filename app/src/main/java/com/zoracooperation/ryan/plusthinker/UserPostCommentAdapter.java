package com.zoracooperation.ryan.plusthinker;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPostCommentAdapter extends RecyclerView.Adapter<UserPostCommentAdapter.ViewHolder> {

    private List<Comments> commentsList;

    public UserPostCommentAdapter(List<Comments> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public UserPostCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_user_comment_layout, parent, false);
        return new UserPostCommentAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserPostCommentAdapter.ViewHolder holder, int position) {

        final Comments model = commentsList.get(position);

        holder.fullName.setText("@" + model.getUsername());
        holder.comments.setText(model.getComment());
        String[] timeArray = model.getTime().split(":");
        if (12 <= Integer.parseInt(timeArray[0])){
            holder.time.setText(model.getTime() + " pm");
        }else {
            holder.time.setText(model.getTime() + " am");
        }
        holder.date.setText(model.getDate());
        Glide.with(holder.itemView.getContext()).load(model.getProfileImage()).into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    // Sub Class to create references of the views in Card
    // view (here "all_user_comment_layout.xml")
    static class ViewHolder
            extends RecyclerView.ViewHolder {
        TextView fullName, comments, date, time;
        CircleImageView profileImage;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            fullName = itemView.findViewById(R.id.comment_profile_name);
            comments = itemView.findViewById(R.id.post_comment);
            date = itemView.findViewById(R.id.comment_post_date);
            time = itemView.findViewById(R.id.comment_post_time);
            profileImage = itemView.findViewById(R.id.comment_profile_image);
        }
    }
}
