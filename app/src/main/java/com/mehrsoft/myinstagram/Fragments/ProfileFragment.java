package com.mehrsoft.myinstagram.Fragments;


import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehrsoft.myinstagram.Adapter.TabAdapter;
import com.mehrsoft.myinstagram.Activity.EditProfileActivity;
import com.mehrsoft.myinstagram.Activity.FolllowersActivity;
import com.mehrsoft.myinstagram.Model.Post;
import com.mehrsoft.myinstagram.Model.User;
import com.mehrsoft.myinstagram.Activity.OptionsActivity;
import com.mehrsoft.myinstagram.R;
import com.mehrsoft.myinstagram.Utilis.Utility;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    public ProfileFragment() {
    }
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.profile_CircleImageProfile)
    CircleImageView circleImageprofile;
    @BindView(R.id.profileToolbar_userName)
    TextView userNameProfile;
    @BindView(R.id.profilePostTextView)
    TextView postTextView;
    @BindView(R.id.profileFollowersTextView)
    TextView followersTextView;
    @BindView(R.id.profileFollowingTextView)
    TextView followingTextView;
    @BindView(R.id.profile_BioTextView)
    TextView bioTextView;
    @BindView(R.id.profile_EditProfileButton)
    MaterialButton editProfileButton;
    @BindView(R.id.showPostsTextView)
    TextView showPostTextView;
    @BindView(R.id.showFollowersTextView)
    TextView showFollowersTextView;
    @BindView(R.id.showFollowingTextView)
    TextView showFollowingTextView;


    @BindView(R.id.profileImageToolbar)
    ImageView optionImageView;


    private FirebaseUser firebaseUser;
    private String profileId=null;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        ButterKnife.bind(this, view);
        Hawk.init(getActivity()).build();
        profileId = Hawk.get(Utility.HawkKey.PROFILE_ID);
        setupTabLayout();
        getUserInfo();
        getFollowers();
        getPosts();


       if (profileId.equals(firebaseUser.getUid()))
            editProfileButton.setText("Edit Profile");
        else
            checkFollow();


        showFollowersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), FolllowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","Followers");

                startActivity(intent);
            }
        });



        showFollowingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), FolllowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","Following");

                startActivity(intent);
            }

        });


        optionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), OptionsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void setupTabLayout() {
        TabAdapter tabAdapter = new TabAdapter(getActivity().getSupportFragmentManager());
        tabAdapter.addFragment(new ProfileTabLeftFragment(), "");
        tabAdapter.addFragment(new ProfileTabRightFragment(), "");

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        int[] tabIcons = {
                R.drawable.ic_view_module,
                R.drawable.ic_profile
        };
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);

        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int tabIconColor = ContextCompat.getColor(getActivity(), R.color.primaryTextColor);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabUnselected(tab);
                        int tabIconColor = ContextCompat.getColor(getActivity(), R.color.accentColor);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                    }
                }
        );
    }

    @OnClick(R.id.profile_EditProfileButton)
    void setProfileEditOnclick() {

        if (editProfileButton.getText().equals("Edit Profile")) {
            //go to editProfile
            startActivity(new Intent(getActivity(), EditProfileActivity.class));
        } else if (editProfileButton.getText().equals("follow")) {
            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                    .child("following").child(profileId).setValue(true);
            FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                    .child("followers").child(firebaseUser.getUid()).setValue(true);
            addNotifications();

        } else if (editProfileButton.getText().equals("following")) {
            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                    .child("following").child(profileId).removeValue();

            FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                    .child("followers").child(firebaseUser.getUid()).removeValue();
        }
    }

    private void getUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (getContext()==null)
                    return;

                User user = dataSnapshot.getValue(User.class);
                Glide.with(getActivity()).load(user.getImageurl()).into(circleImageprofile);
                userNameProfile.setText(user.getUsername());
                bioTextView.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
    }


    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileId).exists()) {
                    editProfileButton.setText("following");
                } else {
                    editProfileButton.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getFollowers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followersTextView.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("followers");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingTextView.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void getPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);

                    if (post.getPublisher().equals(profileId)) {
                        i++;
                    }
                }

                postTextView.setText(i + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void addNotifications()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notifications").child(profileId);
        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","started following you");
        hashMap.put("postid","");
        hashMap.put("ispost",false);

        reference.push().setValue(hashMap);


    }
}
