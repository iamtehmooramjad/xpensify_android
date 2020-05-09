package com.dev175.xpensify.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.dev175.xpensify.Adapter.GroupMembersListAdapter;
import com.dev175.xpensify.Common.Common;
import com.dev175.xpensify.Interface.IGetGroupIdsListener;
import com.dev175.xpensify.Interface.IGetGroupMembersListener;
import com.dev175.xpensify.Interface.IRecyclerViewClickListener;
import com.dev175.xpensify.Model.Expense;
import com.dev175.xpensify.Model.ExpenseUser;
import com.dev175.xpensify.Model.Group;
import com.dev175.xpensify.Model.GroupUser;
import com.dev175.xpensify.R;
import com.dev175.xpensify.ViewAnimation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity implements IGetGroupIdsListener, IGetGroupMembersListener, IRecyclerViewClickListener {


    private static final String TAG = AddExpenseActivity.class.getSimpleName();

    private List<View> view_list = new ArrayList<>();
    private List<RelativeLayout> step_view_list = new ArrayList<>();
    private int success_step = 0;
    private int current_step = 0;


    private Group groupObj;
    private String selectedGroup;
    private String selectedGroupId;
    private ArrayList<GroupUser> selectedMembers;
    private ArrayAdapter<String> spinnerGroupAdapter;
    private RecyclerView.LayoutManager layoutManager_selectMember;
    private ArrayList<GroupUser> groupUserArrayList ;
    private IGetGroupIdsListener iGetGroupIdsListener;
    private IGetGroupMembersListener iGetGroupMembersListener;
    private Expense expense;
    private  GroupMembersListAdapter adapter;

    //Views
    private AppCompatEditText et_title,et_amount;
    private Spinner sp_selectedGroup;
    private RecyclerView recycler_selectMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        try
        {
            getSupportActionBar().setTitle("Add new Expense");
        }
        catch (Exception e){
            Log.e(TAG, "onCreate: "+e.getMessage());
        }

        init();
        groupObj = new Group();
        expense = new Expense();
        iGetGroupIdsListener = this;
        iGetGroupMembersListener = this;

        sp_selectedGroup = findViewById(R.id.sp_selectGroup);
        recycler_selectMember = findViewById(R.id.recycler_selectMember);
        et_title = findViewById(R.id.et_title);
        et_amount = findViewById(R.id.et_amount);


        layoutManager_selectMember = new LinearLayoutManager(this);
        recycler_selectMember.setLayoutManager(layoutManager_selectMember);
        selectedMembers = new ArrayList<GroupUser>();

    }

    public void getAllGroupIdsOfUser(){

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
                                String status = (String) queryDocumentSnapshot.get(Common.groupStatus);
                                if (status.equals(Common.joined))
                                {
                                    //Adding Group Id to groupObj groupIdList
                                    groupList.add(queryDocumentSnapshot.getId());
                                    groupObj.setGroupNameList(queryDocumentSnapshot.getId(),(String)queryDocumentSnapshot.get(Common.groupName));
                                    groupObj.setGroupNameIdList((String)queryDocumentSnapshot.get(Common.groupName),queryDocumentSnapshot.getId());

                                }
                            }
                            groupObj.setGroupIdList(groupList);

                            Log.d(TAG, "groupIdList: "+groupObj.getGroupIdList().size());
                            for (int i=0;i<groupObj.getGroupIdList().size();i++)
                            {
                                String id = groupObj.getGroupIdList().get(i);
                                Log.d(TAG, "groud Id: "+i+" "+id);
                            }


                            iGetGroupIdsListener.onGetGroupsSuccess(groupObj);
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


    public void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    private void init() {

        view_list.add(findViewById(R.id.lyt_title));
        view_list.add(findViewById(R.id.lyt_amount));
        view_list.add(findViewById(R.id.lyt_selectGroup));
        view_list.add(findViewById(R.id.lyt_selectMember));
        view_list.add(findViewById(R.id.lyt_confirmation));


        step_view_list.add((RelativeLayout) findViewById(R.id.step_title));
        step_view_list.add((RelativeLayout) findViewById(R.id.step_amount));
        step_view_list.add((RelativeLayout) findViewById(R.id.step_selectGroup));
        step_view_list.add((RelativeLayout) findViewById(R.id.step_selectMember));
        step_view_list.add((RelativeLayout) findViewById(R.id.step_confirm));


        for (View view : view_list)
        {
            view.setVisibility(View.GONE);
        }


        view_list.get(0).setVisibility(View.VISIBLE);
        hideSoftKeyboard();




    }

    public void clickAction(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.btn_continue_title:
            {
                if (et_title.getText().toString().trim().equals(""))
                {
                    Toast.makeText(this, "Title cannot empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                collapseAndContinue(0);
                break;
            }

            case R.id.btn_continue_amount:
            {
                if (et_amount.getText().toString().trim().equals(""))
                {
                    Toast.makeText(this, "Amount cannot empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                getAllGroupIdsOfUser();
                collapseAndContinue(1);
                break;
            }

            case R.id.btn_continue_group:
            {
                if (sp_selectedGroup.getSelectedItemPosition()==0)
                {
                    Toast.makeText(this, "Please Select Any Group", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    selectedGroup = sp_selectedGroup.getSelectedItem().toString();
                   // Toast.makeText(this, selectedGroup, Toast.LENGTH_SHORT).show();
                    getGroupMembers(selectedGroup);
                }

                collapseAndContinue(2);
                break;

            }

            case R.id.btn_continue_member:
            {
                if (selectedMembers.size()==0)
                {
                    Toast.makeText(this, "Select at least one member", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (selectedMembers.size()!=0)
                {
                    for (int i=0;i<selectedMembers.size();i++)
                    {
                        if (!PhoneNumberUtils.compare(Common.currentUser.getPhoneNumber(),selectedMembers.get(i).getPhoneNumber()))
                        {
                          if (i==selectedMembers.size()-1)
                          {
                              Toast.makeText(this, "You must add yourself into this transaction", Toast.LENGTH_SHORT).show();
                              return;
                          }

                        }
                        else if (PhoneNumberUtils.compare(Common.currentUser.getPhoneNumber(),selectedMembers.get(i).getPhoneNumber()))
                        {
                            break;
                        }
                    }
                }

                collapseAndContinue(3);
                break;

            }

            case R.id.btn_confirm:
            {
                //Getting current date and time
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");

                //total selected Members
                int totalMembers = selectedMembers.size();

                //timestamp
                String timestamp = simpleDateFormat.format(calendar.getTime());
                //Title
                String title= et_title.getText().toString();
                //Amount
                String amount = et_amount.getText().toString();

                //creditorAmount
                int value  = Integer.valueOf(amount);
                int shareAmount = value/totalMembers;

                //Set Data to expense obj
                expense.setTitle(title);
                expense.setAmount(amount);
                expense.setTimestamp(timestamp);

                Iterator iterator = selectedMembers.iterator();
                ArrayList<ExpenseUser> expenseUserArrayList = new ArrayList<>();

                while (iterator.hasNext())
                {
                    GroupUser user = (GroupUser) iterator.next();

                    //Now create expenseuser obj and add into expense obj arralist
                    ExpenseUser expenseUser = new ExpenseUser();
                    expenseUser.setName(user.getFullName());
                    expenseUser.setPhoneNumber(user.getPhoneNumber());
                    expenseUser.setShareAmount(String.valueOf(shareAmount));

                    //Now set role of expense user.Either true=>creditor or false=>debitor
                    if (PhoneNumberUtils.compare(Common.currentUser.getPhoneNumber(),user.getPhoneNumber()))
                    {
                        //set user role to true means he is creditor
                        expenseUser.setUserRole(true);

                    }
                    else {
                        expenseUser.setUserRole(false);
                    }

                expenseUserArrayList.add(expenseUser);
                }

                expense.setExpenseUserList(expenseUserArrayList);


                //Now save this expense object to Firestore
                saveExpenseToDb(expense);

            }
                finish();
                break;
        }
    }

    private void saveExpenseToDb(Expense expense) {

        String uid = FirebaseAuth.getInstance().getUid();
        CollectionReference expenseCol = FirebaseFirestore.getInstance()
            .collection(Common.users)
            .document(uid)
            .collection(Common.groups)
            .document(selectedGroupId)
            .collection(Common.expenses);

        expenseCol
                .add(expense)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddExpenseActivity.this, "Expense Saved Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: "+e.getMessage() );
                    }
                });
    }

    private void getGroupMembers(String selectedGroup) {

        Map<String ,String > groupNameIdMap = groupObj.getGroupNameIdList();
        selectedGroupId  =groupNameIdMap.get(selectedGroup);

        final String userId = FirebaseAuth.getInstance().getUid();

        CollectionReference membersCollection = FirebaseFirestore.getInstance()
                .collection(Common.users)
                .document(userId)
                .collection(Common.groups)
                .document(selectedGroupId)
                .collection(Common.members);

            membersCollection
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())
                        {
                            ArrayList<GroupUser> groupUserArrayList = new ArrayList<>();

                            for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                            {
                                Map<String,Object> obj =   queryDocumentSnapshot.getData();

                                Object groupUserObj =  obj.get(Common.members);
                                Map<String,String> map = (HashMap)groupUserObj;
                                Log.d(TAG, "onComplete: "+map.get(Common.groupName));;
                                Log.d(TAG, "onComplete: "+map.get(Common.fullName));;
                                Log.d(TAG, "onComplete: "+map.get(Common.phoneNumber));;


                                //Now adding this group Obj to Array list and then this arrayList will be maped to groupId of Group
                                GroupUser groupUser = new GroupUser();
                                groupUser.setFullName(map.get(Common.fullName));
                                groupUser.setGroupName(map.get(Common.groupName));
                                groupUser.setPhoneNumber(map.get(Common.phoneNumber));

                                groupUserArrayList.add(groupUser);
                                //groupObj.setMembersIdList(queryDocumentSnapshot.getId());
                            }

                            groupObj.setGroupUsersMap(selectedGroupId,groupUserArrayList);
                            iGetGroupMembersListener.onGetGroupMembersSuccess(groupObj);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iGetGroupMembersListener.onGetGroupMembersFailed(e.getMessage());
                    }
                });


    }


    private void collapseAll() {
        for (View v : view_list) {
            ViewAnimation.collapse(v);
        }
    }


    public void clickLabel(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_label_title:
                if (success_step >= 0 && current_step != 0) {
                    current_step = 0;
                    collapseAll();
                    ViewAnimation.expand(view_list.get(0));
                }
                break;
            case R.id.tv_label_amount:
                if (success_step >= 1 && current_step != 1) {
                    current_step = 1;
                    collapseAll();
                    ViewAnimation.expand(view_list.get(1));
                }
                break;
            case R.id.tv_label_selectGroup:
                if (success_step >= 2 && current_step != 2) {
                    current_step = 2;
                    collapseAll();
                    selectedMembers.clear();
                    adapter = new GroupMembersListAdapter(groupUserArrayList,this,this);
                    recycler_selectMember.setAdapter(adapter);
                    ViewAnimation.expand(view_list.get(2));

                }
                break;

            case R.id.tv_label_selectMember:
                if (success_step >= 3 && current_step != 3) {
                    current_step = 3;
                    collapseAll();
                    ViewAnimation.expand(view_list.get(3));
                }
                break;

            case R.id.tv_label_confirm:
                if (success_step >= 4 && current_step != 4) {
                    current_step = 4;
                    collapseAll();
                    ViewAnimation.expand(view_list.get(4));
                }
                break;
        }
    }


    private void collapseAndContinue(int index) {
        ViewAnimation.collapse(view_list.get(index));
        setCheckedStep(index);
        index++;
        current_step = index;
        success_step = index > success_step ? index : success_step;
        ViewAnimation.expand(view_list.get(index));
    }

    private void setCheckedStep(int index) {
        RelativeLayout relative = step_view_list.get(index);
        relative.removeAllViews();
        ImageButton img = new ImageButton(this);
        img.setImageResource(R.drawable.ic_done_accent);
        img.setBackgroundColor(Color.TRANSPARENT);
        img.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        relative.addView(img);
    }

    @Override
    public void onGetGroupsSuccess(Group groupObj) {

        Iterator iterator = groupObj.getGroupIdList().iterator();
        Map<String ,String > groupsMap = groupObj.getGroupNameList();
        ArrayList<String> groups = new ArrayList<String>();
        groups.add("Select Group");
        while (iterator.hasNext())
        {
            groups.add(groupsMap.get(iterator.next()));
        }
        if (groups.size()==1)
        {
            groups.clear();
            groups.add("No groups found");
        }
        spinnerGroupAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,groups);
        sp_selectedGroup.setAdapter(spinnerGroupAdapter);

    }

    @Override
    public void onGetGroupsLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetGroupMembersSuccess(Group groupObj) {


        Map<String ,ArrayList<GroupUser>> groupUserMap = groupObj.getGroupUsersMap();
        groupUserArrayList = groupUserMap.get(selectedGroupId);

        //Now set recycler view to select members
        adapter = new GroupMembersListAdapter(groupUserArrayList,this,this);
        recycler_selectMember.setAdapter(adapter);
    }

    @Override
    public void onGetGroupMembersFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void recyclerViewItemClicked(int position) {
        GroupUser groupUser= groupUserArrayList.get(position);
        if (!selectedMembers.contains(groupUser))
       {
           selectedMembers.add(groupUser);
        //   Toast.makeText(this,groupUser.getFullName() +" Added", Toast.LENGTH_SHORT).show();

       }
    }

    @Override
    public void recyclerViewItemUnClicked(int position) {
        GroupUser groupUser= groupUserArrayList.get(position);
        if (selectedMembers.contains(groupUser))
        {
            selectedMembers.remove(groupUser);
           // Toast.makeText(this,groupUser.getFullName()+" Removed ", Toast.LENGTH_SHORT).show();
        }

    }
}
