package com.example.pixcy;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pixcy.databinding.ItemPostBinding;
import com.example.pixcy.models.Post;

import java.util.Date;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private ItemPostBinding itemPostBinding;
    private Context context;
    private List<Post> posts;
    public static final String TAG = "PostsAdapter";

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // For every visible item on the screen we want to inflate (create) a view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        itemPostBinding = ItemPostBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemPostBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemPostBinding itemPostBinding;

        public ViewHolder(@NonNull ItemPostBinding itemPostBinding) {
            super(itemPostBinding.getRoot());
            this.itemPostBinding = itemPostBinding;
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            itemPostBinding.tvDescription.setText(post.getDescription());
            Glide.with(context).load(post.getImage_url()).into(itemPostBinding.ivPost);
        }

        @Override
        public void onClick(View v) {
//            //gets item position
//            itemPostBinding.tvUsername.setText(post.getUser_name());
//            Toast.makeText(itemView.getContext(), "This clicks",Toast.LENGTH_SHORT).show();
//            Log.i(TAG, "onClick: this works ");
//            int position = getAdapterPosition();
//            if(position != RecyclerView.NO_POSITION){
//                // get the movie at the position, this won't work if the class is static
//                Post post = posts.get(position);
//                // create intent for the new activity
//                Intent intent = new Intent(context, DetailActivity.class);
//                // serialize the movie using parceler, use its short name as a key
//                //intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
//                intent.putExtra("post", post);
//                // show the activity
//                context.startActivity(intent);
//            }
        }
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> post) {
        posts.addAll(post);
        notifyDataSetChanged();
    }
}
