package com.example.pixcy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pixcy.databinding.ActivityRegisterBinding;
import com.example.pixcy.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class Register_Activity extends AppCompatActivity {

    private ActivityRegisterBinding activityRegisterBinding;
    private RadioButton rbRegisterGenderSelected;
    private DatePickerDialog picker;
    public static final int MAX_BIO_LENGTH = 180;
    public static final String TAG = "Register_Activity";
    private final int REQUEST_CODE = 20;
    private Uri uriImages;
    FirebaseStorage storage;
    StorageReference storageRef;

    private interface GetUriCallback {
        void done(Uri uri, Exception e);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = activityRegisterBinding.getRoot();
        setContentView(view);

        getSupportActionBar().setTitle("User Registration");
        activityRegisterBinding.rgRegisterGender.clearCheck();

        activityRegisterBinding.ivRegisterProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register_Activity.this, ProfilePictureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // Setting up Date picker on EditText
        activityRegisterBinding.etRegisterDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                // Date picker dialog
                picker = new DatePickerDialog(Register_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        activityRegisterBinding.etRegisterDOB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        activityRegisterBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId = activityRegisterBinding.rgRegisterGender.getCheckedRadioButtonId();
                rbRegisterGenderSelected = findViewById(selectedGenderId);

                // Obtain the data entered by the user
                String textFullName = activityRegisterBinding.etRegisterFullName.getText().toString();
                String textUsername = activityRegisterBinding.etRegisterUsername.getText().toString();
                String textBio = activityRegisterBinding.etRegisterBio.getText().toString();
                String textEmail = activityRegisterBinding.etRegisterEmail.getText().toString();
                String textDOB = activityRegisterBinding.etRegisterDOB.getText().toString();
                String textPassword = activityRegisterBinding.etRegisterPassword.getText().toString();
                String textConfirmPassword = activityRegisterBinding.etRegisterConfirmPassword.getText().toString();
                String textGender; // Can't obtain the value before verifying if any button was selected or not

                if (uriImages == null) {
                    Toast.makeText(Register_Activity.this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(Register_Activity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterFullName.setError("Full name is required");
                    activityRegisterBinding.etRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textUsername)) {
                    Toast.makeText(Register_Activity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterUsername.setError("Username is required");
                    activityRegisterBinding.etRegisterUsername.requestFocus();
                } else if (TextUtils.isEmpty(textBio)) {
                    Toast.makeText(Register_Activity.this, "Please enter your bio", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterBio.setError("Bio is required");
                    activityRegisterBinding.etRegisterBio.requestFocus();
                } else if (textBio.length() > MAX_BIO_LENGTH) {
                    Toast.makeText(Register_Activity.this, "Bio should be at most 180 characters", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterBio.setError("Bio is too long");
                    activityRegisterBinding.etRegisterBio.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(Register_Activity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterEmail.setError("Email is required");
                    activityRegisterBinding.etRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(Register_Activity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterEmail.setError("Valid email is required");
                    activityRegisterBinding.etRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDOB)) {
                    Toast.makeText(Register_Activity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterDOB.setError("Date of birth is required");
                    activityRegisterBinding.etRegisterDOB.requestFocus();
                } else if (activityRegisterBinding.rgRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(Register_Activity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.tvRegisterGender.setError("Gender is required");
                    activityRegisterBinding.tvRegisterGender.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(Register_Activity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterPassword.setError("Password is required");
                    activityRegisterBinding.etRegisterPassword.requestFocus();
                } else if (textPassword.length() < 6) {
                    Toast.makeText(Register_Activity.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterPassword.setError("Password is too weak");
                    activityRegisterBinding.etRegisterPassword.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPassword)) {
                    Toast.makeText(Register_Activity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterConfirmPassword.setError("Password confirmation is required");
                    activityRegisterBinding.etRegisterConfirmPassword.requestFocus();
                } else if (!textPassword.equals(textConfirmPassword)) {
                    Toast.makeText(Register_Activity.this, "Please re-enter your passwords and ensure they are the same", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterConfirmPassword.setError("Ensure your passwords are the same");
                    activityRegisterBinding.etRegisterConfirmPassword.requestFocus();
                    // clear the entered passwords
                    activityRegisterBinding.etRegisterPassword.clearComposingText();
                    activityRegisterBinding.etRegisterConfirmPassword.clearComposingText();
                } else {
                    textGender = rbRegisterGenderSelected.getText().toString();
                    activityRegisterBinding.pbRegister.setVisibility(View.VISIBLE);
                    uploadPhoto(new GetUriCallback() {
                        @Override
                        public void done(Uri uri, Exception e) {
                            registerUser(textFullName, textUsername, textBio, textEmail, textDOB, textGender, textPassword, uri.toString());
                        }
                    }, textEmail);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data!= null) {
            uriImages = data.getParcelableExtra("uriImage");
            Log.i(TAG, "Image data: " + uriImages);
            Glide.with(Register_Activity.this).load(uriImages).into(activityRegisterBinding.ivRegisterProfilePic);
        }
    }

    private void registerUser(String textFullName, String textUsername, String textBio, String textEmail, String textDOB, String textGender, String textPassword, String textImageUrl) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(Register_Activity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    firebaseUser.sendEmailVerification();                    // Send Verification Email
                    Toast.makeText(Register_Activity.this, "User registered successfully. Please verify your email", Toast.LENGTH_SHORT).show();

                    // Update display name of user
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    FirebaseFirestore firestoredb = FirebaseFirestore.getInstance();
                    // Enter User Data into the Firebase Firestore Database.
                    User user = new User(textBio, textDOB, textEmail, textGender, textImageUrl, textUsername);
                    firestoredb.collection("users")
                            .document(firebaseUser.getUid())
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    Intent intent = new Intent(Register_Activity.this, MainActivity.class);
                                    // To prevent user from returning back to Register Activity on pressing back button after registration.
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("userId", firebaseUser.getUid());
                                    intent.putExtra("user", Parcels.wrap(user));
                                    startActivity(intent);
                                    finish(); // to close Register Activity
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                    Toast.makeText(Register_Activity.this, "User registered failed. Please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                    activityRegisterBinding.pbRegister.setVisibility(View.GONE);

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        activityRegisterBinding.etRegisterPassword.setError("Your password is too weak." +
                                "Kindly use a mix of alphabets, numbers, and special characters.");
                        activityRegisterBinding.etRegisterPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        activityRegisterBinding.etRegisterEmail.setError("Your email is invalid or already in use. Kindly re-enter.");
                        activityRegisterBinding.etRegisterEmail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        activityRegisterBinding.etRegisterEmail.setError("User is already registered with this email. Use another email.");
                        activityRegisterBinding.etRegisterEmail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Register_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    activityRegisterBinding.pbRegister.setVisibility(View.GONE);
                }
            }
        });
    }

    // Upload photo to Firebase Storage and retrieve photo url of uploaded image
    private void uploadPhoto(GetUriCallback getUriCallback, String email) {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("ProfilePic");
        StorageReference fileReference = storageRef.child(email + "." + getFileExtension(uriImages));
        // Get the data from an ImageView as bytes
        activityRegisterBinding.ivRegisterProfilePic.setDrawingCacheEnabled(true);
        activityRegisterBinding.ivRegisterProfilePic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) activityRegisterBinding.ivRegisterProfilePic.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = fileReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "Post failed", exception);
                Toast.makeText(Register_Activity.this, "Failed" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(Register_Activity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                // Retrieve image url of the uploaded image
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        Log.i(TAG, "result: " + fileReference.getDownloadUrl());
                        return fileReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Log.i(TAG, "result check: " + downloadUri);
                            getUriCallback.done(downloadUri, null);
                        } else {
                            Log.e(TAG, "Failed to get Uri");
                            getUriCallback.done(null, task.getException());
                        }
                    }
                });
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}