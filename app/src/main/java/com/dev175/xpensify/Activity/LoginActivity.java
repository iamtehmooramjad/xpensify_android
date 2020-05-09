package com.dev175.xpensify.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dev175.xpensify.Common.Common;
import com.dev175.xpensify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;


public class LoginActivity extends AppCompatActivity {


    private static final String TAG = LoginActivity.class.getSimpleName();
    ProgressBar progressBar;
    EditText userEmail,userPass;
    TextInputLayout inputLayoutLoginEmail,inputLayoutLoginPassword;
    AppCompatButton userLogin;
    TextView forgotPassword;
    TextView signup;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        initializeWidgets();
        firebaseAuth = FirebaseAuth.getInstance();

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //This method will validate the textfields
                Boolean result =validateLoginFields();

                userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                inputLayoutLoginEmail.setError("");
                                inputLayoutLoginEmail.setErrorTextColor(ColorStateList.valueOf(getColor(R.color.colorText)));
                            }
                        }
                    }
                });

                userPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                inputLayoutLoginPassword.setError("");
                                inputLayoutLoginPassword.setErrorTextColor(ColorStateList.valueOf(getColor(R.color.colorText)));
                            }
                        }
                    }
                });

                if (result){
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(userEmail.getText().toString(),userPass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful())
                                    {
                                           //If email is verified then continue to login
                                        if (firebaseAuth.getCurrentUser().isEmailVerified())
                                        {
                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            Toast.makeText(LoginActivity.this, "Please Verify your email..!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "onComplete: "+task.getException());
                                        Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


        //Reset Password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });

    }


    private Boolean validateLoginFields() {
        Boolean isValid = true;

        //Email
        if (userEmail.getText().toString().isEmpty())
        {
            inputLayoutLoginEmail.setError("\u2022 Email is Required!");
            isValid = false;
        }
        else
        {
            inputLayoutLoginEmail.setErrorEnabled(false);
        }

        //Password
        if (userPass.getText().toString().isEmpty())
        {
            inputLayoutLoginPassword.setError("\u2022 Password is Required!");
            isValid = false;
        }
        else if (userPass.getText().toString().length()<8)
        {
            inputLayoutLoginPassword.setError("\u2022 Password Should be at least 8 chars long!");
            isValid=false;
        }
        else if (userPass.getText().toString().length()>12)
        {
            inputLayoutLoginPassword.setError("\u2022 Password Should not be greater than 12 chars!");
            isValid=false;
        }
        else
        {
            inputLayoutLoginPassword.setErrorEnabled(false);
        }

        return isValid;

    }

    private void initializeWidgets() {
        progressBar = findViewById(R.id.login_progressbar);
        userEmail = findViewById(R.id.etUserEmail);
        userPass = findViewById(R.id.etUserPass);
        userLogin = findViewById(R.id.btnUserLogin);
        forgotPassword = findViewById(R.id.forgotpasswordTV);
        signup = findViewById(R.id.signupTV);
        inputLayoutLoginEmail = findViewById(R.id.inputLayoutLoginEmail);
        inputLayoutLoginPassword = findViewById(R.id.inputLayoutLoginPassword);

    }
}
