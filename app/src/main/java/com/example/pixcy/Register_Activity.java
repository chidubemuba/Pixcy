package com.example.pixcy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.pixcy.databinding.ActivityRegisterBinding;

public class Register_Activity extends AppCompatActivity {

    private ActivityRegisterBinding activityRegisterBinding;
    private RadioButton rbRegisterGenderSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityRegisterBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = activityRegisterBinding.getRoot();
        setContentView(view);

        // TODO: Radio button for gender
        activityRegisterBinding.rgRegisterGender.clearCheck();

        // function that is carried out when registration button is clicked
        activityRegisterBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedGenderId = activityRegisterBinding.rgRegisterGender.getCheckedRadioButtonId();
                rbRegisterGenderSelected = findViewById(selectedGenderId);

                // Obtain the data entered by the user
                String textFullName = activityRegisterBinding.etRegisterFullName.getText().toString();
                String textUsername = activityRegisterBinding.etRegisterUsername.getText().toString();
                String textEmail = activityRegisterBinding.etRegisterEmail.getText().toString();
                String textDOB = activityRegisterBinding.etRegisterDOB.getText().toString();
                String textPassword = activityRegisterBinding.etRegisterPassword.getText().toString();
                String textConfirmPassword = activityRegisterBinding.etRegisterConfirmPassword.getText().toString();
                String textGender; // Can't obtain the value before verifying if any button was selected or not

                // Check if textFullName is empty and display error message
                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(Register_Activity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterFullName.setError("Full name is required");
                    activityRegisterBinding.etRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textUsername)){
                    Toast.makeText(Register_Activity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterUsername.setError("Username is required");
                    activityRegisterBinding.etRegisterUsername.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(Register_Activity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterEmail.setError("Email is required");
                    activityRegisterBinding.etRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(Register_Activity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterUsername.setError("Valid email is required");
                    activityRegisterBinding.etRegisterUsername.requestFocus();
                } else if (TextUtils.isEmpty(textDOB)){
                    Toast.makeText(Register_Activity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterDOB.setError("Date of birth is required");
                    activityRegisterBinding.etRegisterDOB.requestFocus();
                } else if (activityRegisterBinding.rgRegisterGender.getCheckedRadioButtonId() == -1){
                    Toast.makeText(Register_Activity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterUsername.setError("Gender is required");
                    activityRegisterBinding.etRegisterUsername.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(Register_Activity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterUsername.setError("Password is required");
                    activityRegisterBinding.etRegisterUsername.requestFocus();
                } else if (textPassword.length() < 6) {
                    Toast.makeText(Register_Activity.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterUsername.setError("Password is too weak");
                    activityRegisterBinding.etRegisterUsername.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPassword)) {
                    Toast.makeText(Register_Activity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterUsername.setError("Password confirmation is required");
                    activityRegisterBinding.etRegisterUsername.requestFocus();
                } else if (textPassword.equals(textConfirmPassword)) {
                    Toast.makeText(Register_Activity.this, "Please re-enter your passwords and ensure they are the same", Toast.LENGTH_SHORT).show();
                    activityRegisterBinding.etRegisterUsername.setError("Ensure your passwords are the same");
                    activityRegisterBinding.etRegisterUsername.requestFocus();
                    // clear the entered passwords
                    activityRegisterBinding.etRegisterPassword.clearComposingText();
                    activityRegisterBinding.etRegisterConfirmPassword.clearComposingText();
                } else {
                    textGender = rbRegisterGenderSelected.getText().toString();
                    activityRegisterBinding.pgLoading.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textUsername, textEmail, textDOB, textGender, textPassword);
                }
            }
        });
    }


    
    // Register User using the credentials given
    private void registerUser(String textFullName, String textUsername, String textEmail, String textDOB, String textGender, String textPassword) {

    }
}