package com.example.pixcy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    //itemPostBinding.tvRelativeTime.setText(DateUtils.getRelativeTimeSpanString(post.getTimestamp()));
//            long createdAt = post.getCreation_time_ms();
//            String timeAgo = Post.calculateTimeAgo(createdAt);
//            ParseFile image = post.getImage_url();
//            if (image != null) {
//                Glide.with(context).load(image.getUrl()).into(itemPostBinding.ivPost);
//            }
}