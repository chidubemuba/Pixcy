package com.example.pixcy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pixcy.databinding.ActivityLoginBinding;
import com.example.pixcy.models.Post;
import com.example.pixcy.models.User;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;
    private FirebaseAuth mAuth;
    public static final String TAG = "LoginActivity";
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    private BeginSignInRequest signInRequest;
    private SignInClient oneTapClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Login");         //Set or remove the title

        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = activityLoginBinding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();         // Initialize Firebase Auth
        FirebaseUser currentUser = mAuth.getCurrentUser();         // Check if user is signed in (non-null) and update UI accordingly.
        if (currentUser != null && currentUser.isEmailVerified()) {
            activityLoginBinding.pbLogin.setVisibility(View.VISIBLE);
            login(currentUser);
        } else {
            // Show Hide password using Eye Icon
            activityLoginBinding.ivShowHidePassword.setImageResource(R.drawable.ic_baseline_visibility_off_24);
            activityLoginBinding.ivShowHidePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activityLoginBinding.etLoginPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                        // If password is visible then hide it
                        activityLoginBinding.etLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        activityLoginBinding.ivShowHidePassword.setImageResource(R.drawable.ic_baseline_visibility_off_24); // Change icon
                    } else {
                        activityLoginBinding.etLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        activityLoginBinding.ivShowHidePassword.setImageResource(R.drawable.ic_baseline_visibility_24);
                    }
                }
            });

            // Login User
            activityLoginBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activityLoginBinding.btnLogin.setEnabled(false);
                    String textEmail = activityLoginBinding.etLoginEmail.getText().toString();
                    String textPassword = activityLoginBinding.etLoginPassword.getText().toString();

                    if (TextUtils.isEmpty(textEmail)) {
                        Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                        activityLoginBinding.etLoginEmail.setError("Email is required");
                        activityLoginBinding.etLoginEmail.requestFocus();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                        Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                        activityLoginBinding.etLoginEmail.setError("Valid email is required");
                        activityLoginBinding.etLoginEmail.requestFocus();
                    } else if (TextUtils.isEmpty(textPassword)) {
                        Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                        activityLoginBinding.etLoginPassword.setError("Password is required");
                        activityLoginBinding.etLoginPassword.requestFocus();
                    } else {
                        activityLoginBinding.pbLogin.setVisibility(View.VISIBLE);
                        loginUser(textEmail, textPassword);
                    }
                }
            });

            // Reset Password
            activityLoginBinding.tvForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                }
            });

            // Register User
            activityLoginBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, Register_Activity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                activityLoginBinding.btnLogin.setEnabled(true);
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail: success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    // Check if email is verified before user can access their profile
                    if (user.isEmailVerified()) {
                        login(user);
                        Toast.makeText(LoginActivity.this, "Log in successful", Toast.LENGTH_SHORT).show();
                    } else {
                        user.sendEmailVerification();
                        activityLoginBinding.pbLogin.setVisibility(View.GONE);
                        mAuth.signOut(); // sign out user
                        showAlertDialog();
                    }
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        activityLoginBinding.etLoginEmail.setError("User doesn't exist or is no longer valid. Please register again");
                        activityLoginBinding.etLoginEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        activityLoginBinding.etLoginEmail.setError("Invalid credentials. Kindly, check and re-enter");
                        activityLoginBinding.etLoginEmail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Log.w(TAG, "signInWithEmail: failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    activityLoginBinding.pbLogin.setVisibility(View.GONE);
                }
            }
        });
    }

    private void login(FirebaseUser firebaseUser) {
        FirebaseFirestore firestoredb = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestoredb.collection("users").document(firebaseUser.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null || value == null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                User user = value.toObject(User.class);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userId", firebaseUser.getUid());
                intent.putExtra("user", Parcels.wrap(user));
                activityLoginBinding.pbLogin.setVisibility(View.GONE);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showAlertDialog() {
        // Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification.");

        // Open email apps if user clicks/taps continue button
        builder.setPositiveButton("Continue to email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // To open email app in new window and not within our app
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();         // Create the AlertDialog
        alertDialog.show();         // Show the AlertDialog
    }
}
