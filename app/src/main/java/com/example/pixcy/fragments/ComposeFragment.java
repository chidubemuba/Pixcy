package com.example.pixcy.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
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
import com.example.pixcy.LocationData;
import com.example.pixcy.databinding.FragmentComposeBinding;
import com.example.pixcy.models.Post;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeFragment extends Fragment {

    public static final String TAG = "ComposeFragment";
    private FragmentComposeBinding fragmentComposeBinding;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;
    public String userId;
    public String description;
    private String image_url;
    public double longitude;
    public double latitude;
    public String address;
    public String city;
    public String state;
    public String postalCode;
    public String country;
    private LocationData locationData;
    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference mountainImagesRef;

    public ComposeFragment() {
        // Required empty public constructor
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentComposeBinding = FragmentComposeBinding.inflate(getLayoutInflater(), container, false);
        View view = fragmentComposeBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentComposeBinding = null;
    }

    public static ComposeFragment newInstance(LocationData locationData, String userId) {
        ComposeFragment composeFragment = new ComposeFragment();
        Bundle args = new Bundle();

        args.putString(ARG_PARAM1, userId);
        args.putParcelable(ARG_PARAM2, Parcels.wrap(locationData));
        composeFragment.setArguments(args);
        return composeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            locationData = Parcels.unwrap(getArguments().getParcelable(ARG_PARAM2));
            userId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        latitude = locationData.getLatitude();
        longitude = locationData.getLongitude();
        address = locationData.getAddress();
        city = locationData.getCity();
        state = locationData.getState();
        postalCode = locationData.getPostalCode();
        country = locationData.getCountry();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mountainImagesRef = storageRef.child("images/" + UUID.randomUUID() + ".jpg");        // Create a reference to 'images/mountains.jpg'

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        fragmentComposeBinding.btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        fragmentComposeBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentComposeBinding.pbSubmit.setVisibility(View.VISIBLE);
                description = fragmentComposeBinding.etDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (photoFile == null || fragmentComposeBinding.ivPostImage.getDrawable() == null) {
                    Toast.makeText(getContext(), "There's no image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        uploadPhoto();
                    }
                });
            }
        });
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Bitmap correctImage = rotateBitmapOrientation(takenImage);
                fragmentComposeBinding.ivPostImage.setImageBitmap(correctImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap rotateBitmapOrientation(Bitmap bitmap) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bounds);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        return rotatedBitmap;
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
                        Log.i(TAG, "result: " + mountainImagesRef.getDownloadUrl());
                        return mountainImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            image_url = downloadUri.toString();
                            Log.i(TAG, "result check: " + downloadUri);
                            createPost(address, city, country, description, image_url, latitude, longitude, postalCode, state, userId);
                            fragmentComposeBinding.pbSubmit.setVisibility(View.GONE);
                        } else {
                            Log.e(TAG, "Failed to get Uri");
                        }
                    }
                });
            }
        });
        Log.i(TAG, "download_url: " + image_url);
    }

    public void createPost(String address, String city, String country, String description, String image_url,
                           double latitude, double longitude, String postalCode, String state, String userId) {
        FirebaseFirestore firestoredb = FirebaseFirestore.getInstance();
        DocumentReference newPostReference = firestoredb.collection("posts").document();

        // Date timestamp
        Post post = new Post();
        post.setAddress(address);
        post.setCity(city);
        post.setCountry(country);
        post.setDescription(description);
        post.setImage_url(image_url);
        post.setLatitude(latitude);
        post.setLongitude(longitude);
        post.setPostalCode(postalCode);
        post.setState(state);
        post.setUser_id(userId);

        newPostReference.set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                } else {
                }
            }
        });
    }
}
