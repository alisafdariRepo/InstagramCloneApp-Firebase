package com.mehrsoft.myinstagram.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehrsoft.myinstagram.Fragments.PostDetailsFragment;
import com.mehrsoft.myinstagram.Fragments.ProfileFragment;
import com.mehrsoft.myinstagram.Model.Notification;
import com.mehrsoft.myinstagram.Model.Post;
import com.mehrsoft.myinstagram.Model.User;
import com.mehrsoft.myinstagram.R;
import com.mehrsoft.myinstagram.Utilis.UserDetails;
import com.mehrsoft.myinstagram.Utilis.Utility;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;

    List<Notification> notificationList;


    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Notification notification = notificationList.get(position);
        holder.comment.setText(notification.getText());



        //    userDetails.getUserInfo(holder.circleImageView, holder.userName, notification.getUserid());

        getUserInfo(holder.circleImageView, holder.userName, notification.getUserid());

        Hawk.init(context).build();

        if (notification.isIspost()) {
            getPostImage(notification.getPostid(), holder.postImageView);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.isIspost()) {
                    Hawk.put(Utility.HawkKey.POST_ID, notification.getPostid());

                    Utility.navigateFragment(context, new PostDetailsFragment());

                } else {
                    Hawk.put(Utility.HawkKey.PROFILE_ID, notification.getUserid());


                    Utility.navigateFragment(context, new ProfileFragment());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.notification_item_text)
        TextView comment;

        @BindView(R.id.notification_item_userName)
        TextView userName;

        @BindView(R.id.notification_item_imageProfile)
        CircleImageView circleImageView;

        @BindView(R.id.notification_item_postImage)
        ImageView postImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisherid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
               Glide.with(context).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());


                Log.d("NotificationAdapter", "onDataChange: "+user.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getPostImage(String postId, ImageView imageView) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                Picasso.get().load(post.getPostimage()).into(imageView);
                Log.d("postAdapter", "onDataChange: " + post.getPostimage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
