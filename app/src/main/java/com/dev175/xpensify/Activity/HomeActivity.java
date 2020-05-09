package com.dev175.xpensify.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.dev175.xpensify.Common.Common;
import com.dev175.xpensify.LoadingDialog;
import com.dev175.xpensify.Model.CheckContact;
import com.dev175.xpensify.Model.Contact;
import com.dev175.xpensify.Model.User;
import com.dev175.xpensify.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.util.ArrayList;
import java.util.Collections;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static final String TAG = "HomeActivity";
    public Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    public static CircularImageView userImage;

    private LoadingDialog loadingDialog;


    CheckContact checkContact;
    ArrayList<String> registeredContacts;
    Cursor cursor ;


    //FirebaseFirestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    CollectionReference userCol = db.collection("users");
    CollectionReference phonenumbersCol = db.collection("phonenumbers");

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    //Home Activity Username
    TextView userFullName;

    //Navigation View
    TextView header_name;
    TextView header_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        setUserDataOnLogin();
        initComponent();
        setupToolbarMenu();
        setupNavigationDrawerMenu();


        registeredContacts  = new ArrayList<>();
        checkContact = new CheckContact();


       //Get Contacts from db and match with phone contacts
        getRegisteredPhonenumbersFromDb();

    }

    private void setUserDataOnLogin() {
        loadingDialog = new LoadingDialog(HomeActivity.this);
        loadingDialog.startLoadingDialog();

        //   header_name =  findViewById(R.id.header_name);
        header_email = findViewById(R.id.header_email);
        userFullName = findViewById(R.id.userFullName);
        userImage = findViewById(R.id.userImage);
        navView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navView.getHeaderView(0);
        navView.setItemIconTintList(null);

        header_name = headerView.findViewById(R.id.header_name);
        header_email = headerView.findViewById(R.id.header_email);

        String id = firebaseUser.getUid();
        DocumentReference userDoc = userCol.document(id);
        userDoc.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            User user = new User();
                            user.setFullName(documentSnapshot.getString(Common.fullName));
                            user.setEmail(documentSnapshot.getString(Common.email));
                            user.setPassword(documentSnapshot.getString(Common.password));
                            user.setPhoneNumber(documentSnapshot.getString(Common.phoneNumber));
                            user.setUri(documentSnapshot.getString(Common.uri));
                            Common.currentUser = user;

                            userFullName.setText(Common.currentUser.getFullName());
                            header_name.setText(Common.currentUser.getFullName());
                            header_email.setText(Common.currentUser.getEmail());

                            if (!Common.currentUser.getUri().isEmpty())
                            {
                                //Setting user image
                                downloadImage();
                            }

                            loadingDialog.dismissDialog();
                        }
                        else {
                            Toast.makeText(HomeActivity.this, "User not exists !", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });


    }

    private void initComponent() {
        final CircularImageView image =  findViewById(R.id.userImage);
        final CollapsingToolbarLayout collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ((AppBarLayout) findViewById(R.id.app_bar_layout)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int min_height = ViewCompat.getMinimumHeight(collapsing_toolbar) * 2;
                float scale = (float) (min_height + verticalOffset) / min_height;
                image.setScaleX(scale >= 0 ? scale : 0);
                image.setScaleY(scale >= 0 ? scale : 0);
            }
        });
    }


    private void setupToolbarMenu() {
        mToolbar =findViewById(R.id.toolbar);
        mToolbar.setTitle("Home");
    }

    private void setupNavigationDrawerMenu() {
        NavigationView navigationView  = (NavigationView)findViewById(R.id.navigationView);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }



    //On nav items clicked
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId())
        {
            case R.id.item_about:
            {
                Intent intent = new Intent(HomeActivity.this,AboutActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.item_howitworks:{
                Intent intent = new Intent(HomeActivity.this,HowitworksActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.item_editProfile:{
                Intent intent = new Intent(HomeActivity.this,EditprofileActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.item_contactUs:{
                Intent intent = new Intent(HomeActivity.this,ContactusActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.item_signout:{

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                Common.currentUser = null;

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                break;
            }
            default:{

            }

        }

        closeDrawer();
        return false;
    }

    //Close the Drawer
    private void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    //Open the Drawer
    private void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    //If drawer is open then back btn will close it otherwise exit the app
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            closeDrawer();
        }
        else
        {
            super.onBackPressed();
        }
    }

    //Update User Image
    public void downloadImage()
    {
        try
        {
            String  userID = FirebaseAuth.getInstance().getUid();

            if (!userID.isEmpty())
            {
                DocumentReference objectDocumentReference;
                objectDocumentReference = userCol.document(userID);

                objectDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String linkOfImage = documentSnapshot.getString(Common.uri);
                        if (Common.currentUser.getUri() != "")
                        {
                            Glide.with(HomeActivity.this)
                                    .load(linkOfImage)
                                    .into(userImage);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "Failed to update image ", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void showCreateGroupActivity(View view) {
        Intent intent = new Intent(HomeActivity.this, CreateGroupActivity.class);
        startActivity(intent);

    }

    public void showManageGroupActivity(View view) {
        Intent intent = new Intent(HomeActivity.this, ManageGroupActivity.class);
        startActivity(intent);
    }


    public void showAddExpenseActivity(View view) {
        Intent intent = new Intent(HomeActivity.this, AddExpenseActivity.class);
        startActivity(intent);
    }

    //Get phone Contacts and send to Create Group Activity
    public void getContactsIntoArrayList(CheckContact checkContact){
        //Phone Contacts to Send to Next Activity
        Common.contactsArrayList = null;
        ArrayList<Contact> mContactList = new ArrayList<>();
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            if (phoneNumber.startsWith("+92"))
            {
                phoneNumber = phoneNumber.replaceFirst("\\+92","0");
                if (phoneNumber.contains(" ") || phoneNumber.contains("-"))
                {
                    phoneNumber = phoneNumber.replaceAll("\\s","");
                    phoneNumber = phoneNumber.replaceAll("-","");
                }
            }

            Contact contact = new Contact();
            int i=0;
            for ( i=0 ; i<checkContact.getRegisterContactsList().size(); i++)
            {
                if(PhoneNumberUtils.compare(checkContact.getRegisterContactsList().get(i), phoneNumber))
                {
                    contact.setStatus("Available");
                    contact.setPhoneNumber(phoneNumber);
                    break;
                }

                else
                {
                    contact.setStatus("Invite your friend!");
                    contact.setPhoneNumber(phoneNumber);
                }

            }

            contact.setName(name);
            contact.setChecked(false);


            if (!mContactList.contains(contact))
            {
                mContactList.add(contact);

            }

        }

        Collections.sort(mContactList,Contact.contactListSortByName);
        Collections.sort(mContactList,Contact.contactListSortByStatus);

        ArrayList<Contact> noRepeat = new ArrayList<>();

        //Removing Repeated names
        for (Contact contact : mContactList) {
            boolean isFound = false;
            // check if the contact  exists in noRepeat
            for (Contact c : noRepeat) {
                if (c.getPhoneNumber().equals(contact.getPhoneNumber()) || (c.equals(contact))) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound)
            {
                noRepeat.add(contact);
            }
        }

        Common.contactsArrayList=noRepeat;
        cursor.close();

    }

    //Get all docs in "phonenumbers" Collection and then add these doc ids in arraylist
    public void getRegisteredPhonenumbersFromDb()
    {
        final LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();
        phonenumbersCol.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for ( QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            registeredContacts.add((String) documentSnapshot.get(Common.phoneNumber));
//                            Log.d(TAG, "onSuccess: "+registeredContacts.get(i))
                        }

                        loadingDialog.dismissDialog();
                        Log.d(TAG, "onSuccess: "+registeredContacts);
                        checkContact.setRegisterContactsLit(registeredContacts);
                        getContactsIntoArrayList(checkContact);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.dismissDialog();
                        Log.e(TAG, "onFailure: "+e.getMessage());
                    }
                });


        if (loadingDialog.getDialog().isShowing())
            loadingDialog.dismissDialog();
    }




    @Override
    protected void onPause() {
        super.onPause();
        if (loadingDialog.getDialog()!=null)
        {
            if (loadingDialog.getDialog().isShowing())
            {
                loadingDialog.dismissDialog();
            }
        }
    }

    public void showViewExpensesActivity(View view) {
        Intent intent = new Intent(this,ViewExpensesActivity.class);
        startActivity(intent);
    }



}


