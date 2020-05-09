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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = SignupActivity.class.getSimpleName();

    //TextinputLayouts for validation purposes
    TextInputLayout inputLayoutFullName,inputLayoutEmail,inputLayoutPassword,inputLayoutPhone;
    EditText fullName,email,password,phoneNum;

    //Toolbar toolbar;
    ProgressBar progressBar;
    AppCompatButton signup;
    TextView login;
    String userID;

    //For Firebase
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        initializeWidgets();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        //On Clicking Signup Button (Signup Start)
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //This method will validate the textfields
                Boolean result =validateFields();

                fullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                inputLayoutFullName.setError("");
                                inputLayoutFullName.setErrorTextColor(ColorStateList.valueOf(getColor(R.color.colorText)));
                            }
                        }
                    }
                });

                password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                inputLayoutPassword.setError("");
                                inputLayoutPassword.setErrorTextColor(ColorStateList.valueOf(getColor(R.color.colorText)));
                            }
                        }
                    }
                });

                email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                inputLayoutEmail.setError("");
                                inputLayoutEmail.setErrorTextColor(ColorStateList.valueOf(getColor(R.color.colorText)));
                            }
                        }
                    }
                });

                phoneNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                inputLayoutPhone.setError("");
                                inputLayoutPhone.setErrorTextColor(ColorStateList.valueOf(getColor(R.color.colorText)));
                            }
                        }
                    }
                });


                if (result) {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) //If user sign up successfully
                                    {
                                        firebaseAuth.getCurrentUser()
                                                .sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            //Storing User Data in Firebasefirestore
                                                            userID = firebaseAuth.getInstance().getUid();
                                                            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);

                                                            final Map<String, Object> user = new HashMap<>();
                                                            user.put(Common.fullName, fullName.getText().toString());
                                                            user.put(Common.phoneNumber, phoneNum.getText().toString());
                                                            user.put(Common.email, email.getText().toString());
                                                            user.put(Common.password, password.getText().toString());
                                                            user.put(Common.uri,"");

                                                            documentReference.set(user)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d("TAG", "User profile is created for " + userID);
                                                                }
                                                            });
                                                            //Save phoneNumber in "phoneNumbers" Collection
                                                            savePhoneNumber();
                                                            Toast.makeText(SignupActivity.this, "Sign up Successful.\nPlease verify your email to Login.", Toast.LENGTH_SHORT).show();
                                                            email.setText("");
                                                            password.setText("");
                                                            fullName.setText("");
                                                            phoneNum.setText("");
                                                        } else {
                                                            Log.d(TAG, "onComplete: "+task.getException());
                                                            Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });

                                    } else //if any error occurs
                                    {
                                        Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }




            }
        });
        //(Signup End)

        //Login Button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }
        });



    }

    private void savePhoneNumber() {
        String phone = phoneNum.getText().toString();

        //Storing User Data in Firebasefirestore
        userID = firebaseAuth.getInstance().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("phonenumbers").document(userID);
        Map<String,String> map = new HashMap<>();
        map.put(Common.phoneNumber,phone);
        documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "onSuccess: "+userID+" Password saved!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "onFailure: "+userID+" Password not saved!");
                    }
                });

    }


    private Boolean validateFields() {
        Boolean isValid = true;
        //Full name
        if (fullName.getText().toString().isEmpty())
        {
            inputLayoutFullName.setError("\u2022 Full Name is Required!");
            isValid = false;
//            return isValid;
        }
        else if (fullName.getText().toString().length()<3)
        {
            inputLayoutFullName.setError("\u2022 Full Name Should be > 2 chars!");
            isValid = false;
//            return isValid;
        }
        else
        {
            inputLayoutFullName.setErrorEnabled(false);

        }

        //Email
        if (email.getText().toString().isEmpty())
        {
            inputLayoutEmail.setError("\u2022 Email is Required!");
            isValid = false;
//            return isValid;
        }
        else
        {
            inputLayoutEmail.setErrorEnabled(false);
        }
        //Password
        if (password.getText().toString().isEmpty())
        {
            inputLayoutPassword.setError("\u2022 Password is Required!");
            isValid = false;
//            return isValid;
        }
        else if (password.getText().toString().length()<8)
        {
            inputLayoutPassword.setError("\u2022 Password Should be at least 8 chars long!");
            isValid=false;
//            return isValid;
        }
        else if (password.getText().toString().length()>12)
        {
            inputLayoutPassword.setError("\u2022 Password Should not be greater than 12 chars!");
            isValid=false;
//            return isValid;
        }
        else
        {
            inputLayoutPassword.setErrorEnabled(false);

        }

        if (phoneNum.getText().toString().isEmpty())
        {
            inputLayoutPhone.setError("\u2022 Phone number is required!");
            isValid=false;
//            return isValid;
        }
        else if (phoneNum.getText().toString().trim().length()!=11)
        {
            inputLayoutPhone.setError("\u2022 Phone number Length must be 11 digits long");
            isValid = false;
//            return isValid;
        }
        else if (phoneNum.getText().toString().startsWith("+923"))
        {
            inputLayoutPhone.setError("\u2022 Phone number must be started with 03");
            isValid = false;
        }
        else{
            inputLayoutPhone.setErrorEnabled(false);
        }

        return isValid;

//        if (TextUtils.isEmpty(fullName.getText().toString()))
//        {
//            fullName.setError("Name is required !");
//            fullName.requestFocus();
//            return;
//        }

    }

    private void initializeWidgets() {
        //Referencing Views
        progressBar = findViewById(R.id.login_progress);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        signup = findViewById(R.id.btnSignup);
        login = findViewById(R.id.btnLogin);
        phoneNum=findViewById(R.id.phoneNum);
        fullName = findViewById(R.id.fullName);
        inputLayoutEmail = findViewById(R.id.inputLayoutEmail);
        inputLayoutFullName = findViewById(R.id.inputLayoutFullName);
        inputLayoutPassword = findViewById(R.id.inputLayoutPassword);
        inputLayoutPhone = findViewById(R.id.inputLayoutPhone);

    }


}








