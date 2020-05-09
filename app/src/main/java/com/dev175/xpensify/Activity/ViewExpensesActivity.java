package com.dev175.xpensify.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.dev175.xpensify.Common.Common;
import com.dev175.xpensify.Fragment.NoExpenseFoundFragment;
import com.dev175.xpensify.Fragment.ViewExpenseDetailsFragment;
import com.dev175.xpensify.Fragment.ViewGroupExpensesFragment;
import com.dev175.xpensify.Interface.IGetGroupExpensesListener;
import com.dev175.xpensify.Interface.IGetGroupIdsListener;
import com.dev175.xpensify.LoadingDialog;
import com.dev175.xpensify.Model.Expense;
import com.dev175.xpensify.Model.ExpenseUser;
import com.dev175.xpensify.Model.Group;
import com.dev175.xpensify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ViewExpensesActivity extends AppCompatActivity implements IGetGroupIdsListener , IGetGroupExpensesListener , FragmentManager.OnBackStackChangedListener {

    private static final String TAG = ViewExpensesActivity.class.getSimpleName();
    private IGetGroupIdsListener iGetGroupIdsListener;
    private IGetGroupExpensesListener iGetGroupExpensesListener;
    private Group groupObj;
    private String groupId;
    private FragmentManager manager;
    private LoadingDialog dialog;
    private MaterialSpinner groupSpinnerExpense;
    private AppCompatButton groupButtonExpense;
    private ArrayAdapter spinnerGroupAdapter;
    private LinearLayout viewExpenseLyt;
    private String selectedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);

        try {
            getSupportActionBar().setTitle("View Expenses");
        }
        catch (Exception e){
            Log.e(TAG, "onCreate: "+e.getMessage());
        }

        init();
        getGroupIdsFomDb();

            groupButtonExpense.setEnabled(false);
            groupButtonExpense.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_grey));

        groupSpinnerExpense.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (groupSpinnerExpense.getSelectedIndex()==0)
                {
                    groupButtonExpense.setEnabled(false);
                    groupButtonExpense.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_grey));
                    selectedGroup = "";
                    Toast.makeText(ViewExpensesActivity.this, "Please Select any group", Toast.LENGTH_SHORT).show();
                
                }
                else {
                    //Now date has received
                    groupButtonExpense.setEnabled(true);
                    groupButtonExpense.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_accent));
                    selectedGroup = item.toString();
                   // Toast.makeText(ViewExpensesActivity.this, selectedGroup, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        iGetGroupIdsListener = this;
        iGetGroupExpensesListener = this;
        groupObj = new Group();
        manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(this);
        dialog = new LoadingDialog(this);
        groupSpinnerExpense = (MaterialSpinner) findViewById(R.id.groupSpinnerExpense);
        groupButtonExpense = (AppCompatButton) findViewById(R.id.groupButtonExpense);
        viewExpenseLyt = findViewById(R.id.viewExpenseLyt);

    }


    private void getGroupIdsFomDb() {


        dialog.startLoadingDialog();
        final String userId = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d(TAG, "getUserId : "+userId);

        db.collection(Common.users)
                .document(userId)
                .collection(Common.groups)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            ArrayList<String> groupList = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                            {
                                //Adding Group Id to groupObj groupIdList
                                groupList.add(queryDocumentSnapshot.getId());
                                groupObj.setGroupNameIdList((String) queryDocumentSnapshot.get(Common.groupName),queryDocumentSnapshot.getId());
                                groupObj.setGroupNameList(queryDocumentSnapshot.getId(),(String)queryDocumentSnapshot.get(Common.groupName));

                            }
                            groupObj.setGroupIdList(groupList);


                            if (groupList.size()==0)
                            {


                                viewExpenseLyt.setVisibility(View.GONE);
                                addNotExpenseFoundFragment();
                                dialog.dismissDialog();
                            }

                            else
                            {
                                viewExpenseLyt.setVisibility(View.VISIBLE);
                                iGetGroupIdsListener.onGetGroupsSuccess(groupObj);
                            }

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iGetGroupIdsListener.onGetGroupsLoadFailed(e.getMessage());
                    }
                });


    }



    @Override
    public void onGetGroupsSuccess(Group groupObj) {

        //Set spinner
        setGroupSpinner();

        if (groupObj.getGroupIdList().size()!=0)
        {
            groupButtonExpense.setEnabled(true);
            groupButtonExpense.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_accent));
        }

        int totalGroups = groupObj.getGroupIdList().size();
        Log.d(TAG, "onGetGroupsSuccess: "+totalGroups);



        //Now Send Group Id and Get mAP
        String userId = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Iterator iterator = groupObj.getGroupIdList().iterator();
        while (iterator.hasNext())
        {
            groupId =  iterator.next().toString();
            //    Toast.makeText(this, groupId, Toast.LENGTH_SHORT).show();

            CollectionReference expenseCol = db
                    .collection(Common.users)
                    .document(userId)
                    .collection(Common.groups)
                    .document(groupId)
                    .collection(Common.expenses);

            getExpensesOfGroup(expenseCol,groupId,totalGroups);

        }

    }

    private void setGroupSpinner() {
        Iterator iter = groupObj.getGroupIdList().iterator();
        Map<String ,String > groupsMap = groupObj.getGroupNameList();
        ArrayList<String> groups = new ArrayList<String>();
        groups.add("Select Group");
        while (iter.hasNext())
        {
            groups.add(groupsMap.get(iter.next()));
        }
        spinnerGroupAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,groups);
        groupSpinnerExpense.setAdapter(spinnerGroupAdapter);
        dialog.dismissDialog();

    }

    private void getExpensesOfGroup(CollectionReference expenseCol, final String groupId, final int totalGroups) {

        expenseCol.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())
                        {
                            ArrayList<Expense> expenseArrayList = new ArrayList<>();

                            for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                            {

                            //    Log.d(TAG, "onComplete: id = "+queryDocumentSnapshot.getId());
                            //    Log.d(TAG, "onComplete: data = "+queryDocumentSnapshot.getData());

                                    Expense expense = queryDocumentSnapshot.toObject(Expense.class);
                                Log.d(TAG, "onComplete: id = "+queryDocumentSnapshot.getId());
                                Log.d(TAG, "onComplete: Expense Data");
                                Log.d(TAG, "onComplete: Title  = "+expense.getTitle());
                                Log.d(TAG, "onComplete: Amount = "+expense.getAmount());
                                Log.d(TAG, "onComplete: Timestamp = "+expense.getTimestamp());

                                    expenseArrayList.add(expense);
                            }

                            groupObj.setGroupExpenseMap(groupId,expenseArrayList);


                            if (groupObj.getGroupIdList().size()==groupObj.getGroupExpenseMap().size())
                            {
                                //     Toast.makeText(ManageGroupActivity.this, "onGetGroupMembersSuccess", Toast.LENGTH_SHORT).show();
                                iGetGroupExpensesListener.onGetGroupExpenseSuccess(groupObj);
                            }

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       iGetGroupExpensesListener.onGetGroupExpenseFailed(e.getMessage());
                    }
                });


    }

    @Override
    public void onGetGroupsLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onGetGroupExpenseSuccess(Group groupObj) {

        if (groupObj.getGroupIdList().size()==groupObj.getGroupExpenseMap().size())
        {

            Log.d(TAG, "Data Received: ");

            for (int i=0;i<groupObj.getGroupIdList().size();i++)
            {
                String id= groupObj.getGroupIdList().get(i);

                Map<String ,ArrayList<Expense>>  expenseMap= groupObj.getGroupExpenseMap();
                ArrayList<Expense> expenseArrayList = expenseMap.get(id);

                Log.d(TAG, "Group Id : "+id);
                Iterator iterator = expenseArrayList.iterator();
                while (iterator.hasNext())
                {
                    Expense e = (Expense) iterator.next();
                    Log.d(TAG, "DR Title = "+e.getTitle());
                    Log.d(TAG, "DR Amount = "+e.getAmount());
                    Log.d(TAG, "DR Timestamp = "+e.getTimestamp());
                    Log.d(TAG, "DR ExpenseUserList = "+e.getExpenseUserList());
                }

            }


        }
    }

    @Override
    public void onGetGroupExpenseFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackStackChanged() {

        if (manager.getBackStackEntryCount()>1)
        {
            viewExpenseLyt.setVisibility(View.GONE);
        }
        else if (manager.getBackStackEntryCount() == 1)
        {
            if (groupObj.getGroupIdList().size()==0)
            {
                viewExpenseLyt.setVisibility(View.GONE);
            }
            else
            {
                viewExpenseLyt.setVisibility(View.VISIBLE);
            }

        }

        int length = manager.getBackStackEntryCount();
        StringBuilder msg = new StringBuilder("");
        for (int i=length-1;i>=0;i--)
        {
            msg.append("Index ").append(i).append(" : ");
            msg.append(manager.getBackStackEntryAt(i).getName());
            msg.append("\n");
        }
        Log.i(TAG, "\n" + msg.toString() + "\n");

    }

    public void onViewButtonClick(View view) {

        if (haveNetworkConnection())
        {

            List<Fragment> fragmentList= manager.getFragments();
            ViewGroupExpensesFragment fragment = (ViewGroupExpensesFragment)manager.findFragmentByTag("viewGroupExpenseF");
            if (fragmentList.contains(fragment))
            {
                manager.popBackStack("addViewGroupExpenseF",0);
                removeViewGroupExpensesFragment();
            }

            if (groupSpinnerExpense.getSelectedIndex()==0)
            {
                Toast.makeText(this, "Please Select any group!", Toast.LENGTH_SHORT).show();
                groupButtonExpense.setEnabled(false);
                groupButtonExpense.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_grey));
                return;
            }

            Map<String,String> groupIdMap = groupObj.getGroupNameIdList();
            String groupId = groupIdMap.get(selectedGroup);

            Map<String,ArrayList<Expense>> groupExpenses = groupObj.getGroupExpenseMap();

            //Get Group Expense by giving group Id
            ArrayList<Expense> groupExpenseList = groupExpenses.get(groupId);
            addViewGroupExpensesFragment(groupExpenseList);




        }else {
            showDialog();
        }


    }

    private void addViewGroupExpensesFragment(ArrayList<Expense> groupExpenseList) {


        int yourTotalExpense = 0 ;

        for (int i=0;i<groupExpenseList.size();i++)
        {
            Expense expense = groupExpenseList.get(i);
            ArrayList<ExpenseUser> expenseUserList = expense.getExpenseUserList();
            for (int k = 0; k<expenseUserList.size();k++)
            {
                ExpenseUser expenseUser = expenseUserList.get(k);
                //if the user phone number matches with the current logged in user and user role is true
                if (Common.currentUser.getPhoneNumber().equals(expenseUser.getPhoneNumber()) && expenseUser.getUserRole())
                {
                    yourTotalExpense = yourTotalExpense+Integer.parseInt(expenseUser.getShareAmount());
                }

            }

        }

        Log.d(TAG, "totalexpense rs =  "+yourTotalExpense);
      //  Toast.makeText(this, ""+yourTotalExpense, Toast.LENGTH_SHORT).show();

        //
        ViewGroupExpensesFragment viewGroupExpensesFragment = new ViewGroupExpensesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.expenseContainer,viewGroupExpensesFragment,"viewGroupExpenseF");
        viewGroupExpensesFragment.setExpenseList(groupExpenseList,yourTotalExpense);
        transaction.addToBackStack("addViewGroupExpenseF");
        transaction.commit();

    }

    private void removeViewGroupExpensesFragment()
    {
        ViewGroupExpensesFragment  fragment = (ViewGroupExpensesFragment) manager.findFragmentByTag("viewGroupExpenseF");
        if (fragment!=null)
        {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
            manager.popBackStack();
        }
    }




