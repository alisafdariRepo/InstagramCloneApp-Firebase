package com.mehrsoft.myinstagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mehrsoft.myinstagram.Activity.MainActivity;
import com.mehrsoft.myinstagram.Model.Comment;
import com.mehrsoft.myinstagram.R;
import com.mehrsoft.myinstagram.Utilis.UserDetails;
import com.orhanobut.hawk.Hawk;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private FirebaseUser firebaseUser;
    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = commentList.get(position);
        holder.displayComment.setText(comment.getComment());
        Hawk.init(context).build();
        UserDetails userInfo = new UserDetails(context);
        userInfo.getUserInfo(holder.circleImageView, holder.userName, comment.getPublisher());

        holder.displayComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("publisherid", comment.getPublisher());
                context.startActivity(intent);
            }
        });


        holder.displayComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("publisherid", comment.getPublisher());
                context.startActivity(intent);
            }
        });


        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("publisherid", comment.getPublisher());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.comment_item_displayComment)
        TextView displayComment;
        @BindView(R.id.comment_item_userName)
        TextView userName;
        @BindView(R.id.comment_item_profile)
        CircleImageView circleImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }


    }




}