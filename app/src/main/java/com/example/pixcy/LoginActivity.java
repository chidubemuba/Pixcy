package com.example.pixcy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    public static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set or remove the title
        getSupportActionBar().setTitle("Login");

        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = activityLoginBinding.getRoot();
        setContentView(view);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Show Hide password using Eye Icon
        activityLoginBinding.ivShowHidePassword.setImageResource(R.drawable.ic_baseline_visibility_off_24);
        activityLoginBinding.ivShowHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityLoginBinding.etLoginPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    // If password is visible then hide it
                    activityLoginBinding.etLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // Change icon
                    activityLoginBinding.ivShowHidePassword.setImageResource(R.drawable.ic_baseline_visibility_off_24);
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


        // Register User
        activityLoginBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Register_Activity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail: success");
                    Toast.makeText(LoginActivity.this, "Log in successful", Toast.LENGTH_SHORT).show();
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    updateUI(user);
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
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail: failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
                activityLoginBinding.pbLogin.setVisibility(View.GONE);
            }
        });
    }
}
