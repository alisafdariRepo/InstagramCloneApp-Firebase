package com.mehrsoft.myinstagram.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehrsoft.myinstagram.Activity.CommentsActivity;
import com.mehrsoft.myinstagram.Activity.FolllowersActivity;
import com.mehrsoft.myinstagram.Fragments.PostDetailsFragment;
import com.mehrsoft.myinstagram.Fragments.ProfileFragment;
import com.mehrsoft.myinstagram.Model.Post;
import com.mehrsoft.myinstagram.Model.User;
import com.mehrsoft.myinstagram.R;
import com.mehrsoft.myinstagram.Utilis.DoubleClickListener;
import com.mehrsoft.myinstagram.Utilis.Utility;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {


    List<Post> postList;
    Context mContext;

    private FirebaseUser firebaseUser;

    public PostAdapter(List<Post> postList, Context mContext) {
        this.postList = postList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Post post = postList.get(position);
        Hawk.init(mContext).build();
        Glide.with(mContext).load(post.getPostimage()).apply(new RequestOptions().placeholder(R.drawable.place_holder)).into(holder.postImageView);

        if (post.getDescription().equals("")) {
            holder.descriptionTextView.setVisibility(View.GONE);
        } else {
            holder.descriptionTextView.setVisibility(View.VISIBLE);
            holder.descriptionTextView.setText(post.getDescription());
        }


        publisherInfo(holder.profileImage, holder.userNameTextView, holder.publisherTextView, post.getPublisher());

        isLikes(post.getPostid(), holder.likeImageView);
        likeCount(post.getPostid(), holder.likeCountTextView);

        holder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.likeImageView.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())

                            .child(firebaseUser.getUid()).setValue(true);

                    addNotifications(post.getPublisher(), post.getPostid());


                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });


        holder.commentCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);

                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisher", post.getPublisher());
                mContext.startActivity(intent);
            }
        });


        holder.commentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);

                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisher", post.getPublisher());
                mContext.startActivity(intent);


            }
        });
        isSaved(post.getPostid(), holder.bookmarkImageView);
        getComments(post.getPostid(), holder.commentCountTextView);
        holder.bookmarkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.bookmarkImageView.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).
                            child(post.getPostid()).setValue(true);
                } else
                    FirebaseDatabase.getInstance().getReference().child("Saves").
                            child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
            }
        });


        holder.userNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Hawk.put(Utility.HawkKey.PROFILE_ID, post.getPublisher());
                Utility.navigateFragment(mContext, new ProfileFragment());
            }
        });

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Hawk.put(Utility.HawkKey.PROFILE_ID, post.getPublisher());


                Utility.navigateFragment(mContext, new ProfileFragment());

            }
        });




        holder.postImageView.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
           /*     Hawk.put(Utility.HawkKey.POST_ID, post.getPostid());

                Utility.navigateFragment(mContext, new PostDetailsFragment());*/
            }

            @Override
            public void onDoubleClick(View v) {
                if (holder.likeImageView.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())

                            .child(firebaseUser.getUid()).setValue(true);

                    addNotifications(post.getPublisher(), post.getPostid());


                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }

            }
        });

        holder.likeCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FolllowersActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("title", "Likes");

                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.postItem_circleImageView)
        CircleImageView profileImage;
        @BindView(R.id.postItem_postImageView)
        ImageView postImageView;
        @BindView(R.id.postItem_bookmarkImageView)
        ImageView bookmarkImageView;
        @BindView(R.id.postItem_commentImageView)
        ImageView commentImageView;
        @BindView(R.id.postItem_likeimageView)
        ImageView likeImageView;

        @BindView(R.id.postItem_descriptionTextView)
        TextView descriptionTextView;
        @BindView(R.id.postItem_likeCountTextView)
        TextView likeCountTextView;
        @BindView(R.id.postItem_commentCountTextView)
        TextView commentCountTextView;
        @BindView(R.id.postItem_publisherTextView)
        TextView publisherTextView;

        @BindView(R.id.postItem_userNameTextView)
        TextView userNameTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }


    private void getComments(String postid, TextView textView) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Comments")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                textView.setText("view all " + dataSnapshot.getChildrenCount() + " " + "Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void publisherInfo(CircleImageView imageViewProfile, TextView userName, TextView publisher, String userId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Glide.with(mContext).load(user.getImageurl()).into(imageViewProfile);

                userName.setText(user.getUsername());

                publisher.setText(user.getUsername());


                Log.d("User", "onDataChange: " + user.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void isLikes(String postId, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    // textView.setText(dataSnapshot.getChildrenCount() + "Likes");

                    imageView.setImageResource(R.drawable.ic_favorite_red);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_favorite);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void likeCount(String postId, TextView likeCount) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likeCount.setText(dataSnapshot.getChildrenCount() + " " + "Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isSaved(String postId, ImageView saveImageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(postId).exists()) {
                    saveImageView.setImageResource(R.drawable.ic_bookmark_full);
                    saveImageView.setTag("saved");
                } else {

                    saveImageView.setImageResource(R.drawable.ic_bookmark_border);
                    saveImageView.setTag("save");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void addNotifications(String userId, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "Liked your post");
        hashMap.put("postid", postId);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);


    }
}
