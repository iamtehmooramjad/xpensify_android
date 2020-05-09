package com.dev175.xpensify.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dev175.xpensify.R;

public class ContactusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);

        getSupportActionBar().setTitle("Contact Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
