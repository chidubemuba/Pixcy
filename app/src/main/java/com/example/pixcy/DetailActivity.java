package com.example.pixcy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.pixcy.databinding.ActivityDetailBinding;
import com.example.pixcy.models.Post;
import com.example.pixcy.models.User;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding activityDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDetailBinding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(activityDetailBinding.getRoot());

        Intent intent = getIntent();
        Post post = Parcels.unwrap(intent.getParcelableExtra("post"));
        User user = Parcels.unwrap(intent.getParcelableExtra("user"));

//        activityDetailBinding.ivPost.set
        Glide.with(this).load(post.getImage_url()).transform(new RoundedCorners(5)).into(activityDetailBinding.ivPost);
        activityDetailBinding.tvDescription.setText(post.getDescription());
//        activityDetailBinding.tvUsername.setText(user.getUsername());


    }

    //itemPostBinding.tvRelativeTime.setText(DateUtils.getRelativeTimeSpanString(post.getTimestamp()));
//            long createdAt = post.getCreation_time_ms();
//            String timeAgo = Post.calculateTimeAgo(createdAt);
//            ParseFile image = post.getImage_url();
//            if (image != null) {
//                Glide.with(context).load(image.getUrl()).into(itemPostBinding.ivPost);
//            }
}