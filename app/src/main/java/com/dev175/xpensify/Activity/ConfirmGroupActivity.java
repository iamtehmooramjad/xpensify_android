package com.dev175.xpensify.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.dev175.xpensify.Adapter.ConfirmContactAdapter;
import com.dev175.xpensify.Common.Common;
import com.dev175.xpensify.LoadingDialog;
import com.dev175.xpensify.Model.Contact;
import com.dev175.xpensify.Model.GroupUser;
import com.dev175.xpensify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ConfirmGroupActivity extends AppCompatActivity {

    ListView listview_contact;
    AppCompatButton confirmGroup;
    ConfirmContactAdapter adapter;
    ContactId contactId;
    TextInputEditText groupNameEditText;

    LoadingDialog dialog ;
    String groupId;
    CollectionReference membersCollection;
    public static final String TAG = ConfirmGroupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_group);
        setTitle();
        init();

        listview_contact.setAdapter(adapter);

    }

    private void init() {
        listview_contact = (ListView)findViewById(R.id.listview_contact);
        confirmGroup = (AppCompatButton) findViewById(R.id.confirmGroup);
        groupNameEditText = (TextInputEditText) findViewById(R.id.groupNameEditText);
        adapter = new ConfirmContactAdapter(this, Common.groupContactsList);
        dialog = new LoadingDialog(this);

        contactId = new ContactId();


        Boolean isUserAdded = false;
        for (int i=0; i<Common.groupContactsList.size();i++)
        {
            Contact contact = Common.groupContactsList.get(i);
            if (PhoneNumberUtils.compare(contact.getPhoneNumber(),Common.currentUser.getPhoneNumber()))
            {
                isUserAdded = true;
                break;
            }
        }

        if (isUserAdded)
        {

            if (!Common.groupContactsList.isEmpty()){
                confirmGroup.setBackgroundResource(R.drawable.btn_rounded_accent);
                confirmGroup.setEnabled(true);

            }
        }
        else {
            Toast.makeText(this, "Please must add yourself to group!", Toast.LENGTH_LONG).show();
        }


    }

    private void setTitle() {
        try {
            getSupportActionBar().setTitle("Confirm Group");
        }
        catch (NullPointerException e)
        {
            Log.e(TAG, "onCreate: "+e.getMessage() );
        }
    }

    //When clicked on back arrow button in actionBar,to save the state of previous checked contacts
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    public void confirmGroup(View view) {

        getContactIdsOfNumbersFromDb();

        Intent intent = new Intent(this,HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    private void addContacts(ContactId contactId) {

        //contact Id contains contactIds of all contacts,Now, we will get only those ids which are selected contatcs
        dialog.startLoadingDialog();
        final ArrayList<Contact> contactsGroup = new ArrayList<Contact>();

        for (int i=0;i<Common.groupContactsList.size();i++)
        {
            if (Common.groupContactsList.get(i).getChecked()==true)
            {
                contactsGroup.add(Common.groupContactsList.get(i));
            }
        }

        //contactsGroup :> Selected contacts
        //contactIds :> all ids of registered contacts, we have to get ids of only selected contacts

        Map<String,String> contactIdOfSelectedNumbers = new HashMap<>();
        for (int j=0;j<contactsGroup.size();j++)
        {
            if (contactId.getContactId().containsKey(contactsGroup.get(j).getPhoneNumber()))
            {
                String id=contactId.getContactId().get(contactsGroup.get(j).getPhoneNumber());
                contactIdOfSelectedNumbers.put(contactsGroup.get(j).getPhoneNumber(),id);
            }
        }


        //contactsGroup = > Contains contacts of group members
        //contactId = > Contains Ids of docs of each user.We will pass it phoneNumbers of contactsGroup as key to get id

        Map<String,String> userIds = contactIdOfSelectedNumbers;
        ArrayList<String> user_IdList = new ArrayList<>();

       for (int i=0;i<userIds.size();i++)
       {
           user_IdList.add(userIds.get(contactsGroup.get(i).getPhoneNumber()));
           Log.d(TAG, "addContacts: "+user_IdList.get(i));

       }

        Log.d(TAG, "addContacts: user_Id"+user_IdList.size());
       
       //ArrayList(user_IdList) => contains userIds of 
        
        //Loop over user ids
        for (int j=0;j<user_IdList.size();j++)
        {

            Map<String ,String> groupNameMap = new HashMap<>();
            groupNameMap.put(Common.groupName,groupNameEditText.getText().toString());
            groupNameMap.put(Common.groupStatus,Common.joined);

            final DocumentReference documentReference = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user_IdList.get(j))
                    .collection("groups")
                    .document();
            documentReference.set(groupNameMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: Group Name Saved");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: "+e.getMessage() );
                        }
                    });

            membersCollection = documentReference.collection("members");

            for (int k=0;k<contactsGroup.size();k++)
            {
                Contact contact = contactsGroup.get(k);

                GroupUser groupUser = new GroupUser();
                groupUser.setGroupName(groupNameEditText.getText().toString());
                groupUser.setFullName(contact.getName());
                groupUser.setPhoneNumber(contact.getPhoneNumber());

                Map<String,GroupUser> groupUserMap = new HashMap<>();
                groupUserMap.put(membersCollection.getId().toString(),groupUser);

                Log.d(TAG, "addContacts: membersCollectionID"+membersCollection.getId());

                membersCollection.add(groupUserMap)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "onSuccess: User Added");
                                dialog.dismissDialog();
                                Common.groupContactsList.clear();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: User not Added");
                                dialog.dismissDialog();
                                Common.groupContactsList.clear();

                            }
                        });

            }
            
        }


        if (dialog.getDialog().isShowing())
        {
            dialog.dismissDialog();
        }

    }



    //Get Map Object of phone numbers and their ids
    public void getContactIdsOfNumbersFromDb()
    {
        final Map<String,String> registeredContacts = new HashMap<>();

        CollectionReference phonenumbersCol = FirebaseFirestore.getInstance().collection("phonenumbers");

        phonenumbersCol.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        int i =0;
                        for ( QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            //key,value(number as key,id as value);
                            registeredContacts.put((String) documentSnapshot.get(Common.phoneNumber),documentSnapshot.getId());
                            Log.d(TAG, "onSuccess: "+registeredContacts.get((String)documentSnapshot.get(Common.phoneNumber)));
                            i++;
                        }

                        Log.d(TAG, "onSuccess: "+registeredContacts);
                        contactId.setContactId(registeredContacts);//This contains ids of numbers
                        addContacts(contactId);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: "+e.getMessage());
                    }
                });


    }

    //Class to get registere number id's from db.
    class ContactId
    {
        private Map<String ,String> contactId;

        public ContactId(Map<String, String> contactId) {
            this.contactId = contactId;
        }

        public ContactId() {
        }

        public Map<String, String> getContactId() {
            return contactId;
        }

        public void setContactId(Map<String, String> contactId) {
            this.contactId = contactId;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (dialog.getDialog()!=null)
        {
            if (dialog.getDialog().isShowing())
            {
                dialog.dismissDialog();
            }
        }
    }
}

