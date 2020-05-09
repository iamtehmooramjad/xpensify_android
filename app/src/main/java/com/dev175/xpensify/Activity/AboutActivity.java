package com.dev175.xpensify.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.dev175.xpensify.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
}
