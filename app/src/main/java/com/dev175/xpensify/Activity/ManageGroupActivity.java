package com.dev175.xpensify.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.dev175.xpensify.Common.Common;
import com.dev175.xpensify.Fragment.ManageGroupsDisplayFragment;
import com.dev175.xpensify.Fragment.ManageGroupsFragment;
import com.dev175.xpensify.Fragment.NoGroupsFoundFragment;
import com.dev175.xpensify.Interface.IGetGroupIdsListener;
import com.dev175.xpensify.Interface.IGetGroupMembersListener;
import com.dev175.xpensify.LoadingDialog;
import com.dev175.xpensify.Model.Group;
import com.dev175.xpensify.Model.GroupUser;
import com.dev175.xpensify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ManageGroupActivity extends AppCompatActivity implements IGetGroupIdsListener, IGetGroupMembersListener, FragmentManager.OnBackStackChangedListener {

    private static final String TAG = ManageGroupActivity.class.getSimpleName();
    public FragmentManager manager;
    public Group groupObj;
    String groupId;

    private IGetGroupIdsListener iGetGroupIdsListener;         //interface to get groupIds
    private IGetGroupMembersListener iGetGroupMembersListener;  //interface to get Group members inside each group
    private LoadingDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_group);

        try {
            getSupportActionBar().setTitle("Manage Groups");
        }
        catch (Exception e){
            Log.e(TAG, "onCreate: "+e.getMessage());
        }

        init();
        iGetGroupIdsListener = this;
        iGetGroupMembersListener = this;

        getAllGroupIdsOfUser();

    }

    private void init()
    {
        dialog = new LoadingDialog(this);
        manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(this);
        groupObj = new Group();
    }


    public void addManageGroupsFragment(Group groupObj)
    {
        Map<String,ArrayList<GroupUser>> groupUsersList2 = groupObj.getGroupUsersMap();


        //This loop will iterate to the no of group Id

        ArrayList<String > newGroupIdList = new ArrayList<>();

        for (int i=0; i < groupObj.getGroupIdList().size();i++)
        {
            String id = groupObj.getGroupIdList().get(i);
            ArrayList<GroupUser>  groupUserArrayList =groupUsersList2.get(groupObj.getGroupIdList().get(i));
            if (groupUserArrayList.size()==0)
            {
          //      Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "addManageGroupsFragment: "+id);
            }
            else
            {
                newGroupIdList.add(id);
            }

           for (int j=0;j<groupUserArrayList.size();j++)
           {
               Log.d(TAG, "Group SIZE: "+groupUserArrayList.size());
               GroupUser groupUser = groupUserArrayList.get(j);
               String group_Name = groupUser.getGroupName();
               groupObj.setGroupNameList(id,group_Name);
               groupObj.setGroupNameIdList(group_Name,id);
               Log.d(TAG, "Group id : "+id+" Group name : "+group_Name);
           }
        }
        groupObj.setGroupIdList(newGroupIdList);

        ArrayList<String> groups = new ArrayList<>();

        for (int i=0;i<groupObj.getGroupIdList().size();i++)
        {
            String groupId = groupObj.getGroupIdList().get(i);
            String groupName = groupObj.getGroupNameList().get(groupId);
            groups.add(groupName);
            Log.d(TAG, "onCreateView: Group Name "+i+" "+groupName);
        }


        if (groups.size()!=0)
        {
            ManageGroupsFragment fragment = new ManageGroupsFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.container,fragment,"manageGroupsF");
            Log.i(TAG, "addManageGroupsFragment: added");
            fragment.setGroupObj(groupObj,groups);
            transaction.addToBackStack("addManageGroupF");
            transaction.commit();
        }
        else
        {
            addNotGroupsFragment();
        }
    }




    public void getAllGroupIdsOfUser(){

        dialog.startLoadingDialog();
        final String userId = FirebaseAuth.getInstance().getUid();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                            }
                            groupObj.setGroupIdList(groupList);