private void removeViewGroupExpenseDetailsFragment()
    {
        ViewExpenseDetailsFragment  fragment = (ViewExpenseDetailsFragment) manager.findFragmentByTag("viewGroupExpenseDetailsF");
        if (fragment!=null)
        {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
            manager.popBackStack();
        }
    }

    private void addNotExpenseFoundFragment() {

        NoExpenseFoundFragment noExpenseFoundFragment = new NoExpenseFoundFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.expenseContainer,noExpenseFoundFragment,"noExpenseFoundF");
        transaction.addToBackStack("noExpenseF");
        transaction.commit();
    }


    private void removeNoExpenseFoundFragment()
    {
        NoExpenseFoundFragment  fragment = (NoExpenseFoundFragment) manager.findFragmentByTag("noExpenseFoundF");
        if (fragment!=null)
        {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
            manager.popBackStack();
        }
    }


    //on Back pressing
    @Override
    public void onBackPressed() {

        if (manager.getBackStackEntryCount()>1)
        {
            removeViewGroupExpenseDetailsFragment();
            showViewGroupExpensesFragment();
        }
        else if (manager.getBackStackEntryCount() == 1)
        {
            if (groupObj.getGroupIdList().size()==0)
            {
                removeNoExpenseFoundFragment();
            }
            else
            {
                removeViewGroupExpensesFragment();
            }
            super.onBackPressed();
        }
        else if (manager.getFragments().size()==0)
        {
            super.onBackPressed();
        }

    }



    //on Back Navigation of toolbar
    @Override
    public boolean onSupportNavigateUp() {
        if (manager.getBackStackEntryCount()>1)
        {
            removeViewGroupExpenseDetailsFragment();
            showViewGroupExpensesFragment();

        }
        else if (manager.getBackStackEntryCount() == 1)
        {
            if (groupObj.getGroupIdList().size()==0)
            {
                removeNoExpenseFoundFragment();
            }
            else
            {
                removeViewGroupExpensesFragment();
            }
            return super.onSupportNavigateUp();
        }
        else if (manager.getFragments().size()==0)
        {
            return super.onSupportNavigateUp();
        }
        return false;
    }


    public void showViewGroupExpensesFragment() {
        ViewGroupExpensesFragment fragment = (ViewGroupExpensesFragment)manager.findFragmentByTag("viewGroupExpenseF");
        if (fragment!=null)
        {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.show(fragment);
            transaction.commit();
        }
        else
        {
            Log.d(TAG, "showViewGroupExpensesFragment: not found !!");
         //   Toast.makeText(this, "ViewGroupExpenseF not found", Toast.LENGTH_SHORT).show();
        }
    }


    //Check internet connection
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "haveNetworkConnection: "+e );
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    //Dialog show on checking internet connection, if app is not connected to internet
    private void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Connect to Internet or Cancel")
                .setCancelable(false)
                .setPositiveButton("Connect to WIFI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //ViewExpensesActivity.this.finish();
                        onBackPressed();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
