package com.example.pixcy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.pixcy.databinding.ActivityProfilePictureBinding;

public class ProfilePictureActivity extends AppCompatActivity {

    private ActivityProfilePictureBinding activityProfilePictureBinding;
    public static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;
    private static final String TAG = "ProfilePictureActivity";
    private String image_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfilePictureBinding = ActivityProfilePictureBinding.inflate(getLayoutInflater());
        View view = activityProfilePictureBinding.getRoot();
        setContentView(view);

        getSupportActionBar().setTitle("Choose Profile Picture");

        activityProfilePictureBinding.btnChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        activityProfilePictureBinding.btnSavePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriImage != null) {
                    Intent data = new Intent();
                    Log.i(TAG, "Uri: " + uriImage);
                    data.putExtra("uriImage", uriImage);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            image_url = uriImage.toString();
            Glide.with(ProfilePictureActivity.this)
                    .load(uriImage)
                    .into(activityProfilePictureBinding.ivProfileDp);
        }
    }

}