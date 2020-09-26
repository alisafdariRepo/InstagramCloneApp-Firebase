package com.mehrsoft.myinstagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehrsoft.myinstagram.Fragments.ProfileFragment;
import com.mehrsoft.myinstagram.Activity.MainActivity;
import com.mehrsoft.myinstagram.Model.User;
import com.mehrsoft.myinstagram.R;
import com.mehrsoft.myinstagram.Utilis.Utility;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> mUsersList;
    private FirebaseUser firebaseUser;
    private boolean isFragment;


    public UserAdapter(Context context, List<User> mUsersList,boolean isFragment) {
        this.context = context;
        this.mUsersList = mUsersList;
        this.isFragment=isFragment;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = mUsersList.get(position);
        holder.bind(user);

        holder.btnFollow.setVisibility(View.VISIBLE);

        isFollowing(user.getId(), holder.btnFollow);

        Hawk.init(context).build();
        if (user.getId().equals(firebaseUser.getUid())) {
            holder.btnFollow.setVisibility(View.GONE);
            holder.userName.setVisibility(View.GONE);
            holder.fullName.setVisibility(View.GONE);
            holder.circleImageView.setVisibility(View.GONE);

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFragment) {
                    Hawk.put(Utility.HawkKey.PROFILE_ID, user.getId());
                  /*  ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().
                            replace(R.id.fragment_container, new ProfileFragment())
                            .commit();*/

                Utility.navigateFragment(context,new ProfileFragment());
                }else{
                    Intent intent=new Intent(context, MainActivity.class);
                    intent.putExtra(Utility.HawkKey.PUBLISHER,user.getId());
                    context.startActivity(intent);
                }
            }
        });

       


        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btnFollow.getText().equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotifications(user.getId());
                    holder.btnFollow.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.primaryColor));
                    holder.btnFollow.setTextColor(context.getResources().getColor(R.color.primaryTextColor));


                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("following").child(firebaseUser.getUid()).removeValue();

                    holder.btnFollow.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.blueColor));
                   holder.btnFollow.setTextColor(context.getResources().getColor(R.color.primaryLightColor));



                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_item_btn_follow)
        MaterialButton btnFollow;
        @BindView(R.id.user_item_fullName)
        TextView fullName;
        @BindView(R.id.user_item_userName)
        TextView userName;
        @BindView(R.id.profile_image)
        CircleImageView circleImageView;



        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(User user) {
            fullName.setText(user.getFullname());
            userName.setText(user.getUsername());
            Glide.with(context).load(user.getImageurl()).into(circleImageView);


        }

    }

    private void addNotifications(String userId)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notifications").child(userId);
        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","started following you");
        hashMap.put("postid","");
        hashMap.put("ispost",false);

        reference.push().setValue(hashMap);


    }

    private void isFollowing(String userId, MaterialButton button) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("following");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(userId).exists())
                    button.setText("Following");
                else
                    button.setText("Follow");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


                Toast.makeText(context, databaseError.getDetails() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }




}