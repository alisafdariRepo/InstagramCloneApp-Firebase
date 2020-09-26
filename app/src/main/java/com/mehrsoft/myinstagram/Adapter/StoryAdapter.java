package com.mehrsoft.myinstagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mehrsoft.myinstagram.Model.Story;
import com.mehrsoft.myinstagram.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    private Context mContext;
    private List<Story> storyList;

    public StoryAdapter(Context mContext, List<Story> mStory) {
        this.mContext = mContext;
        this.storyList = mStory;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.story_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Story story = storyList.get(position);

        holder.userNameTextView.setText(story.getUserName());
        Glide.with(mContext).load(story.getImageUrl()).into(holder.storyPhoto);


    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.storyItem_Photo)
        CircleImageView storyPhoto;
        @BindView(R.id.storyItem_PlusImageView)
        CircleImageView plusImageView;
        @BindView(R.id.storyItem_userName)
        TextView userNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
