package com.mehrsoft.myinstagram.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.mehrsoft.myinstagram.Adapter.CommentAdapter;
import com.mehrsoft.myinstagram.Model.Comment;
import com.mehrsoft.myinstagram.Model.User;
import com.mehrsoft.myinstagram.R;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    @BindView(R.id.commentRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.commentEditText)
    EditText commentInput;
    @BindView(R.id.commentButton)
    MaterialButton sendCommentButton;
    @BindView(R.id.commentCircleImageProfile)
    CircleImageView circleImageView;

    @BindView(R.id.commentToolbar)
    Toolbar toolbar;

    String postId;
    String publisherId;
    FirebaseUser firebaseUser;


    CommentAdapter adapter;
    List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Hawk.init(this).build();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        commentList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(adapter);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            postId = bundle.getString("postid");
            publisherId = bundle.getString("publisher");
        }


        if (commentInput.getText().toString().equals("")) {
            sendCommentButton.setEnabled(true);
            sendCommentButton.setTextColor(getResources().getColor(R.color.accentColor));
        } else {
            sendCommentButton.setEnabled(false);
        }

        commentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    sendCommentButton.setEnabled(false);
                    sendCommentButton.setTextColor(getResources().getColor(R.color.accentColor));
                } else {
                    sendCommentButton.setEnabled(true);

                    sendCommentButton.setTextColor(Color.parseColor("#0277BD"));


                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendCommentButton.isEnabled())
                    addComment();

            }
        });

        getImage();
        showAllComment();
    }

    private void addNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherId);
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "Commented\t" +commentInput.getText().toString());
        hashMap.put("postid", postId);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);


    }


    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("comment", commentInput.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        reference.push().setValue(hashMap);
        addNotifications();
        commentInput.setText("");
    }


    private void getImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").
                child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                Glide.with(CommentsActivity.this).load(user.getImageurl()).into(circleImageView);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void showAllComment() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
