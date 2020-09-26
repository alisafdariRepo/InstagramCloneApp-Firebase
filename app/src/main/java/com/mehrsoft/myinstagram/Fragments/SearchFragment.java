package com.mehrsoft.myinstagram.Fragments;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mehrsoft.myinstagram.Adapter.UserAdapter;
import com.mehrsoft.myinstagram.Model.User;
import com.mehrsoft.myinstagram.R;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.search_bar)
    EditText searchBar;
    private List<User> mUsers;
    private UserAdapter userAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Hawk.init(getActivity()).build();
        ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mUsers = new ArrayList<>();



        recyclerView.setAdapter(userAdapter);

        try {
            readUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    searchUser(s.toString().toLowerCase());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;

    }

    private void searchUser(String s) throws Exception {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s+"\uf8ff");



        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {

                    User user = Snapshot.getValue(User.class);

                    mUsers.add(user);



                }
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getDetails()+"", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void readUsers() throws Exception {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchBar.getText().toString().equals("") && dataSnapshot.exists()) {
                    mUsers.clear();

                    for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {

                        User user = Snapshot.getValue(User.class);


                        mUsers.add(user);



                    }
                    userAdapter=new UserAdapter(getActivity(),mUsers,true);
                    recyclerView.setAdapter(userAdapter);
                    userAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity()
                        ,databaseError.getDetails()+ "", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), databaseError.getMessage()+"", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), databaseError.toException()+"", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
