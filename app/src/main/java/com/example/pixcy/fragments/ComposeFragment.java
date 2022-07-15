package com.example.pixcy.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pixcy.BuildConfig;
import com.example.pixcy.MainActivity;
import com.example.pixcy.PostsAdapter;
import com.example.pixcy.R;
import com.example.pixcy.databinding.FragmentComposeBinding;
import com.example.pixcy.models.Post;
import com.example.pixcy.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeFragment extends Fragment {

    public static final String TAG = "ComposeFragment";
    private FragmentComposeBinding fragmentComposeBinding;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;
    public double longitude;
    public double latitude;
    public String address;
    public String city;
    public String state;
    public String postal_code;
    public String country;

    // Declare variables for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference mountainImagesRef;

    public ComposeFragment() {
        // Required empty public constructor
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentComposeBinding = FragmentComposeBinding.inflate(getLayoutInflater(), container, false);
        View view = fragmentComposeBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentComposeBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle location_data = getArguments();
        if (location_data != null) {
            latitude = location_data.getDouble("latitude");
            longitude = location_data.getDouble("longitude");
            address = location_data.getString("address");
            city = location_data.getString("city");
            state = location_data.getString("state");
            postal_code = location_data.getString("postal_code");
            country = location_data.getString("country");
        }

        // Create an instance of FirebaseStorage
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();
        // Create a reference to 'images/mountains.jpg'
        mountainImagesRef = storageRef.child("images/mountains.jpg");

        fragmentComposeBinding.btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        fragmentComposeBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = fragmentComposeBinding.etDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (photoFile == null || fragmentComposeBinding.ivPostImage.getDrawable() == null) {
                    Toast.makeText(getContext(), "There's no image!", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Create a post object with the image url and all the required posts documents to the posts collection


                //createPost(String address, String city, String country, String description, String image_url,
               //double latitude, double longitude, String postalCode, String state, Date timestamp);
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                fragmentComposeBinding.ivPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    // Upload photo to Firebase Storage and retrieve photo url of uploaded image
    private void uploadPhoto() {
        // Get the data from an ImageView as bytes
        fragmentComposeBinding.ivPostImage.setDrawingCacheEnabled(true);
        fragmentComposeBinding.ivPostImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) fragmentComposeBinding.ivPostImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

//// Create file metadata including the content type
//        StorageMetadata metadata = new StorageMetadata.Builder()
//                .setContentType("image/jpg")
//                .build();
//
//// Upload the file and metadata
//        uploadTask = storageRef.child("images/mountains.jpg").putFile(file, metadata);


        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "Post failed", exception);
                Toast.makeText(getContext(), "Failed" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                // Retrieve image url of the uploaded image
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return mountainImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });


            }
        });
    }



    public void createPost(String address, String city, String country, String description, String image_url,
                           double latitude, double longitude, String postal_code, String state, Date timestamp) {

        FirebaseFirestore firestoredb = FirebaseFirestore.getInstance();

        // Create a reference to the posts collection where we will be inserting the data
        DocumentReference newPostReference = firestoredb.collection("posts").document();

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Date timestamp
        Post post = new Post();
        post.setAddress(address);
        post.setCity(city);
        post.setCountry(country);
        post.setDescription(description);
        //post.setImage_url(image_url);
        post.setLatitude(latitude);
        post.setLongitude(longitude);
        post.setPostal_code(postal_code);
        post.setState(state);

        post.setUser_id(user_id);




        newPostReference.set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Created new post", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Post failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}