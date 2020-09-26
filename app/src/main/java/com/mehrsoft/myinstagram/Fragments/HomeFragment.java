package com.mehrsoft.myinstagram.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehrsoft.myinstagram.Adapter.PostAdapter;
import com.mehrsoft.myinstagram.Adapter.StoryAdapter;
import com.mehrsoft.myinstagram.Model.Post;
import com.mehrsoft.myinstagram.Model.Story;
import com.mehrsoft.myinstagram.Model.User;
import com.mehrsoft.myinstagram.R;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    public HomeFragment() {
        // Required empty public constructor
    }


    @BindView(R.id.home_recyclerView)
    RecyclerView recyclerView;


    private PostAdapter postAdapter;
    private List<Post> postList;

    private List<String> followingList;
    @BindView(R.id.fragmentHome_progressBar)
    ProgressBar progressBar;

    @BindView(R.id.home_recyclerView_story)
    RecyclerView storyRecyclerView;
    private StoryAdapter storyAdapter;

    private List<Story> storyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Hawk.init(getActivity()).build();
        ButterKnife.bind(this, view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);

        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, getActivity());
        recyclerView.setAdapter(postAdapter);
        checkFollowing();

        storyList = new ArrayList<>();
        LinearLayoutManager storyManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        storyRecyclerView.setHasFixedSize(true);
        storyRecyclerView.setLayoutManager(storyManager);


        userInfo();
        return view;

    }


    private void checkFollowing() {

        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    followingList.add(snapshot.getKey());
                }

                readPosts();
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void readPosts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Post post = snapshot.getValue(Post.class);

                  for (String id : followingList) {

                        if (post.getPublisher().equals(id)) {
                            postList.add(post);
                        }
                    }
                }

                postAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (int i = 1; i <= 5; i++) {
                        Story story = new Story(user.getUsername(), user.getImageurl());
                        storyList.add(story);
                    }
                }
                storyAdapter = new StoryAdapter(getActivity(), storyList);
                storyRecyclerView.setAdapter(storyAdapter);
                storyAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
