package com.example.pixcy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.pixcy.databinding.ActivityForgotPasswordBinding;
import com.example.pixcy.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding activityForgotPasswordBinding;
    private FirebaseAuth mAuth;
    private static final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = activityForgotPasswordBinding.getRoot();
        setContentView(view);

        getSupportActionBar().setTitle("Forgot Password");

        activityForgotPasswordBinding.btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = activityForgotPasswordBinding.etPasswordResetEmail.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                    activityForgotPasswordBinding.etPasswordResetEmail.setError("Registered email is required");
                    activityForgotPasswordBinding.etPasswordResetEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                    activityForgotPasswordBinding.etPasswordResetEmail.setError("Valid email is required");
                    activityForgotPasswordBinding.etPasswordResetEmail.requestFocus();
                } else {
                    activityForgotPasswordBinding.pbResetPassword.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });


    }

    private void resetPassword(String email) {
        mAuth = FirebaseAuth.getInstance();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your inbox for password reset link", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    // To prevent user from returning back to Main Activity on pressing back button after logging out.
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Close ForgotPasswordActivity
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        activityForgotPasswordBinding.etPasswordResetEmail.setError("User does not exist or is no longer valid. Please register again");
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        activityForgotPasswordBinding.pbResetPassword.setVisibility(View.GONE);
    }
}