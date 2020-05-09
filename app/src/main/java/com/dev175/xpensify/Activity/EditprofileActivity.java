package com.dev175.xpensify.Activity;


import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.bumptech.glide.Glide;
import com.dev175.xpensify.Common.Common;
import com.dev175.xpensify.LoadingDialog;
import com.dev175.xpensify.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class EditprofileActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    AppCompatEditText editFullName,editEmail,editPassword,editPhoneNum;
    TextInputLayout inputLayoutUpdateFullName;
    TextInputLayout inputLayoutUpdatePassword;
    LoadingDialog loadingDialog;

    //Image Uploading & Retrieval
    final int IMAGE_REQUEST = 71;
    Uri imageLocationPath;
    CircularImageView editProfileImage;

    StorageReference objectStorageReference;
    FirebaseFirestore objectFirebaseFirestore;

    //Edit Profile Data
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userCol = db.collection("users");

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final String userID = firebaseAuth.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        init();
        downloadImage();
    }


    private void init() {
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadingDialog = new LoadingDialog(EditprofileActivity.this);

        editFullName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editPhoneNum = findViewById(R.id.editPhoneNumber);
        editProfileImage = findViewById(R.id.editProfileImage);


        //Setting fields in Edit profile activty
        editFullName.setText(Common.currentUser.getFullName());
        editEmail.setText(Common.currentUser.getEmail());
        editPassword.setText(Common.currentUser.getPassword());
        editPhoneNum.setText(Common.currentUser.getPhoneNumber());

        inputLayoutUpdateFullName = findViewById(R.id.inputLayoutUpdateFullName);
        inputLayoutUpdatePassword = findViewById(R.id.inputLayoutUpdatePassword);



        objectStorageReference = FirebaseStorage.getInstance().getReference("imageFolder");
        objectFirebaseFirestore = FirebaseFirestore.getInstance();
    }



    public void editFullNameIcon(View view) {
        editFullName.setEnabled(true);
    }

    public void editPasswordIcon(View view) {
        editPassword.setEnabled(true);
    }

    public void updateUserData(View view) {

        Boolean result = validateFields();

        editFullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        inputLayoutUpdateFullName.setError("");
                        inputLayoutUpdateFullName.setErrorTextColor(ColorStateList.valueOf(getColor(R.color.colorText)));
                    }
                }
            }
        });


        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        inputLayoutUpdatePassword.setError("");
                        inputLayoutUpdatePassword.setErrorTextColor(ColorStateList.valueOf(getColor(R.color.colorText)));
                    }
                }
            }
        });


        //if result == true means valid
        if (result){


            String uid = firebaseUser.getUid();
            //Now find document under this user id and update
            DocumentReference userDoc = userCol.document(uid);
            String fullName = editFullName.getText().toString();
            String password = editPassword.getText().toString();

            Map<String,Object> user = new HashMap<>();
            user.put(Common.fullName,fullName);
            user.put(Common.password,password);
            userDoc.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){


                        Toast.makeText(EditprofileActivity.this, "Your Profile has updated!", Toast.LENGTH_SHORT).show();
                    }
                    else if (!task.isSuccessful())
                    {

                        Toast.makeText(EditprofileActivity.this, "Error Uploading data", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            inputLayoutUpdateFullName.setDefaultHintTextColor(ColorStateList.valueOf(Color.rgb(158,158,158)));
            inputLayoutUpdatePassword.setDefaultHintTextColor(ColorStateList.valueOf(Color.rgb(158,158,158)));
            editFullName.setEnabled(false);
            editPassword.setEnabled(false);

        }
        else
        {

        }

        
    }

    private Boolean validateFields() {

        Boolean isValid = true;

        //Full name
        if (editFullName.getText().toString().isEmpty())
        {
            inputLayoutUpdateFullName.setError("\u2022 Full Name must not be empty!");
            isValid = false;
        }
        else if (editFullName.getText().toString().length()<3)
        {
            inputLayoutUpdateFullName.setError("\u2022 Full Name Should be > 3 chars!");
            isValid = false;
        }
        else
        {
            inputLayoutUpdateFullName.setErrorEnabled(false);

        }


        //Password
        if (editPassword.getText().toString().isEmpty())
        {
            inputLayoutUpdatePassword.setError("\u2022 Password must not be empty!");
            isValid = false;
        }
        else if (editPassword.getText().toString().length()<8)
        {
            inputLayoutUpdatePassword.setError("\u2022 Password Should be at least 8 chars long!");
            isValid=false;
        }
        else if (editPassword.getText().toString().length()>12)
        {
            inputLayoutUpdatePassword.setError("\u2022 Password Should not be greater than 12 chars!");
            isValid=false;
        }
        else
        {
            inputLayoutUpdatePassword.setErrorEnabled(false);

        }

        return isValid;
    }


    //Image Uploading
    public void selectImage(View view){
        try
        {

            Intent objectIntent = new Intent();
            objectIntent.setType("image/*");
            objectIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(objectIntent,IMAGE_REQUEST);
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {   super.onActivityResult(requestCode, resultCode, data);
            loadingDialog.startLoadingDialog();

            imageLocationPath = data.getData();
            Bitmap objectBitmap = null;
            try
            {

                    objectBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageLocationPath);
                    editProfileImage.setImageBitmap(objectBitmap);
                    //Upload image
                    uploadImage();


            }
            catch (IOException e) {
                loadingDialog.dismissDialog();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                loadingDialog.dismissDialog();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void uploadImage(){

        try
        {

            if (!userID.isEmpty() && imageLocationPath!=null)
            {
                String nameOfImage = userID+"."+getExtension(imageLocationPath);
                final StorageReference imageRef = objectStorageReference.child(nameOfImage);

                UploadTask objectUploadTask =  imageRef.putFile(imageLocationPath);
                objectUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful())
                        {
                            throw  task.getException();
                        }
                        return imageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful())
                        {
                            Map<String,String> objectMap = new HashMap<>();
                            objectMap.put("uri",task.getResult().toString());

                            userCol.document(userID)
                                    .set(objectMap, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(EditprofileActivity.this, "Image has uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismissDialog();
                                            downloadImage();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditprofileActivity.this, "Failed to stored Image !", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismissDialog();

                                }
                            });
                        }
                        else if (!task.isSuccessful())
                        {
                            loadingDialog.dismissDialog();
                            Toast.makeText(EditprofileActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            loadingDialog.dismissDialog();
        }

    }

    private String getExtension(Uri uri){
        try
        {
            ContentResolver objectContentResolver = getContentResolver();
            MimeTypeMap objectMimeTypeMap = MimeTypeMap.getSingleton();
            return  objectMimeTypeMap.getExtensionFromMimeType(objectContentResolver.getType(uri));
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public void downloadImage()
    {
        try
        {
            if (!userID.isEmpty())
            {
                DocumentReference objectDocumentReference;
                objectDocumentReference = userCol.document(userID);

                objectDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                       try {

                           String linkOfImage = documentSnapshot.getString("uri");
                           if (!Common.currentUser.getUri().isEmpty())
                           {
                               Common.currentUser.setUri(linkOfImage);
                                Glide.with(EditprofileActivity.this)
                                   .load(linkOfImage)
                                   .into(editProfileImage);
                            }
                       }
                       catch (Exception e)
                       {
                           //You can not start a load for a destroyed activity
                           Toast.makeText(EditprofileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                       }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditprofileActivity.this, "Failed to update image ", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: ");
        Intent intent = new Intent(EditprofileActivity.this,HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
