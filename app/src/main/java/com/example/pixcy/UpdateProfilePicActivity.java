package com.example.pixcy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pixcy.databinding.ActivityUpdateProfilePicBinding;
import com.example.pixcy.fragments.MemoriesFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateProfilePicActivity extends AppCompatActivity {

    private ActivityUpdateProfilePicBinding activityUpdateProfilePicBinding;
    private FirebaseUser firebaseUser;
    public static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUpdateProfilePicBinding = ActivityUpdateProfilePicBinding.inflate(getLayoutInflater());
        View view = activityUpdateProfilePicBinding.getRoot();
        setContentView(view);

        getSupportActionBar().setTitle("Update Profile Picture");

        storage = FirebaseStorage.getInstance();        // Create an instance of FirebaseStorage
        storageRef = storage.getReference("ProfilePic");        // Create a storage reference from our app

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Uri uri = firebaseUser.getPhotoUrl();
        Glide.with(UpdateProfilePicActivity.this)
                .load(uri)
                .into(activityUpdateProfilePicBinding.ivProfileDp);

        activityUpdateProfilePicBinding.btnChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        activityUpdateProfilePicBinding.btnUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityUpdateProfilePicBinding.pbUpload.setVisibility(View.VISIBLE);
                UploadPic();
                Intent intent = new Intent(UpdateProfilePicActivity.this, MemoriesFragment.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void UploadPic() {
        if (uriImage != null) {
            // Save the image with uid of the currently logged user
            StorageReference fileReference = storageRef.child(firebaseUser.getUid() + "." + getFileExtension(uriImage));
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;

                            //Finally set the display image of the user after upload
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);
                            Toast.makeText(UpdateProfilePicActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                    activityUpdateProfilePicBinding.pbUpload.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateProfilePicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(UpdateProfilePicActivity.this, "No picture selected", Toast.LENGTH_SHORT).show();
            activityUpdateProfilePicBinding.pbUpload.setVisibility(View.GONE);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
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
            Glide.with(UpdateProfilePicActivity.this)
                    .load(uriImage)
                    .into(activityUpdateProfilePicBinding.ivProfileDp);
        }
    }
}