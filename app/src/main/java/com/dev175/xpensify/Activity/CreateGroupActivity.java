package com.dev175.xpensify.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import com.dev175.xpensify.Adapter.ContactListAdapter;
import com.dev175.xpensify.Common.Common;
import com.dev175.xpensify.Model.Contact;
import com.dev175.xpensify.R;
import java.util.ArrayList;

public class CreateGroupActivity extends AppCompatActivity{


    private ListView lvContacts;
    ContactListAdapter adapter;

    private AppCompatButton next;
    private SearchView searchContactSV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        getSupportActionBar().setTitle("Select Contacts");

        ArrayList<Contact> contactList = Common.contactsArrayList;

        init();


        adapter = new ContactListAdapter(this,contactList);
        lvContacts.setAdapter(adapter);

        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(CreateGroupActivity.this,ConfirmGroupActivity.class));
            }
        });

        searchContactSV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText))
                {
                    adapter.filter("");
                    lvContacts.clearTextFilter();
                }
                else {
                    adapter.filter(newText);
                }
                return true;
            }
        });
    }

    private void init() {
        lvContacts = findViewById(R.id.listview_contact);
        next = (AppCompatButton) findViewById(R.id.next);
        searchContactSV = findViewById(R.id.searchContactSV);
    }


    @Override
    public boolean onSupportNavigateUp() {
        Common.groupContactsList.clear();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
        super.onBackPressed();
    }


}