//                            Log.d(TAG, "groupIdList: "+groupObj.getGroupIdList().size());
//                            for (int i=0;i<groupObj.getGroupIdList().size();i++)
//                            {
//                                String id = groupObj.getGroupIdList().get(i);
//                                Log.d(TAG, "groud Id: "+i+" "+id);
//                            }

                            if (groupList.size()==0)
                            {
                                addNotGroupsFragment();
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

    private void addNotGroupsFragment() {
        NoGroupsFoundFragment noGroupsFoundFragment = new NoGroupsFoundFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container,noGroupsFoundFragment,"noGroupFoundF");
        transaction.addToBackStack("noGroupF");
        transaction.commit();
    }

    private void getMembersOfGroup(CollectionReference membersCol, final String groupId, final int totalGroups) {

            membersCol.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful())
                            {
                                ArrayList<GroupUser> groupUserArrayList = new ArrayList<>();

                                for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                                {
                                      Map<String,Object> obj =   queryDocumentSnapshot.getData();
                                  //   Log.d(TAG, "onComplete: mapdata"+queryDocumentSnapshot.getData());
                                     //Log.d(TAG, "onComplete: mapdata obj"+obj.get(Common.members).toString());
                                     Object groupUserObj =  obj.get(Common.members);
                                     Map<String,String> map = (HashMap)groupUserObj;
                                 //   Log.d(TAG, "onComplete: "+map.get(Common.groupName));;
                                 //   Log.d(TAG, "onComplete: "+map.get(Common.fullName));;
                                 //   Log.d(TAG, "onComplete: "+map.get(Common.phoneNumber));;


                                    //Now adding this group Obj to Array list and then this arrayList will be maped to groupId of Group
                                    GroupUser groupUser = new GroupUser();
                                    groupUser.setFullName(map.get(Common.fullName));
                                    groupUser.setGroupName(map.get(Common.groupName));
                                    groupUser.setPhoneNumber(map.get(Common.phoneNumber));

                                     groupUserArrayList.add(groupUser);
                                    //groupObj.setMembersIdList(queryDocumentSnapshot.getId());
                                }

                                groupObj.setGroupUsersMap(groupId,groupUserArrayList);
                                
                                if (groupObj.getGroupIdList().size()==totalGroups)
                                {
                               //     Toast.makeText(ManageGroupActivity.this, "onGetGroupMembersSuccess", Toast.LENGTH_SHORT).show();
                                    iGetGroupMembersListener.onGetGroupMembersSuccess(groupObj);
                                }

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


    @Override
    public void onGetGroupsSuccess(Group groupObj)
    {
        int totalGroups = groupObj.getGroupIdList().size();
        Log.d(TAG, "onGetGroupsSuccess: "+totalGroups);

        if(totalGroups==0)
        {
            dialog.dismissDialog();
        }

        //Now Send Group Id and Get mAP
        String userId = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Iterator iterator = groupObj.getGroupIdList().iterator();
        while (iterator.hasNext())
        {
            groupId =  iterator.next().toString();
        //    Toast.makeText(this, groupId, Toast.LENGTH_SHORT).show();

            CollectionReference membersCol = db
                    .collection(Common.users)
                    .document(userId)
                    .collection(Common.groups)
                    .document(groupId)
                    .collection(Common.members);

              getMembersOfGroup(membersCol,groupId,totalGroups);

        }

    }

    @Override
    public void onGetGroupsLoadFailed(String message) {
        dialog.dismissDialog();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onGetGroupMembersSuccess(Group groupObj) {


        if (groupObj.getGroupIdList().size()==groupObj.getGroupUsersMap().size())
        {
         //   Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();

            //Finally we get group Members of group.
            //Group object containts "ArrayList<String> groupIds"  & "Map<String,ArrayList<GroupUser>> groupMembers"
            //we pass group id from groupIds to Map object and get all members from that group

            //Adding first Fragemenr
            dialog.dismissDialog();
            addManageGroupsFragment(groupObj);
            
        }

    }

    @Override
    public void onGetGroupMembersFailed(String message) {
        dialog.dismissDialog();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }


    //on Back pressed
    @Override
    public void onBackStackChanged() {

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




    //on Back pressing

    @Override
    public void onBackPressed() {

        if (manager.getBackStackEntryCount()>1)
        {
            removeManageGroupsDisplayF();
        }
        else if (manager.getBackStackEntryCount() == 1)
        {
            if (groupObj.getGroupIdList().size()==0)
            {
                removeNoGroupsFoundF();
            }
            else
            {
                removeManageGroupsF();
            }
            super.onBackPressed();
        }

    }

    private void removeManageGroupsDisplayF()
    {
        ManageGroupsDisplayFragment  fragment = (ManageGroupsDisplayFragment) manager.findFragmentByTag("manageGroupsDisplayAddF");
        if (fragment!=null)
        {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
            manager.popBackStack();
        }
    }

    private void removeManageGroupsF()
    {
        ManageGroupsFragment fragment2 = (ManageGroupsFragment) manager.findFragmentByTag("manageGroupsF");
        if (fragment2!=null)
        {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(fragment2);
            transaction.commit();
            manager.popBackStack();
            manager.popBackStack();
        }
    }

    //on Back Navigation of toolbar
    @Override
    public boolean onSupportNavigateUp() {
        if (manager.getBackStackEntryCount()>1)
        {
            removeManageGroupsDisplayF();
        }
        else if (manager.getBackStackEntryCount() == 1)
        {
            if (groupObj.getGroupIdList().size()==0)
            {
                removeNoGroupsFoundF();
            }
            else
            {
                removeManageGroupsF();
            }
            return super.onSupportNavigateUp();
        }
        return false;
    }


    private void removeNoGroupsFoundF()
    {
        NoGroupsFoundFragment  fragment = (NoGroupsFoundFragment) manager.findFragmentByTag("noGroupFoundF");
        if (fragment!=null)
        {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
            manager.popBackStack();
        }
    }
}
