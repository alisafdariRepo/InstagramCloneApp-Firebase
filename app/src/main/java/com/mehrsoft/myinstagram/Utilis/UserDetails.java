package com.mehrsoft.myinstagram.Utilis;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehrsoft.myinstagram.Model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetails {


    Context context;

    public UserDetails(Context context) {
        this.context = context;
    }

    public void getUserInfo(CircleImageView circleImageView, TextView username, String publisher) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisher);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImageurl()).into(circleImageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
